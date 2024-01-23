<jsp:useBean id="managebotsBot" scope="session" class="fr.paris.lutece.plugins.knowledge.web.BotJspBean" />
<% String strContent = managebotsBot.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
