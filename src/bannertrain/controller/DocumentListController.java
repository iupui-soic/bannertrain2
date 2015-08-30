package bannertrain.controller;

import org.codehaus.jackson.map.DeserializationConfig.Feature;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;



//import bannertrain.SofaDocumentTmp;
import bannertrain.mediator.Mediator;
import bannertrain.mediator.MediatorFactory;

//import bannertrain.serialize.BannerDataDump;
import bannertrain.transport.SofaDocumentTransport;

/**
 * This controller manages interacts with the document list, provides 2 URL endpoints.  
 * 
 * The base URL, ie /document-list, returns a JSON string of documents currently loaded
 * 
 * The URL /document-list/load-documents reads in a JSON string of stringified SofaDocumentTransport objects and reads them as POJOs
 * 
 * @author ryaneshleman
 *
 */
@Path("/document-list")
public class DocumentListController {

	private List<SofaDocumentTransport> allSofaDocuments;

	private Mediator mediator;

	
	public DocumentListController()
	{
		mediator = MediatorFactory.getMediator();
		
	}
	
	/**
	 * URL takes a JSON string representation of SofaDocumentTransport objects as produced by the jackson library.  jackson is then used to read them in as 
	 * 
	 * @param multivaluedMap
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Path("/load-documents")
	@POST
	public String loadDocuments(MultivaluedMap<String,String> multivaluedMap) throws JsonParseException, JsonMappingException, IOException 
	{
		
		//extract and clean data string
		String json = multivaluedMap.getFirst("data");
		json = json.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        json = json.replaceAll("\\+", "%2B");
        json = URLDecoder.decode(json, "utf-8");
		

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// read objects and load into mediator
		mediator.setSofaDocumentTransport(mapper.readValue(json, SofaDocumentTransport[].class));

		return "ok";
	}


	/**
	 * returns a list of 
	 * @return
	 */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getDocumentsJson() {
    return documentList();
    		
  }

  private String documentList() {
	  
	  
	  DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	  String data = "{\"data\":[";
	
	
	// if no data is loaded, then return none list
	if(!mediator.isPopulated())
		return  data+ "[\"none\",\" none\", \" none\"]]}";
	
	for(SofaDocumentTransport d : mediator.getSofaDocumentTransport())
	{
		Date date = d.getDateCreated();
		String name = String.format(" %s, %s\\n%s",
									d.getPatientFamilyName(),
									d.getPatientGivenName(),
									d.getPatientID());
		
		String dateString = df.format(date).replace(" ", "\\n");
		data += String.format("[\"%d\",\"%s\",\"%s\"],",
						d.getSofaDocumentId(),
						dateString,
						name);
	}
	//remove trailing comma
	data = data.substring(0, data.length()-1);
	data += "]}";
	
	return data;
}


} 
