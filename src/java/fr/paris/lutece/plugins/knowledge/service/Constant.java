package fr.paris.lutece.plugins.knowledge.service;

import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class holds the constants used in the Knowledge plugin.
 */
public class Constant
{
    public static final String API_KEY = AppPropertiesService.getProperty( "knowledge.openai.apiKey" );
    public static final String PROXY_HOST = AppPropertiesService.getProperty( "knowledge.proxy.host" );
    public static final String PROXY_PORT = AppPropertiesService.getProperty( "knowledge.proxy.port" );
    public static final String ELASTIC_URL = AppPropertiesService.getProperty( "knowledge.elastic.url" );
    public static final String ELASTIC_USERNAME = AppPropertiesService.getProperty( "knowledge.elastic.username" );
    public static final String ELASTIC_PASSWORD = AppPropertiesService.getProperty( "knowledge.elastic.password" );
    public static final String STEP_CHAT = "chat";
}

