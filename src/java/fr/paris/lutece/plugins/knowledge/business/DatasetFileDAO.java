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
 * This class provides Data Access methods for DatasetFile objects
 */
public final class DatasetFileDAO implements IDatasetFileDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_dataset_files, name, description, dataset_id, file_key FROM knowledge_dataset_file WHERE id_dataset_files = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_dataset_file ( name, description, dataset_id, file_key ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_dataset_file WHERE id_dataset_files = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_dataset_file SET name = ?, description = ?, dataset_id = ?, file_key = ? WHERE id_dataset_files = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_dataset_files, name, description, dataset_id, file_key FROM knowledge_dataset_file";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_dataset_files FROM knowledge_dataset_file";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_dataset_files, name, description, dataset_id, file_key FROM knowledge_dataset_file WHERE id_dataset_files IN (  ";
    private static final String SQL_QUERY_SELECTALL_BY_DATASET_ID = "SELECT id_dataset_files, name, description, dataset_id, file_key FROM knowledge_dataset_file WHERE dataset_id = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( DatasetFile datasetFile, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, datasetFile.getName( ) );
            daoUtil.setString( nIndex++, datasetFile.getDescription( ) );
            daoUtil.setInt( nIndex++, datasetFile.getDatasetId( ) );
            daoUtil.setString( nIndex++, datasetFile.getFileKey( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                datasetFile.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DatasetFile> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            DatasetFile datasetFile = null;

            if ( daoUtil.next( ) )
            {
                datasetFile = new DatasetFile( );
                int nIndex = 1;

                datasetFile.setId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setName( daoUtil.getString( nIndex++ ) );
                datasetFile.setDescription( daoUtil.getString( nIndex++ ) );
                datasetFile.setDatasetId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setFileKey( daoUtil.getString( nIndex ) );
            }

            return Optional.ofNullable( datasetFile );
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
    public void store( DatasetFile datasetFile, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, datasetFile.getName( ) );
            daoUtil.setString( nIndex++, datasetFile.getDescription( ) );
            daoUtil.setInt( nIndex++, datasetFile.getDatasetId( ) );
            daoUtil.setString( nIndex++, datasetFile.getFileKey( ) );
            daoUtil.setInt( nIndex, datasetFile.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DatasetFile> selectDatasetFilesList( Plugin plugin )
    {
        List<DatasetFile> datasetFileList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DatasetFile datasetFile = new DatasetFile( );
                int nIndex = 1;

                datasetFile.setId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setName( daoUtil.getString( nIndex++ ) );
                datasetFile.setDescription( daoUtil.getString( nIndex++ ) );
                datasetFile.setDatasetId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setFileKey( daoUtil.getString( nIndex ) );

                datasetFileList.add( datasetFile );
            }

            return datasetFileList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDatasetFilesList( Plugin plugin )
    {
        List<Integer> datasetFileList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                datasetFileList.add( daoUtil.getInt( 1 ) );
            }

            return datasetFileList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectDatasetFilesReferenceList( Plugin plugin )
    {
        ReferenceList datasetFileList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                datasetFileList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return datasetFileList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DatasetFile> selectDatasetFilesListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<DatasetFile> datasetFileList = new ArrayList<>( );

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
                    DatasetFile datasetFile = new DatasetFile( );
                    int nIndex = 1;

                    datasetFile.setId( daoUtil.getInt( nIndex++ ) );
                    datasetFile.setName( daoUtil.getString( nIndex++ ) );
                    datasetFile.setDescription( daoUtil.getString( nIndex++ ) );
                    datasetFile.setDatasetId( daoUtil.getInt( nIndex++ ) );
                    datasetFile.setFileKey( daoUtil.getString( nIndex ) );

                    datasetFileList.add( datasetFile );
                }

                daoUtil.free( );

            }
        }
        return datasetFileList;

    }

    @Override
    public List<DatasetFile> selectDatasetFilesListByDatasetId( int nDatasetId, Plugin plugin )
    {
        List<DatasetFile> datasetFileList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_DATASET_ID, plugin ) )
        {
            daoUtil.setInt( 1, nDatasetId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DatasetFile datasetFile = new DatasetFile( );
                int nIndex = 1;

                datasetFile.setId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setName( daoUtil.getString( nIndex++ ) );
                datasetFile.setDescription( daoUtil.getString( nIndex++ ) );
                datasetFile.setDatasetId( daoUtil.getInt( nIndex++ ) );
                datasetFile.setFileKey( daoUtil.getString( nIndex ) );

                datasetFileList.add( datasetFile );
            }

            return datasetFileList;
        }
    }
}
