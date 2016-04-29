package org.headvances.search.rest.webmonitor;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.search.ESClientService;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WebMonitor {
	private String entity;
	private String description ;
	
	private ExecuteInfo executeInfo ;
	
	private Map<String, ContentDistribution> contentDistribution = new HashMap<String, ContentDistribution>() ;
	private Map<String, OpinionDistribution> opinionContentDistribution = new HashMap<String, OpinionDistribution>() ;
	private Map<String, OpinionDistribution> opinionCategoryDistribution = new HashMap<String, OpinionDistribution>() ;
	
	private OpinionHolder opinions = new OpinionHolder() ;
	
	private WebMonitorExecutor executor ;
	
	public String getEntity() { return entity; }
	public void setEntity(String entity) { this.entity = entity; }
	
	public String getDescription() { return description; }
	public void   setDescription(String description) { this.description = description; }

	public ExecuteInfo getExecuteInfo() { return executeInfo; }
	public void setExecuteInfo(ExecuteInfo executeInfo) { this.executeInfo = executeInfo; }

	public void addContentDistribution(String name, int count) {
		ContentDistribution cd = contentDistribution.get(name) ;
		if(cd == null) {
			cd = new ContentDistribution() ;
			cd.setName(name) ;
			contentDistribution.put(name, cd) ;
		}
		cd.addCount(count) ;
	}
	
	public ContentDistribution[] getContentDistribution() {
		ContentDistribution[] array = new ContentDistribution[contentDistribution.size()] ;
		array =  contentDistribution.values().toArray(array);
		return array ;
	}
	
	public void addOpinionContentDistribution(String contentType, Opinion opinion, boolean duplicate) {
		OpinionDistribution od = this.opinionContentDistribution.get(contentType) ;
		if(od == null) {
			od = new OpinionDistribution() ;
			od.setName(contentType) ;
			opinionContentDistribution.put(contentType, od) ;
		}
		od.addOpinion(opinion, duplicate);
	}
	
	public OpinionDistribution[] getOpinionContentDistribution() {
		OpinionDistribution[] array = new OpinionDistribution[opinionContentDistribution.size()] ;
		array =  opinionContentDistribution.values().toArray(array);
		return array ;
	}
	
	public void addOpinionCategoryDistribution(Opinion opinion, boolean duplicate) {
		String [] category = opinion.getCategory() ;
		for(int i = 0; i < category.length; i++) {
			OpinionDistribution od = this.opinionCategoryDistribution.get(category[i]) ;
			if(od == null) {
				od = new OpinionDistribution() ;
				od.setName(category[i]) ;
				opinionCategoryDistribution.put(category[i], od) ;
			}
			od.addOpinion(opinion, duplicate);
		}
	}
	
	public void addOpinion(String contentType, Opinion opinion) {
		if("i".equals(opinion.getLabel())) return ;
		if("3".equals(opinion.getLabel())) return ;
		if("-3".equals(opinion.getLabel())) return ;
		opinion.setOpinion(opinion.getOpinion() + " - Rank " + opinion.getLabel()) ;
		opinion.addTag(contentType) ;
		boolean added = opinions.add(opinion, false) ;
		addOpinionContentDistribution(contentType, opinion, !added) ;
		addOpinionCategoryDistribution(opinion, !added) ;
	}
	
	public OpinionDistribution[] getOpinionCategoryDistribution() {
		OpinionDistribution[] array = new OpinionDistribution[opinionCategoryDistribution.size()] ;
		array =  opinionCategoryDistribution.values().toArray(array);
		return array ;
	}
	
	@JsonIgnore
	public OpinionHolder getOpinions() { return this.opinions ; }
	
	synchronized public void update(ESClientService service) throws Exception {
		if(executor != null && executor.isAlive()) return  ;
		contentDistribution.clear() ;
		opinionCategoryDistribution.clear();
		opinionContentDistribution.clear() ;
		opinions.clear() ;
		executor = new WebMonitorExecutor(service, this) ;
		executor.start() ;
	}
	
	static public class ContentDistribution {
		private String name  ;
		private int    count ;
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		public void addCount(int count) { this.count += count ; }
		public int getCount() { return count; }
		public void setCount(int count) { this.count = count; }
	}
	
	static public class OpinionDistribution {
		private String name  ;
		private OpinionRank rankMinus2 = new OpinionRank("rank -2");
		private OpinionRank rankMinus1 = new OpinionRank("rank -1");
		private OpinionRank rankPlus0  = new OpinionRank("rank +0");
		private OpinionRank rankPlus1  = new OpinionRank("rank +1");
		private OpinionRank rankPlus2  = new OpinionRank("rank +2");

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		public OpinionRank getRankMinus2() { return rankMinus2; }
		public void setRankMinus2(OpinionRank rankMinus2) { this.rankMinus2 = rankMinus2; }
		
		public OpinionRank getRankMinus1() { return rankMinus1; }
		public void setRankMinus1(OpinionRank rankMinus1) { this.rankMinus1 = rankMinus1; }
		
		public OpinionRank getRankPlus0() { return rankPlus0; }
		public void setRankPlus0(OpinionRank rankPlus0) { this.rankPlus0 = rankPlus0; }
		
		public OpinionRank getRankPlus1() { return rankPlus1; }
		public void setRankPlus1(OpinionRank rankPlus1) { this.rankPlus1 = rankPlus1; }
		
		public OpinionRank getRankPlus2() { return rankPlus2; }
		public void setRankPlus2(OpinionRank rankPlus2) { this.rankPlus2 = rankPlus2; }
	
		public void addOpinion(Opinion opinion, boolean duplicate) {
			OpinionRank orank = null ;
			String rank = Byte.toString(opinion.getRank()) ;
			if("-2".equals(rank)) orank = this.rankMinus2 ;
			else if("-1".equals(rank)) orank = this.rankMinus1 ;
			else if("0".equals(rank)) orank = this.rankPlus0 ;
			else if("1".equals(rank)) orank = this.rankPlus1 ;
			else if("2".equals(rank)) orank = this.rankPlus2 ;
			else return ;
			orank.addCount(1, duplicate ? 0 : 1) ;
		}
	}
	
	static public class OpinionRank {
		private String name  ;
		private int    count ;
		private int    uniqueCount ;
		
		public OpinionRank() {} 
		
		public OpinionRank(String name) { this.name = name ; }
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		public int getCount() { return count; }
		public void setCount(int count) { this.count = count; }
		
		public int getUniqueCount() { return uniqueCount; }
		public void setUniqueCount(int uniqueCount) { this.uniqueCount = uniqueCount; }
	
		public void addCount(int count, int uniqueCount) {
			this.count += count ;
			this.uniqueCount += uniqueCount ;
		}
	}
}