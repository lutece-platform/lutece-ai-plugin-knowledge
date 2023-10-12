<jsp:useBean id="managebotsDatasetFile" scope="session" class="fr.paris.lutece.plugins.knowledge.web.DatasetFileJspBean" />
<% String strContent = managebotsDatasetFile.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
