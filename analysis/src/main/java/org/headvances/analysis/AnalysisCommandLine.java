package org.headvances.analysis;

import org.headvances.analysis.integration.AnalysisInputGateway;
import org.headvances.data.Document;
import org.headvances.hadoop.batchdb.document.DocumentIterator;
import org.headvances.util.CommandParser;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
/**
 * @author Tuan Nguyen
 */
public class AnalysisCommandLine {
	static GenericApplicationContext createGenericApplicationContext() throws Exception  {
		final GenericApplicationContext context = new GenericApplicationContext() ;
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context) ;
		String[] res = {
				"classpath:/META-INF/analysis-server.xml",
				"classpath:/META-INF/connection-factory-activemq.xml",
				"classpath:/META-INF/analysis-integration.xml"
		} ;
		xmlReader.loadBeanDefinitions(res) ;
		context.refresh() ;
		context.registerShutdownHook() ;
		return context ;
	}

	static void input(AnalysisInputGateway gw, DocumentIterator itr) throws Exception {
		Document doc = null ;
		while((doc = itr.next()) != null) gw.send(doc) ;
		itr.close() ;
	}

	public static void main(String[] args) throws Exception {
		if(args.length == 0 || args == null) {
			args = new String[] {
					//"-db",   "data/labeleddb",
					"-data", "testdata",
					"-output", "report"
			};
		}
		CommandParser command = new CommandParser("analysis:") ;
		command.addOption("db",   true, "The input db location") ;
		command.addOption("data", true, "The input data location") ;
		command.addOption("output", true, "The output adapter: console,report,file,index") ;
		command.addOption("threads", true, "The frequency of the thread analyzer") ;
		if(!command.parse(args)) return ;
		command.printHelp() ;

		String db = command.getOption("db", null) ;
		String dataLoc = command.getOption("data", null) ;
		String outputs = command.getOption("output", "report") ;
		for(String sel : outputs.split(",")) {
			System.setProperty("analysis.output." + sel, "true") ;
		}
		String threads = command.getOption("threads", "1") ;
		System.setProperty("analysis.number-of-thread", threads) ;
		
		final GenericApplicationContext context = createGenericApplicationContext() ;
		AnalysisInputGateway gw = context.getBean(AnalysisInputGateway.class) ;
		if(db != null) input(gw, DocumentIterator.getIterator(db, "db")) ;
		if(dataLoc != null) input(gw, DocumentIterator.getIterator(dataLoc, "file")) ;
		Thread.sleep(1000) ;
		context.close() ;
	}
}
