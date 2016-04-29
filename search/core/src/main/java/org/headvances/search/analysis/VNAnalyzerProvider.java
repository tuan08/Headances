package org.headvances.search.analysis;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNAnalyzerProvider extends AbstractIndexAnalyzerProvider<VNAnalyzer> {

	private final VNAnalyzer vnAnalyzer;

	@Inject 
	public VNAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, 
			                      @Assisted String name, @Assisted Settings settings) {
		super(index, indexSettings, name, settings);
		this.vnAnalyzer = new VNAnalyzer();
	}

	@Override 
	public VNAnalyzer get() { return this.vnAnalyzer; }
}
