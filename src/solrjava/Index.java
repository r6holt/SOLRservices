package solrjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;


public class Index {
	private HttpSolrClient solr = null;
	private SchemaEditor schema;
	private int status = 1;
	
    public Index(SchemaEditor se) throws SolrServerException, IOException {
    	solr = new HttpSolrClient.Builder(GUI.urlString).build();
    	
    	schema=se;
    }
    
    public int acceptDocument(File[] files) throws SolrServerException, IOException {
    	//Connect to server
    	
        for(File f:files) {
	        String name = f.getName();
	        if(name.length()<5) {
	        	return -1;
	        }
	        
	        try {
		        //JSON file addition
		        if(name.substring(name.length()-5).equals(".json")) {
		        	ContentStreamUpdateRequest csureq = new ContentStreamUpdateRequest("/update/json");
		        	csureq.addFile(f, name);
		        	solr.request(csureq);
		        }
		        //CSV file addition
		        if(name.substring(name.length()-4).equals(".csv")) {
		        	ContentStreamUpdateRequest csureq = new ContentStreamUpdateRequest("/update/csv");
		        	csureq.addFile(f, name);
		        	solr.request(csureq);
		        }
		      //PDF file addition
		        if(name.substring(name.length()-4).equals(".pdf")) {
		        	ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
		        	up.addFile(f, name);
		        	up.setParam("literal.id", name);
		        	up.setParam("uprefix", "attr_");
		        	up.setParam("fmap.content", "attr_content");
		        	//up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		        	solr.request(up);
		        }
		        //XML file addition
		        if(name.substring(name.length()-4).equalsIgnoreCase(".xml")) {
		            solr.setParser(new XMLResponseParser());
		            InputStream is = new FileInputStream(f);
		            @SuppressWarnings("deprecation")
		    		String xml = IOUtils.toString(is);
		            
		            DirectXmlRequest xmlreq = new DirectXmlRequest( "/update", xml);
		            solr.request(xmlreq);
		        }
		        status=1;
	        }
	        catch(RemoteSolrException rse) {
	        	try {
	        		String fieldname = "";
		        	try {fieldname = rse.getMessage().split("\'")[1];} catch(Exception e) {fieldname = rse.getMessage().split("\"")[1];}
		        	if(fieldname.equals("price")) {
		        		schema.addField(fieldname, "double");
		        	}
		        	else if(fieldname.equals("store")) {
		        		schema.addField(fieldname, "location");
		        		
		        	}
		        	else {
		        		String[] options = {"string", "float", "location", "date", "boolean"};
		        		Object data = JOptionPane.showInputDialog(new JFrame(), "Select data type for new field \""+fieldname+"\":", "Creating Field", JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
		        		
		        		schema.addField(fieldname, data.toString());
		        	}
		        	acceptDocument(files);
		        	status=0;
	        	}
	        	catch(Exception e) {
	        		JOptionPane.showMessageDialog(new JFrame(), "Failed to create new field...\n\n___Document not loaded___");
	        		return -1;
	        	}
	        }
    	}
        
        try {
        	if(status==1) {
        		solr.commit();
        		JOptionPane.showMessageDialog(new JFrame(), "Document(s) Added!");
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    	
    	return 1;
    }
    
    public void exampleDocs() throws SolrServerException, IOException {
    	Random r = new Random();
        solr.setParser(new XMLResponseParser());
        
        try {
        	ArrayList<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
	        for(int i=0;i<500000; i++) {
	            SolrInputDocument doc = new SolrInputDocument();
	            doc.addField("cat", "workers");
	            doc.addField("id", "employee-" + i);
	            doc.addField("blah", "total leg-1."+i);
	            doc.addField("text", "there are lots and lots and lots and lots of cars out there that can go on roads and even off road. Check them out cuz theyre cool. Im also"
	            		+ "trying to eat up space on the SOLR instance to get to 1gb that would sweet if this indexed pretty quickly too but well have to see."
	            		+ "I dont have a lot of text on here yet so not as much data");
	            doc.addField("name", "card " + i);
	            doc.addField("price", r.nextInt(1000));
	            doc.addField("store", (r.nextInt(180)-90)+", "+(r.nextInt(360)-180));
	            
	            list.add(doc);
	            
	            if(i%100000==0) {
	            	solr.add(list);
	            	list.clear();
	            	solr.commit();
	            }
	        }
	            
	        solr.add(list);
        	solr.commit();// periodically flush
        	
        }
        catch(RemoteSolrException rse) {
        	System.out.println("ADD NEW FIELD");
        	String fieldname = "";
        	try {fieldname = rse.getMessage().split("\'")[1];} catch(Exception e) {fieldname = rse.getMessage().split("\"")[1];}
        	if(fieldname.equals("price")) {
        		schema.addField(fieldname, "double");
        	}
        	else if(fieldname.equals("store")) {
        		schema.addField(fieldname, "location");
        	}
        	else {
        		schema.addField(fieldname, "string");
        	}
        	exampleDocs();
        	status=0;
        }
        
        /*if(status==1) {
        	JOptionPane.showMessageDialog(new JFrame(), "Examples Added!");
        	solr.commit();
        }*/
    }
    
    public void reload(ProductBean b, String field, String value) throws SolrServerException, IOException {
    	try {
    		SolrInputDocument sd = new SolrInputDocument();
    		
    		for(int i=0; i<b.getFields().size(); i++) {
    			if(b.getField(i).equals(field)) {
    				sd.addField(b.getField(i), value);
    			}
    			else if(b.getField(i).equals("_version_"));
    			else {
    				sd.addField(b.getField(i), b.getValue(i));
    			}
    		}
    		
    		solr.add(sd);
	    	solr.commit();
	    	
    	} catch (RemoteSolrException e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(new JFrame(), "Error editing field: "+field);
    	}
    	
    }
    
    public void reload(ArrayList<ProductBean> beans) throws SolrServerException, IOException {
    	int count=0;
    	
    	try {
    		ArrayList<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
	    	for(ProductBean b:beans) {
	    		count++;
	    		SolrInputDocument sd = new SolrInputDocument();
	    		
	    		for(int i=0; i<b.getFields().size(); i++) {
	    			sd.addField(b.getField(i), b.getValue(i));
	    		}
	    		
	    		list.add(sd);
	    		
	    		if(list.size()>100000) {
    				solr.add(list);
    				solr.commit();
    				list.clear();
    			}
	    	}
	    	
	    	if(!list.isEmpty()) {
	    		solr.add(list);
	    		solr.commit();
	    	}
	    	
    	} catch (RemoteSolrException e) {
    		JOptionPane.showMessageDialog(new JFrame(), "Incorrect data type for new field!\nFailed to add field...");
    	}
    	
    }
    
    public void reload(ArrayList<ProductBean> beans, String delete) throws SolrServerException, IOException {
    	int count=0;
    	
    	//update solr docs in index
    	try {
    		ArrayList<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
	    	for(ProductBean b:beans) {
	    		count++;
	    		SolrInputDocument sd = new SolrInputDocument();
	    		
	    		//adds beans to the next commit only if they contain the field that has been deleted
    			for(int i=0; i<b.getFields().size(); i++) {
	    			if(!b.getField(i).equals(delete)) {
	    				sd.addField(b.getField(i), b.getValue(i));
	    			}
	    		}
    			list.add(sd);
    			
    			if(list.size()>100000) {
    				solr.add(list);
    				solr.commit();
    				list.clear();
    			}
	    	}
	    	
	    	if(!list.isEmpty()) {
	    		solr.add(list);
	    		solr.commit();
	    	}
	    	
    	} catch (RemoteSolrException e) {
    		JOptionPane.showMessageDialog(new JFrame(), "Error deleting field...");
    	}
    	
    }
}