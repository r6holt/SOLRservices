package solrjava;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class Query {
    public Query() {}
    
    public SolrDocumentList acceptQuery(String q) throws SolrServerException, IOException{
    	String urlString = "http://localhost:8983/solr/bigboxstore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        
        
        //Starting up a query
        SolrQuery query = new SolrQuery();
        
        //Adding fields to the query
        query.setQuery(q);
        query.set("wt", "xml");
        query.set("indent", "on");
        
        //Getting results
        QueryResponse resp = solr.query(query);
        SolrDocumentList list =resp.getResults();
        
        return list;
    }
}