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
 * This is the business class for the object FineTuning
 */
public class FineTuning implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nProjectId;

    @NotEmpty( message = "#i18n{knowledge.validation.finetuning.Role.notEmpty}" )
    private String _strRole;

    @NotEmpty( message = "#i18n{knowledge.validation.finetuning.Content.notEmpty}" )
    private String _strContent;

    private int _nOrder;

    private int _nConversationId;

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
     * Returns the ProjectId
     * 
     * @return The ProjectId
     */
    public int getProjectId( )
    {
        return _nProjectId;
    }

    /**
     * Sets the ProjectId
     * 
     * @param nProjectId
     *            The ProjectId
     */
    public void setProjectId( int nProjectId )
    {
        _nProjectId = nProjectId;
    }

    /**
     * Returns the Role
     * 
     * @return The Role
     */
    public String getRole( )
    {
        return _strRole;
    }

    /**
     * Sets the Role
     * 
     * @param strRole
     *            The Role
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**
     * Returns the Content
     * 
     * @return The Content
     */
    public String getContent( )
    {
        return _strContent;
    }

    /**
     * Sets the Content
     * 
     * @param strContent
     *            The Content
     */
    public void setContent( String strContent )
    {
        _strContent = strContent;
    }

    /**
     * Returns the Order
     * 
     * @return The Order
     */
    public int getOrder( )
    {
        return _nOrder;
    }

    /**
     * Sets the Order
     * 
     * @param nOrder
     *            The Order
     */
    public void setOrder( int nOrder )
    {
        _nOrder = nOrder;
    }

    /**
     * Returns the ConversationId
     * 
     * @return The ConversationId
     */
    public int getConversationId( )
    {
        return _nConversationId;
    }

    /**
     * Sets the ConversationId
     * 
     * @param nConversationId
     *            The ConversationId
     */
    public void setConversationId( int nConversationId )
    {
        _nConversationId = nConversationId;
    }

}
