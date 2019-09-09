<#include "header.ftl">

<#if loggedInUser?has_content>
    <#if loggedInUser == timelineUser>
    <@spring.bind "message"/>
    <form action="" method="post">
        <@spring.formHiddenInput "message.author"/>
        <@spring.formInput "message.message"/>
        <input type="submit" value="Post">
    </form>
    <#else>
        <#if following?has_content && following >
            <a href="<@spring.url "/unfollow/${timelineUser}"/>">Unfollow ${timelineUser}</a>
        <#else>
            <a href="<@spring.url "/follow/${timelineUser}"/>">Follow ${timelineUser}</a>
        </#if>
    </#if>
</#if>

<#if timelines?has_content >
    <ul>
    <#list timelines as entry>
        <li>${entry.createdDttm?number_to_date?string("yyyy-MM-dd HH:mm:ss")} : <a href="<@spring.url "/timeline/${entry.author}"/>">${entry.author}</a> : ${entry.message}</li>
    </#list>
    </ul>
<#else>
    This timeline has no content.
</#if>

<#include "footer.ftl">