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

package fr.paris.lutece.plugins.knowledge.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.knowledge.business.Bot;
import fr.paris.lutece.plugins.knowledge.business.BotHome;
import fr.paris.lutece.plugins.knowledge.business.Dataset;
import fr.paris.lutece.plugins.knowledge.business.DatasetHome;
import fr.paris.lutece.plugins.knowledge.business.ToolSet;
import fr.paris.lutece.plugins.knowledge.business.ToolSetHome;

/**
 * This class provides the user interface to manage Bot features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageBots.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class BotJspBean extends AbstractManageBotsJspBean<Integer, Bot>
{
    // Templates
    private static final String TEMPLATE_MANAGE_BOTS = "/admin/plugins/knowledge/manage_bots.html";
    private static final String TEMPLATE_CREATE_BOT = "/admin/plugins/knowledge/create_bot.html";
    private static final String TEMPLATE_MODIFY_BOT = "/admin/plugins/knowledge/modify_bot.html";

    // Parameters
    private static final String PARAMETER_ID_BOT = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_BOTS = "knowledge.manage_bots.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_BOT = "knowledge.modify_bot.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_BOT = "knowledge.create_bot.pageTitle";

    // Markers
    private static final String MARK_BOT_LIST = "bot_list";
    private static final String MARK_DATASET_LIST = "dataset_list";
    private static final String MARK_BOT = "bot";
    private static final String MARK_DATASET = "dataset";
    private static final String MARK_TOOLSET = "toolset";

    private static final String JSP_MANAGE_BOTS = "jsp/admin/plugins/knowledge/ManageBots.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_BOT = "knowledge.message.confirmRemoveBot";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.bot.attribute.";

    // Views
    private static final String VIEW_MANAGE_BOTS = "manageBots";
    private static final String VIEW_CREATE_BOT = "createBot";
    private static final String VIEW_MODIFY_BOT = "modifyBot";

    // Actions
    private static final String ACTION_CREATE_BOT = "createBot";
    private static final String ACTION_MODIFY_BOT = "modifyBot";
    private static final String ACTION_REMOVE_BOT = "removeBot";
    private static final String ACTION_CONFIRM_REMOVE_BOT = "confirmRemoveBot";

    // Infos
    private static final String INFO_BOT_CREATED = "knowledge.info.bot.created";
    private static final String INFO_BOT_UPDATED = "knowledge.info.bot.updated";
    private static final String INFO_BOT_REMOVED = "knowledge.info.bot.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Bot _bot;
    private List<Integer> _listIdBots;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_BOTS, defaultView = true )
    public String getManageBots( HttpServletRequest request )
    {
        _bot = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdBots.isEmpty( ) )
        {
            _listIdBots = BotHome.getIdBotsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_BOT_LIST, _listIdBots, JSP_MANAGE_BOTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_BOTS, TEMPLATE_MANAGE_BOTS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Bot> getItemsFromIds( List<Integer> listIds )
    {
        List<Bot> listBot = BotHome.getBotsListByIds( listIds );

        // keep original order
        return listBot.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdBots list
     */
    public void resetListId( )
    {
        _listIdBots = new ArrayList<>( );
    }

    /**
     * Returns the form to create a bot
     *
     * @param request
     *            The Http request
     * @return the html code of the bot form
     */
    @View( VIEW_CREATE_BOT )
    public String getCreateBot( HttpServletRequest request )
    {
        _bot = ( _bot != null ) ? _bot : new Bot( );

        Map<String, Object> model = getModel( );
        model.put( MARK_BOT, _bot );
        model.put( MARK_DATASET_LIST, DatasetHome.getDatasetsList( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BOT ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_BOT, TEMPLATE_CREATE_BOT, model );
    }

    /**
     * Process the data capture form of a new bot
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_BOT )
    public String doCreateBot( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _bot, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_BOT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _bot, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_BOT );
        }

        // Check if dataset already exist for this bot and create it if not
        if ( _bot.getDatasetId( ) == 0 )
        {
            Dataset dataset = new Dataset( );
            dataset.setName( _bot.getName( ) + " dataset" );
            dataset.setDescription( _bot.getDescription( ) + " dataset" );
            dataset.setMatchInstruction( "Répond à la question avec les données suivantes uniquement : '''{{search_result}}''''" );
            dataset.setRecordMaxTokens( 200 );
            dataset.setSearchMaxRecord( 10 );
            dataset.setSearchMaxTokens( 2000 );
            dataset.setMismatchInstruction( "Je n'ai pas compris votre question, pouvez-vous reformuler ?" );
            dataset = DatasetHome.create( dataset );
            _bot.setDatasetId( dataset.getId( ) );
        }

        // Check if toolset already exist for this bot and create it if not
        if ( _bot.getToolsetId( ) == 0 )
        {
            ToolSet toolSet = new ToolSet( );
            toolSet.setName( _bot.getName( ) + " toolset" );
            toolSet = ToolSetHome.create( toolSet );
            _bot.setToolsetId( toolSet.getId( ) );
        }

        BotHome.create( _bot );
        addInfo( INFO_BOT_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }

    /**
     * Manages the removal form of a bot whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_BOT )
    public String getConfirmRemoveBot( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_BOT ) );
        url.addParameter( PARAMETER_ID_BOT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_BOT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a bot
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage bots
     */
    @Action( ACTION_REMOVE_BOT )
    public String doRemoveBot( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );

        BotHome.remove( nId );
        addInfo( INFO_BOT_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }

    /**
     * Returns the form to update info about a bot
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_BOT )
    public String getModifyBot( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );

        if ( _bot == null || ( _bot.getId( ) != nId ) )
        {
            Optional<Bot> optBot = BotHome.findByPrimaryKey( nId );
            _bot = optBot.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Optional<Dataset> optDataset = DatasetHome.findByPrimaryKey( _bot.getDatasetId( ) );
        Optional<ToolSet> optToolSet = ToolSetHome.findByPrimaryKey( _bot.getToolsetId( ) );


        Map<String, Object> model = getModel( );
        model.put( MARK_BOT, _bot );
        model.put( MARK_DATASET_LIST, DatasetHome.getDatasetsList( ) );
        model.put( MARK_DATASET, optDataset.isPresent() ? optDataset.get() : null );
        model.put( MARK_TOOLSET, optToolSet.isPresent() ? optToolSet.get() : null );

        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_BOT ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_BOT, TEMPLATE_MODIFY_BOT, model );
    }

    /**
     * Process the change form of a bot
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_BOT )
    public String doModifyBot( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _bot, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_BOT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _bot, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_BOT, PARAMETER_ID_BOT, _bot.getId( ) );
        }

        BotHome.update( _bot );
        addInfo( INFO_BOT_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }
}
