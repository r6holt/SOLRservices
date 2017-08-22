package solrjava;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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
	private List<String> spellcheck;
	
    public Query(FieldTracker ft) {
    	this.ft=ft;
    }
    
    public ArrayList<ProductBean> acceptQuery(String q, int START, int ROWS) throws SolrServerException, IOException{
        HttpSolrClient solr = new HttpSolrClient.Builder(GUI.urlString).build();
        solr.setParser(new XMLResponseParser());
        ft.update();
        
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
        query.set("sort", ft.getSortfield()+" "+ft.isSort());
        
        
        //set price and category facets
        query.setFacet(true);
        ArrayList<String> facets = new ArrayList<String>();
        for(int i=0; i<ft.numFields(); i++) {
        	if(ft.getDatatypes().get(i).contains("string") && !ft.getField(i).equals("_version_") && !ft.getField(i).equals("id")) {
        		facets.add(ft.getField(i));
        	}
        }
        query.set("facet.field", facets.toArray(new String[facets.size()]));
        if(ft.getPrice()) {
        	query.set("facet.query", "price:[0 TO 9.99]", "price:[10 TO 24.99]", "price:[25 TO 49.99]", "price:[50 TO 99.99]", "price:[100 TO 199.99]", "price:[200 TO 499.99]", "price:[500 TO *]");
        }
        
        // spatial
        if(ft.getSortfield().equals("location")) {
            query.set("pt", ft.getLat()+", "+ft.getLon());
            query.set("sfield", ft.getLocatefield());
            query.set("sort", "geodist() "+ft.isSort());
        }
        else {
        	query.set("sort", ft.getSortfield()+" "+ft.isSort());
        }
        
        // set price sort
        if(ft!=null && ft.getPriceQuery()) {
        	if(ft.getMinPrice()==-1) {
        		fq+="-price:["+ft.getMaxPrice()+" TO *], ";
        	}
        	else if(ft.getMaxPrice()==-1) {
        		fq+= "-price:[* TO "+ft.getMinPrice()+"], ";
        	}
        	else {
        		fq+="-price:[* TO "+ft.getMinPrice()+"], -price:["+ft.getMaxPrice()+" TO *], ";
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
        	if(ft.getDatatypes().get(ft.getFields().indexOf(ft.getCategory())).equals("location")) {
        		if(q.equals("")) query.setQuery(ft.getCategory()+":[-90,-180 TO 90,180]");//query.setQuery("{!geofilt sfield="+ft.getCategory()+" pt="+q.replace(" ", "")+" d="+100+"}");
        		else /*query.setQuery(ft.getCategory()+":[-90,-180 TO 90,180]")*/ {
        			query.set("sfield", ft.getCategory());
        			query.set("pt", q);
        			query.set("d", 100);
        			query.setQuery("{!geofilt}");
        		}
        	}
        }
        
        // facet filtering by price and category
        if(!ft.getPricechoice().equals("")) {
        	String[] subsets = ft.getPricechoice().split(" ");
        	String tot = "";
        	
        	for(String s: subsets) {        		
	        	if(s.equals("0")) {
	        		tot+="price:[0 TO 9.99], ";
	        	}
	        	else if(s.equals("10")) {
	        		tot+= "price:[10 TO 24.99], ";
	        	}
	        	else if(s.equals("25")) {
	        		tot+= "price:[25 TO 49.99], ";
	        	}
	        	else if(s.equals("50")) {
	        		tot+= "price:[50 TO 99.99], ";
	        	}
	        	else if(s.equals("100")) {
	        		tot+= "price:[100 TO 199.99], ";
	        	}
	        	else if(s.equals("200")) {
	        		tot+= "price:[200 TO 499.99], ";
	        	}
	        	else if(s.equals("500")) {
	        		tot+= "price:[500 TO *], ";
	        	}
        	}
        	fq+=tot;
        }
        
        facets.clear();
        // controls selection of faceted fields
        if(ft.getFacetchoice()!=null) {//ft.getFacetchoice()[0].equals("facet")) {
        	for(int h=0; h<ft.getFacetchoice().length; h++) {
        		if(ft.getFacetchoice()[h]!=null) {
		        	String[] subsets = ft.getFacetchoice()[h].split("~~~");//[0].split(", ");
		        	
		        	for(int i=0; i<subsets.length; i++) {
		        		facets.add(ft.getChoices().get(h)+":"+subsets[i]);
		        	}
        		}
        	}
        	
        	facets.add(fq);
        	
        	query.set("fq", facets.toArray(new String[facets.size()]));
        	ft.resetChoices();
        }
        else {
            query.set("fq", fq);
        }
        
        //Getting results
        ArrayList<ProductBean> beans = new ArrayList<ProductBean>();
        try {
        	QueryResponse resp = solr.query(query);
        	facet = resp.getFacetFields();
        	range = resp.getFacetQuery();
        	if(resp.getSpellCheckResponse()!=null && !resp.getSpellCheckResponse().getSuggestions().isEmpty()) spellcheck = resp.getSpellCheckResponse().getSuggestions().get(0).getAlternatives();
        	else spellcheck=null;
        	SolrDocumentList list =resp.getResults();
        	FOUND = list.getNumFound();
        	searches+=1;
        	
        	for(int i=0; (i<list.size() && i<list.getNumFound()); i++) {
        		beans.add(createBean(list.get(i)));
        	}
        	
        	return beans;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(new JFrame(), "An error occured while attempting\nto search.");
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
	
	public List<String> getSpellcheck() {
		return spellcheck;
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