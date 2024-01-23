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

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * This is the business class for the object Dataset
 */
public class Dataset implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{knowledge.validation.dataset.Name.notEmpty}" )
    @Size( max = 255, message = "#i18n{knowledge.validation.dataset.Name.size}" )
    private String _strName;

    private String _strDescription;

    private int _nRecordMaxTokens;

    private int _nSearchMaxRecord;

    private int _nSearchMaxTokens;

    @NotEmpty( message = "#i18n{knowledge.validation.dataset.MatchInstruction.notEmpty}" )
    private String _strMatchInstruction;

    @NotEmpty( message = "#i18n{knowledge.validation.dataset.MismatchInstruction.notEmpty}" )
    private String _strMismatchInstruction;

    private List<DatasetFile> _listDatasetFiles;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Description
     * 
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * 
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the RecordMaxTokens
     * 
     * @return The RecordMaxTokens
     */
    public int getRecordMaxTokens( )
    {
        return _nRecordMaxTokens;
    }

    /**
     * Sets the RecordMaxTokens
     * 
     * @param nRecordMaxTokens
     *            The RecordMaxTokens
     */
    public void setRecordMaxTokens( int nRecordMaxTokens )
    {
        _nRecordMaxTokens = nRecordMaxTokens;
    }

    /**
     * Returns the SearchMaxRecord
     * 
     * @return The SearchMaxRecord
     */
    public int getSearchMaxRecord( )
    {
        return _nSearchMaxRecord;
    }

    /**
     * Sets the SearchMaxRecord
     * 
     * @param nSearchMaxRecord
     *            The SearchMaxRecord
     */
    public void setSearchMaxRecord( int nSearchMaxRecord )
    {
        _nSearchMaxRecord = nSearchMaxRecord;
    }

    /**
     * Returns the SearchMaxTokens
     * 
     * @return The SearchMaxTokens
     */
    public int getSearchMaxTokens( )
    {
        return _nSearchMaxTokens;
    }

    /**
     * Sets the SearchMaxTokens
     * 
     * @param nSearchMaxTokens
     *            The SearchMaxTokens
     */
    public void setSearchMaxTokens( int nSearchMaxTokens )
    {
        _nSearchMaxTokens = nSearchMaxTokens;
    }

    /**
     * Returns the MatchInstruction
     * 
     * @return The MatchInstruction
     */
    public String getMatchInstruction( )
    {
        return _strMatchInstruction;
    }

    /**
     * Sets the MatchInstruction
     * 
     * @param strMatchInstruction
     *            The MatchInstruction
     */
    public void setMatchInstruction( String strMatchInstruction )
    {
        _strMatchInstruction = strMatchInstruction;
    }

    /**
     * Returns the MismatchInstruction
     * 
     * @return The MismatchInstruction
     */
    public String getMismatchInstruction( )
    {
        return _strMismatchInstruction;
    }

    /**
     * Sets the MismatchInstruction
     * 
     * @param strMismatchInstruction
     *            The MismatchInstruction
     */
    public void setMismatchInstruction( String strMismatchInstruction )
    {
        _strMismatchInstruction = strMismatchInstruction;
    }

    /**
     * Returns the DatasetFile
     * 
     * @return The DatasetFile
     */
    public List<DatasetFile> getDatasetFile( )
    {
        return _listDatasetFiles;
    }

    /**
     * Sets the DatasetFile
     * 
     * @param listDatasetFile
     *            The DatasetFile
     */
    public void setDatasetFiles( List<DatasetFile> listDatasetFile )
    {
        _listDatasetFiles = listDatasetFile;
    }

}
