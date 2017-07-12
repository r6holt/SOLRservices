package solrjava;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class Query {
    public Query() {}
    
    public SolrDocumentList acceptQuery(String q, ArrayList<String> s) throws SolrServerException, IOException{
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        
        
        //Starting up a query
        SolrQuery query = new SolrQuery();
        
        //Adding fields to the query
        query.setQuery(q);
        query.set("wt", s.get(0));
        if(s.get(1).trim()!="") {
        	query.set("ls", s.get(1));
        }
        query.set("start", s.get(2));
        query.set("rows", s.get(3));
        if(s.get(4).trim()!="") {
        	query.set("sort", s.get(4));
        }
        query.set("defType", s.get(5));
        
        //Getting results
        try {
        	QueryResponse resp = solr.query(query);
        	SolrDocumentList list =resp.getResults();
        	return list;
        }
        catch (Exception e) {
        	SolrDocumentList bad = new SolrDocumentList();
        	bad.setNumFound(-1);
        	return bad;
        }
        
        
    }
}