package bannertrain.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SofaDocumentTransport implements Serializable {
	private int sofaDocumentId;
	//private Encounter encounter;
	//private Patient patient;
	private Date dateCreated;
	private String text;
	
	
	private String annotatedHTML;
	private String uuid;
	private Set<SofaTextTransport> sofaText = new HashSet<SofaTextTransport>();
	private int patientID;
	private String patientGivenName;
	private String patientFamilyName;
	
	public SofaDocumentTransport()
	{
		
	}
	
	

	/**
	 * @return the sofaDocumentId
	 */
	public int getSofaDocumentId() {
		return sofaDocumentId;
	}
	
	public SofaTextTransport addSofaText(SofaTextTransport sofaText)
	{
		this.sofaText.add(sofaText);
		return sofaText;
	}

	/**
	 * @param sofaDocumentId the sofaDocumentId to set
	 */
	public void setSofaDocumentId(int sofaDocumentId) {
		this.sofaDocumentId = sofaDocumentId;
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
	 * @return the sofaText
	 */
	public Set<SofaTextTransport> getSofaText() {
		return sofaText;
	}

	/**
	 * @param sofaText the sofaText to set
	 */
	public void setSofaText(Set<SofaTextTransport> sofaText) {
		this.sofaText = sofaText;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the annotatedHTML
	 */
	public String getAnnotatedHTML() {
		return generateAnnotatedHTML();
	}

	private String generateAnnotatedHTML() {
		StringBuffer out = new StringBuffer();
		
		List<SofaTextTransport> sortedSofaTexts = new ArrayList<SofaTextTransport>(sofaText);
		Collections.sort(sortedSofaTexts);
		
		for(SofaTextTransport st : sortedSofaTexts)
		{	
			
			out.append(st.getAnnotatedHTML());
		}	
	
	return new String(out);
	}



	/**
	 * @param annotatedHTML the annotatedHTML to set
	 */
	public void setAnnotatedHTML(String annotatedHTML) {
		this.annotatedHTML = annotatedHTML;
	}




	public String generateMentionString() 
	{
		String out = "";
		for(SofaTextTransport stt : sofaText)
		{
			for(SofaTextMentionTransport m: stt.getSofaTextMention())
				out += " "+ m.getMentionText();
		}
		
		return out;
		
	}



	public int getPatientID() {
		return patientID;
	}



	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}



	public String getPatientGivenName() {
		return patientGivenName;
	}



	public void setPatientGivenName(String patientGivenName) {
		this.patientGivenName = patientGivenName;
	}



	public String getPatientFamilyName() {
		return patientFamilyName;
	}



	public void setPatientFamilyName(String patientFamilyName) {
		this.patientFamilyName = patientFamilyName;
	}

	

}
