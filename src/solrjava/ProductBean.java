package solrjava;

import java.util.ArrayList;
import java.util.Iterator;

public class ProductBean {
	private String id = null;
    private ArrayList<String> fields = new ArrayList<String>();
    private ArrayList<Object> values = new ArrayList<Object>();
    
	@SuppressWarnings("rawtypes")
	public ProductBean(Iterator f, Iterator v) {
		
		while(f.hasNext()) {
			String f1 = (String) f.next();
			Object v1 = v.next();
			
			if(f1.equals("id")) {
				id = v1.toString();
			}
			
			fields.add(f1);
			values.add(v1);
		}
		
		sort();
	}
	
	public ProductBean(String id) {
		this.id=id;
		fields.add("id");
		values.add(id);
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	public void setValues(ArrayList<Object> values) {
		this.values = values;
	}

	public String getId() {
		return id;
	}
	
	public Object getValue(int n) {
		return values.get(n);
	}
	
	public String getField(int n) {
		return fields.get(n);
	}
	
	public int numFields() {
		return fields.size();
	}
	
	public void addField(String n, String v) {
		fields.add(n);
		values.add(v);
	}
	
	public void setValue(int i, Object o) {
		values.set(i, o);
	}
	
	public void sort() {
		values.remove(fields.indexOf("id"));
		fields.remove("id");
		
		for(int i=0; i<numFields(); i++) {
			String low = getField(i);
			int index = i;
			for(int j=i; j<numFields(); j++) {
				if(low.compareTo(getField(j))>0) {
					low=getField(j);
					index=j;
				}
			}
			String temp = getValue(index).toString();
			fields.set(index, getField(i));
			fields.set(i, low);
			values.set(index, getValue(i));
			values.set(i, temp);
		}
		
		fields.add(0, "id");
		values.add(0, id);
	}
}