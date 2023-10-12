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
import fr.paris.lutece.plugins.knowledge.business.DatasetFile;
import fr.paris.lutece.plugins.knowledge.business.DatasetFileHome;

/**
 * This class provides the user interface to manage DatasetFile features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDatasetFiles.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class DatasetFileJspBean extends AbstractManageBotsJspBean<Integer, DatasetFile>
{
    // Templates
    private static final String TEMPLATE_MANAGE_DATASETFILES = "/admin/plugins/knowledge/manage_datasetfiles.html";
    private static final String TEMPLATE_CREATE_DATASETFILE = "/admin/plugins/knowledge/create_datasetfile.html";
    private static final String TEMPLATE_MODIFY_DATASETFILE = "/admin/plugins/knowledge/modify_datasetfile.html";

    // Parameters
    private static final String PARAMETER_ID_DATASETFILE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DATASETFILES = "knowledge.manage_datasetfiles.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DATASETFILE = "knowledge.modify_datasetfile.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DATASETFILE = "knowledge.create_datasetfile.pageTitle";

    // Markers
    private static final String MARK_DATASETFILE_LIST = "datasetfile_list";
    private static final String MARK_DATASETFILE = "datasetfile";

    private static final String JSP_MANAGE_DATASETFILES = "jsp/admin/plugins/knowledge/ManageDatasetFiles.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DATASETFILE = "knowledge.message.confirmRemoveDatasetFile";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.datasetfile.attribute.";

    // Views
    private static final String VIEW_MANAGE_DATASETFILES = "manageDatasetFiles";
    private static final String VIEW_CREATE_DATASETFILE = "createDatasetFile";
    private static final String VIEW_MODIFY_DATASETFILE = "modifyDatasetFile";

    // Actions
    private static final String ACTION_CREATE_DATASETFILE = "createDatasetFile";
    private static final String ACTION_MODIFY_DATASETFILE = "modifyDatasetFile";
    private static final String ACTION_REMOVE_DATASETFILE = "removeDatasetFile";
    private static final String ACTION_CONFIRM_REMOVE_DATASETFILE = "confirmRemoveDatasetFile";

    // Infos
    private static final String INFO_DATASETFILE_CREATED = "knowledge.info.datasetfile.created";
    private static final String INFO_DATASETFILE_UPDATED = "knowledge.info.datasetfile.updated";
    private static final String INFO_DATASETFILE_REMOVED = "knowledge.info.datasetfile.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private DatasetFile _datasetfile;
    private List<Integer> _listIdDatasetFiles;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DATASETFILES, defaultView = true )
    public String getManageDatasetFiles( HttpServletRequest request )
    {
        _datasetfile = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdDatasetFiles.isEmpty( ) )
        {
            _listIdDatasetFiles = DatasetFileHome.getIdDatasetFilesList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DATASETFILE_LIST, _listIdDatasetFiles, JSP_MANAGE_DATASETFILES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DATASETFILES, TEMPLATE_MANAGE_DATASETFILES, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<DatasetFile> getItemsFromIds( List<Integer> listIds )
    {
        List<DatasetFile> listDatasetFile = DatasetFileHome.getDatasetFilesListByIds( listIds );

        // keep original order
        return listDatasetFile.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdDatasetFiles list
     */
    public void resetListId( )
    {
        _listIdDatasetFiles = new ArrayList<>( );
    }

    /**
     * Returns the form to create a datasetfile
     *
     * @param request
     *            The Http request
     * @return the html code of the datasetfile form
     */
    @View( VIEW_CREATE_DATASETFILE )
    public String getCreateDatasetFile( HttpServletRequest request )
    {
        _datasetfile = ( _datasetfile != null ) ? _datasetfile : new DatasetFile( );

        Map<String, Object> model = getModel( );
        model.put( MARK_DATASETFILE, _datasetfile );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DATASETFILE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DATASETFILE, TEMPLATE_CREATE_DATASETFILE, model );
    }

    /**
     * Process the data capture form of a new datasetfile
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DATASETFILE )
    public String doCreateDatasetFile( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _datasetfile, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DATASETFILE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _datasetfile, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DATASETFILE );
        }

        DatasetFileHome.create( _datasetfile );
        addInfo( INFO_DATASETFILE_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETFILES );
    }

    /**
     * Manages the removal form of a datasetfile whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DATASETFILE )
    public String getConfirmRemoveDatasetFile( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASETFILE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DATASETFILE ) );
        url.addParameter( PARAMETER_ID_DATASETFILE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DATASETFILE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a datasetfile
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage datasetfiles
     */
    @Action( ACTION_REMOVE_DATASETFILE )
    public String doRemoveDatasetFile( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASETFILE ) );

        DatasetFileHome.remove( nId );
        addInfo( INFO_DATASETFILE_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETFILES );
    }

    /**
     * Returns the form to update info about a datasetfile
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DATASETFILE )
    public String getModifyDatasetFile( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASETFILE ) );

        if ( _datasetfile == null || ( _datasetfile.getId( ) != nId ) )
        {
            Optional<DatasetFile> optDatasetFile = DatasetFileHome.findByPrimaryKey( nId );
            _datasetfile = optDatasetFile.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_DATASETFILE, _datasetfile );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DATASETFILE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DATASETFILE, TEMPLATE_MODIFY_DATASETFILE, model );
    }

    /**
     * Process the change form of a datasetfile
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DATASETFILE )
    public String doModifyDatasetFile( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _datasetfile, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DATASETFILE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _datasetfile, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DATASETFILE, PARAMETER_ID_DATASETFILE, _datasetfile.getId( ) );
        }

        DatasetFileHome.update( _datasetfile );
        addInfo( INFO_DATASETFILE_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETFILES );
    }
}
