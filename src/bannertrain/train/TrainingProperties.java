package bannertrain.train;
//import dragon.nlp.tool.MedPostTagger;

public class TrainingProperties {

	private int order = 2;
	private boolean isUseFeatureinduction = false;
	private String tagFormat="IOB";
	private String textDirection="Forward";
	//private MedPostTagger posTagger = new MedPostTagger();
	
	public int getOrder() {
		return order;
		
	}

	public boolean isUseFeatureInduction() {
		return isUseFeatureinduction;
	}

	public String getTagFormat() {
		return tagFormat;
	}

	public String getTextDirection()
	{
		return textDirection;
	}

	public Object getLemmatiser() {
		// no lemmatiser
		return null;
	}

	public Object getPosTagger() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object isUseNumericNormalization() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getPreTagger() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
