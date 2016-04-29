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
      <h1>UI Config</h1>
      <div class="span-12 last">  
        <form:form modelAttribute="uiconfig" action="/search/uiconfig" method="post">
          <fieldset>    
            <legend>Fields</legend>
            <p>
              <form:label for="showTag" path="showTag" cssErrorClass="error">Show Tag</form:label><br/>
              <form:checkbox path="showTag" /> <form:errors path="showTag" />      
            </p>
            <p>
              <form:label for="showLabel" path="showLabel" cssErrorClass="error">Show Label</form:label><br/>
              <form:checkbox path="showLabel" /> <form:errors path="showLabel" />      
            </p>
            <p>
              <form:label for="showToolLabel" path="showToolLabel" cssErrorClass="error">Show Label Tool</form:label><br/>
              <form:checkbox path="showToolLabel" /> <form:errors path="showToolLabel" />      
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
