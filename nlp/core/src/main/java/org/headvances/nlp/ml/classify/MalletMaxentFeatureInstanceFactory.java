package org.headvances.nlp.ml.classify;

import java.io.Serializable;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.SparseVector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MalletMaxentFeatureInstanceFactory  implements Serializable {
	private LabelAlphabet targetLabelAlphabet ;
	private Alphabet dataAlphabet ;

	public MalletMaxentFeatureInstanceFactory () {
		this.dataAlphabet = new Alphabet(Entry.class) ;
		this.targetLabelAlphabet = new LabelAlphabet() ;
	}

	public MalletMaxentFeatureInstanceFactory(Alphabet dataAlphabet, LabelAlphabet targetLabelAlphabet) {
		this.dataAlphabet = dataAlphabet ;
		this.targetLabelAlphabet = targetLabelAlphabet ;
	}

	public Alphabet getDataAlphabet() { return this.dataAlphabet ; }

	public Alphabet getTargetAlphabet() { return this.targetLabelAlphabet ; }

	public Instance createTrainInstance(Feature[] feature, String label) {
		SparseVector fv = createFeatureVector(feature, true);
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(fv);
		int labelIndex = targetLabelAlphabet.lookupIndex(label, true) ;
		carrier.setTarget(targetLabelAlphabet.lookupLabel(labelIndex));
		return carrier;
	}

	public Instance createDecodeInstance(Feature[] feature) {
		SparseVector fvector = createFeatureVector(feature, false);
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(fvector);
		carrier.setTarget(targetLabelAlphabet);
		return carrier;
	}
	
	public SparseVector createFeatureVector(Feature[] feature, boolean add) {
		int[] findices = new int[feature.length] ;
		double[] weight = new double[feature.length] ;
		int fidx = 0 ;
		for(int i = 0; i < findices.length; i++) {
			int index = -1 ;
			Entry entry = new Entry(feature[i].getFeature(), feature[i].getWeight()) ;
			if(add) {
				index  = dataAlphabet.lookupIndex(entry, add);
			} else {
				index  = dataAlphabet.lookupIndex(entry);
				if(index < 0) continue ;
				entry = (Entry) dataAlphabet.lookupObject(index) ;
			}
			findices[fidx] = index;
			weight[fidx]   = entry.getWeight() ;
			fidx++ ;
		}
		if(fidx < feature.length) {
			int[] tmpfindices = new int[fidx];
			System.arraycopy(findices, 0, tmpfindices, 0, tmpfindices.length) ;
			findices = tmpfindices ;
			
			double[] tmpweight = new double[fidx];
			System.arraycopy(weight, 0, tmpweight, 0, tmpweight.length) ;
			weight = tmpweight ;
		}
		SparseVector fv = new FeatureVector(dataAlphabet, findices, weight);
		return fv ;
	}
	
	static public class Entry implements Serializable {
		private String feature ;
		private float  weight ;

		public Entry() { }
		
		public Entry(String feature, float weight) {
			this.feature = feature ;
			this.weight = weight ;
		}

		public String getFeature() { return feature; }
		public void setFeature(String feature) { this.feature = feature; }

		public float getWeight() { return weight; }
		public void setWeight(float weight) { this.weight = weight; }
	
		public int hashCode() { return feature.hashCode() ; }
		
		public boolean equals(Object object) {
			Entry other = (Entry)object ;
			return feature.equals(other.feature) ;
		}
	}
}