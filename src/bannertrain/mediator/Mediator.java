package bannertrain.mediator;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import bannertrain.train.BannerTrainer;
import bannertrain.transport.ClientMentionTransport;
import bannertrain.transport.SofaDocumentTransport;
import bannertrain.transport.SofaTextMentionTransport;
import bannertrain.transport.SofaTextTransport;

public class Mediator implements Serializable {
	private SofaDocumentTransport sdt[];
	private boolean isPopulated = false;
	private BannerTrainer trainer;
	private String recentTrainedPath;
	
	public void setSofaDocumentTransport(SofaDocumentTransport t[])
	{
		this.sdt = t;
		isPopulated = true;
	}
	
	public SofaDocumentTransport[] getSofaDocumentTransport()
	{
		return sdt;
	}
	
	public boolean isPopulated(){
		return isPopulated;
	}

	public void updateSofaDocument(int docId, ClientMentionTransport[] cmt) {
		
		SofaDocumentTransport doc = null;
		
		for (SofaDocumentTransport sd : sdt)
			if (sd.getSofaDocumentId() == docId)
			{
				doc = sd;
				break;
			}
		
		
		for(SofaTextTransport sofaText : doc.getSofaText())
		{
			Set<SofaTextMentionTransport> mentions = translateMentions(doc,sofaText,cmt);
			sofaText.setSofaTextMention(mentions);
			
		}
		
	}
	/**
	 * translates ClientMentionTransport objects to SofaTextMentionTransport objects
	 * @param doc
	 * @param sofaText
	 * @param cmt
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void train(String path) 
	{
		trainer = new BannerTrainer(sdt);
		try {
			setRecentTrainedPath(trainer.train(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private Set<SofaTextMentionTransport> translateMentions(
			SofaDocumentTransport doc, SofaTextTransport sofaText,
			ClientMentionTransport[] cmt) {
		
		String text = sofaText.getText();
		Set<SofaTextMentionTransport> out = new HashSet<SofaTextMentionTransport>();
		
		int textOffset = doc.getText().indexOf(sofaText.getText());
		int textExtent = textOffset + sofaText.getText().length();
		System.out.println("in translateMention");
		for(ClientMentionTransport clientMention : cmt)
		{
			// determine if ClientMentionTransport appears in current SofaTextTransport
			// could optimize by observing that clientMentions are sorted by offset. O(n+m) instead of O(m^2)
			// however, method infrequently used and relatively small data
			if(clientMention.getOffset()>= textOffset && clientMention.getOffset() <= textExtent)
			{
				int tokenIndex = 0;
				int numTokens = 1;
				int charIndex = 0;
				
				//count number of tokens before mention offset
				while(charIndex < clientMention.getOffset() - textOffset)
				{
					if(text.charAt(charIndex)==' ')
						tokenIndex++;
					
					charIndex++;
				}
				
				// count number of tokens in mention
				while(charIndex < clientMention.getExtent() - textOffset)
				{
					if(text.charAt(charIndex)==' ')
						numTokens++;
					charIndex++;
				}
				
				SofaTextMentionTransport m = new SofaTextMentionTransport();
				m.setMentionText(clientMention.getText());
				m.setMentionStart(tokenIndex);
				m.setMentionEnd(tokenIndex+numTokens-1);
				
				m.setMentionType(clientMention.getClass_name().substring(14,18));

				out.add(m);
				
			}
			
			
			
		}
		
		return out;
	}

	public String getRecentTrainedPath() {
		return recentTrainedPath;
	}

	public void setRecentTrainedPath(String recentTrainedPath) {
		this.recentTrainedPath = recentTrainedPath;
	}
	
}
