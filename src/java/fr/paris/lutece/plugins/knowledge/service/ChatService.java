package fr.paris.lutece.plugins.knowledge.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.glassfish.jersey.media.sse.EventOutput;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import fr.paris.lutece.plugins.knowledge.business.Bot;
import fr.paris.lutece.plugins.knowledge.business.BotHome;
import fr.paris.lutece.plugins.knowledge.business.Dataset;
import fr.paris.lutece.plugins.knowledge.business.DatasetHome;
import fr.paris.lutece.plugins.knowledge.rs.BotResponse;
import fr.paris.lutece.plugins.knowledge.rs.RequestData;
import fr.paris.lutece.plugins.knowledge.service.ChatMemoryService.PersistentChatMemoryStore;


public class ChatService
{

    public static CompletableFuture<Void> run( HttpServletRequest request, RequestData data, EventOutput output, String sessionId )
    {
        BotResponse botResponse = new BotResponse( output, request.getSession( ) );
        botResponse.initStep( 0, Constant.STEP_CHAT );
        CompletableFuture<Void> stepFuture = new CompletableFuture<>( );
        int botId = Integer.parseInt( data.getBotId( ) );
        Bot bot = BotHome.findByPrimaryKey( botId ).get( );


        Pair<PersistentChatMemoryStore, String> chatMemoryStore = ChatMemoryService.getChatMemory( request, data, bot, sessionId );

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder().id( memoryId ).maxMessages(10).chatMemoryStore( chatMemoryStore.getLeft( ) ).build();
        StreamingChatLanguageModel chatLanguageModel = buildChatLanguageModel( bot );
        StreamingAssistant assistant = AiServices.builder( StreamingAssistant.class ).streamingChatLanguageModel( chatLanguageModel ).chatMemoryProvider( chatMemoryProvider )
                .build( );

                
        if ( bot.getDatasetId( ) != 0 )
        {
            Dataset dataSet = DatasetHome.findByPrimaryKey( bot.getDatasetId( ) ).get( );
            EmbeddingStore<TextSegment> embeddingStore = ElasticStoreService.getEmbeddingStore( bot.getDatasetId( ) );
            String promptText = generatePromptText( data, embeddingStore, dataSet );
            return processChatStream( assistant, botResponse, stepFuture, promptText, chatMemoryStore.getRight( ) );
        }
        else
        {
            return processChatStream( assistant, botResponse, stepFuture, data.getQuestion( ), chatMemoryStore.getRight( ) );
        }
    }

    private static StreamingChatLanguageModel buildChatLanguageModel( Bot bot )
    {
        if ( Constant.PROXY_HOST != null && Constant.PROXY_PORT != null )
        {
            SocketAddress _proxyAddress = new InetSocketAddress( Constant.PROXY_HOST, Integer.valueOf( Constant.PROXY_PORT ) );
            Proxy proxy = new Proxy( Proxy.Type.HTTP, _proxyAddress );
            return OpenAiStreamingChatModel.builder( ).proxy( proxy ).apiKey( Constant.API_KEY ).modelName( bot.getModelId( ) ).temperature( 0.0 )
                    .timeout( Duration.ofSeconds( 30 ) ).build( );
        }
        else
        {
            return OpenAiStreamingChatModel.builder( ).apiKey( Constant.API_KEY ).modelName( bot.getModelId( ) ).temperature( 0.0 )
                    .timeout( Duration.ofSeconds( 30 ) ).build( );
        }
    }

    private static CompletableFuture<Void> processChatStream( StreamingAssistant assistant, BotResponse botResponse, CompletableFuture<Void> stepFuture,
            String inputText, String memoryId )
    {
        TokenStream chatStream = assistant.chat( memoryId, inputText );
        StringBuilder responseBuilder = new StringBuilder( );
        chatStream.onNext( token -> {
            responseBuilder.append( token );
            botResponse.updateStep( 0, responseBuilder.toString( ) );
        } ).onComplete( token -> {
            stepFuture.complete( null );
        } ).onError( e -> {
            botResponse.updateStep( 0, e.getMessage( ) );
            stepFuture.completeExceptionally( e );
        } ).start( );
        return stepFuture;
    }

    private static String generatePromptText( RequestData data, EmbeddingStore<TextSegment> embeddingStore, Dataset dataSet )
    {
        Response<Embedding> questionEmbedding = ElasticStoreService.getEmbeddingModel( ).embed( data.getQuestion( ) );
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant( questionEmbedding.content( ), dataSet.getSearchMaxRecord( ), 0.7 );
        String embeddingMatchText = relevantEmbeddings.stream( ).map( match -> match.embedded( ).text( ) ).collect( Collectors.joining( "\n\n" ) );
        List<String> fileNamesSources = Collections.singletonList( "lutece.pdf" );
        return PromptUtils.generateQAPrompt( data.getQuestion( ), embeddingMatchText, fileNamesSources, dataSet.getMatchInstruction( ),
                dataSet.getMismatchInstruction( ) ).text( );
    }

    interface StreamingAssistant
    {
        TokenStream chat( @MemoryId String memoryId, @UserMessage String userMessage);
    }
}
