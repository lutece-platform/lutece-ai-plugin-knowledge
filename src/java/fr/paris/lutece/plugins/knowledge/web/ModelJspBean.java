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
import fr.paris.lutece.plugins.knowledge.business.Model;
import fr.paris.lutece.plugins.knowledge.business.ModelHome;

/**
 * This class provides the user interface to manage Model features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageModels.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT" )
public class ModelJspBean extends AbstractKnowledgeManagerJspBean<Integer, Model>
{
    // Templates
    private static final String TEMPLATE_MANAGE_MODELS = "/admin/plugins/knowledge/manage_models.html";
    private static final String TEMPLATE_CREATE_MODEL = "/admin/plugins/knowledge/create_model.html";
    private static final String TEMPLATE_MODIFY_MODEL = "/admin/plugins/knowledge/modify_model.html";

    // Parameters
    private static final String PARAMETER_ID_MODEL = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_MODELS = "knowledge.manage_models.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_MODEL = "knowledge.modify_model.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_MODEL = "knowledge.create_model.pageTitle";

    // Markers
    private static final String MARK_MODEL_LIST = "model_list";
    private static final String MARK_MODEL = "model";

    private static final String JSP_MANAGE_MODELS = "jsp/admin/plugins/knowledge/ManageModels.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_MODEL = "knowledge.message.confirmRemoveModel";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.model.attribute.";

    // Views
    private static final String VIEW_MANAGE_MODELS = "manageModels";
    private static final String VIEW_CREATE_MODEL = "createModel";
    private static final String VIEW_MODIFY_MODEL = "modifyModel";

    // Actions
    private static final String ACTION_CREATE_MODEL = "createModel";
    private static final String ACTION_MODIFY_MODEL = "modifyModel";
    private static final String ACTION_REMOVE_MODEL = "removeModel";
    private static final String ACTION_CONFIRM_REMOVE_MODEL = "confirmRemoveModel";

    // Infos
    private static final String INFO_MODEL_CREATED = "knowledge.info.model.created";
    private static final String INFO_MODEL_UPDATED = "knowledge.info.model.updated";
    private static final String INFO_MODEL_REMOVED = "knowledge.info.model.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Model _model;
    private List<Integer> _listIdModels;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_MODELS, defaultView = true )
    public String getManageModels( HttpServletRequest request )
    {
        _model = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdModels.isEmpty( ) )
        {
            _listIdModels = ModelHome.getIdModelsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_MODEL_LIST, _listIdModels, JSP_MANAGE_MODELS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_MODELS, TEMPLATE_MANAGE_MODELS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Model> getItemsFromIds( List<Integer> listIds )
    {
        List<Model> listModel = ModelHome.getModelsListByIds( listIds );

        // keep original order
        return listModel.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdModels list
     */
    public void resetListId( )
    {
        _listIdModels = new ArrayList<>( );
    }

    /**
     * Returns the form to create a model
     *
     * @param request
     *            The Http request
     * @return the html code of the model form
     */
    @View( VIEW_CREATE_MODEL )
    public String getCreateModel( HttpServletRequest request )
    {
        _model = ( _model != null ) ? _model : new Model( );

        Map<String, Object> model = getModel( );
        model.put( MARK_MODEL, _model );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_MODEL ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_MODEL, TEMPLATE_CREATE_MODEL, model );
    }

    /**
     * Process the data capture form of a new model
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_MODEL )
    public String doCreateModel( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _model, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_MODEL ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _model, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_MODEL );
        }

        ModelHome.create( _model );
        addInfo( INFO_MODEL_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_MODELS );
    }

    /**
     * Manages the removal form of a model whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_MODEL )
    public String getConfirmRemoveModel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_MODEL ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MODEL ) );
        url.addParameter( PARAMETER_ID_MODEL, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MODEL, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a model
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage models
     */
    @Action( ACTION_REMOVE_MODEL )
    public String doRemoveModel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_MODEL ) );

        ModelHome.remove( nId );
        addInfo( INFO_MODEL_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_MODELS );
    }

    /**
     * Returns the form to update info about a model
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_MODEL )
    public String getModifyModel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_MODEL ) );

        if ( _model == null || ( _model.getId( ) != nId ) )
        {
            Optional<Model> optModel = ModelHome.findByPrimaryKey( nId );
            _model = optModel.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_MODEL, _model );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_MODEL ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_MODEL, TEMPLATE_MODIFY_MODEL, model );
    }

    /**
     * Process the change form of a model
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_MODEL )
    public String doModifyModel( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _model, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_MODEL ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _model, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_MODEL, PARAMETER_ID_MODEL, _model.getId( ) );
        }

        ModelHome.update( _model );
        addInfo( INFO_MODEL_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_MODELS );
    }
}
