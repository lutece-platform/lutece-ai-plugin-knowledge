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
 * This class provides Data Access methods for Bot objects
 */
public final class BotDAO implements IBotDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_bots, name, description, story, dataset_id, toolset_id, model_id, type_id FROM knowledge_bot WHERE id_bots = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_bot ( name, description, story, dataset_id, toolset_id, model_id, type_id ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_bot WHERE id_bots = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_bot SET name = ?, description = ?, story = ?, dataset_id = ?, toolset_id = ?, model_id = ?, type_id = ? WHERE id_bots = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_bots, name, description, story, dataset_id, toolset_id, model_id, type_id FROM knowledge_bot";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_bots FROM knowledge_bot";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_bots, name, description, story, dataset_id, toolset_id, model_id, type_id FROM knowledge_bot WHERE id_bots IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Bot bot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, bot.getName( ) );
            daoUtil.setString( nIndex++, bot.getDescription( ) );
            daoUtil.setString( nIndex++, bot.getStory( ) );
            daoUtil.setInt( nIndex++, bot.getDatasetId( ) );
            daoUtil.setInt( nIndex++, bot.getToolsetId( ) );
            daoUtil.setString( nIndex++, bot.getModelId( ) );
            daoUtil.setString( nIndex++, bot.getTypeId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                bot.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Bot> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Bot bot = null;

            if ( daoUtil.next( ) )
            {
                bot = new Bot( );
                int nIndex = 1;

                bot.setId( daoUtil.getInt( nIndex++ ) );
                bot.setName( daoUtil.getString( nIndex++ ) );
                bot.setDescription( daoUtil.getString( nIndex++ ) );
                bot.setStory( daoUtil.getString( nIndex++ ) );
                bot.setDatasetId( daoUtil.getInt( nIndex++ ) );
                bot.setToolsetId( daoUtil.getInt( nIndex++ ) );
                bot.setModelId( daoUtil.getString( nIndex++ ) );
                bot.setTypeId( daoUtil.getString( nIndex ) );
            }

            return Optional.ofNullable( bot );
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
    public void store( Bot bot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, bot.getName( ) );
            daoUtil.setString( nIndex++, bot.getDescription( ) );
            daoUtil.setString( nIndex++, bot.getStory( ) );
            daoUtil.setInt( nIndex++, bot.getDatasetId( ) );
            daoUtil.setInt( nIndex++, bot.getToolsetId( ) );
            daoUtil.setString( nIndex++, bot.getModelId( ) );
            daoUtil.setString( nIndex++, bot.getTypeId( ) );
            daoUtil.setInt( nIndex, bot.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Bot> selectBotsList( Plugin plugin )
    {
        List<Bot> botList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Bot bot = new Bot( );
                int nIndex = 1;

                bot.setId( daoUtil.getInt( nIndex++ ) );
                bot.setName( daoUtil.getString( nIndex++ ) );
                bot.setDescription( daoUtil.getString( nIndex++ ) );
                bot.setStory( daoUtil.getString( nIndex++ ) );
                bot.setDatasetId( daoUtil.getInt( nIndex++ ) );
                bot.setToolsetId( daoUtil.getInt( nIndex++ ) );
                bot.setModelId( daoUtil.getString( nIndex++ ) );
                bot.setTypeId( daoUtil.getString( nIndex ) );

                botList.add( bot );
            }

            return botList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdBotsList( Plugin plugin )
    {
        List<Integer> botList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                botList.add( daoUtil.getInt( 1 ) );
            }

            return botList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectBotsReferenceList( Plugin plugin )
    {
        ReferenceList botList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                botList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return botList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Bot> selectBotsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<Bot> botList = new ArrayList<>( );

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
                    Bot bot = new Bot( );
                    int nIndex = 1;

                    bot.setId( daoUtil.getInt( nIndex++ ) );
                    bot.setName( daoUtil.getString( nIndex++ ) );
                    bot.setDescription( daoUtil.getString( nIndex++ ) );
                    bot.setStory( daoUtil.getString( nIndex++ ) );
                    bot.setDatasetId( daoUtil.getInt( nIndex++ ) );
                    bot.setToolsetId( daoUtil.getInt( nIndex++ ) );
                    bot.setModelId( daoUtil.getString( nIndex++ ) );
                    bot.setTypeId( daoUtil.getString( nIndex ) );

                    botList.add( bot );
                }

                daoUtil.free( );

            }
        }
        return botList;

    }
}
