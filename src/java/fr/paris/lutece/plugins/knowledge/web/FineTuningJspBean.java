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
import fr.paris.lutece.plugins.knowledge.business.FineTuning;
import fr.paris.lutece.plugins.knowledge.business.FineTuningHome;

/**
 * This class provides the user interface to manage FineTuning features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageFineTunings.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT" )
public class FineTuningJspBean extends AbstractKnowledgeManagerJspBean<Integer, FineTuning>
{
    // Templates
    private static final String TEMPLATE_MANAGE_FINETUNINGS = "/admin/plugins/knowledge/manage_finetunings.html";
    private static final String TEMPLATE_CREATE_FINETUNING = "/admin/plugins/knowledge/create_finetuning.html";
    private static final String TEMPLATE_MODIFY_FINETUNING = "/admin/plugins/knowledge/modify_finetuning.html";

    // Parameters
    private static final String PARAMETER_ID_FINETUNING = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FINETUNINGS = "knowledge.manage_finetunings.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FINETUNING = "knowledge.modify_finetuning.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FINETUNING = "knowledge.create_finetuning.pageTitle";

    // Markers
    private static final String MARK_FINETUNING_LIST = "finetuning_list";
    private static final String MARK_FINETUNING = "finetuning";

    private static final String JSP_MANAGE_FINETUNINGS = "jsp/admin/plugins/knowledge/ManageFineTunings.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_FINETUNING = "knowledge.message.confirmRemoveFineTuning";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.finetuning.attribute.";

    // Views
    private static final String VIEW_MANAGE_FINETUNINGS = "manageFineTunings";
    private static final String VIEW_CREATE_FINETUNING = "createFineTuning";
    private static final String VIEW_MODIFY_FINETUNING = "modifyFineTuning";

    // Actions
    private static final String ACTION_CREATE_FINETUNING = "createFineTuning";
    private static final String ACTION_MODIFY_FINETUNING = "modifyFineTuning";
    private static final String ACTION_REMOVE_FINETUNING = "removeFineTuning";
    private static final String ACTION_CONFIRM_REMOVE_FINETUNING = "confirmRemoveFineTuning";

    // Infos
    private static final String INFO_FINETUNING_CREATED = "knowledge.info.finetuning.created";
    private static final String INFO_FINETUNING_UPDATED = "knowledge.info.finetuning.updated";
    private static final String INFO_FINETUNING_REMOVED = "knowledge.info.finetuning.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private FineTuning _finetuning;
    private List<Integer> _listIdFineTunings;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FINETUNINGS, defaultView = true )
    public String getManageFineTunings( HttpServletRequest request )
    {
        _finetuning = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdFineTunings.isEmpty( ) )
        {
            _listIdFineTunings = FineTuningHome.getIdFineTuningsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_FINETUNING_LIST, _listIdFineTunings, JSP_MANAGE_FINETUNINGS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FINETUNINGS, TEMPLATE_MANAGE_FINETUNINGS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<FineTuning> getItemsFromIds( List<Integer> listIds )
    {
        List<FineTuning> listFineTuning = FineTuningHome.getFineTuningsListByIds( listIds );

        // keep original order
        return listFineTuning.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdFineTunings list
     */
    public void resetListId( )
    {
        _listIdFineTunings = new ArrayList<>( );
    }

    /**
     * Returns the form to create a finetuning
     *
     * @param request
     *            The Http request
     * @return the html code of the finetuning form
     */
    @View( VIEW_CREATE_FINETUNING )
    public String getCreateFineTuning( HttpServletRequest request )
    {
        _finetuning = ( _finetuning != null ) ? _finetuning : new FineTuning( );

        Map<String, Object> model = getModel( );
        model.put( MARK_FINETUNING, _finetuning );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_FINETUNING ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_FINETUNING, TEMPLATE_CREATE_FINETUNING, model );
    }

    /**
     * Process the data capture form of a new finetuning
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_FINETUNING )
    public String doCreateFineTuning( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _finetuning, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_FINETUNING ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _finetuning, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_FINETUNING );
        }

        FineTuningHome.create( _finetuning );
        addInfo( INFO_FINETUNING_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FINETUNINGS );
    }

    /**
     * Manages the removal form of a finetuning whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_FINETUNING )
    public String getConfirmRemoveFineTuning( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FINETUNING ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FINETUNING ) );
        url.addParameter( PARAMETER_ID_FINETUNING, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FINETUNING, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a finetuning
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage finetunings
     */
    @Action( ACTION_REMOVE_FINETUNING )
    public String doRemoveFineTuning( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FINETUNING ) );

        FineTuningHome.remove( nId );
        addInfo( INFO_FINETUNING_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FINETUNINGS );
    }

    /**
     * Returns the form to update info about a finetuning
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_FINETUNING )
    public String getModifyFineTuning( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FINETUNING ) );

        if ( _finetuning == null || ( _finetuning.getId( ) != nId ) )
        {
            Optional<FineTuning> optFineTuning = FineTuningHome.findByPrimaryKey( nId );
            _finetuning = optFineTuning.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_FINETUNING, _finetuning );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_FINETUNING ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FINETUNING, TEMPLATE_MODIFY_FINETUNING, model );
    }

    /**
     * Process the change form of a finetuning
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_FINETUNING )
    public String doModifyFineTuning( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _finetuning, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_FINETUNING ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _finetuning, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FINETUNING, PARAMETER_ID_FINETUNING, _finetuning.getId( ) );
        }

        FineTuningHome.update( _finetuning );
        addInfo( INFO_FINETUNING_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FINETUNINGS );
    }
}
