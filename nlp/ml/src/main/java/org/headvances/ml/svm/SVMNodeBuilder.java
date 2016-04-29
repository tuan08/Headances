package org.headvances.ml.svm;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm_node;

import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SVMNodeBuilder {
	private FeatureDictionary	dictionary ;
	
	public SVMNodeBuilder(FeatureDictionary	dictionary) {
		this.dictionary = dictionary ;
	}
	
	public svm_node[] getSVMVector(Feature[] features) {
		return getBoolVector(features);
	}
	
	public svm_node[] getBoolVector(Feature[] features) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : features) {
			Feature feature = dictionary.getFeature(rawfeature.getFeature());
			if (feature != null) {
				svm_node node = new svm_node();
				node.index = feature.getId();
				node.value = 1 ;
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}
	
	public svm_node[] getTFIDFVector(Feature[] features) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : features) {
			Feature feature = dictionary.getFeature(rawfeature.getFeature());
			if (feature != null) {
				svm_node node = new svm_node();
				node.index = feature.getId();
				node.value = rawfeature.getTermFrequency() * feature.getWeight();
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}

	public svm_node[] getTFIDFVector(Feature[] rawfeatures, String[] featuregroup, boolean possitive) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : rawfeatures) {
			Feature feature = dictionary.getFeature(rawfeature.getFeature());
			if(feature != null && StringUtil.isContain(feature.getFeature(), featuregroup, possitive)) {
				svm_node node = new svm_node();
				node.index = feature.getId();
				node.value = rawfeature.getTermFrequency() * feature.getWeight();
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}

	public svm_node[] getTFVector(Feature[] rawfeatures) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for(Feature rawfeature : rawfeatures) {
			Feature feature = dictionary.getFeature(rawfeature.getFeature());
			if(feature != null) {
				svm_node node = new svm_node();
				node.index = feature.getId();
				node.value = rawfeature.getWeight();
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}

	public svm_node[] getSparseTFVector(Feature[] rawfeatures) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : rawfeatures) {
			Feature[] features = dictionary.getFeatures();
			for (int i = 0; i < features.length; i++) {
				svm_node node = new svm_node();
				if (rawfeature.getFeature().equals(features[i].getFeature())) {
					node.index = features[i].getId();
					node.value = rawfeature.getWeight();
				} else {
					node.index = features[i].getId();
					node.value = 0;
				}
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}

	public svm_node[] getSparseTFIDFVector(Feature[] rawfeatures) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : rawfeatures) {
			Feature[] features = dictionary.getFeatures();
			for (int i = 0; i < features.length; i++) {
				svm_node node = new svm_node();
				if (rawfeature.getFeature().equals(features[i].getFeature())) {
					node.index = features[i].getId();
					node.value = rawfeature.getWeight() * features[i].getWeight();
				} else {
					node.index = features[i].getId();
					node.value = 0;
				}
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}

	public svm_node[] getSparseBoolVector(Feature[] rawfeatures) {
		List<svm_node> nodes = new ArrayList<svm_node>();
		for (Feature rawfeature : rawfeatures) {
			Feature[] features = dictionary.getFeatures();
			for (int i = 0; i < features.length; i++) {
				svm_node node = new svm_node();
				if(rawfeature.getFeature().equals(features[i].getFeature())) {
					node.index = features[i].getId();
					if(rawfeature.getWeight() > 0) node.value = 1;
				} else {
					node.index = features[i].getId();
					node.value = 0;
				}
				nodes.add(node);
			}
		}
		return nodes.toArray(new svm_node[nodes.size()]);
	}
	
	public Feature[] getValidFeatures(Feature[] features) {
		List<Feature> holder = new ArrayList<Feature>();
		for (Feature rawfeature : features) {
			Feature feature = dictionary.getFeature(rawfeature.getFeature());
			if (feature != null) {
				holder.add(feature);
			}
		}
		return holder.toArray(new Feature[holder.size()]);
	}
}