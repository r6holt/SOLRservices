package solrjava;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;

public class Server {
	public Server() throws SolrServerException, IOException {}
	
	public int tryPort () {
		try {
			String urlString = "http://localhost:8900/solr/solrservices";
	        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
	        
	        solr.commit();
		}
		catch (Exception e)
		{
			return -1;
		}
		return 1;
	}
}
