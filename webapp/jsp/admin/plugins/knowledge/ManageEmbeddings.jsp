<jsp:useBean id="manageembeddingEmbedding" scope="session" class="fr.paris.lutece.plugins.knowledge.web.EmbeddingJspBean" />
<% String strContent = manageembeddingEmbedding.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
