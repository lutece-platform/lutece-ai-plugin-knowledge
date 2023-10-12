<jsp:useBean id="managebotsToolSet" scope="session" class="fr.paris.lutece.plugins.knowledge.web.ToolSetJspBean" />
<% String strContent = managebotsToolSet.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
