<#include "header.ftl">
<#if users?has_content >
    <ul>
    <#list users as username, user>
        <li><a href="<@spring.url "/timeline/${username}"/>">${username} (${user.messages}/${user.follows})</a> - <a href="<@spring.url "/login/${username}"/>">Login</a></li>
    </#list>
    </ul>
<#else>
    There are no users.
</#if>
<#include "footer.ftl">