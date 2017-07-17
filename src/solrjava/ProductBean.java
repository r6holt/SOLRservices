package solrjava;

public class ProductBean {
	String id = null;
    String[] fields;
    String[] values;
    
	public ProductBean(String f, String v) {
		f=f.substring(1, f.length()-2);
		fields = f.split(", ");
		
		v=v.substring(1, v.length()-2);
		values = v.split(", ");
		
		for(int i=0; i<fields.length; i++) {
			if(fields[i].equals("id")) {
				id = values[i];
				i=fields.length+1;
			}
		}
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String getId() {
		return id;
	}
	
	public String getValue(int n) {
		return this.values[n];
	}
	
	public String getField(int n) {
		return this.fields[n];
	}
	
	public int numFields() {
		return fields.length;
	}
}