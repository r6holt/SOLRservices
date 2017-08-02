package solrjava;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.apache.solr.common.util.NamedList;

public class SchemaEditor {
	
	private HttpSolrClient solr;
	private HttpSolrClient load;
	
	public SchemaEditor() {
		String urlString = "http://localhost:8900/solr/solrservices";
    	solr = new HttpSolrClient.Builder(urlString).build();
    	
    	String update = "http://localhost:8900/solr";
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
	
	public String deleteField(FieldTracker ft) throws SolrServerException, IOException {
		String[] options = new String[(ft.numFields()-2)];
		int count=0;
		for(int i=0; i<(ft.numFields()); i++) {
			if(!ft.getField(i).equals("id") && !ft.getField(i).equals("_version_")) options[i-count] = ft.getField(i);
			else count++;
		}
		
		Object fieldName = JOptionPane.showInputDialog(new JFrame(), "Select the field to be deleted:", "Delete Field", JOptionPane.OK_CANCEL_OPTION, null, options, "Select Field");
		
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
