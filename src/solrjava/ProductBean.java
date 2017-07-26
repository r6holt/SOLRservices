package solrjava;

import java.util.ArrayList;
import java.util.Iterator;

public class ProductBean {
	private String id = null;
    private ArrayList<String> fields = new ArrayList<String>();
    private ArrayList<String> values = new ArrayList<String>();
    
	@SuppressWarnings("rawtypes")
	public ProductBean(Iterator f, Iterator v) {
		
		while(f.hasNext()) {
			String f1 = (String) f.next();
			String v1 = v.next().toString();
			
			if(f1.equals("id")) {
				id = v1;
			}
			
			fields.add(f1);
			values.add(v1);
		}
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	public String getId() {
		return id;
	}
	
	public String getValue(int n) {
		return values.get(n);
	}
	
	public String getField(int n) {
		return fields.get(n);
	}
	
	public int numFields() {
		return fields.size();
	}
}