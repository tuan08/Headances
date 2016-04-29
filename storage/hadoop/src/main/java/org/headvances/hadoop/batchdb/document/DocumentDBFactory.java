package org.headvances.hadoop.batchdb.document;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.hadoop.batchdb.ColumnDefinition;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.DatabaseConfiguration;
import org.headvances.hadoop.batchdb.RowIdPartitioner;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentDBFactory {
	static public Database getDatabase(Configuration conf, String loc, String[] entity, int partition) throws Exception {
		ColumnDefinition[] columnDefinition = new ColumnDefinition[2 + entity.length] ;
		columnDefinition[0]= new ColumnDefinition(DocumentMapper.META_COLUMN) ;
		columnDefinition[1]= new ColumnDefinition(DocumentMapper.DATA_COLUMN) ;
		for(int i = 0; i < entity.length; i++) {
			columnDefinition[i + 2]= new ColumnDefinition(entity[i]) ;
		}
		DatabaseConfiguration dbconfiguration = 
			new DatabaseConfiguration(columnDefinition, new RowIdPartitioner.RowIdHashPartioner(partition, ":")) ;
		dbconfiguration.setHadoopConfiguration(conf) ;
		return Database.getDatabase(loc, dbconfiguration) ;
	}
	
	static public Database getHtmlDocumentDatabase(Configuration conf, String loc, int partition) throws Exception {
		return getDatabase(conf, loc, HtmlDocumentUtil.ENTITIES, partition) ;
	}
}