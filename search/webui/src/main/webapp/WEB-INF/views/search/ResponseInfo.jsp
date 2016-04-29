<c:set var="resInfo" value="${iterator.indexInfo}" scope="page"/>
<table>
  <tr>
    <td>Total Hits</td>
    <td>${resInfo.totalHit}</td>
    <td>Number of the documents</td>
  </tr>
  <tr>
    <td>Search Time</td>
    <td>${resInfo.tookTime}</td>
    <td>Time to search in ms</td>
  </tr>
  <tr>
    <td>Shards</td>
    <td>${resInfo.totalShards}</td>
    <td>Number of shards</td>
  </tr>
  <tr>
    <td>Successful Shards</td>
    <td>${resInfo.successfulShards}</td>
    <td>Number of the successful shards</td>
  </tr>
  <tr>
    <td>Failed Shards</td>
    <td>${resInfo.failedShards}</td>
    <td>${resInfo.shardFailures}</td>
  </tr>
</table>
