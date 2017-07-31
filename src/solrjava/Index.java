package solrjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.solr.common.SolrInputDocument;


public class Index {
    public Index() throws SolrServerException, IOException {}
    
    public int acceptDocument(File f) throws SolrServerException, IOException {
    	//Connect to server
    	String urlString = "http://localhost:8900/solr/solrservices";
    	HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        
        String name = f.getName();
        if(name.length()<5) {
        	return -1;
        }
        
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
        //XML file addition
        if(name.substring(name.length()-4).equalsIgnoreCase(".xml")) {
            solr.setParser(new XMLResponseParser());
            InputStream is = new FileInputStream(f);
            @SuppressWarnings("deprecation")
    		String xml = IOUtils.toString(is);
            
            
            try {
            	DirectXmlRequest xmlreq = new DirectXmlRequest( "/update", xml); 
        		
        		solr.request(xmlreq);
            }
            catch(RemoteSolrException rse) {
            	rse.printStackTrace();
            }
        }
        
    	solr.commit();
    	JOptionPane.showMessageDialog(new JFrame(), "Document Added!");
    	
    	return 1;
    }
    
    public void exampleDocs() throws SolrServerException, IOException {
    	Random r = new Random();
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        
        //Adding Documents
        //SolrInputDocument document = new SolrInputDocument();
        //document.addField("id", "fruit-1");
        //document.addField("name", "cherry");
        //document.addField("TEST", 18);
        //document.addField("price", 86);
        //solr.add(document);
        
        //Commit Changes
        solr.commit();
        
        for(int i=0;i<100000; i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("cat", "cards");
            doc.addField("id", "pack-" + i);
            doc.addField("blah", "total leg-1."+i);
            doc.addField("name", "card " + i);
            doc.addField("NYAA", "extra text to slow down the sort");
            doc.addField("price", r.nextInt(1000));
            doc.addField("TEST", 152);
            solr.add(doc);
            if(i%100==0) {
            	solr.commit();// periodically flush
            }
            if(i%1000==0) {
            	System.out.println(System.currentTimeMillis());
            }
          }
        
        JOptionPane.showMessageDialog(new JFrame(), "Examples Added!");
        solr.commit();
    }
}