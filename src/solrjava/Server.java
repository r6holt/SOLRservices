package solrjava;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class Server {
	public Server() {}
	
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
