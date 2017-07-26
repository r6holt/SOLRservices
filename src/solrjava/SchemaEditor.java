package solrjava;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;

public class SchemaEditor {
	public SchemaEditor(String name, String datatype) throws SolrServerException, IOException {
		
		//String urlString = "http://localhost:8900/solr/solrservices";
    	//HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
		
    	 Map<String, Object> fieldAttributes = new LinkedHashMap<>();
    	    fieldAttributes.put("name", name);
    	    fieldAttributes.put("type", datatype);
    	    fieldAttributes.put("stored", true);
    	    fieldAttributes.put("indexed", true);

    	    //SchemaRequest.AddField addFieldUpdateSchemaRequest =
    	            new SchemaRequest.AddField(fieldAttributes);
    	    
    	    /*String update = "http://localhost:8900/solr/#";
        	HttpSolrClient load = new HttpSolrClient.Builder(update).build();
    	    
    	    CoreAdminRequest adminRequest = new CoreAdminRequest();
    	    adminRequest.setAction(CoreAdminAction.RELOAD);
    	    CoreAdminResponse adminResponse = adminRequest.process(load);
    	    NamedList<NamedList<Object>> coreStatus = adminResponse.getCoreStatus();*/
	}
}
