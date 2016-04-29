package org.headvances.swingui.component;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.headvances.util.jvm.AppInfo;
import org.headvances.util.jvm.AppThreadInfo;
import org.headvances.util.jvm.GarbageCollectorInfo;
import org.headvances.util.jvm.JVMInfo;
import org.headvances.util.jvm.MemoryInfo;
/**
 * $Author: Tuan Nguyen$
 **/
public class JVMInfoPanel extends UpdatableJPanel {
	private JVMInfo jvmInfo;

	private TableView memoryInfo;
	private TableView garbageCollectionInfo;
	private ThreadInfoTab threadInfo;

	public JVMInfoPanel() throws Exception {
		add(createBodyPane(), BorderLayout.CENTER);
	}

	protected JComponent createBodyPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		this.memoryInfo = new TableView();
		tabbedPane.add("Memory Info", new JScrollPane(memoryInfo));

		this.garbageCollectionInfo = new TableView();
		tabbedPane.add("Garbage Collection Info", new JScrollPane(garbageCollectionInfo));

		this.threadInfo = new ThreadInfoTab();
		tabbedPane.add("Thread Info", threadInfo );
		
		return tabbedPane;
	}

	protected void update() throws Exception {
		jvmInfo = getJVMInfo() ;

		memoryInfoPopulate();
		garbageCollectionInfoPopulate();
		threadInfo.populate(jvmInfo);
	}

	protected JVMInfo getJVMInfo() throws Exception { return new JVMInfo() ; }

	public void memoryInfoPopulate() {
		if (jvmInfo == null) return;
		MemoryInfo mInfo = jvmInfo.getMemoryInfo();
		String[] header = { "Name", "Value" };
		Object[][] data = new Object[][] {
				{ "Init", mInfo.getInit()},
				{ "Use", mInfo.getUse()},
				{ "Committed", mInfo.getCommitted()},
				{ "Max", mInfo.getMax()}
		} ;
		memoryInfo.setData(header, data);
	}

	public void garbageCollectionInfoPopulate() {
		if (jvmInfo == null) return;
		String[] header = { "Name", "Collection Count", "Collection Time", "Pool Names" };
		Object[][] data = new Object[jvmInfo.getGarbageCollectorInfo().size()][];
		ArrayList<GarbageCollectorInfo> gInfos = jvmInfo.getGarbageCollectorInfo();
		for (int i = 0; i < gInfos.size(); i++) {
			GarbageCollectorInfo gInfo = gInfos.get(i);
			data[i] = new Object[] { 
					gInfo.getName(),
					gInfo.getCollectionCount(),
					gInfo.getCollectionTime(), 
					gInfo.getPoolNames() 
			};
		}
		garbageCollectionInfo.setData(header, data);
	}

	static public class ThreadInfoTab extends JPanel{
	  private JEditorPane stackTraceInfo;
	  private JTablePagination detailThreadInfo;
	  
	  private AppInfo appInfo;
	  private ArrayList<AppThreadInfo> threadInfos;
	  
	  public ThreadInfoTab(){
	    setLayout(new BorderLayout());
	    this.stackTraceInfo = new JEditorPane() ;
	    this.stackTraceInfo.setContentType("plain/text");
	    stackTraceInfo.setEditable(false);

	    this.detailThreadInfo =  new JTablePagination();
	    detailThreadInfo.getJTable().addMouseListener(new MouseAdapter() {
	      public void mouseClicked(MouseEvent e) {
	        int id = detailThreadInfo.getModelSelectedRow();
	        printStackTrace(threadInfos.get(id));
	      }
	    });

	    JSplitPane splitPane = 
	      new JSplitPane(JSplitPane.VERTICAL_SPLIT, detailThreadInfo, new JScrollPane(stackTraceInfo));
	    splitPane.setOneTouchExpandable(true);
	    splitPane.setDividerLocation(250);

	    add(splitPane, BorderLayout.CENTER);
	  }
	  
	  public void populate(JVMInfo jvmInfo) {
	    if (jvmInfo == null) return;
	    appInfo = jvmInfo.getThreadInfo();

	    String[] detailHeader = {
	        "Name", "ID", "Block count", "Block time", "Waited count", 
	        "Waited time", "State", "CPU time", "User time"
	    };
	    threadInfos = appInfo.getAllThreadInfo();
	    Object[][] detailData = new Object[threadInfos.size()][];
	    for (int i = 0; i < threadInfos.size(); i++) {
	      AppThreadInfo apptInfo = threadInfos.get(i);
	      detailData[i] = new Object[] { 
	          apptInfo.getThreadName(),
	          apptInfo.getThreadId(),
	          apptInfo.getThreadBlockCount(),
	          apptInfo.getThreadBlockTime(), 
	          apptInfo.getThreadWaitedCount(),
	          apptInfo.getThreadWaitedTime(),
	          apptInfo.getThreadState(),
	          apptInfo.getThreadCPUTime(),
	          apptInfo.getThreadUserTime()
	      };
	    }
	    printStackTrace(threadInfos.get(0));
	    detailThreadInfo.setData(detailHeader, detailData, 50);
	  }
	  
	  private void printStackTrace(AppThreadInfo tinfo){
	    String stackTrace = "Stack Trace of thread: " + tinfo.getThreadName() + "\n";
	    stackTrace +=  tinfo.getThreadStackTrace();
	    stackTraceInfo.setText(stackTrace);
	  }
	  
	}
}