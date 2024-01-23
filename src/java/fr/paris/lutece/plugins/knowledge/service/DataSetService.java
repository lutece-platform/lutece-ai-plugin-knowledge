package fr.paris.lutece.plugins.knowledge.service;

import java.util.Optional;
import org.apache.commons.fileupload.FileItem;
import fr.paris.lutece.plugins.knowledge.business.Dataset;
import fr.paris.lutece.plugins.knowledge.business.DatasetFile;
import fr.paris.lutece.plugins.knowledge.business.DatasetFileHome;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppLogService;

public class DataSetService
{
    public static final IFileStoreServiceProvider FILE_STORE_SERVICE = FileService.getInstance( ).getFileStoreServiceProvider( );

    private DataSetService( )
    {
    }

    /**
     * Create a new document in the datastore. This will store the file in the storage and create an entry in the database.
     * @param document The file to be stored.
     * @param dataSet The dataset to which the file belongs.
     * @return The created document.
     */
    public static DatasetFile create( FileItem document, Dataset dataSet )
    {

        if ( document != null && document.getSize( ) > 0 )
        {
            try
            {
                String fileStoreKey = FILE_STORE_SERVICE.storeFileItem( document );
                DatasetFile doc = new DatasetFile( );
                doc.setFile( document.getInputStream( ) );
                doc.setFileKey( fileStoreKey );
                doc.setName( document.getName( ) );
                doc.setDatasetId( dataSet.getId( ) );
                DatasetFile datasetFile = DatasetFileHome.create( doc );
                ElasticStoreService.store( datasetFile, dataSet );
                return datasetFile;

            }
            catch( Exception e )
            {
                AppLogService.error( "Error storing the file", e );
                throw new IllegalStateException( "Error storing the file", e );
            }
        }
        return null;

    }

    /**
     * Delete a document identified by its ID from the datastore. This will remove both the file from the storage and the entry from the database.
     *
     * @param documentId
     *            The ID of the document to be deleted.
     */
    public static void deleteDocument( int documentId )
    {
        Optional<DatasetFile> document = DatasetFileHome.findByPrimaryKey( documentId );
        document.ifPresent( doc -> {
            FILE_STORE_SERVICE.delete( doc.getFileKey( ) );
            DatasetFileHome.remove( documentId );
        } );
    }

    /**
     * Get the file store service provider.
     * @return The file store service provider.
     */
    public static IFileStoreServiceProvider getFileStoreServiceProvider( )
    {
        return FILE_STORE_SERVICE;
    }

}
