package bannertrain.train;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletContext;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.sfsu.bannertrain.train.CRFTagger;

import banner.BannerProperties.TextDirection;
import banner.Sentence;
import banner.tagging.Mention;
import banner.tagging.MentionType;
import banner.tokenization.Tokenizer;
import dragon.nlp.tool.*;
import bannertrain.train.BannerProperties;
import bannertrain.transport.SofaDocumentTransport;
import bannertrain.transport.SofaTextMentionTransport;
import bannertrain.transport.SofaTextTransport;

public class BannerTrainer implements Serializable {

	
	private  CRFTagger tagger;
	private SofaDocumentTransport[] sofaDocumentTransports;
	private BannerProperties properties;
	
	public BannerTrainer(SofaDocumentTransport[] docs)
	{
		this.setSofaDocumentTransports(docs);
	}

	public SofaDocumentTransport[] getSofaDocumentTransports() {
		return sofaDocumentTransports;
	}

	public void setSofaDocumentTransports(SofaDocumentTransport[] sofaDocumentTransports) {
		this.sofaDocumentTransports = sofaDocumentTransports;
	}
	
	
	public String train(String file_name) throws URISyntaxException, ClassNotFoundException, IOException
	{
		
		
		
		
		URL resource = BannerTrainer.class.getResource("banner.properties");
		properties = BannerProperties.load(resource.toString().substring(5));
		Tokenizer tokenizer = properties.getTokenizer();	 
		List<Sentence> sentences =  new ArrayList<Sentence>();
		Map<String, String> mentionClassMap = new HashMap<String,String>();
		mentionClassMap.put("prob", "problem");
		mentionClassMap.put("problem", "problem");
		mentionClassMap.put("trea", "treatment");
		mentionClassMap.put("treatment", "treatment");
		mentionClassMap.put("test", "test");
		
		for(SofaDocumentTransport doc : sofaDocumentTransports)
		{
			for(SofaTextTransport text : doc.getSofaText())
			{
				Sentence sentence = new Sentence(text.getText());
				tokenizer.tokenize(sentence);
				for(SofaTextMentionTransport mention : text.getSofaTextMention())
				{
					//System.out.println("MENTION-TEXT: "+mention.getMentionText());
					//System.out.println(mention.getMentionType());
					String type = mentionClassMap.get(mention.getMentionType());
					//System.out.println(type);
					try{
					Mention m = new Mention(sentence,MentionType.getType(type), mention.getMentionStart(), mention.getMentionEnd()+1);
					sentence.addMention(m);
					//System.out.println(m);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
				sentences.add(sentence);
			}
			
		}
		
		
		String modelName = "/Users/ryaneshleman/Documents/workspace/NER_OpenMRS/Banner/I2B2Model14";
		File f = new File(modelName);
		//CRFTagger tagger = CRFTagger.load(f, properties.getLemmatiser(), properties.getPosTagger(), properties.getPreTagger());
		
		
	    tagger = CRFTagger.train(sentences, properties.getOrder(), properties.isUseFeatureInduction(), properties.getTagFormat(),  properties.getTextDirection(), properties.getLemmatiser(), properties.getPosTagger(), properties.isUseNumericNormalization(),properties.getPreTagger(),properties.getRegexFilename());
		System.out.println("DONE TRAINING!");
		String path = resource.toString().substring(5).replace("banner.properties", "");
		
		String outPath = path+"models/"+file_name;
		System.out.println("Serializing");
		serialize(outPath,tagger);
		
		System.out.println("deserializing");
		Object o =  deserialize(new File(outPath));
		
		return outPath;
		
	}
	
	

	public void serialize(String filename, CRFTagger object)
	{
		try {
			      OutputStream file = new FileOutputStream(filename);
			      OutputStream buffer = new BufferedOutputStream(file);
			      ObjectOutput output = new ObjectOutputStream(buffer);
			    
			      output.writeObject(object);
			      output.flush();
			      output.close();
			      buffer.flush();
			      buffer.close();
			      file.flush();
			      file.close();
			    }  
			    catch(IOException ex){
			      ex.printStackTrace();
			    }
		
	}
	
	public Object deserialize(File f) throws IOException, ClassNotFoundException
	{
		FileInputStream fio = new FileInputStream(f);
		InputStream buffer = new BufferedInputStream(fio);
		ObjectInputStream ois = new ObjectInputStream (buffer);
		Object o = ois.readObject();
		
		fio.close();
		buffer.close();
		ois.close();
		
		return o;
		
	}
}
