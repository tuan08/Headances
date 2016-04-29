package org.headvances.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class IndexInfo {
	private long   totalHit ;
	private long   tookTime ;
	private int    totalShards ;
	private int    successfulShards ;
	private int    failedShards ;
	private String shardFailures ;
	
	public IndexInfo(SearchResponse response) {
		this.totalHit = response.getHits().getTotalHits() ;
		this.tookTime = response.getTookInMillis() ;
		this.totalShards = response.getTotalShards() ;
		this.successfulShards = response.getSuccessfulShards() ;
		this.failedShards = response.getFailedShards() ;
		ShardSearchFailure[] fails = response.getShardFailures() ;
		if(fails != null) {
			StringBuilder b = new StringBuilder() ;
			for(ShardSearchFailure sel : fails) {
				b.append(sel.toString()).append("\n") ;
			}
			this.shardFailures = b.toString() ;
		}
	}

	public long getTotalHit() { return totalHit; }

	public long getTookTime() { return tookTime; }

	public int getTotalShards() { return totalShards; }

	public int getSuccessfulShards() { return successfulShards; }

	public int getFailedShards() { return failedShards; }

	public String getShardFailures() { return shardFailures; }
}
