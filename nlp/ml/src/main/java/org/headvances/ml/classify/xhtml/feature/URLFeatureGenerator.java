package org.headvances.ml.classify.xhtml.feature;

import java.util.List;
import java.util.Map;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.FeatureUtil;
import org.headvances.util.html.URLNormalizer;

public class URLFeatureGenerator implements FeatureGenerator<TDocument> {
	private PatternFeatureGenerator patternGenerator = new PatternFeatureGenerator() ;
	
	public void generate(FeatureHolder holder, TDocument doc) {
		String url = doc.getUrl().toLowerCase();
		URLNormalizer normalizer = new URLNormalizer(url);
		generateHostFeatures(holder, normalizer);
		generatePathFeatures(holder, normalizer);
		generateParamsFeatures(holder, normalizer);
	}

	private void generateHostFeatures(FeatureHolder holder, URLNormalizer urlnorm) {
		holder.add("url:host", urlnorm.getSources());
		patternGenerator.generate(holder, "url:pattern", urlnorm.getHost()) ;
	}

	private void generatePathFeatures(FeatureHolder holder, URLNormalizer urlnorm) {
		List<String> segs = urlnorm.getPathSegments();
		if(segs.size() == 0) return;
		for(String seg : segs){
			int idx = seg.lastIndexOf('.') ;
			if(idx > 0) seg = seg.substring(0, idx) ;
			seg = FeatureUtil.normalize(seg);
		  holder.add("url:path:token", seg) ;
		  patternGenerator.generate(holder, "url:pattern", seg) ;
		}
	}

	private void generateParamsFeatures(FeatureHolder holder, URLNormalizer urlnorm) {
		Map<String, String[]> params = urlnorm.getParams();
		if (params == null) return;
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();
			key = FeatureUtil.normalize(key);
			holder.add("url:params:name", key);
			patternGenerator.generate(holder, "url:pattern", key) ;
		}
	}
}