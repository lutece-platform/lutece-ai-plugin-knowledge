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
import fr.paris.lutece.plugins.knowledge.business.BotSession;
import fr.paris.lutece.plugins.knowledge.business.BotSessionHome;

/**
 * This class provides the user interface to manage BotSession features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageBotSessions.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class BotSessionJspBean extends AbstractManageBotsJspBean<Integer, BotSession>
{
    // Templates
    private static final String TEMPLATE_MANAGE_BOTSESSIONS = "/admin/plugins/knowledge/manage_botsessions.html";
    private static final String TEMPLATE_CREATE_BOTSESSION = "/admin/plugins/knowledge/create_botsession.html";
    private static final String TEMPLATE_MODIFY_BOTSESSION = "/admin/plugins/knowledge/modify_botsession.html";

    // Parameters
    private static final String PARAMETER_ID_BOTSESSION = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_BOTSESSIONS = "knowledge.manage_botsessions.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_BOTSESSION = "knowledge.modify_botsession.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_BOTSESSION = "knowledge.create_botsession.pageTitle";

    // Markers
    private static final String MARK_BOTSESSION_LIST = "botsession_list";
    private static final String MARK_BOTSESSION = "botsession";

    private static final String JSP_MANAGE_BOTSESSIONS = "jsp/admin/plugins/knowledge/ManageBotSessions.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_BOTSESSION = "knowledge.message.confirmRemoveBotSession";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.botsession.attribute.";

    // Views
    private static final String VIEW_MANAGE_BOTSESSIONS = "manageBotSessions";
    private static final String VIEW_CREATE_BOTSESSION = "createBotSession";
    private static final String VIEW_MODIFY_BOTSESSION = "modifyBotSession";

    // Actions
    private static final String ACTION_CREATE_BOTSESSION = "createBotSession";
    private static final String ACTION_MODIFY_BOTSESSION = "modifyBotSession";
    private static final String ACTION_REMOVE_BOTSESSION = "removeBotSession";
    private static final String ACTION_CONFIRM_REMOVE_BOTSESSION = "confirmRemoveBotSession";

    // Infos
    private static final String INFO_BOTSESSION_CREATED = "knowledge.info.botsession.created";
    private static final String INFO_BOTSESSION_UPDATED = "knowledge.info.botsession.updated";
    private static final String INFO_BOTSESSION_REMOVED = "knowledge.info.botsession.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private BotSession _botsession;
    private List<Integer> _listIdBotSessions;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_BOTSESSIONS, defaultView = true )
    public String getManageBotSessions( HttpServletRequest request )
    {
        _botsession = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdBotSessions.isEmpty( ) )
        {
            _listIdBotSessions = BotSessionHome.getIdBotSessionsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_BOTSESSION_LIST, _listIdBotSessions, JSP_MANAGE_BOTSESSIONS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_BOTSESSIONS, TEMPLATE_MANAGE_BOTSESSIONS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<BotSession> getItemsFromIds( List<Integer> listIds )
    {
        List<BotSession> listBotSession = BotSessionHome.getBotSessionsListByIds( listIds );

        // keep original order
        return listBotSession.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdBotSessions list
     */
    public void resetListId( )
    {
        _listIdBotSessions = new ArrayList<>( );
    }

    /**
     * Returns the form to create a botsession
     *
     * @param request
     *            The Http request
     * @return the html code of the botsession form
     */
    @View( VIEW_CREATE_BOTSESSION )
    public String getCreateBotSession( HttpServletRequest request )
    {
        _botsession = ( _botsession != null ) ? _botsession : new BotSession( );

        Map<String, Object> model = getModel( );
        model.put( MARK_BOTSESSION, _botsession );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BOTSESSION ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_BOTSESSION, TEMPLATE_CREATE_BOTSESSION, model );
    }

    /**
     * Process the data capture form of a new botsession
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_BOTSESSION )
    public String doCreateBotSession( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _botsession, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_BOTSESSION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _botsession, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_BOTSESSION );
        }

        BotSessionHome.create( _botsession );
        addInfo( INFO_BOTSESSION_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTSESSIONS );
    }

    /**
     * Manages the removal form of a botsession whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_BOTSESSION )
    public String getConfirmRemoveBotSession( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOTSESSION ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_BOTSESSION ) );
        url.addParameter( PARAMETER_ID_BOTSESSION, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_BOTSESSION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a botsession
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage botsessions
     */
    @Action( ACTION_REMOVE_BOTSESSION )
    public String doRemoveBotSession( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOTSESSION ) );

        BotSessionHome.remove( nId );
        addInfo( INFO_BOTSESSION_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTSESSIONS );
    }

    /**
     * Returns the form to update info about a botsession
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_BOTSESSION )
    public String getModifyBotSession( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOTSESSION ) );

        if ( _botsession == null || ( _botsession.getId( ) != nId ) )
        {
            Optional<BotSession> optBotSession = BotSessionHome.findByPrimaryKey( nId );
            _botsession = optBotSession.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_BOTSESSION, _botsession );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_BOTSESSION ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_BOTSESSION, TEMPLATE_MODIFY_BOTSESSION, model );
    }

    /**
     * Process the change form of a botsession
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_BOTSESSION )
    public String doModifyBotSession( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _botsession, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_BOTSESSION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _botsession, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_BOTSESSION, PARAMETER_ID_BOTSESSION, _botsession.getId( ) );
        }

        BotSessionHome.update( _botsession );
        addInfo( INFO_BOTSESSION_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BOTSESSIONS );
    }
}
