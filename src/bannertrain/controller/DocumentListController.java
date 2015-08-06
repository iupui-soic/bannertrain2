package bannertrain.controller;

import java.io.File;

import org.codehaus.jackson.map.DeserializationConfig.Feature;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
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

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/document-list")
public class DocumentListController {
	//private List<SofaDocumentTmp> documentList = new ArrayList<SofaDocumentTmp>();
	private List<SofaDocumentTransport> allSofaDocuments;
	//private BannerDataDump dump;
	private Mediator mediator;

	
	public DocumentListController()
	{
		mediator = MediatorFactory.getMediator();
		
	}
	
	@SuppressWarnings("unchecked")
	@Path("/load-documents")
	@POST
	public String loadDocuments(MultivaluedMap<String,String> multivaluedMap) throws JsonParseException, JsonMappingException, IOException 
	{
		
		String json = multivaluedMap.getFirst("data");
		json = json.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        json = json.replaceAll("\\+", "%2B");
        json = URLDecoder.decode(json, "utf-8");
		
		//System.out.println(json);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//User user = mapper.readValue(new File("c:\\user.json"), User.class);
		
		
		mediator.setSofaDocumentTransport(mapper.readValue(json, SofaDocumentTransport[].class));
		System.out.println(documentList());
	
		return "ok";
	}


  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getDocumentsJson() {
    return documentList();
    		
  }

  private String documentList() {
	  
	  
	  DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	  String data = "{\"data\":[";
	
	
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
