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
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.knowledge.business.Dataset;
import fr.paris.lutece.plugins.knowledge.business.DatasetFileHome;
import fr.paris.lutece.plugins.knowledge.business.DatasetHome;
import fr.paris.lutece.plugins.knowledge.service.DataSetService;

/**
 * This class provides the user interface to manage Dataset features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDatasets.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENT_BOTS" )
public class DatasetJspBean extends AbstractManageBotsJspBean<Integer, Dataset>
{
    // Templates
    private static final String TEMPLATE_MANAGE_DATASETS = "/admin/plugins/knowledge/manage_datasets.html";
    private static final String TEMPLATE_CREATE_DATASET = "/admin/plugins/knowledge/create_dataset.html";
    private static final String TEMPLATE_MODIFY_DATASET = "/admin/plugins/knowledge/modify_dataset.html";

    // Parameters
    private static final String PARAMETER_ID_DATASET = "id";
    private static final String PARAMETER_ID_DATASET_FILE = "id_dataset_file";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DATASETS = "knowledge.manage_datasets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DATASET = "knowledge.modify_dataset.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DATASET = "knowledge.create_dataset.pageTitle";

    // Markers
    private static final String MARK_DATASET_LIST = "dataset_list";
    private static final String MARK_DATASET = "dataset";
    private static final String MARK_DATASET_FILE_LIST = "dataset_file_list";

    private static final String JSP_MANAGE_DATASETS = "jsp/admin/plugins/knowledge/ManageDatasets.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DATASET = "knowledge.message.confirmRemoveDataset";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.dataset.attribute.";

    // Views
    private static final String VIEW_MANAGE_DATASETS = "manageDatasets";
    private static final String VIEW_CREATE_DATASET = "createDataset";
    private static final String VIEW_MODIFY_DATASET = "modifyDataset";

    // Actions
    private static final String ACTION_CREATE_DATASET = "createDataset";
    private static final String ACTION_MODIFY_DATASET = "modifyDataset";
    private static final String ACTION_REMOVE_DATASET = "removeDataset";
    private static final String ACTION_CONFIRM_REMOVE_DATASET = "confirmRemoveDataset";
    private static final String ACTION_ADD_DATASET_FILE = "addDatasetFile";
    private static final String ACTION_REMOVE_DATASET_FILE = "removeDatasetFile";

    // Infos
    private static final String INFO_DATASET_CREATED = "knowledge.info.dataset.created";
    private static final String INFO_DATASET_UPDATED = "knowledge.info.dataset.updated";
    private static final String INFO_DATASET_REMOVED = "knowledge.info.dataset.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Dataset _dataset;
    private List<Integer> _listIdDatasets;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DATASETS, defaultView = true )
    public String getManageDatasets( HttpServletRequest request )
    {
        _dataset = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdDatasets.isEmpty( ) )
        {
            _listIdDatasets = DatasetHome.getIdDatasetsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DATASET_LIST, _listIdDatasets, JSP_MANAGE_DATASETS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DATASETS, TEMPLATE_MANAGE_DATASETS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Dataset> getItemsFromIds( List<Integer> listIds )
    {
        List<Dataset> listDataset = DatasetHome.getDatasetsListByIds( listIds );

        // keep original order
        return listDataset.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdDatasets list
     */
    public void resetListId( )
    {
        _listIdDatasets = new ArrayList<>( );
    }

    /**
     * Returns the form to create a dataset
     *
     * @param request
     *            The Http request
     * @return the html code of the dataset form
     */
    @View( VIEW_CREATE_DATASET )
    public String getCreateDataset( HttpServletRequest request )
    {
        _dataset = ( _dataset != null ) ? _dataset : new Dataset( );

        Map<String, Object> model = getModel( );
        model.put( MARK_DATASET, _dataset );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DATASET ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DATASET, TEMPLATE_CREATE_DATASET, model );
    }

    /**
     * Process the data capture form of a new dataset
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DATASET )
    public String doCreateDataset( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _dataset, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DATASET ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _dataset, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DATASET );
        }

        DatasetHome.create( _dataset );
        addInfo( INFO_DATASET_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETS );
    }

    /**
     * Manages the removal form of a dataset whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DATASET )
    public String getConfirmRemoveDataset( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DATASET ) );
        url.addParameter( PARAMETER_ID_DATASET, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DATASET, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a dataset
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage datasets
     */
    @Action( ACTION_REMOVE_DATASET )
    public String doRemoveDataset( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASET ) );

        DatasetHome.remove( nId );
        addInfo( INFO_DATASET_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETS );
    }

    /**
     * Returns the form to update info about a dataset
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DATASET )
    public String getModifyDataset( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASET ) );

        if ( _dataset == null || ( _dataset.getId( ) != nId ) )
        {
            Optional<Dataset> optDataset = DatasetHome.findByPrimaryKey( nId );
            _dataset = optDataset.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }
        Map<String, Object> model = getModel( );
        model.put( MARK_DATASET, _dataset );
        model.put( MARK_DATASET_FILE_LIST, DatasetFileHome.getDatasetFilesListByDataSetId( _dataset.getId( ) ) );

        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DATASET ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DATASET, TEMPLATE_MODIFY_DATASET, model );
    }

    @Action( ACTION_ADD_DATASET_FILE )
    public String doAddDatasetFile( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( "file" );
        DataSetService.create( fileItem, _dataset );

        return redirect( request, VIEW_MODIFY_DATASET, PARAMETER_ID_DATASET, _dataset.getId( ) );
    }

    @Action( ACTION_REMOVE_DATASET_FILE )
    public String doRemoveDatasetFile( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DATASET_FILE ) );
        DataSetService.deleteDocument( nId );

        return redirect( request, VIEW_MODIFY_DATASET, PARAMETER_ID_DATASET, _dataset.getId( ) );
    }

    /**
     * Process the change form of a dataset
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DATASET )
    public String doModifyDataset( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _dataset, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DATASET ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _dataset, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DATASET, PARAMETER_ID_DATASET, _dataset.getId( ) );
        }

        DatasetHome.update( _dataset );
        addInfo( INFO_DATASET_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DATASETS );
    }
}
