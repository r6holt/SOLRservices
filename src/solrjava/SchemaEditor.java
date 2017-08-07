package solrjava;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;

public class SchemaEditor {
	
	private HttpSolrClient solr;
	private HttpSolrClient load;
	
	public SchemaEditor() {
    	solr = new HttpSolrClient.Builder(GUI.urlString).build();
    	
    	String update = GUI.urlString.substring(0, 30);
    	load = new HttpSolrClient.Builder(update).build();
	}
	
	public void addField(String name, String datatype) throws SolrServerException, IOException {
		
    	Map<String, Object> fieldAttributes = new LinkedHashMap<>();
	    fieldAttributes.put("name", name);
	    fieldAttributes.put("type", datatype);
	    fieldAttributes.put("stored", true);
	    fieldAttributes.put("indexed", true);
	    
	    if(datatype.equals("string")) {
	    	fieldAttributes.put("multiValued", true);
	    }

	    SchemaRequest.AddField addFieldUpdateSchemaRequest =
	            new SchemaRequest.AddField(fieldAttributes);
	    
	    addFieldUpdateSchemaRequest.process(solr);
	    
	    CoreAdminRequest adminRequest = new CoreAdminRequest();
	    adminRequest.setCoreName("solrservices");
	    adminRequest.setOtherCoreName("solrservices");
	    adminRequest.setAction(CoreAdminAction.RELOAD);
	    adminRequest.process(load);
	}
	
	public String deleteField(String fieldName) throws SolrServerException, IOException {
		
		if(fieldName!=null && !fieldName.toString().equals("Select Field")) {
			SchemaRequest.DeleteField deleteSchemaField = new SchemaRequest.DeleteField(fieldName.toString());
			deleteSchemaField.process(solr);
			
			CoreAdminRequest adminRequest = new CoreAdminRequest();
		    adminRequest.setCoreName("solrservices");
		    adminRequest.setOtherCoreName("solrservices");
		    adminRequest.setAction(CoreAdminAction.RELOAD);
		    adminRequest.process(load);
		    
		    return fieldName.toString();
		}
		
		return null;
	}
}
