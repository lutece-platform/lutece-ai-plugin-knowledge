<jsp:useBean id="managebotsBotSession" scope="session" class="fr.paris.lutece.plugins.knowledge.web.BotSessionJspBean" />
<% String strContent = managebotsBotSession.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
