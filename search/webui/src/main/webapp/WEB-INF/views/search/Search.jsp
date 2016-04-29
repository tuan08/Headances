<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
                      "http://www.w3.org/TR/html4/strict.dtd"> 

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/search" var="searchURL">
  <c:param name="index" value="${query.index}"/>
  <c:param name="tags" value="${query.tags}"/>
  <c:param name="deep" value="${query.deep}"/>
  <c:param name="site" value="${query.site}"/>
  <c:param name="page" value="${query.page}"/>
  <c:param name="query" value="${query.query}"/>
  <c:param name="title" value="${query.title}"/>
  <c:param name="description" value="${query.description}"/>
  <c:param name="content" value="${query.content}"/>
</c:url>

<html>
  <head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title><fmt:message key="welcome.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/jquery/css/headvances/jquery-ui-1.8.15.custom.css" />" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection" />
    <link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print" />
    <!--[if lt IE 8]>
      <link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection" />
    <![endif]-->

    <script type="text/javascript" 
            src="<c:url value="/resources/jquery/js/jquery-1.6.2.min.js" />"></script>
    <script type="text/javascript" 
            src="<c:url value="/resources/jquery/js/jquery-ui-1.8.15.custom.min.js" />"></script>
  </head>

  <body> 
    <div class="container"> 
      <%@include file="SearchBanner.jsp" %>
      <hr />

      <div class="span-4"> 
        <%@include file="SearchFilters.jsp" %>
      </div> 

      <div class="span-19 prepend-1 last"> 
        <div id="tabs">
          <ul>
            <li><a style="padding: 3px 12px" href="#SearchResult">Search Result</a></li>
            <li><a style="padding: 3px 12px" href="#QueryInfo">Query Info</a></li>
            <li><a style="padding: 3px 12px" href="#ResponseInfo">Response Info</a></li>
          </ul>
          <div id="SearchResult">
            <%@include file="SearchResult.jsp" %>
          </div>
          <div id="QueryInfo">
            <pre>
              ${queryJson}
            </pre>
          </div>
          <div id="ResponseInfo">
            <%@include file="ResponseInfo.jsp" %>
          </div>
          <script type="text/javascript">
            $(function(){ $('#tabs').tabs(); });
          </script>
        </div>
      </div> 

      <hr class="space" /> 

      <div>
        <hr /> 
        <h2 class="alt">
          Thank you for trying our data mining system!!!
        </h2> 
        <hr /> 
      </div>
    </div> 
  </body>
</html>
