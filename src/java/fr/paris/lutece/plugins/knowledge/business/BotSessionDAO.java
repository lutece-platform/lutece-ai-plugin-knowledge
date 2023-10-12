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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for BotSession objects
 */
public final class BotSessionDAO implements IBotSessionDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session WHERE id_bot_session = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_bot_session ( user_id, creation_date, content, bot_id, user_access_code, session_id ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_bot_session WHERE id_bot_session = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_bot_session SET user_id = ?, creation_date = ?, content = ?, bot_id = ?, user_access_code = ?, session_id = ? WHERE id_bot_session = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_bot_session FROM knowledge_bot_session";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session WHERE id_bot_session IN (  ";
    private static final String SQL_QUERY_SELECT_BY_SESSION_ID_AND_USER_ACCESS_CODE = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session WHERE session_id = ? AND user_access_code = ?";
    private static final String SQL_QUERY_SELECTALL_BY_SESSION_ID = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session WHERE session_id = ?";
    private static final String SQL_QUERY_SELECTALL_BY_ACCESS_CODE = "SELECT id_bot_session, user_id, creation_date, content, bot_id, user_access_code, session_id FROM knowledge_bot_session WHERE user_access_code = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( BotSession botSession, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, botSession.getUserId( ) );
            daoUtil.setDate( nIndex++, botSession.getCreationDate( ) );
            daoUtil.setString( nIndex++, botSession.getContent( ) );
            daoUtil.setInt( nIndex++, botSession.getBotId( ) );
            daoUtil.setString( nIndex++, botSession.getAccessCode( ) );
            daoUtil.setString( nIndex, botSession.getSessionId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                botSession.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<BotSession> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            BotSession botSession = null;

            if ( daoUtil.next( ) )
            {
                botSession = new BotSession( );
                int nIndex = 1;

                botSession.setId( daoUtil.getInt( nIndex++ ) );
                botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                botSession.setContent( daoUtil.getString( nIndex++ ) );
                botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                botSession.setSessionId( daoUtil.getString( nIndex ) );

            }

            return Optional.ofNullable( botSession );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( BotSession botSession, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, botSession.getUserId( ) );
            daoUtil.setDate( nIndex++, botSession.getCreationDate( ) );
            daoUtil.setString( nIndex++, botSession.getContent( ) );
            daoUtil.setInt( nIndex++, botSession.getBotId( ) );
            daoUtil.setString( nIndex++, botSession.getAccessCode( ) );
            daoUtil.setString( nIndex++, botSession.getSessionId( ) );
            daoUtil.setInt( nIndex, botSession.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<BotSession> selectBotSessionsList( Plugin plugin )
    {
        List<BotSession> botSessionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                BotSession botSession = new BotSession( );
                int nIndex = 1;

                botSession.setId( daoUtil.getInt( nIndex++ ) );
                botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                botSession.setContent( daoUtil.getString( nIndex++ ) );
                botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                botSession.setSessionId( daoUtil.getString( nIndex ) );

                botSessionList.add( botSession );
            }

            return botSessionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdBotSessionsList( Plugin plugin )
    {
        List<Integer> botSessionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                botSessionList.add( daoUtil.getInt( 1 ) );
            }

            return botSessionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectBotSessionsReferenceList( Plugin plugin )
    {
        ReferenceList botSessionList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                botSessionList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return botSessionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<BotSession> selectBotSessionsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<BotSession> botSessionList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    BotSession botSession = new BotSession( );
                    int nIndex = 1;

                    botSession.setId( daoUtil.getInt( nIndex++ ) );
                    botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                    botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                    botSession.setContent( daoUtil.getString( nIndex++ ) );
                    botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                    botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                    botSession.setSessionId( daoUtil.getString( nIndex ) );

                    botSessionList.add( botSession );
                }

                daoUtil.free( );

            }
        }
        return botSessionList;

    }

    @Override
    public Optional<BotSession> loadBySessionIdAndAccessCode(String strAccessCode, String strSessionId, Plugin plugin) {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_SESSION_ID_AND_USER_ACCESS_CODE, plugin ) )
        {
            daoUtil.setString( 1, strSessionId );
            daoUtil.setString( 2, strAccessCode );
            daoUtil.executeQuery( );
            BotSession botSession = null;

            if ( daoUtil.next( ) )
            {
                botSession = new BotSession( );
                int nIndex = 1;

                botSession.setId( daoUtil.getInt( nIndex++ ) );
                botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                botSession.setContent( daoUtil.getString( nIndex++ ) );
                botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                botSession.setSessionId( daoUtil.getString( nIndex ) );

            }

            return Optional.ofNullable( botSession );
        }
    }


    @Override
    public Optional<BotSession> loadBySessionId(String strSessionId, Plugin plugin) {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_SESSION_ID, plugin ) )
        {
            daoUtil.setString( 1, strSessionId );
            daoUtil.executeQuery( );
            BotSession botSession = null;

            if ( daoUtil.next( ) )
            {
                botSession = new BotSession( );
                int nIndex = 1;

                botSession.setId( daoUtil.getInt( nIndex++ ) );
                botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                botSession.setContent( daoUtil.getString( nIndex++ ) );
                botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                botSession.setSessionId( daoUtil.getString( nIndex ) );

            }

            return Optional.ofNullable( botSession );
        }
    }

    @Override
    public List<BotSession> selectBotSessionsListByAccessCode(String strAccessCode, Plugin plugin) {
        List<BotSession> botSessionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_ACCESS_CODE, plugin ) )
        {
            daoUtil.setString( 1, strAccessCode );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                BotSession botSession = new BotSession( );
                int nIndex = 1;

                botSession.setId( daoUtil.getInt( nIndex++ ) );
                botSession.setUserId( daoUtil.getInt( nIndex++ ) );
                botSession.setCreationDate( daoUtil.getDate( nIndex++ ) );
                botSession.setContent( daoUtil.getString( nIndex++ ) );
                botSession.setBotId( daoUtil.getInt( nIndex++ ) );
                botSession.setAccessCode( daoUtil.getString( nIndex++ ) );
                botSession.setSessionId( daoUtil.getString( nIndex ) );

                botSessionList.add( botSession );
            }

            return botSessionList;
        }
    }

}
