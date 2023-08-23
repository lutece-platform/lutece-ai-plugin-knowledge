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

package fr.paris.lutece.plugins.knowledge.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Embedding objects
 */
public final class EmbeddingHome
{
    // Static variable pointed at the DAO instance
    private static IEmbeddingDAO _dao = SpringContextService.getBean( "knowledge.embeddingDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "knowledge" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EmbeddingHome( )
    {
    }

    /**
     * Create an instance of the embedding class
     * 
     * @param embedding
     *            The instance of the Embedding which contains the informations to store
     * @return The instance of embedding which has been created with its primary key.
     */
    public static Embedding create( Embedding embedding )
    {
        _dao.insert( embedding, _plugin );

        return embedding;
    }

    /**
     * Update of the embedding which is specified in parameter
     * 
     * @param embedding
     *            The instance of the Embedding which contains the data to store
     * @return The instance of the embedding which has been updated
     */
    public static Embedding update( Embedding embedding )
    {
        _dao.store( embedding, _plugin );

        return embedding;
    }

    /**
     * Remove the embedding whose identifier is specified in parameter
     * 
     * @param nKey
     *            The embedding Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a embedding whose identifier is specified in parameter
     * 
     * @param nKey
     *            The embedding primary key
     * @return an instance of Embedding
     */
    public static Optional<Embedding> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the embedding objects and returns them as a list
     * 
     * @return the list which contains the data of all the embedding objects
     */
    public static List<Embedding> getEmbeddingsList( )
    {
        return _dao.selectEmbeddingsList( _plugin );
    }

    /**
     * Load the data of all the embedding objects and returns them as a list
     * 
     * @param strFileId
     *            the file id
     * @return the list which contains the data of all the embedding objects
     */
    public static List<Embedding> getEmbeddingsListByFileId( String strFileId )
    {
        return _dao.selectEmbeddingsListByFileId( strFileId, _plugin );
    }

    public static List<Embedding> getEmbeddingsListByProjectId( int nProjectId )
    {
        return _dao.selectEmbeddingsListByProjectId( nProjectId, _plugin );
    }

    /**
     * Load the id of all the embedding objects and returns them as a list
     * 
     * @return the list which contains the id of all the embedding objects
     */
    public static List<Integer> getIdEmbeddingsList( )
    {
        return _dao.selectIdEmbeddingsList( _plugin );
    }

    /**
     * Load the data of all the embedding objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the embedding objects
     */
    public static ReferenceList getEmbeddingsReferenceList( )
    {
        return _dao.selectEmbeddingsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Embedding> getEmbeddingsListByIds( List<Integer> listIds )
    {
        return _dao.selectEmbeddingsListByIds( _plugin, listIds );
    }

}
