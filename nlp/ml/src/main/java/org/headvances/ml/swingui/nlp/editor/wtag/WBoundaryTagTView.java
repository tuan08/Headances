package org.headvances.ml.swingui.nlp.editor.wtag;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.headvances.ml.nlp.SuggestTag;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.util.text.StringUtil;

public class WBoundaryTagTView extends JPanel {
	private WTagDocument doc ;
	public WBoundaryTagTView(WTagDocument doc) {
		this.doc = doc ;
		Object columns[] = { "Token", "Tag", "Suggest Tag" };
		IToken[] token = new ElementSelector.TokenElementSelector().selectToken(doc) ;
		Object[][] rows = new Object[token.length][columns.length] ;
		for(int i = 0; i < token.length; i++) {
			rows[i][0] = token[i].getOriginalForm() ;
			WTagBoundaryTag btag = token[i].getFirstTagType(WTagBoundaryTag.class) ;
			if(btag != null) {
				rows[i][1] =  StringUtil.joinStringArray(btag.getFeatures()) ;
			} else {
				rows[i][1] =  "" ;
			}
			
			SuggestTag stag = token[i].getFirstTagType(SuggestTag.class) ;
			if(stag != null) {
				rows[i][2] =  StringUtil.joinStringArray(stag.getFeatures()) ;
			} else {
				rows[i][2] =  "" ;
			}
		}
		setLayout(new BorderLayout()) ;
		JTable table = new JTable(rows, columns);
    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, BorderLayout.CENTER);
	}
}
