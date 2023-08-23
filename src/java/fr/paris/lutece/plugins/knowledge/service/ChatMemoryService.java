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
package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class ChatMemoryService
{
    private static final String ATTRIBUTE_CHAT_MEMORY_MAP = "CHAT_MEMORY_MAP";

    /**
     * Retrieves a chat memory associated with the given project ID from the provided session. If no chat memory exists for the given project ID, a new one is
     * created.
     * 
     * @param session
     *            The current HTTP session.
     * @param projectId
     *            The ID of the project for which the chat memory is to be fetched.
     * @return The associated MessageWindowChatMemory object or a new one if none exists.
     */
    @SuppressWarnings( "unchecked" )
    public static MessageWindowChatMemory getChatMemory( HttpSession session, Integer projectId )
    {
        Map<Integer, MessageWindowChatMemory> chatMemoryMap = (HashMap<Integer, MessageWindowChatMemory>) session.getAttribute( ATTRIBUTE_CHAT_MEMORY_MAP );

        if ( chatMemoryMap == null )
        {
            chatMemoryMap = new HashMap<>( );
            session.setAttribute( ATTRIBUTE_CHAT_MEMORY_MAP, chatMemoryMap );
        }

        MessageWindowChatMemory messageWindowChatMemory = chatMemoryMap.get( projectId );

        if ( messageWindowChatMemory == null )
        {
            messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages( 5 );
            chatMemoryMap.put( projectId, messageWindowChatMemory );
        }

        return messageWindowChatMemory;
    }

    /**
     * Resets the chat memory for the specified project ID in the provided session.
     * 
     * @param session
     *            The current HTTP session.
     * @param projectId
     *            The ID of the project for which the chat memory is to be reset.
     */
    public static void resetChatMemory( HttpSession session, Integer projectId )
    {
        Map<Integer, MessageWindowChatMemory> chatMemoryMap = getChatMemoryMap( session );
        chatMemoryMap.put( projectId, MessageWindowChatMemory.withMaxMessages( 5 ) );
    }

    /**
     * Retrieves the chat memory map from the provided session. If the map doesn't exist in the session, a new one is created.
     * 
     * @param session
     *            The current HTTP session.
     * @return The existing chat memory map or a new one if none exists.
     */
    private static Map<Integer, MessageWindowChatMemory> getChatMemoryMap( HttpSession session )
    {
        Object object = session.getAttribute( ATTRIBUTE_CHAT_MEMORY_MAP );
        if ( object == null || !( object instanceof Map ) )
        {
            Map<Integer, MessageWindowChatMemory> newMap = new HashMap<>( );
            session.setAttribute( ATTRIBUTE_CHAT_MEMORY_MAP, newMap );
            return newMap;
        }
        return (Map<Integer, MessageWindowChatMemory>) object;
    }
}
