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
import fr.paris.lutece.plugins.knowledge.business.ToolSetAbility;
import fr.paris.lutece.plugins.knowledge.business.ToolSetAbilityHome;

/**
 * This class provides the user interface to manage ToolSetAbility features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageToolSetAbilitys.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class ToolSetAbilityJspBean extends AbstractManageBotsJspBean<Integer, ToolSetAbility>
{
    // Templates
    private static final String TEMPLATE_MANAGE_TOOLSETABILITYS = "/admin/plugins/knowledge/manage_toolsetabilitys.html";
    private static final String TEMPLATE_CREATE_TOOLSETABILITY = "/admin/plugins/knowledge/create_toolsetability.html";
    private static final String TEMPLATE_MODIFY_TOOLSETABILITY = "/admin/plugins/knowledge/modify_toolsetability.html";

    // Parameters
    private static final String PARAMETER_ID_TOOLSETABILITY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TOOLSETABILITYS = "knowledge.manage_toolsetabilitys.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TOOLSETABILITY = "knowledge.modify_toolsetability.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TOOLSETABILITY = "knowledge.create_toolsetability.pageTitle";

    // Markers
    private static final String MARK_TOOLSETABILITY_LIST = "toolsetability_list";
    private static final String MARK_TOOLSETABILITY = "toolsetability";

    private static final String JSP_MANAGE_TOOLSETABILITYS = "jsp/admin/plugins/knowledge/ManageToolSetAbilitys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TOOLSETABILITY = "knowledge.message.confirmRemoveToolSetAbility";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.toolsetability.attribute.";

    // Views
    private static final String VIEW_MANAGE_TOOLSETABILITYS = "manageToolSetAbilitys";
    private static final String VIEW_CREATE_TOOLSETABILITY = "createToolSetAbility";
    private static final String VIEW_MODIFY_TOOLSETABILITY = "modifyToolSetAbility";

    // Actions
    private static final String ACTION_CREATE_TOOLSETABILITY = "createToolSetAbility";
    private static final String ACTION_MODIFY_TOOLSETABILITY = "modifyToolSetAbility";
    private static final String ACTION_REMOVE_TOOLSETABILITY = "removeToolSetAbility";
    private static final String ACTION_CONFIRM_REMOVE_TOOLSETABILITY = "confirmRemoveToolSetAbility";

    // Infos
    private static final String INFO_TOOLSETABILITY_CREATED = "knowledge.info.toolsetability.created";
    private static final String INFO_TOOLSETABILITY_UPDATED = "knowledge.info.toolsetability.updated";
    private static final String INFO_TOOLSETABILITY_REMOVED = "knowledge.info.toolsetability.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private ToolSetAbility _toolsetability;
    private List<Integer> _listIdToolSetAbilitys;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TOOLSETABILITYS, defaultView = true )
    public String getManageToolSetAbilitys( HttpServletRequest request )
    {
        _toolsetability = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdToolSetAbilitys.isEmpty( ) )
        {
            _listIdToolSetAbilitys = ToolSetAbilityHome.getIdToolSetAbilitysList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_TOOLSETABILITY_LIST, _listIdToolSetAbilitys, JSP_MANAGE_TOOLSETABILITYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TOOLSETABILITYS, TEMPLATE_MANAGE_TOOLSETABILITYS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<ToolSetAbility> getItemsFromIds( List<Integer> listIds )
    {
        List<ToolSetAbility> listToolSetAbility = ToolSetAbilityHome.getToolSetAbilitysListByIds( listIds );

        // keep original order
        return listToolSetAbility.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdToolSetAbilitys list
     */
    public void resetListId( )
    {
        _listIdToolSetAbilitys = new ArrayList<>( );
    }

    /**
     * Returns the form to create a toolsetability
     *
     * @param request
     *            The Http request
     * @return the html code of the toolsetability form
     */
    @View( VIEW_CREATE_TOOLSETABILITY )
    public String getCreateToolSetAbility( HttpServletRequest request )
    {
        _toolsetability = ( _toolsetability != null ) ? _toolsetability : new ToolSetAbility( );

        Map<String, Object> model = getModel( );
        model.put( MARK_TOOLSETABILITY, _toolsetability );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_TOOLSETABILITY ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TOOLSETABILITY, TEMPLATE_CREATE_TOOLSETABILITY, model );
    }

    /**
     * Process the data capture form of a new toolsetability
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_TOOLSETABILITY )
    public String doCreateToolSetAbility( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _toolsetability, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_TOOLSETABILITY ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _toolsetability, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TOOLSETABILITY );
        }

        ToolSetAbilityHome.create( _toolsetability );
        addInfo( INFO_TOOLSETABILITY_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETABILITYS );
    }

    /**
     * Manages the removal form of a toolsetability whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TOOLSETABILITY )
    public String getConfirmRemoveToolSetAbility( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSETABILITY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TOOLSETABILITY ) );
        url.addParameter( PARAMETER_ID_TOOLSETABILITY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TOOLSETABILITY, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a toolsetability
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage toolsetabilitys
     */
    @Action( ACTION_REMOVE_TOOLSETABILITY )
    public String doRemoveToolSetAbility( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSETABILITY ) );

        ToolSetAbilityHome.remove( nId );
        addInfo( INFO_TOOLSETABILITY_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETABILITYS );
    }

    /**
     * Returns the form to update info about a toolsetability
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TOOLSETABILITY )
    public String getModifyToolSetAbility( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TOOLSETABILITY ) );

        if ( _toolsetability == null || ( _toolsetability.getId( ) != nId ) )
        {
            Optional<ToolSetAbility> optToolSetAbility = ToolSetAbilityHome.findByPrimaryKey( nId );
            _toolsetability = optToolSetAbility.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_TOOLSETABILITY, _toolsetability );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_TOOLSETABILITY ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TOOLSETABILITY, TEMPLATE_MODIFY_TOOLSETABILITY, model );
    }

    /**
     * Process the change form of a toolsetability
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_TOOLSETABILITY )
    public String doModifyToolSetAbility( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _toolsetability, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_TOOLSETABILITY ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _toolsetability, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TOOLSETABILITY, PARAMETER_ID_TOOLSETABILITY, _toolsetability.getId( ) );
        }

        ToolSetAbilityHome.update( _toolsetability );
        addInfo( INFO_TOOLSETABILITY_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TOOLSETABILITYS );
    }
}
