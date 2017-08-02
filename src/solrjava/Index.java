package solrjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;


public class Index {
	private HttpSolrClient solr = null;
	private SchemaEditor schema;
	private int status = 1;
	
    public Index(SchemaEditor se) throws SolrServerException, IOException {
    	String urlString = "http://localhost:8900/solr/solrservices";
    	solr = new HttpSolrClient.Builder(urlString).build();
    	
    	schema=se;
    }
    
    public int acceptDocument(File f) throws SolrServerException, IOException {
    	//Connect to server
    	
        
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
        	rse.printStackTrace();
        	String fieldname = rse.getMessage().split("\"")[1];
        	if(fieldname.equals("price")) {
        		schema.addField(fieldname, "double");
        	}
        	else {
        		schema.addField(fieldname, "string");
        	}
        	acceptDocument(f);
        	status=0;
        }
        
        try {
        	if(status==1) {
        		solr.commit();
        		JOptionPane.showMessageDialog(new JFrame(), "Document Added!");
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
        
        
        //Adding Documents
        //SolrInputDocument document = new SolrInputDocument();
        //document.addField("id", "fruit-1");
        //document.addField("name", "cherry");
        //document.addField("TEST", 18);
        //document.addField("price", 86);
        //solr.add(document);
        
        //Commit Changes
        //solr.commit();
        
        try {
	        for(int i=0;i<10000; i++) {
	            SolrInputDocument doc = new SolrInputDocument();
	            doc.addField("cat", "cards");
	            doc.addField("id", "pack-" + i);
	            doc.addField("blah", "total leg-1."+i);
	            doc.addField("name", "card " + i);
	            doc.addField("NYAA", "extra text to slow down the sort");
	            doc.addField("price", r.nextInt(1000));
	            doc.addField("store", (r.nextInt(180)-90)+", "+(r.nextInt(360)-180));
	            solr.add(doc);
	            if(i%500==0) {
	            	solr.commit();// periodically flush
	            }
	            if(i%1000==0) {
	            	System.out.println(System.currentTimeMillis());
	            }
	        }
	        status=1;
        }
        catch(RemoteSolrException rse) {
        	String fieldname = rse.getMessage().split("\"")[1];
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
        
        if(status==1) {
        	JOptionPane.showMessageDialog(new JFrame(), "Examples Added!");
        	solr.commit();
        }
    }
    
    public void reload(ArrayList<ProductBean> beans, String delete) throws SolrServerException, IOException {
    	int count=0;
    	
    	try {
	    	for(ProductBean b:beans) {
	    		count++;
	    		SolrInputDocument sd = new SolrInputDocument();
	    		
	    		for(int i=0; i<b.getFields().size(); i++) {
	    			if(b.getField(i).equals(delete)) {}
	    			else {
	    				sd.addField(b.getField(i), b.getValue(i));
	    			}
	    		}
	    		
	    		solr.add(sd);
	    		if(count%100==0) solr.commit();
	    	}
	    	
	    	solr.commit();
    	} catch (RemoteSolrException e) {
    		JOptionPane.showMessageDialog(new JFrame(), "Incorrect data type for new field!\nFailed to add field...");
    	}
    	
    }
}