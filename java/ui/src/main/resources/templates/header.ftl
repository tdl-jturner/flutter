<#import "/spring.ftl" as spring />
<!DOCTYPE HTML>
<head>
    <title>Flutter</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <#if redirect?exists >
        <#if redirect?starts_with("http") >
            <meta http-equiv="refresh" content="${redirectTime?default(1)};url=${redirect}" />
        <#else >
        <meta http-equiv="refresh" content="${redirectTime?default(1)};url=<@spring.url "${redirect}"/>" />
        </#if>
    </#if>
</head>
<body>
    <div style="text-align:right">

        <#if loggedInUser?exists && loggedInUser?has_content >
            ${loggedInUser} | <a href="<@spring.url "/timeline/${loggedInUser}"/>">Timeline</a> | <a href="<@spring.url "/logout"/>">Logout</a>
        <#else>
            <a href="<@spring.url "/userList"/>">Login</a>
        </#if>
    </div>
<#if notice?has_content >
<h1>${notice}</h1>
</#if>