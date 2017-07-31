package solrjava;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class Refresh {
    public Refresh() {}
    
    public void deleteAll() throws SolrServerException, IOException {
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        //delete all
        solr.deleteByQuery("*:*");
        solr.commit();
        JOptionPane.showMessageDialog(new JFrame(), "All documents have been deleted!");
    }
}