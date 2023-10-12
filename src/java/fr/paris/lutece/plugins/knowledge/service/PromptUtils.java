package fr.paris.lutece.plugins.knowledge.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

/**
 * Nested utility class for prompt-related operations.
 */
public class PromptUtils
{

    /**
     * Generates a prompt from the given question and information.
     */
    public static Prompt generateQAPrompt( String question, String information, List<String> fileNamesSources, String systemPrompt, String noDataResponse )
    {
        String prompt = systemPrompt + "\n" + noDataResponse;
        String formattedTemplate = String.format( prompt, noDataResponse );
        Map<String, Object> variables = new HashMap<>( );
        variables.put( "question", question );
        variables.put( "informations", information );
        String fileNamesSourcesString = String.join( ", ", fileNamesSources );
        variables.put( "file_names_sources", fileNamesSourcesString );
        Prompt promptObject = PromptTemplate.from( formattedTemplate ).apply( variables );
        System.out.println( promptObject.toString( ) );
        return promptObject;
    }
}
