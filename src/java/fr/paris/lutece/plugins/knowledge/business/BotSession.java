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

import java.io.Serializable;
import java.sql.Date;

/**
 * This is the business class for the object BotSession
 */
public class BotSession implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nUserId;

    private Date _dateCreationDate;

    private String _strContent;

    private int _nBotId;

    private String _strAccessCode;

    private String _sessionId;

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
     * Returns the UserId
     * 
     * @return The UserId
     */
    public int getUserId( )
    {
        return _nUserId;
    }

    /**
     * Sets the UserId
     * 
     * @param nUserId
     *            The UserId
     */
    public void setUserId( int nUserId )
    {
        _nUserId = nUserId;
    }

    /**
     * Returns the CreationDate
     * 
     * @return The CreationDate
     */
    public Date getCreationDate( )
    {
        return _dateCreationDate;
    }

    /**
     * Sets the CreationDate
     * 
     * @param dateCreationDate
     *            The CreationDate
     */
    public void setCreationDate( Date dateCreationDate )
    {
        _dateCreationDate = dateCreationDate;
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
     * Returns the BotId
     * 
     * @return The BotId
     */
    public int getBotId( )
    {
        return _nBotId;
    }

    /**
     * Sets the BotId
     * 
     * @param nBotId
     *            The BotId
     */
    public void setBotId( int nBotId )
    {
        _nBotId = nBotId;
    }

    /**
     * Returns the AccessCode
     * 
     * @return The AccessCode
     */
    public String getAccessCode( )
    {
        return _strAccessCode;
    }

    /**
     * Sets the AccessCode
     * 
     * @param strAccessCode
     *            The AccessCode
     */

    public void setAccessCode( String strAccessCode )
    {
        _strAccessCode = strAccessCode;
    }


    /**
     * Returns the SessionId
     * 
     * @return The SessionId
     */
    public String getSessionId( )
    {
        return _sessionId;
    }

    /**
     * Sets the SessionId
     * 
     * @param sessionId
     *            The SessionId
     */
    public void setSessionId( String sessionId )
    {
        _sessionId = sessionId;
    }

}
