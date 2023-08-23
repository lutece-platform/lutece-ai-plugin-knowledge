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

import java.util.List;
import java.util.Optional;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.knowledge.business.Document;
import fr.paris.lutece.plugins.knowledge.business.DocumentHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This service class provides functionalities to manage and interact with documents. It facilitates the addition, deletion, retrieval, and listing of
 * documents.
 */
public class DocumentService
{

    private static final IFileStoreServiceProvider FILE_STORE_SERVICE = FileService.getInstance( ).getFileStoreServiceProvider( );

    /**
     * Private constructor to prevent the creation of instances of this utility class.
     */
    private DocumentService( )
    {
        // Private constructor to prevent instantiation
    }

    /**
     * Add a document associated with a specific project to the datastore. If the provided document has content, it will be stored and an entry will be created
     * in the database.
     *
     * @param document
     *            The document item to be added.
     * @param projectId
     *            The ID of the project with which the document is associated.
     * @throws IllegalStateException
     *             if there's an error storing the document.
     */
    public static void addDocument( FileItem document, int projectId )
    {
        if ( document != null && document.getSize( ) > 0 )
        {
            try
            {
                String fileStoreKey = FILE_STORE_SERVICE.storeFileItem( document );
                File localFile = new File( );
                localFile.setFileKey( fileStoreKey );

                Document doc = new Document( );
                doc.setDocumentData( localFile );
                doc.setDocumentName( document.getName( ) );
                doc.setProjectId( projectId );
                DocumentHome.create( doc );

            }
            catch( Exception e )
            {
                AppLogService.error( "Error storing the file", e );
                throw new IllegalStateException( "Error storing the file", e );
            }
        }
    }

    /**
     * Delete a document identified by its ID from the datastore. This will remove both the file from the storage and the entry from the database.
     *
     * @param documentId
     *            The ID of the document to be deleted.
     */
    public static void deleteDocument( int documentId )
    {
        Optional<Document> document = DocumentHome.findByPrimaryKey( documentId );
        document.ifPresent( doc -> {
            FILE_STORE_SERVICE.delete( doc.getDocumentData( ).getFileKey( ) );
            DocumentHome.remove( documentId );
        } );
    }

    /**
     * Retrieve a file representation of a document identified by its ID.
     *
     * @param documentId
     *            The ID of the document to be retrieved.
     * @return A {@link File} object representing the document or null if the document is not found.
     */
    public static File getDocument( int documentId )
    {
        Optional<Document> document = DocumentHome.findByPrimaryKey( documentId );
        return document.map( doc -> FILE_STORE_SERVICE.getFile( doc.getDocumentData( ).getFileKey( ) ) ).orElse( null );
    }

    /**
     * Get a list of all documents associated with a specific project.
     *
     * @param projectId
     *            The ID of the project for which the documents are to be listed.
     * @return A list of {@link Document} objects associated with the given project.
     */
    public static List<Document> getDocumentsList( int projectId )
    {
        return DocumentHome.getDocumentsList( projectId );
    }
}
