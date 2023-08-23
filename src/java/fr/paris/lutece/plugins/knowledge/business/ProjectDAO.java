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
 * This class provides Data Access methods for Project objects
 */
public final class ProjectDAO implements IProjectDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_project, title, description, model_id, prompt_system_id FROM knowledge_project WHERE id_project = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_project ( title, description, model_id, prompt_system_id ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_project WHERE id_project = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_project SET title = ?, description = ?, model_id = ?, prompt_system_id = ? WHERE id_project = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_project, title, description, model_id, prompt_system_id FROM knowledge_project";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_project FROM knowledge_project";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_project, title, description, model_id, prompt_system_id FROM knowledge_project WHERE id_project IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Project project, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, project.getTitle( ) );
            daoUtil.setString( nIndex++, project.getDescription( ) );
            daoUtil.setInt( nIndex++, project.getModelId( ) );
            daoUtil.setInt( nIndex++, project.getPromptSystemId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                project.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Project> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Project project = null;

            if ( daoUtil.next( ) )
            {
                project = new Project( );
                int nIndex = 1;

                project.setId( daoUtil.getInt( nIndex++ ) );
                project.setTitle( daoUtil.getString( nIndex++ ) );
                project.setDescription( daoUtil.getString( nIndex++ ) );
                project.setModelId( daoUtil.getInt( nIndex++ ) );
                project.setPromptSystemId( daoUtil.getInt( nIndex ) );
            }

            return Optional.ofNullable( project );
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
    public void store( Project project, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, project.getTitle( ) );
            daoUtil.setString( nIndex++, project.getDescription( ) );
            daoUtil.setInt( nIndex++, project.getModelId( ) );
            daoUtil.setInt( nIndex++, project.getPromptSystemId( ) );
            daoUtil.setInt( nIndex, project.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Project> selectProjectsList( Plugin plugin )
    {
        List<Project> projectList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Project project = new Project( );
                int nIndex = 1;

                project.setId( daoUtil.getInt( nIndex++ ) );
                project.setTitle( daoUtil.getString( nIndex++ ) );
                project.setDescription( daoUtil.getString( nIndex++ ) );
                project.setModelId( daoUtil.getInt( nIndex++ ) );
                project.setPromptSystemId( daoUtil.getInt( nIndex ) );

                projectList.add( project );
            }

            return projectList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdProjectsList( Plugin plugin )
    {
        List<Integer> projectList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                projectList.add( daoUtil.getInt( 1 ) );
            }

            return projectList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectProjectsReferenceList( Plugin plugin )
    {
        ReferenceList projectList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                projectList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return projectList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Project> selectProjectsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<Project> projectList = new ArrayList<>( );

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
                    Project project = new Project( );
                    int nIndex = 1;

                    project.setId( daoUtil.getInt( nIndex++ ) );
                    project.setTitle( daoUtil.getString( nIndex++ ) );
                    project.setDescription( daoUtil.getString( nIndex++ ) );
                    project.setModelId( daoUtil.getInt( nIndex++ ) );
                    project.setPromptSystemId( daoUtil.getInt( nIndex ) );

                    projectList.add( project );
                }

                daoUtil.free( );

            }
        }
        return projectList;

    }
}
