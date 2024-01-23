
package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.parser.*;
import dev.langchain4j.data.document.splitter.*;
import dev.langchain4j.data.embedding.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.*;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import fr.paris.lutece.plugins.knowledge.business.Dataset;
import fr.paris.lutece.plugins.knowledge.business.DatasetFile;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import java.io.InputStream;
import java.util.*;
import static dev.langchain4j.model.openai.OpenAiModelName.*;
import static java.time.Duration.*;

public class ElasticStoreService
{
    private static IFileStoreServiceProvider fileStoreService = DataSetService.getFileStoreServiceProvider( );
    private static final Map<Integer, EmbeddingStore<TextSegment>> embeddingStores = new HashMap<>( );
    private static final EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder( ).apiKey( Constant.API_KEY ).modelName( TEXT_EMBEDDING_ADA_002 )
            .timeout( ofSeconds( 600 ) ).logRequests( true ).logResponses( true ).build( );

    /**
     * Stores a file.
     * 
     * @param document
     *            The document to store.
     * @param projectId
     *            The ID of the project.
     */
    public static void store( DatasetFile dataSetFile, Dataset dataSet )
    {
        String fileKey = dataSetFile.getFileKey( );
        EmbeddingStore<TextSegment> projectEmbeddingStore;

        projectEmbeddingStore = embeddingStores.computeIfAbsent( dataSet.getId( ), key -> {
            return getElasticsearchEmbeddingStore( dataSet.getId( ) );
        } );

        // get file
        InputStream file = fileStoreService.getInputStream( fileKey );
        Document document4j = parseDocument( file, dataSetFile.getName( ) );

        // Generate embeddings
        List<TextSegment> segments = new DocumentByLineSplitter( dataSet.getRecordMaxTokens( ), 5 ).split( document4j );

        Response<List<Embedding>> embeddings = embeddingModel.embedAll( segments );

        // Store embeddings
        projectEmbeddingStore.addAll( embeddings.content( ), segments );

    }

    /**
     * Gets the embedding sources for the relevant embeddings.
     * 
     * @param relevantEmbeddings
     *            The relevant embeddings.
     * @return The list of embedding sources.
     */
    private static Document parseDocument( InputStream inputStream, String fileName )
    {
        String extension = fileName.substring( fileName.lastIndexOf( "." ), fileName.length( ) );

        return extensionSwitcher( extension, inputStream );
    }

    /**
     * Switches on the extension of the file.
     * 
     * @param extension
     *            The extension of the file.
     * @param inputStream
     *            The input stream of the file.
     * @return The parsed document.
     */
    private static Document extensionSwitcher( String extension, InputStream inputStream )
    {
        switch( extension )
        {
            case ".pdf":
                return new PdfDocumentParser( ).parse( inputStream );
            case ".docx":
                return new MsOfficeDocumentParser( DocumentType.DOC ).parse( inputStream );
            case ".pptx":
                return new MsOfficeDocumentParser( DocumentType.PPT ).parse( inputStream );
            case ".xlsx":
                return new MsOfficeDocumentParser( DocumentType.XLS ).parse( inputStream );
            default:
                return new TextDocumentParser( DocumentType.TXT ).parse( inputStream );
        }
    }

    /**
     * Gets the embedding store for the given project ID.
     * 
     * @param projectId
     *            The ID of the project.
     * @return The embedding store.
     */
    private static ElasticsearchEmbeddingStore getElasticsearchEmbeddingStore( int dateSetId )
    {
        // null basicauth credentials are managed by the builder
        return ElasticsearchEmbeddingStore.builder( ).serverUrl( Constant.ELASTIC_URL ).userName( Constant.ELASTIC_USERNAME ).password( Constant.ELASTIC_PASSWORD )
                .indexName( "luteceai-embeddings-" + dateSetId ).build( );
    };

    /**
     * Gets the embedding store by project ID.
     * 
     * @param projectId
     *            The ID of the project.
     * @return The embedding store.
     */
    public static EmbeddingStore<TextSegment> getEmbeddingStore( int dateSetId )
    {
        if ( !embeddingStores.containsKey( dateSetId ) )
        {
            embeddingStores.put( dateSetId, getElasticsearchEmbeddingStore( dateSetId ) );
        }

        return embeddingStores.get( dateSetId );
    }

    /**
     * Gets the embedding model.
     * 
     * @return The embedding model.
     */
    public static EmbeddingModel getEmbeddingModel( )
    {
        return embeddingModel;
    }

}
