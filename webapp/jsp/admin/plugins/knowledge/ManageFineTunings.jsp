<jsp:useBean id="managefinetuningFineTuning" scope="session" class="fr.paris.lutece.plugins.knowledge.web.FineTuningJspBean" />
<% String strContent = managefinetuningFineTuning.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
