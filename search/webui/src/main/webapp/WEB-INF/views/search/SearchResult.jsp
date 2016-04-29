<script type="text/javascript">
  function label(lb, did, emLabelId) {
    $.ajax({
      url: 'search/label',
      data: ({docId : did, label : lb}),
      success: function(data) {
        if(data == 'ok') {
          $("#" + emLabelId).text(lb) ;
        }
      }
    });
  }
</script>

<div>
  <strong>Search Result:</strong> ${iterator.available} in 0.5s
</div>
<hr class="space"/>

<div>
  <c:forEach var="record" items="${records}" varStatus="loop">
    <div>
      <div><a href="${record.url}">${record.title}</a></div>
      <div>
        <span class="small">
          ${record.displayUrl} - 
          <a href="search/cache?id=${record.id}"><em>cache</em></a>
        </span>
      </div>
      <div>${record.matchHighlight}</div> 
      <c:if test="${uiconfig.showTag}">
        <div style="font-size: 0.8em">
          <strong>Tag:</strong>
          <c:forEach var="tag" items="${record.tags}">
            <em>${tag}</em>
          </c:forEach>
        </div>
      </c:if>
      <c:if test="${uiconfig.showLabel}">
        <c:set var="emLabelId" value="emLabel${loop.index}" scope="page" />
        <div style="font-size: 0.8em">
          <strong>Label:</strong>
          <em id="${emLabelId}">
            <c:forEach var="label" items="${record.labels}">
              ${label}
            </c:forEach>
          </em>
        </div>

        <div style="font-size: 0.8em">
          <strong>Set Label:</strong>
          Article[
          <a href="javascript:label('content:article:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:article:list', '${record.id}', '${emLabelId}')">list</a>
          ] - 
          Blog[
          <a href="javascript:label('content:blog:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:blog:list', '${record.id}', '${emLabelId}')">list</a>
          ] -
          Forum[
          <a href="javascript:label('content:forum:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:forum:list', '${record.id}', '${emLabelId}')">list</a>
          ] -
          Product[
          <a href="javascript:label('content:product:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:product:list', '${record.id}', '${emLabelId}')">list</a>
          ] -
          Classified[
          <a href="javascript:label('content:classified:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:classified:list', '${record.id}', '${emLabelId}')">list</a>
          ] -
          Job[
          <a href="javascript:label('content:job:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:job:list', '${record.id}', '${emLabelId}')">list</a>
          ] - 
          Other[
          <a href="javascript:label('content:other:detail', '${record.id}', '${emLabelId}')">detail</a>
          <a href="javascript:label('content:other:list', '${record.id}', '${emLabelId}')">list</a>
          ] - 

          <a href="javascript:label('content:ignore', '${record.id}', '${emLabelId}')">Ignore</a> -
          <a href="javascript:label('content:error', '${record.id}', '${emLabelId}')">Error</a>
        </div>
      </c:if>
    </div>
    <hr class="space"/>
  </c:forEach>
</div>

<div>
  <div style="float: right">
    <a href="${searchURL}&setPage=${iterator.previousPage}">prev</a>
    <c:forEach var="i" begin="${iterator.subRangeFrom}" end="${iterator.subRangeTo}" step="1">
      <c:choose>
        <c:when test="${query.page == i}">
          <span>${i}</span>
        </c:when>
        <c:otherwise>
          <a style="padding: 2px 3px" href="${searchURL}&setPage=${i}">${i}</a>
        </c:otherwise>
       </c:choose>
    </c:forEach>
    <a href="${searchURL}&setPage=${iterator.nextPage}">next</a>
  </div>
  <br style="clear: both" />
</div>
