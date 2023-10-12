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

import fr.paris.lutece.plugins.knowledge.business.Bot;
import fr.paris.lutece.plugins.knowledge.business.BotHome;
import fr.paris.lutece.plugins.knowledge.business.BotSessionHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.page.PageNotFoundException;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Bot xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = "bot", pageTitleI18nKey = "knowledge.xpage.bot.pageTitle", pagePathI18nKey = "knowledge.xpage.bot.pagePathLabel" )
public class BotXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_MANAGE_BOTS = "/skin/plugins/knowledge/manage_bots.html";
    private static final String TEMPLATE_CREATE_BOT = "/skin/plugins/knowledge/create_bot.html";
    private static final String TEMPLATE_MODIFY_BOT = "/skin/plugins/knowledge/modify_bot.html";

    // Parameters
    private static final String PARAMETER_ID_BOT = "id";
    private static final String PARAMETER_ID_BOT_SESSION_ID = "bot_session_id";

    // Markers
    private static final String MARK_BOT_LIST = "bot_list";
    private static final String MARK_BOT = "bot";
    private static final String MARK_BOT_SESSION_LIST = "bot_session_list";
    private static final String MARK_BOT_SESSION = "bot_session";
    private static final String MARK_MYLUTECE_USER = "mylutece_user";



    // Message
    private static final String MESSAGE_CONFIRM_REMOVE_BOT = "knowledge.message.confirmRemoveBot";

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

    /**
     * return the form to manage bots
     * 
     * @param request
     *            The Http request
     * @return the html code of the list of bots
     * @throws UserNotSignedException
     */
    @View( value = VIEW_MANAGE_BOTS, defaultView = true )
    public XPage getManageBots( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = getUser( request );
        _bot = null;
        List<Bot> listBots = BotHome.getBotsList( );

        Map<String, Object> model = getModel( );
        model.put( MARK_MYLUTECE_USER, user );
        model.put( MARK_BOT_LIST, listBots );
        model.put( MARK_BOT_SESSION_LIST, BotSessionHome.getBotSessionsByAccessCode( SecurityService.getInstance( ).getRegisteredUser( request ).getAccessCode( ) ) );
        model.put( MARK_MYLUTECE_USER, user );


        return getXPage( TEMPLATE_MANAGE_BOTS, getLocale( request ), model );
    }

    /**
     * Returns the form to create a bot
     *
     * @param request
     *            The Http request
     * @return the html code of the bot form
     * @throws UserNotSignedException
     */
    @View( VIEW_CREATE_BOT )
    public XPage getCreateBot( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = getUser( request );
        _bot = ( _bot != null ) ? _bot : new Bot( );

        Map<String, Object> model = getModel( );
        model.put( MARK_BOT, _bot );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BOT ) );
        model.put( MARK_MYLUTECE_USER, user );


        return getXPage( TEMPLATE_CREATE_BOT, getLocale( request ), model );
    }

    /**
     * Process the data capture form of a new bot
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     * @throws UserNotSignedException
     */
    @Action( ACTION_CREATE_BOT )
    public XPage doCreateBot( HttpServletRequest request ) throws AccessDeniedException, UserNotSignedException
    {
        LuteceUser user = getUser( request );
        populate( _bot, request, getLocale( request ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_BOT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _bot ) )
        {
            return redirectView( request, VIEW_CREATE_BOT );
        }

        BotHome.create( _bot );
        addInfo( INFO_BOT_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }

    /**
     * Manages the removal form of a bot whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             {@link fr.paris.lutece.portal.service.message.SiteMessageException}
     * @throws UserNotSignedException
     */
    @Action( ACTION_CONFIRM_REMOVE_BOT )
    public XPage getConfirmRemoveBot( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        LuteceUser user = getUser( request );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );

        UrlItem url = new UrlItem( getActionFullUrl( ACTION_REMOVE_BOT ) );
        url.addParameter( PARAMETER_ID_BOT, nId );

        SiteMessageService.setMessage( request, MESSAGE_CONFIRM_REMOVE_BOT, SiteMessage.TYPE_CONFIRMATION, url.getUrl( ) );
        return null;
    }

    /**
     * Handles the removal form of a bot
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage bots
     * @throws UserNotSignedException
     */
    @Action( ACTION_REMOVE_BOT )
    public XPage doRemoveBot( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = getUser( request );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );
        BotHome.remove( nId );
        addInfo( INFO_BOT_REMOVED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }

    /**
     * Returns the form to update info about a bot
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     * @throws UserNotSignedException
     */
    @View( VIEW_MODIFY_BOT )
    public XPage getModifyBot( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = getUser( request );
        String userAccessCode = SecurityService.getInstance( ).getRegisteredUser( request ).getAccessCode( );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BOT ) );
        String botSessionId = request.getParameter( PARAMETER_ID_BOT_SESSION_ID );

        if ( _bot == null || ( _bot.getId( ) != nId ) )
        {
            Optional<Bot> optBot = BotHome.findByPrimaryKey( nId );
            _bot = optBot.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_BOT, _bot );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_BOT ) );
        model.put( MARK_BOT_SESSION_LIST, BotSessionHome.getBotSessionsByAccessCode( userAccessCode ) );
        model.put( MARK_MYLUTECE_USER, user );
        if ( botSessionId != null )
        {
            model.put( MARK_BOT_SESSION, BotSessionHome.findByAccessCode( userAccessCode, botSessionId).get( ) );
        }
        
        return getXPage( TEMPLATE_MODIFY_BOT, getLocale( request ), model );
    }

    /**
     * Process the change form of a bot
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     * @throws UserNotSignedException
     */
    @Action( ACTION_MODIFY_BOT )
    public XPage doModifyBot( HttpServletRequest request ) throws AccessDeniedException, UserNotSignedException
    {
        LuteceUser user = getUser( request );
        populate( _bot, request, getLocale( request ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_BOT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _bot ) )
        {
            return redirect( request, VIEW_MODIFY_BOT, PARAMETER_ID_BOT, _bot.getId( ) );
        }

        BotHome.update( _bot );
        addInfo( INFO_BOT_UPDATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_BOTS );
    }

        /**
     * Gets the user from the request
     * @param request The HTTP user
     * @return The Lutece User
     * @throws UserNotSignedException exception if user not connected
     */
    public LuteceUser getUser( HttpServletRequest request )
        throws UserNotSignedException
    {
        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            LuteceUser user = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( user == null )
            {
                throw new UserNotSignedException(  );
            }

            return user;
        }
        else
        {
            throw new PageNotFoundException(  );
        }
    }

}
