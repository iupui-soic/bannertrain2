package bannertrain.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import bannertrain.mediator.Mediator;
import bannertrain.mediator.MediatorFactory;

import bannertrain.transport.ClientMentionTransport;
import bannertrain.transport.SofaDocumentTransport;
import bannertrain.transport.SofaTextMentionTransport;
import bannertrain.transport.SofaTextTransport;

@Path("/document-view")
public class DocumentViewController implements Serializable {
	  private Mediator mediator;
	
	  public DocumentViewController()
	  {
		  mediator = MediatorFactory.getMediator();
	  }


	  // This method is called if HTML is request
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String doGetDocument(@QueryParam("docId") int docId) {
		  for (SofaDocumentTransport sd : mediator.getSofaDocumentTransport())
		  {
			  if(sd.getSofaDocumentId() == docId)
				  return sd.getText();
		  }
		  return "oops";
		  
	  }
	  @GET
	  @Path("/mentions")
	  @Produces(MediaType.APPLICATION_JSON)
	  public String doGetMentions(@QueryParam("docId") int docId) throws JsonGenerationException, JsonMappingException, IOException {
		  for (SofaDocumentTransport sd : mediator.getSofaDocumentTransport())
		  {
			  if(sd.getSofaDocumentId() == docId)
				  return getMentionsJson(sd);
		  }
		  return "oops";
		  
	  }
	  
	  @SuppressWarnings("unchecked")
		@Path("/save-mentions")
		@POST
		public String saveMentions(MultivaluedMap<String,String> multivaluedMap) throws JsonParseException, JsonMappingException, IOException 
		{
		  	
		  	String json = multivaluedMap.getFirst("data");
		  	String docIdStr = multivaluedMap.getFirst("docId");
		    json = URLDecoder.decode(json, "UTF-8");
		  	System.out.println(json);
		  	int docId = Integer.parseInt(URLDecoder.decode(docIdStr, "UTF-8"));
			System.out.println(docId);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			
			ClientMentionTransport cmt[];
			cmt = mapper.readValue(json, ClientMentionTransport[].class);
			
			updateSofaDocument(docId,cmt);
			
			return "ok";
		  
		}
	  
	  	@SuppressWarnings("unchecked")
		@Path("/train")
		@POST
		public String train(MultivaluedMap<String,String> multivaluedMap)
		{
		    String path = multivaluedMap.getFirst("path");
		  	mediator.train(path);
			return "ok";
		  
		}
	  
	  @GET
	  @Path("/download-most-recent")
	  @Produces(MediaType.APPLICATION_OCTET_STREAM)
	  public Response getFile() {
	    File file = new File(mediator.getRecentTrainedPath());
	    return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
	        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
	        .build();
	  }
	  
	  private void updateSofaDocument(int docId, ClientMentionTransport[] cmt) {
		mediator.updateSofaDocument(docId, cmt);
		
	}


	private String getMentionsJson(SofaDocumentTransport sd) throws JsonGenerationException, JsonMappingException, IOException {
		  String out = "[";
		  ArrayList<ClientMentionTransport> cmt = new ArrayList<ClientMentionTransport>();
		  for(SofaTextTransport text : sd.getSofaText())
			  for(SofaTextMentionTransport stm : text.getSofaTextMention())
			  {  
				  int offset = getCharacterOffset(sd,stm,text);
				  if(offset == -1)
					  continue;
				  
				  int extent = offset + stm.getMentionText().length();
				  out += String.format("{\"class_name\":\"concept-class-%s\",\"extent\":%d,\"offset\":%d,\"text\":\"%s\"},",
						  stm.getMentionType().substring(0,4),
						  extent,
						  offset,
						  stm.getMentionText());
				  
				  cmt.add(new ClientMentionTransport(offset,
						  extent,
						  "concept-class-"+stm.getMentionType().substring(0,4),
						  stm.getMentionText()));
			  }
		  
		  out = out.substring(0,out.length()-1);
		  //return out+"]";
		  ObjectMapper mapper = new ObjectMapper();
			
		  String jackson_out = mapper.writeValueAsString(cmt);
		  return jackson_out;
				  
	}


	private int getCharacterOffset(SofaDocumentTransport sd, SofaTextMentionTransport stm,
			SofaTextTransport text) {
		
			String doc = sd.getText().replaceAll("\\r|\\n", "");
			String docText = text.getText().replaceAll("\\r|\\n", "");
			String mentionText = stm.getMentionText().replaceAll("\\r|\\n", "");
		
			/*
			System.out.println("*****************************");
			System.out.println(doc);
			System.out.println(docText);
			System.out.println(mentionText);
			*/
			int textOffset = doc.indexOf(docText);
			int mentionOffset = docText.indexOf(mentionText);
			
			//System.out.println(textOffset);
			//System.out.println(mentionOffset);
			
			//Something went wrong...
			if(textOffset == -1 || mentionOffset == -1)
				return -1;
			
			return textOffset+mentionOffset+1;
		
	}


	

	} 