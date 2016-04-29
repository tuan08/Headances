<div>
  <h6>Classified Content</h6> 
  <hr/>
 
  <c:forEach var="tag" items="${filterOptions.contentTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setContentType=${tag}">
          <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
        </a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear'}">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Content Language</h6> 
  <hr/>
  <c:forEach var="tag" items="${filterOptions.languageTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setLanguage=${tag}">
          <fmt:message key="${filterOptions.getLabel(tag)}"/>
        </a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear'}">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Page Types</h6> 
  <hr/>
  <c:forEach var="tag" items="${filterOptions.pageTypeTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setPageType=${tag}">
          <fmt:message key="${filterOptions.getLabel(tag)}"/>
        </a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear' }">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Tags</h6> 
  <hr/>
  <c:forEach var="tag" items="${filterOptions.otherTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setOtherTag=${tag}">
          <fmt:message key="${filterOptions.getLabel(tag)}"/>
        </a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear' }">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Page Deep</h6> 
  <hr/>
  <c:forEach var="deep" items="${filterOptions.deeps}" varStatus="loop">
    <c:choose>
      <c:when test="${query.deep == deep}">
      <span><fmt:message key="filter.lb.deep.${deep}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setDeep=${deep}"><fmt:message key="filter.lb.deep.${deep}"/></a>
      </c:otherwise>
    </c:choose>
    <c:if test="${deep != '-1'}">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Predict Tag</h6> 
  <hr/>
  <c:forEach var="tag" items="${filterOptions.predictTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setPredictTag=${tag}"><fmt:message key="${filterOptions.getLabel(tag)}"/></a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear'}">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Suspect Tag Error</h6> 
  <hr/>
  <c:forEach var="tag" items="${filterOptions.suspectTags}" varStatus="loop">
    <c:choose>
      <c:when test="${query.hasTag(tag)}">
        <span><fmt:message key="${filterOptions.getLabel(tag)}"/></span>
      </c:when>
      <c:otherwise>
        <a href="${searchURL}&setSuspectTag=${tag}"><fmt:message key="${filterOptions.getLabel(tag)}"/></a>
      </c:otherwise>
    </c:choose>
    <c:if test="${tag != 'clear'}">
      -
    </c:if> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Site</h6> 
  <hr/>
  <form:form modelAttribute="query" action="search" method="get">
    <form:hidden path="query" />
    <form:hidden path="index" value="${query.index}"/>
    <form:hidden path="tags" value="${query.tags}"/>
    <form:hidden path="deep" />
    <form:hidden path="page" value="1"/>
    <form:hidden path="title" value="${query.title}"/>
    <form:hidden path="description" value="${query.description}"/>
    <form:hidden path="content" value="${query.content}"/>

    <input name="setSite" value="${query.site}"/>
    <button style="height: 25px;">Update</button>
  </form:form>
</div>

<hr class="space"/>

<div>
  <h6>Indices</h6> 
  <hr/>
  <c:forEach var="index" items="${indices}">
    <a href="${searchURL}&setIndex=${index}">${index}</a> 
  </c:forEach>
</div>

<hr class="space"/>

<div>
  <h6>Misc</h6> 
  <hr/>
  <a href="${searchURL}&clearCache=true">Clear Cache</a> 
</div>

<div>
  <h6>Rich UI</h6> 
  <hr/>
  <a href="/jsapp/headvances/index.html">JS APP UI</a> 
</div>

<%
/*
<div>
  <h6>Dates</h6> 
  <hr/>
  <div>
    Created:
    <!-- Slider -->
    <div id="CreatedDateSlider"></div>
  </div>

  <div>
    Updated:
    <!-- Slider -->
    <div id="UpdatedDateSlider"></div>
  </div>
  <script type="text/javascript">
    $(function(){
      // Slider
      $('#CreatedDateSlider').slider({
        range: true,
        values: [0, 50]
      });
      $('#UpdatedDateSlider').slider({
        range: true,
        values: [0, 100]
      });
    });
  </script>
</div>

<hr class="space"/>


<div>
  <h6>Languages</h6> 
  <hr/>
  <div><a href="?locale=en">en</a> |  <a href="?locale=vn">vn</a></div>
</div>
*/
%>
