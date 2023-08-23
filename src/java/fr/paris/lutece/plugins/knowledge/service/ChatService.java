/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import fr.paris.lutece.plugins.knowledge.business.ModelConfiguration;
import fr.paris.lutece.plugins.knowledge.utils.PromptUtils;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_4;

import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.joining;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.checkerframework.checker.units.qual.A;

import com.uwyn.jhighlight.fastutil.Arrays;

/**
 * This service handles the interactions and processing related to chat functionalities. It utilizes AI models to generate answers for given questions and
 * streams the results.
 */
public class ChatService
{

    private static final int MAX_RESULTS = 10;
    private static final double MIN_SIMILARITY = 0.5;
    private static final String API_KEY = AppPropertiesService.getProperty( "knowledge.apiKey" );
    private static final String MODEL_NAME = AppPropertiesService.getProperty( "knowledge.modelName" );

    /**
     * Represents an AI assistant that can engage in a chat with the user.
     */
    interface Assistant
    {
        /**
         * Interact with the AI assistant using a user message.
         *
         * @param userMessage
         *            The message from the user to start the chat.
         * @return A token stream representing the AI's response.
         */
        TokenStream chat( String userMessage );
    }

    /**
     * Get an AI-generated answer for a given question. The AI utilizes embeddings and relevant information to generate its response.
     *
     * @param question
     *            The question from the user.
     * @param session
     *            The current HTTP session.
     * @param projectId
     *            The ID of the project associated with the chat session.
     * @param clientOutputStream
     *            The output stream to which the response will be written.
     * @return A CompletableFuture that will complete once the AI-generated answer has been handled.
     */
    public static CompletableFuture<Void> getAnswer( String question, HttpSession session, Integer projectId, OutputStream clientOutputStream,
            ModelConfiguration config )
    {
        CompletableFuture<Void> future = new CompletableFuture<>( );

        EmbeddingStore<TextSegment> projectEmbeddingStore = EmbeddingService.getEmbeddingStoreByProjectId( projectId );

        StreamingChatLanguageModel chatLanguageModel = OpenAiStreamingChatModel.builder( ).apiKey( API_KEY )
                .modelName( config != null && config.getModelName( ) != null ? config.getModelName( ) : MODEL_NAME )
                .temperature( config != null && config.getTemperature( ) != null ? config.getTemperature( ) : 0.0 )
                .maxTokens( config != null && config.getMaxTokens( ) != null ? config.getMaxTokens( ) : 300 )
                .topP( config != null && config.getTopP( ) != null ? config.getTopP( ) : 1.0 )
                .presencePenalty( config != null && config.getPresencePenalty( ) != null ? config.getPresencePenalty( ) : 0.0 )
                .frequencyPenalty( config != null && config.getFrequencyPenalty( ) != null ? config.getFrequencyPenalty( ) : 0.0 ).timeout( ofSeconds( 120 ) )
                .logRequests( true ).logResponses( true ).build( );

        if ( projectEmbeddingStore == null )
        {
            throw new IllegalArgumentException( "No embedding store found for the provided projectId." );
        }

        Embedding questionEmbedding = EmbeddingService.getEmbeddingModel( ).embed( question );
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = projectEmbeddingStore.findRelevant( questionEmbedding, MAX_RESULTS, MIN_SIMILARITY );
        String information = relevantEmbeddings.stream( ).map( match -> match.embedded( ).text( ) ).collect( joining( "\n\n" ) );

        Prompt prompt = PromptUtils.generateQAPrompt( question, information );

        MessageWindowChatMemory messageWindowChatMemory = ChatMemoryService.getChatMemory( session, projectId );
        Assistant assistant = AiServices.builder( Assistant.class ).streamingChatLanguageModel( chatLanguageModel ).chatMemory( messageWindowChatMemory )
                .build( );

        handleTokenStream( assistant.chat( prompt.toUserMessage( ).toString( ) ), clientOutputStream, future );

        return future;
    }

    /**
     * Handles the token stream logic. As the AI generates its response, this method captures and processes the tokens, and writes them to an output stream.
     *
     * @param tokenStream
     *            The token stream representing the AI's response.
     * @param clientOutputStream
     *            The output stream to which tokens will be written.
     * @param future
     *            A CompletableFuture to track the process's completion.
     */
    private static void handleTokenStream( TokenStream tokenStream, OutputStream clientOutputStream, CompletableFuture<Void> future )
    {
        tokenStream.onNext( token -> {
            try ( Writer writer = new OutputStreamWriter( clientOutputStream, StandardCharsets.UTF_8 ) )
            {
                writer.write( token );
                writer.flush( );
            }
            catch( IOException e )
            {
                AppLogService.error( "Error during streaming: ", e );
                future.completeExceptionally( e );
            }
        } ).onComplete( ( ) -> {

            future.complete( null );
        } ).onError( e -> {
            AppLogService.error( "Error during streaming: ", e );
            future.completeExceptionally( e );
        } ).start( );
    }

}
