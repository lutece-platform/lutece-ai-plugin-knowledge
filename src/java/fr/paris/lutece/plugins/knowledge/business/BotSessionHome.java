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
 * This class provides instances management methods (create, find, ...) for BotSession objects
 */
public final class BotSessionHome
{
    // Static variable pointed at the DAO instance
    private static IBotSessionDAO _dao = SpringContextService.getBean( "knowledge.botSessionDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "knowledge" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private BotSessionHome( )
    {
    }

    /**
     * Create an instance of the botSession class
     * 
     * @param botSession
     *            The instance of the BotSession which contains the informations to store
     * @return The instance of botSession which has been created with its primary key.
     */
    public static BotSession create( BotSession botSession )
    {
        _dao.insert( botSession, _plugin );

        return botSession;
    }

    /**
     * Update of the botSession which is specified in parameter
     * 
     * @param botSession
     *            The instance of the BotSession which contains the data to store
     * @return The instance of the botSession which has been updated
     */
    public static BotSession update( BotSession botSession )
    {
        _dao.store( botSession, _plugin );

        return botSession;
    }

    /**
     * Remove the botSession whose identifier is specified in parameter
     * 
     * @param nKey
     *            The botSession Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a botSession whose identifier is specified in parameter
     * 
     * @param nKey
     *            The botSession primary key
     * @return an instance of BotSession
     */
    public static Optional<BotSession> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the botSession objects and returns them as a list
     * 
     * @return the list which contains the data of all the botSession objects
     */
    public static List<BotSession> getBotSessionsList( )
    {
        return _dao.selectBotSessionsList( _plugin );
    }

    /**
     * Load the id of all the botSession objects and returns them as a list
     * 
     * @return the list which contains the id of all the botSession objects
     */
    public static List<Integer> getIdBotSessionsList( )
    {
        return _dao.selectIdBotSessionsList( _plugin );
    }

    /**
     * Load the data of all the botSession objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the botSession objects
     */
    public static ReferenceList getBotSessionsReferenceList( )
    {
        return _dao.selectBotSessionsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<BotSession> getBotSessionsListByIds( List<Integer> listIds )
    {
        return _dao.selectBotSessionsListByIds( _plugin, listIds );
    }

    /**
     * load the data of botSession by sessionId
     * 
     * @param strAccessCode
     *            The access code
     * @param strSessionId
     *            The session id
     * @return The instance of the botSession
     */
    public static Optional<BotSession> findByAccessCode( String strAccessCode, String strSessionId )
    {
        return _dao.loadBySessionIdAndAccessCode( strAccessCode, strSessionId, _plugin );
    }

    /**
     * load the data of botSession by sessionId
     * 
     * @param strSessionId
     *            The session id
     * @return The instance of the botSession
     */
    public static Optional<BotSession> findBySessionId( String strSessionId )
    {
        return _dao.loadBySessionId( strSessionId, _plugin );
    }

    /**
     * load the data of botSession by sessionId
     * 
     * @param strSessionId
     *            The session id
     * @return The instance of the botSession
     */
    public static List<BotSession> getBotSessionsByAccessCode( String strAccessCode )
    {
        return _dao.selectBotSessionsListByAccessCode( strAccessCode, _plugin );
    }

}
