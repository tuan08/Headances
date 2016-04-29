package org.headvances.crawler.cluster.task;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.CrawlerFetcher;
import org.headvances.crawler.master.CrawlerMaster;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ClusterServiceTask  extends ClusterTask<TaskResult>  {
  private static final long serialVersionUID = 435649253875629113L;

  static public enum Command { START, STOP, SHUTDOWN }
	
	private Command command ;
	private long delayTime = 0;
	
	public ClusterServiceTask(Command command) {
		this.command = command ;
	}
	
	public ClusterServiceTask(Command command, long delayTime) {
		this.command = command ;
		this.delayTime = delayTime ;
	}
	
	public ClusterServiceTask(long waitTime) {
		this.delayTime = waitTime ;
	}
	
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		ApplicationContext ctx = getApplicationContext() ;
		if(ctx == null) return result ;
		if(command == Command.SHUTDOWN) shutdown(ctx, result) ;
		else if(command == Command.START) start(ctx, result) ;
		else if(command == Command.STOP) stop(ctx, result) ;
		return result ;
	}
	
	private void shutdown(ApplicationContext ctx, TaskResult result) {
		if(ctx instanceof DisposableBean) {
			final DisposableBean disposable = (DisposableBean) ctx ;
			new Thread() {
				public void run() {
					if(delayTime > 0) {
						try {
							Thread.sleep(delayTime) ;
							disposable.destroy() ;
							System.exit(0) ;
						} catch (Exception e) {
	            e.printStackTrace();
            }
					}
				}
			}.start() ;
		}
	}
	
	private void start(ApplicationContext ctx, TaskResult result) {
		if(ctx == CrawlerMaster.getApplicationContext()) {
			CrawlerMaster master = ctx.getBean(CrawlerMaster.class) ;
			master.start() ;
			result.addMessage("Start Crawler Master") ;
		}
		if(ctx == CrawlerFetcher.getApplicationContext()) {
			CrawlerFetcher fetcher = ctx.getBean(CrawlerFetcher.class) ;
			fetcher.start() ;
			result.addMessage("Start Crawler Fectcher") ;
		}
	}
	
	private void stop(ApplicationContext ctx, TaskResult result) {
		if(ctx == CrawlerMaster.getApplicationContext()) {
			CrawlerMaster master = ctx.getBean(CrawlerMaster.class) ;
			master.stop() ;
			Member member = this.getMember(ctx) ;
			result.addMessage("Stop Crawler Master on " + member.toString()) ;
		}
		ctx = CrawlerFetcher.getApplicationContext() ;
		if(ctx == CrawlerFetcher.getApplicationContext()) {
			CrawlerFetcher fetcher = ctx.getBean(CrawlerFetcher.class) ;
			fetcher.stop() ;
			Member member = getMember(ctx) ;
			result.addMessage("Stop Crawler Fectcher on  " + member.toString()) ;
		}
	}
}