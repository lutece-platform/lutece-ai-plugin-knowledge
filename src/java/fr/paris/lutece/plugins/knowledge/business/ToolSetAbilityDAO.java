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
 * This class provides Data Access methods for ToolSetAbility objects
 */
public final class ToolSetAbilityDAO implements IToolSetAbilityDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_tool_set_ability, name, description, instruction, toolset_id FROM knowledge_toolset_ability WHERE id_tool_set_ability = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_toolset_ability ( name, description, instruction, toolset_id ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_toolset_ability WHERE id_tool_set_ability = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_toolset_ability SET name = ?, description = ?, instruction = ?, toolset_id = ? WHERE id_tool_set_ability = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_tool_set_ability, name, description, instruction, toolset_id FROM knowledge_toolset_ability";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_tool_set_ability FROM knowledge_toolset_ability";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_tool_set_ability, name, description, instruction, toolset_id FROM knowledge_toolset_ability WHERE id_tool_set_ability IN (  ";
    private static final String SQL_QUERY_SELECTALL_BY_TOOLSET_ID = "SELECT id_tool_set_ability, name, description, instruction, toolset_id FROM knowledge_toolset_ability WHERE toolset_id = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ToolSetAbility toolSetAbility, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, toolSetAbility.getName( ) );
            daoUtil.setString( nIndex++, toolSetAbility.getDescription( ) );
            daoUtil.setString( nIndex++, toolSetAbility.getInstruction( ) );
            daoUtil.setInt( nIndex++, toolSetAbility.getToolsetId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                toolSetAbility.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ToolSetAbility> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            ToolSetAbility toolSetAbility = null;

            if ( daoUtil.next( ) )
            {
                toolSetAbility = new ToolSetAbility( );
                int nIndex = 1;

                toolSetAbility.setId( daoUtil.getInt( nIndex++ ) );
                toolSetAbility.setName( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setDescription( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setInstruction( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setToolsetId( daoUtil.getInt( nIndex ) );
            }

            return Optional.ofNullable( toolSetAbility );
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
    public void store( ToolSetAbility toolSetAbility, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, toolSetAbility.getName( ) );
            daoUtil.setString( nIndex++, toolSetAbility.getDescription( ) );
            daoUtil.setString( nIndex++, toolSetAbility.getInstruction( ) );
            daoUtil.setInt( nIndex++, toolSetAbility.getToolsetId( ) );
            daoUtil.setInt( nIndex, toolSetAbility.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ToolSetAbility> selectToolSetAbilitysList( Plugin plugin )
    {
        List<ToolSetAbility> toolSetAbilityList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ToolSetAbility toolSetAbility = new ToolSetAbility( );
                int nIndex = 1;

                toolSetAbility.setId( daoUtil.getInt( nIndex++ ) );
                toolSetAbility.setName( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setDescription( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setInstruction( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setToolsetId( daoUtil.getInt( nIndex ) );

                toolSetAbilityList.add( toolSetAbility );
            }

            return toolSetAbilityList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdToolSetAbilitysList( Plugin plugin )
    {
        List<Integer> toolSetAbilityList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                toolSetAbilityList.add( daoUtil.getInt( 1 ) );
            }

            return toolSetAbilityList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectToolSetAbilitysReferenceList( Plugin plugin )
    {
        ReferenceList toolSetAbilityList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                toolSetAbilityList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return toolSetAbilityList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ToolSetAbility> selectToolSetAbilitysListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<ToolSetAbility> toolSetAbilityList = new ArrayList<>( );

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
                    ToolSetAbility toolSetAbility = new ToolSetAbility( );
                    int nIndex = 1;

                    toolSetAbility.setId( daoUtil.getInt( nIndex++ ) );
                    toolSetAbility.setName( daoUtil.getString( nIndex++ ) );
                    toolSetAbility.setDescription( daoUtil.getString( nIndex++ ) );
                    toolSetAbility.setInstruction( daoUtil.getString( nIndex++ ) );
                    toolSetAbility.setToolsetId( daoUtil.getInt( nIndex ) );

                    toolSetAbilityList.add( toolSetAbility );
                }

                daoUtil.free( );

            }
        }
        return toolSetAbilityList;

    }

    @Override
    public List<ToolSetAbility> selectToolSetAbilitysListByToolSetId( Plugin plugin, int nIdToolSet )
    {
        List<ToolSetAbility> toolSetAbilityList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_TOOLSET_ID, plugin ) )
        {
            daoUtil.setInt( 1, nIdToolSet );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ToolSetAbility toolSetAbility = new ToolSetAbility( );
                int nIndex = 1;

                toolSetAbility.setId( daoUtil.getInt( nIndex++ ) );
                toolSetAbility.setName( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setDescription( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setInstruction( daoUtil.getString( nIndex++ ) );
                toolSetAbility.setToolsetId( daoUtil.getInt( nIndex ) );

                toolSetAbilityList.add( toolSetAbility );
            }

            return toolSetAbilityList;
        }
    }
}
