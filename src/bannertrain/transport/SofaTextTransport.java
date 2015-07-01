package bannertrain.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SofaTextTransport  implements Serializable,Comparable {
	private int sofaTextId;

	private SofaDocumentTransport sofaDocument;
	private int textStart;
	private int textEnd;
	private String text;
	private String uuid;
	private Set<SofaTextMentionTransport> sofaTextMention = new HashSet<SofaTextMentionTransport>();

	
	public SofaTextTransport()
	{
	}
	
	
	public SofaTextTransport(String text, int textStart, int textEnd)
	{
		this.text = text;
		this.textStart = textStart;
		this.textEnd = textEnd;
	}
	
	
	
	
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setId(Integer arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the sofaTextId
	 */
	public int getSofaTextId() {
		return sofaTextId;
	}

	/**
	 * @param sofaTextId the sofaTextId to set
	 */
	public void setSofaTextId(int sofaTextId) {
		this.sofaTextId = sofaTextId;
	}

	/**
	 * @return the encounter
	 */


	/**
	 * @return the sofaTextMention
	 */
	public Set<SofaTextMentionTransport> getSofaTextMention() {
		return sofaTextMention;
	}

	/**
	 * @param sofaTextMention the sofaTextMention to set
	 */
	public void setSofaTextMention(Set<SofaTextMentionTransport> sofaTextMention) {
		this.sofaTextMention = sofaTextMention;
	}

	/**
	 * @return the sofaDocument
	 */
	public SofaDocumentTransport getSofaDocument() {
		return sofaDocument;
	}

	/**
	 * @param sofaDocument the sofaDocument to set
	 */
	public void setSofaDocument(SofaDocumentTransport sofaDocument) {
		this.sofaDocument = sofaDocument;
	}

	/**
	 * @return the textStart
	 */
	public int getTextStart() {
		return textStart;
	}

	/**
	 * @param textStart the textStart to set
	 */
	public void setTextStart(int textStart) {
		this.textStart = textStart;
	}

	/**
	 * @return the textEnd
	 */
	public int getTextEnd() {
		return textEnd;
	}

	/**
	 * @param textEnd the textEnd to set
	 */
	public void setTextEnd(int textEnd) {
		this.textEnd = textEnd;
	}

	public String getAnnotatedHTML() {
		String html = new String(text);
		String tagged;
		for(SofaTextMentionTransport m : sofaTextMention)
		{
			tagged = wrapInMentionTypeTag(m.getMentionText(),m.getMentionType());
			
			
			html = html.replace(m.getMentionText(), tagged);
			html = html.replaceAll("\\n", "<br/>");
		}
		return html;
	}

	

	private String wrapInMentionTypeTag(String mentionText, String mentionType ) {
		
		return String.format("<span class=\"mention-type-%s\">%s</span>",mentionType,mentionText);
		
		
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
