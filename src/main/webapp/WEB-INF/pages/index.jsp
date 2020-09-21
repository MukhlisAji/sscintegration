<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
    <head>
        <title>Hello Spring Security</title>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="/css/main.css" th:href="@{/css/main.css}" />
    </head>
    <body>
        <div th:fragment="logout" class="logout" sec:authorize="isAuthenticated()">		
            Logged in user: <span sec:authentication="name"></span> |					
            Roles: <span sec:authentication="principal.authorities"></span>				
            <div>
                <form action="#" th:action="@{/logout}" method="post">					
                    <input type="submit" value="Logout" />
                </form>
            </div>
        </div>
        <h1>Hello Spring Security</h1>
        <p>This is an unsecured page, but you can access the secured pages after authenticating.</p>
        <ul>
            <li>Go to the <a href="/user/index" th:href="@{/user/index}">secured pages</a></li>
        </ul>
    </body>
</html>