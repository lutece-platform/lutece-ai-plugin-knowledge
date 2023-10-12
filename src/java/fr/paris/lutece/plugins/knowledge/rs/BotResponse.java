package fr.paris.lutece.plugins.knowledge.rs;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PipelineResult class represents the result of a pipeline execution.
 */
public class BotResponse
{
    private Map<Integer, Map<String, Object>> steps = new LinkedHashMap<>( );
    private EventOutput output;
    private HttpSession session;

    public BotResponse( EventOutput output, HttpSession session )
    {
        this.output = output;
        this.session = session;
    }

    /**
     * Initializes a new step in the pipeline with the given step ID and container name. The step is added to the steps map with a "pending" status and a null
     * message. A pipeline event is sent to notify listeners of the new step.
     * 
     * @param stepId
     *            the ID of the step to initialize
     * @param container
     *            the name of the container associated with the step
     */
    public void initStep( int stepId, String container )
    {
        Map<String, Object> step = new HashMap<>( );
        step.put( "status", "pending" );
        step.put( "message", null );
        step.put( "container", container );
        steps.put( stepId, step );
        sendPipelineEvent( );
    }

    /**
     * Updates the message of an existing step in the pipeline with the given step ID. If the step does not exist, this method does nothing. A pipeline event is
     * sent to notify listeners of the updated step.
     * 
     * @param stepId
     *            the ID of the step to update
     * @param message
     *            the new message to set for the step
     */
    public void updateStep( int stepId, String message )
    {
        if ( steps.containsKey( stepId ) )
        {
            steps.get( stepId ).put( "message", message );
            sendPipelineEvent( );
        }
    }

    /**
     * Marks an existing step in the pipeline with the given step ID as completed. If the step does not exist, this method does nothing. A pipeline event is
     * sent to notify listeners of the completed step.
     * 
     * @param stepId
     *            the ID of the step to complete
     */
    public void completeStep( int stepId )
    {
        if ( steps.containsKey( stepId ) )
        {
            steps.get( stepId ).put( "status", "completed" );
            sendPipelineEvent( );
        }
    }

    /**
     * Marks an existing step in the pipeline with the given step ID as failed, with the given error message. If the step does not exist, this method does
     * nothing. A pipeline event is sent to notify listeners of the failed step.
     * 
     * @param stepId
     *            the ID of the step to fail
     * @param errorMessage
     *            the error message to set for the step
     */
    public void failStep( int stepId, String errorMessage )
    {
        if ( steps.containsKey( stepId ) )
        {
            steps.get( stepId ).put( "status", "failed" );
            steps.get( stepId ).put( "message", errorMessage );
            sendPipelineEvent( );
        }
    }

    /**
     * Sends a pipeline event to all listeners, containing the current state of the steps map. The steps map is serialized to JSON using the Jackson
     * ObjectMapper. The event is sent using the output EventOutput object.
     */
    private void sendPipelineEvent( )
    {
        ObjectMapper objectMapper = new ObjectMapper( );
        String json = "";
        try
        {
            json = objectMapper.writeValueAsString( steps );
        }
        catch( IOException e )
        {
            e.printStackTrace( );
        }

        OutboundEvent event = new OutboundEvent.Builder( ).name( "pipeline" ).mediaType( MediaType.APPLICATION_JSON_TYPE ).data( String.class, json ).build( );
        try
        {
            output.write( event );
        }
        catch( IOException e )
        {
            e.printStackTrace( );
        }
    }

    /**
     * Returns the HTTP session associated with this PipelineResult object.
     * 
     * @return the HTTP session
     */
    public HttpSession getSession( )
    {
        return session;
    }

    /**
     * Sets the HTTP session associated with this PipelineResult object.
     * 
     * @param session
     *            the new HTTP session to set
     */
    public void setSession( HttpSession session )
    {
        this.session = session;
    }
}
