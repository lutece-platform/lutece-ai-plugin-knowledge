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
import fr.paris.lutece.plugins.knowledge.business.ToolSet;
import fr.paris.lutece.plugins.knowledge.business.ToolSetHome;

/**
 * This class provides the user interface to manage ToolSet features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageToolSets.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class ToolSetJspBean extends AbstractManageBotsJspBean<Integer, ToolSet>
{
    // Templates
    private static final String TEMPLATE_MANAGE_TOOLSETS = "/admin/plugins/knowledge/manage_toolsets.html";
    private static final String TEMPLATE_CREATE_TOOLSET = "/admin/plugins/knowledge/create_toolset.html";
    private static final String TEMPLATE_MODIFY_TOOLSET = "/admin/plugins/knowledge/modify_toolset.html";

    // Parameters
    private static final String PARAMETER_ID_TOOLSET = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TOOLSETS = "knowledge.manage_toolsets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TOOLSET = "knowledge.modify_toolset.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TOOLSET = "knowledge.create_toolset.pageTitle";

    // Markers
    private static final String MARK_TOOLSET_LIST = "toolset_list";
    private static final String MARK_TOOLSET = "toolset";

    private static final String JSP_MANAGE_TOOLSETS = "jsp/admin/plugins/knowledge/ManageToolSets.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TOOLSET = "knowledge.message.confirmRemoveToolSet";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.toolset.attribute.";

    // Views
    private static final String VIEW_MANAGE_TOOLSETS = "manageToolSets";
    private static final String VIEW_CREATE_TOOLSET = "createToolSet";
    private static final String VIEW_MODIFY_TOOLSET = "modifyToolSet";

    // Actions
    private static final String ACTION_CREATE_TOOLSET = "createToolSet";
    private static final String ACTION_MODIFY_TOOLSET = "modifyToolSet";
    private static final String ACTION_REMOVE_TOOLSET = "removeToolSet";
    private static final String ACTION_CONFIRM_REMOVE_TOOLSET = "confirmRemoveToolSet";

    // Infos
    private static final String INFO_TOOLSET_CREATED = "knowledge.info.toolset.created";
    private static final String INFO_TOOLSET_UPDATED = "knowledge.info.toolset.updated";
    private static final String INFO_TOOLSET_REMOVED = "knowledge.info.toolset.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private ToolSet _toolset;
    private List<Integer> _listIdToolSets;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TOOLSETS, defaultView = true )
    public String getManageToolSets( HttpServletRequest request )
    {
        _toolset = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdToolSets.isEmpty( ) )
        {
            _listIdToolSets = ToolSetHome.getIdToolSetsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_TOOLSET_LIST, _listIdToolSets, JSP_MANAGE_TOOLSETS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TOOLSETS, TEMPLATE_MANAGE_TOOLSETS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<ToolSet> getItemsFromIds( List<Integer> listIds )
    {
        List<ToolSet> listToolSet = ToolSetHome.getToolSetsListByIds( listIds );

        // keep original order
        return listToolSet.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdToolSets list
     */
    public void resetListId( )
    {
        _listIdToolSets = new ArrayList<>( );
    }

    /**
     * Returns the form to create a toolset
     *
     * @param request
     *            The Http request
     * @return the html code of the toolset form
     */
    @View( VIEW_CREATE_TOOLSET )
    public String getCreateToolSet( HttpServletRequest request )
    {
        _toolset = ( _toolset != null ) ? _toolset : new ToolSet( );

        Map<String, Object> model = getModel( );
        model.put( MARK_TOOLSET, _toolset );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_TOOLSET ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TOOLSET, TEMPLATE_CREATE_TOOLSET, model );
    }

    /**
     * Process the data capture form of a new toolset
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_TOOLSET )
    public String doCreateToolSet( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _toolset, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_TOOLSET ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _toolset, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TOOLSET );
        }

        ToolSetHome.create( _toolset );
        addInfo( INFO_TOOLSET_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETS );
    }

    /**
     * Manages the removal form of a toolset whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TOOLSET )
    public String getConfirmRemoveToolSet( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TOOLSET ) );
        url.addParameter( PARAMETER_ID_TOOLSET, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TOOLSET, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a toolset
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage toolsets
     */
    @Action( ACTION_REMOVE_TOOLSET )
    public String doRemoveToolSet( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSET ) );

        ToolSetHome.remove( nId );
        addInfo( INFO_TOOLSET_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETS );
    }

    /**
     * Returns the form to update info about a toolset
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TOOLSET )
    public String getModifyToolSet( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSET ) );

        if ( _toolset == null || ( _toolset.getId( ) != nId ) )
        {
            Optional<ToolSet> optToolSet = ToolSetHome.findByPrimaryKey( nId );
            _toolset = optToolSet.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_TOOLSET, _toolset );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_TOOLSET ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TOOLSET, TEMPLATE_MODIFY_TOOLSET, model );
    }

    /**
     * Process the change form of a toolset
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_TOOLSET )
    public String doModifyToolSet( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _toolset, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_TOOLSET ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _toolset, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TOOLSET, PARAMETER_ID_TOOLSET, _toolset.getId( ) );
        }

        ToolSetHome.update( _toolset );
        addInfo( INFO_TOOLSET_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETS );
    }
}
