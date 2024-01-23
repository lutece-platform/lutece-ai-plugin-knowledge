<jsp:useBean id="managebotsToolSetAbility" scope="session" class="fr.paris.lutece.plugins.knowledge.web.ToolSetAbilityJspBean" />
<% String strContent = managebotsToolSetAbility.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
