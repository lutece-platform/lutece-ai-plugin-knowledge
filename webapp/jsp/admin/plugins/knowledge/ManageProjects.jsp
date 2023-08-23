<jsp:useBean id="knowledgemanagerProject" scope="session" class="fr.paris.lutece.plugins.knowledge.web.ProjectJspBean" />
<% String strContent = knowledgemanagerProject.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
