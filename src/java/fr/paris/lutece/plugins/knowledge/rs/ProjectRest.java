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

import fr.paris.lutece.plugins.knowledge.business.ModelConfiguration;
import fr.paris.lutece.plugins.knowledge.service.ChatService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.StreamingOutput;

/**
 * RESTful service endpoint providing functionalities related to projects, especially to retrieve answers for a given question within the context of a specific
 * project.
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.PROJECT_PATH )
public class ProjectRest
{
    private static final int VERSION_1 = 1;

    /**
     * Retrieve an answer from the chat service based on the given question and project ID.
     *
     * @param request
     *            The HTTP servlet request.
     * @param question
     *            The question for which an answer is required.
     * @param projectId
     *            The project ID to which the question pertains.
     * @return A response containing the answer as an octet-stream or an error message if any parameters are missing.
     */
    @GET
    @Path( "/answer" )
    @Produces( "application/octet-stream" )
    public Response getAnswer( @Context HttpServletRequest request, @QueryParam( "question" ) String question, @QueryParam( "projectId" ) Integer projectId )
    {
        if ( question == null || projectId == null )
        {
            return Response.status( Response.Status.BAD_REQUEST ).entity( "Question or projectId missing" ).build( );
        }

        return getOpenAiAnswer( request, question, projectId );

    }

    /**
     * Provides an answer by querying the ChatService with the provided question and project ID.
     *
     * @param request
     *            The HTTP servlet request.
     * @param question
     *            The question to be asked.
     * @param projectId
     *            The ID of the project context.
     * @return A response containing the answer as a plain text or an error if any issue occurs.
     */
    private Response getOpenAiAnswer( HttpServletRequest request, String question, Integer projectId )
    {
        ModelConfiguration config = new ModelConfiguration( );

        for ( String param : ModelConfiguration.getParameters( ) )
        {
            String value = request.getParameter( param );
            if ( value != null )
            {
                config.setConfigValue( param, value );
            }
        }

        StreamingOutput streamingOutput = new StreamingOutput( )
        {
            /**
             * Writes the response containing the answer for the given question into the provided output stream.
             *
             * @param os
             *            The output stream where the answer will be written.
             * @throws IOException
             *             If there's an I/O error.
             * @throws WebApplicationException
             *             If any web application-related error occurs.
             */
            @Override
            public void write( OutputStream os ) throws IOException, WebApplicationException
            {
                try
                {
                    ChatService.getAnswer( question, request.getSession( ), projectId, os, config ).get( );
                }
                catch( InterruptedException | ExecutionException e )
                {
                    e.printStackTrace( );
                }
            }
        };

        return Response.ok( streamingOutput ).type( MediaType.TEXT_PLAIN ).build( );
    }
}
