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
 * This class provides Data Access methods for Dataset objects
 */
public final class DatasetDAO implements IDatasetDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_dataset, name, description, record_max_tokens, search_max_record, search_max_tokens, match_instruction, mismatch_instruction FROM knowledge_dataset WHERE id_dataset = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_dataset ( name, description, record_max_tokens, search_max_record, search_max_tokens, match_instruction, mismatch_instruction ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_dataset WHERE id_dataset = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_dataset SET name = ?, description = ?, record_max_tokens = ?, search_max_record = ?, search_max_tokens = ?, match_instruction = ?, mismatch_instruction = ? WHERE id_dataset = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_dataset, name, description, record_max_tokens, search_max_record, search_max_tokens, match_instruction, mismatch_instruction FROM knowledge_dataset";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_dataset FROM knowledge_dataset";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_dataset, name, description, record_max_tokens, search_max_record, search_max_tokens, match_instruction, mismatch_instruction FROM knowledge_dataset WHERE id_dataset IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Dataset dataset, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, dataset.getName( ) );
            daoUtil.setString( nIndex++, dataset.getDescription( ) );
            daoUtil.setInt( nIndex++, dataset.getRecordMaxTokens( ) );
            daoUtil.setInt( nIndex++, dataset.getSearchMaxRecord( ) );
            daoUtil.setInt( nIndex++, dataset.getSearchMaxTokens( ) );
            daoUtil.setString( nIndex++, dataset.getMatchInstruction( ) );
            daoUtil.setString( nIndex++, dataset.getMismatchInstruction( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                dataset.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Dataset> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Dataset dataset = null;

            if ( daoUtil.next( ) )
            {
                dataset = new Dataset( );
                int nIndex = 1;

                dataset.setId( daoUtil.getInt( nIndex++ ) );
                dataset.setName( daoUtil.getString( nIndex++ ) );
                dataset.setDescription( daoUtil.getString( nIndex++ ) );
                dataset.setRecordMaxTokens( daoUtil.getInt( nIndex++ ) );
                dataset.setSearchMaxRecord( daoUtil.getInt( nIndex++ ) );
                dataset.setSearchMaxTokens( daoUtil.getInt( nIndex++ ) );
                dataset.setMatchInstruction( daoUtil.getString( nIndex++ ) );
                dataset.setMismatchInstruction( daoUtil.getString( nIndex ) );
            }

            return Optional.ofNullable( dataset );
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
    public void store( Dataset dataset, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, dataset.getName( ) );
            daoUtil.setString( nIndex++, dataset.getDescription( ) );
            daoUtil.setInt( nIndex++, dataset.getRecordMaxTokens( ) );
            daoUtil.setInt( nIndex++, dataset.getSearchMaxRecord( ) );
            daoUtil.setInt( nIndex++, dataset.getSearchMaxTokens( ) );
            daoUtil.setString( nIndex++, dataset.getMatchInstruction( ) );
            daoUtil.setString( nIndex++, dataset.getMismatchInstruction( ) );
            daoUtil.setInt( nIndex, dataset.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Dataset> selectDatasetsList( Plugin plugin )
    {
        List<Dataset> datasetList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Dataset dataset = new Dataset( );
                int nIndex = 1;

                dataset.setId( daoUtil.getInt( nIndex++ ) );
                dataset.setName( daoUtil.getString( nIndex++ ) );
                dataset.setDescription( daoUtil.getString( nIndex++ ) );
                dataset.setRecordMaxTokens( daoUtil.getInt( nIndex++ ) );
                dataset.setSearchMaxRecord( daoUtil.getInt( nIndex++ ) );
                dataset.setSearchMaxTokens( daoUtil.getInt( nIndex++ ) );
                dataset.setMatchInstruction( daoUtil.getString( nIndex++ ) );
                dataset.setMismatchInstruction( daoUtil.getString( nIndex ) );

                datasetList.add( dataset );
            }

            return datasetList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDatasetsList( Plugin plugin )
    {
        List<Integer> datasetList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                datasetList.add( daoUtil.getInt( 1 ) );
            }

            return datasetList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectDatasetsReferenceList( Plugin plugin )
    {
        ReferenceList datasetList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                datasetList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return datasetList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Dataset> selectDatasetsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<Dataset> datasetList = new ArrayList<>( );

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
                    Dataset dataset = new Dataset( );
                    int nIndex = 1;

                    dataset.setId( daoUtil.getInt( nIndex++ ) );
                    dataset.setName( daoUtil.getString( nIndex++ ) );
                    dataset.setDescription( daoUtil.getString( nIndex++ ) );
                    dataset.setRecordMaxTokens( daoUtil.getInt( nIndex++ ) );
                    dataset.setSearchMaxRecord( daoUtil.getInt( nIndex++ ) );
                    dataset.setSearchMaxTokens( daoUtil.getInt( nIndex++ ) );
                    dataset.setMatchInstruction( daoUtil.getString( nIndex++ ) );
                    dataset.setMismatchInstruction( daoUtil.getString( nIndex ) );

                    datasetList.add( dataset );
                }

                daoUtil.free( );

            }
        }
        return datasetList;

    }
}
