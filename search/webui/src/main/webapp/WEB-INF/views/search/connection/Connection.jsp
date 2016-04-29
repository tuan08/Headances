<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
  <head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>Search Connection</title>
    <link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
    <!--[if lt IE 8]>
      <link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
    <![endif]-->
  </head>  

  <body>
    <div class="container">
      <h1>Search Connection</h1>
      <div class="span-12 last">  
        <form:form modelAttribute="connection" action="/search/connection" method="post">
          <fieldset>    
            <legend>Connection Fields</legend>
            <p>
              <form:label for="index" path="index" cssErrorClass="error">Index</form:label><br/>
              <form:input path="index" style="width: 400px" /> <form:errors path="index" />      
            </p>
            <p>
              <form:label for="type" path="type" cssErrorClass="error">Type</form:label><br/>
              <form:input path="type" style="width: 400px" /> <form:errors path="type" />      
            </p>
            <p>
              <form:label for="urls" path="urls" cssErrorClass="error">Url</form:label><br/>
              <form:input path="urls" style="width: 400px" /> <form:errors path="urls" />      
            </p>
            <p>  
              <input type="submit" />
            </p>
          </fieldset>
        </form:form>
      </div>
    </div>
  </body>
</html>
