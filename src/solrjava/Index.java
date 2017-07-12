package solrjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;


public class Index {
    public Index() throws SolrServerException, IOException {
    	String urlString = "http://localhost:8983/solr/bigboxstore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        //Adding Documents
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "389hf398ry");
        document.addField("name", "cherry");
        document.addField("price", "50.0");
        solr.add(document);
        
        //Remember to commit your changes!
        solr.commit();
        
        for(int i=0;i<100;++i) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("cat", "book");
            doc.addField("id", "book-" + i);
            doc.addField("name", "The Legend of the Hobbit part " + i);
            solr.add(doc);
            if(i%100==0) solr.commit();  // periodically flush
          }
          solr.commit(); 
          
          //Beans
          solr.addBean( new ProductBean("888", "Apple iPhone 6s", "299.99") );
          solr.commit();
    }
    
    public void acceptDocument(File f) throws SolrServerException, IOException {
    	String urlString = "http://localhost:8983/solr/bigboxstore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        InputStream is = new FileInputStream(f);
        String xml = IOUtils.toString(is);
        
    	DirectXmlRequest xmlreq = new DirectXmlRequest( "/update", xml); 
    	
    	solr.request(xmlreq);
    	solr.commit();
    }
}