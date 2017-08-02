package solrjava;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class Query {
	private long FOUND = 0;
	private FieldTracker ft;
	private int searches = 0;
	private List<FacetField> facet;
	private Map<String, Integer> range;
	
    public Query(FieldTracker ft) {
    	this.ft=ft;
    }
    
    public ArrayList<ProductBean> acceptQuery(String q, int START, int ROWS) throws SolrServerException, IOException{
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        // Starting up a query
        SolrQuery query = new SolrQuery();
        
        String fq = "";
        
        // set blank query
        query.setQuery("*"+q+"*");
        if(q.trim().equals("")) {
        	query.setQuery("*");
        }
        
        // Rows and starting row
        query.set("rows", ROWS);
        query.set("start", START);
        //query.set("sort", ft.getSortfield()+" "+ft.isSort());
        
        
        //set price and category facets
        query.setFacet(true);
        if(ft.isCat()) {
        	query.set("facet.field", "cat");
        }
        if(ft.getPrice()) {
        	query.set("facet.query", "price:[0 TO 9.99]", "price:[10 TO 24.99]", "price:[25 TO 49.99]", "price:[50 TO 99.99]", "price:[100 TO 199.99]", "price:[200 TO 499.99]", "price:[500 TO *]");
        }
        
        // spatial
        if(ft.getSortfield().equals("location")) {
            query.set("pt", ft.getLat()+", "+ft.getLon());
            query.set("sfield", "store");
            query.set("sort", "geodist() "+ft.isSort());
        }
        else {
        	query.set("sort", ft.getSortfield()+" "+ft.isSort());
        }
        
        // set price sort
        if(ft!=null && ft.getPriceQuery()) {
        	if(ft.getMinPrice()==-1) {
        		fq+="-price:["+ft.getMaxPrice()+" TO *] ";
        	}
        	else if(ft.getMaxPrice()==-1) {
        		fq+= "-price:[* TO "+ft.getMinPrice()+"] ";
        	}
        	else {
        		fq+="-price:[* TO "+ft.getMinPrice()+"] -price:["+ft.getMaxPrice()+" TO *] ";
        	}
        }
        
        //set query field
        if(ft.getCategory()!="null") {
        	if(q.trim().equals("")) {
        		query.setQuery(ft.getCategory()+":*");
        	}
        	else if(ft.getCategory().equals("price")) {
        		query.setQuery("price:"+q);
        	}
        	else {
        		query.setQuery(ft.getCategory()+":*"+q+"*");
        	}
        }
        
        // facet filtering by price and category
        if(!ft.getPricechoice().equals("")) {
        	String[] subsets = ft.getPricechoice().split(" ");
        	String tot = "";
        	
        	for(String s: subsets) {        		
	        	if(s.equals("0")) {
	        		tot+="price:[0 TO 9.99] ";
	        	}
	        	else if(s.equals("10")) {
	        		tot+= "price:[10 TO 24.99] ";
	        	}
	        	else if(s.equals("25")) {
	        		tot+= "price:[25 TO 49.99] ";
	        	}
	        	else if(s.equals("50")) {
	        		tot+= "price:[50 TO 99.99] ";
	        	}
	        	else if(s.equals("100")) {
	        		tot+= "price:[100 TO 199.99] ";
	        	}
	        	else if(s.equals("200")) {
	        		tot+= "price:[200 TO 499.99] ";
	        	}
	        	else if(s.equals("500")) {
	        		tot+= "price:[500 TO *] ";
	        	}
        	}
        	fq+=tot;
        }
        
        // controls selection of faceted fields
        if(!ft.getFacetchoice().equals("")) {
        	String[] subsets = ft.getFacetchoice().split(", ");
        	
        	if(subsets.length==1) {
        		query.set("fq", fq, "cat:"+subsets[0]);
        	}
        	else if(subsets.length==2) {
        		query.set("fq", fq, "cat:"+subsets[0], "cat: "+subsets[1]);
        	}
        	else if(subsets.length==3) {
        		query.set("fq", fq, "cat:"+subsets[0], "cat: "+subsets[1],
        				"cat: "+subsets[2]);
        	}
        	else if(subsets.length==4) {
        		query.set("fq", fq, "cat:"+subsets[0], "cat: "+subsets[1],
        				"cat: "+subsets[2], "cat: "+subsets[3]);
        	}
        }
        else {
            query.set("fq", fq);
        }
        
        System.out.println(fq);
        System.out.println(q);
        
        //Getting results
        ArrayList<ProductBean> beans = new ArrayList<ProductBean>();
        try {
        	QueryResponse resp = solr.query(query);
        	facet = resp.getFacetFields();
        	range = resp.getFacetQuery();
        	SolrDocumentList list =resp.getResults();
        	FOUND = list.getNumFound();
        	searches+=1;
        	
        	for(int i=0; (i<list.size() && i<list.getNumFound()); i++) {
        		beans.add(createBean(list.get(i)));
        	}
        	
        	
        	return beans;
        }
        catch (RemoteSolrException e1) {
        	e1.printStackTrace();
        	return beans;
        }
        catch (Exception e) {
        	System.out.println("ERROR");
        	e.printStackTrace();
        	return beans; 
        }
        
        
    }
    
    public List<FacetField> getFacet() {
		return facet;
	}

	public long getFOUND() {
		return FOUND;
	}

	public Map<String, Integer> getRange() {
		return range;
	}

	public ProductBean createBean(SolrDocument sd) {
		Iterator<String> fields = sd.getFieldNames().iterator();
		@SuppressWarnings("rawtypes")
		Iterator values = sd.values().iterator();
		
    	ProductBean b = new ProductBean(fields, values);
    	
    	return b;
    	
    }
    
    public void updateFT(FieldTracker f) {
    	this.ft=f;
    }
    
    public void addSearch() {
    	searches+=1;
    }
    
    public int getSearches() {
    	return searches;
    }
}