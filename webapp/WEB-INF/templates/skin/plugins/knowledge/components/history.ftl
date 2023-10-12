<nav class="order-first overflow-auto offcanvas offcanvas-end p-3 p-lg-0 border-start d-flex" id="sidebarHistory"
    aria-labelledby="sidebarHistory">
     <div class="offcanvas-body position-relative d-flex flex-column flex-shrink-0 p-0 h-100">

      <a href="/" class="d-flex align-items-center flex-shrink-0 p-3 link-dark text-decoration-none border-bottom">
         <button class="btn btn-circle ms-auto z-2 position-absolute end-0 me-3 top-0 mt-3"
            type="button" id="trash"><i class="ti ti-trash fs-5"></i></button>
        <h3 class="text-center mb-0 w-100">Historique</h3>
    </a>
    <div class=" border-bottom overflow-auto px-3">

<#if bot_session??>
    <#assign currentid = bot_session.id>
<#else>
    <#assign currentid = 0>
</#if>
      <#list bot_session_list?reverse as bot_session>
        <#if bot_session.content??>
        <#assign isCurrent = bot_session.id == currentid>
        <#assign jsonStr = bot_session.content?replace("\\\\u", "\\u")>
        <#assign conversations = jsonStr?replace('[', '')?replace(']', '')?split('},{"')>
      <a href="Portal.jsp?page=bot&view=modifyBot&id=${bot_session.botId}&bot_session_id=${bot_session.sessionId}" class="list-group-item list-group-item-action border-2 border p-3 lh-tight bg-light bg-opacity-50 my-2 rounded-4 <#if isCurrent>border border-primary border-2 bg-opacity-100</#if>"  aria-current="true">
          <#assign lastIndex = conversations?size - 1>
            <#assign lastConversation = conversations[lastIndex]>
            <#assign parts = lastConversation?replace('{', '')?replace('}', '')?replace('"\\"', '"')?replace('\\u0027', "'")?split('","')>
            <#assign lastText = "">
            <#list parts as part>
                <#assign keyValue = part?split('":"')>
                <#if keyValue[0]?contains("text")>
                <#assign lastText = keyValue[1]?replace('"', '')>
                </#if>
            </#list>
            <#if lastText?length gt 100>
                <#assign trimmed_text = lastText?substring(0, 100) + "...">
            <#else>
                <#assign trimmed_text = lastText>
            </#if>
            <div class="mb-2">${trimmed_text}</div>
            <#if bot_list??>
            <#list bot_list as bot>
                <#if bot.id == bot_session.botId>
                    <span class="badge bg-primary-subtle text-primary-emphasis border border-primary-subtle rounded-5"><i
                            class="ti ti-message-chatbot"></i> ${bot.name}</span>
                </#if>
            </#list>
            </#if>
             <span class="badge bg-primary-subtle text-primary-emphasis border border-primary-subtle rounded-5">
                <i class="ti ti-calendar"></i>${bot_session.creationDate}
            </span>
      </a>
  </#if>
        </#list>
    </div>
</nav>