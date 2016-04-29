<div>
  <div class="span-4"> 
    <h2>
      <fmt:message key="welcome.title"/>
    </h2> 
  </div>

  <div class="span-10 prepend-1 last"> 
    <hr class="space" />
    <form:form modelAttribute="query" action="search" method="get" style="width: 525px; margin: 0px auto">
      <form:input  path="query" style="width: 450px; height: 25px;" />
      <form:hidden path="index" value="${query.index}"/>
      <form:hidden path="tags" value="${query.tags}"/>
      <form:hidden path="page" value="1"/>
      <button style="height: 25px;">Search</button>
      <div>
        <label>Title</label> <form:checkbox path="title"/>
        <label>Description</label> <form:checkbox path="description"/>
        <label>Content</label> <form:checkbox path="content"/>
      </div>
    </form:form>
  </div>
</div>
