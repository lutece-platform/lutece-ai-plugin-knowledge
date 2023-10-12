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

/**
 * This is the business class for the object Bot
 */
public class Bot implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{knowledge.validation.bot.Name.notEmpty}" )
    @Size( max = 255, message = "#i18n{knowledge.validation.bot.Name.size}" )
    private String _strName;

    private String _strDescription;

    private String _strStory;

    private int _nDatasetId;

    private int _nToolsetId;

    @NotEmpty( message = "#i18n{knowledge.validation.bot.ModelId.notEmpty}" )
    @Size( max = 255, message = "#i18n{knowledge.validation.bot.ModelId.size}" )
    private String _strModelId;

    @NotEmpty( message = "#i18n{knowledge.validation.bot.TypeId.notEmpty}" )
    private String _strTypeId;

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
     * Returns the Story
     * 
     * @return The Story
     */
    public String getStory( )
    {
        return _strStory;
    }

    /**
     * Sets the Story
     * 
     * @param strStory
     *            The Story
     */
    public void setStory( String strStory )
    {
        _strStory = strStory;
    }

    /**
     * Returns the DatasetId
     * 
     * @return The DatasetId
     */
    public int getDatasetId( )
    {
        return _nDatasetId;
    }

    /**
     * Sets the DatasetId
     * 
     * @param nDatasetId
     *            The DatasetId
     */
    public void setDatasetId( int nDatasetId )
    {
        _nDatasetId = nDatasetId;
    }

    /**
     * Returns the ToolsetId
     * 
     * @return The ToolsetId
     */
    public int getToolsetId( )
    {
        return _nToolsetId;
    }

    /**
     * Sets the ToolsetId
     * 
     * @param nToolsetId
     *            The ToolsetId
     */
    public void setToolsetId( int nToolsetId )
    {
        _nToolsetId = nToolsetId;
    }

    /**
     * Returns the ModelId
     * 
     * @return The ModelId
     */
    public String getModelId( )
    {
        return _strModelId;
    }

    /**
     * Sets the ModelId
     * 
     * @param strModelId
     *            The ModelId
     */
    public void setModelId( String strModelId )
    {
        _strModelId = strModelId;
    }

    /**
     * Returns the TypeId
     * 
     * @return The TypeId
     */
    public String getTypeId( )
    {
        return _strTypeId;
    }

    /**
     * Sets the TypeId
     * 
     * @param strTypeId
     *            The TypeId
     */
    public void setTypeId( String strTypeId )
    {
        _strTypeId = strTypeId;
    }

}
