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

package fr.paris.lutece.plugins.knowledge.rs;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.portal.service.security.SecurityService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import fr.paris.lutece.plugins.knowledge.service.ChatService;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * ProjectRest
 */
@Path( Constants.API_PATH + Constants.VERSION_PATH + Constants.BOT_PATH )
public class BotRest
{
    private static ConcurrentHashMap<String, EventOutput> sessions = new ConcurrentHashMap<>( );

    @POST
    @Path( "/chat" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response postChat( RequestData data, @Context HttpServletRequest request )
    {
        if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
        {
            return Response.status( Response.Status.UNAUTHORIZED ).entity( new ErrorJsonResponse( "Unauthorized" ) ).build( );
        }

        String sessionId = UUID.randomUUID( ).toString( );
        final EventOutput eventOutput = new EventOutput( );
        sessions.put( sessionId, eventOutput );
        CompletableFuture<Void> future = ChatService.run( request, data, eventOutput, sessionId );
        future.whenComplete( ( result, ex ) -> {
            try
            {

                OutboundEvent closeEvent = new OutboundEvent.Builder( ).name( "message" ).mediaType( MediaType.APPLICATION_JSON_TYPE )
                        .data( String.class, "CLOSE" ).build( );
                eventOutput.write( closeEvent );
                eventOutput.close( );

            }
            catch( IOException ioClose )
            {
                throw new RuntimeException( "Error when closing the event output.", ioClose );
            }
        } );

        return Response.ok( ).entity( "{\"sessionId\":\"" + sessionId + "\"}" ).build( );
    }

    @GET
    @Path( "/chat/sse" )
    @Produces( SseFeature.SERVER_SENT_EVENTS )
    public EventOutput getChatEvents( @QueryParam( "sessionId" ) String sessionId, @Context HttpServletRequest request )
    {
        if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
        {
            return null;
        }
        return sessions.get( sessionId );
    }

}
