package org.headvances.ml.nlp.opinion;

import java.util.List;

import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.feature.MapFeatureHolder;
import org.junit.Test;

public class OpinionFeatureGeneratorUnitTest {
  @Test
	public void test() throws Exception{
		String[] keyword = { "HTC Desire" };
		String text = "HTC Desire có màn hình rất đẹp";
		OpinionMaxentTrainer trainer = new OpinionMaxentTrainer(keyword) ;
		FeatureGenerator<Opinion> fgenerator = OpinionMaxentTrainer.createFeatureGenerator(keyword);
		FeatureHolder holder = null;
		List<Opinion> opinions = trainer.getOpinionExtractor().extract(new OpinionDocument("", text));

		int idx = 0;
		for(Opinion op: opinions) {  
			holder = new MapFeatureHolder(idx);
			fgenerator.generate(holder, op);

			for(GenerateFeature gf: holder.getFeatures())
				System.out.print(gf.getFeature() + " ");
			System.out.println();
			idx++; 
		}
	}

	//@Test
	public void testFileFeatureExtract() throws Exception{
		String file = "file:src/data/opinion/product_train_data.txt";
		String[] keyword = { "product" };
		OpinionMaxentTrainer trainer = new OpinionMaxentTrainer(keyword) ;
		FeatureGenerator<Opinion> fgenerator = OpinionMaxentTrainer.createFeatureGenerator(keyword);
		FeatureHolder holder = null;

		List<Opinion> opinions = MLUtil.readSampleOpinion(keyword, file);
		opinions = trainer.doExtract(opinions) ;
		int idx = 0;
		for(Opinion op: opinions) {  
			holder = new MapFeatureHolder(idx);
			fgenerator.generate(holder, op);

			if(holder.getFeatures().length == 0) continue;

			for(GenerateFeature gf: holder.getFeatures())
				System.out.print(gf.getFeature() + " ");
			System.out.println(op.getLabel());
			idx++; 
		}   
	}
}