package com.krishagni.participantcsv.core;

import java.util.HashMap;
import java.util.Map;

public class Record {
	private Map<String, Object> record = new HashMap<String, Object>();

	public Map<String, Object> get() {
		return record;
	}
	
	public Object getValue(String column) {
		return record.get(column);
	}
	
	public void addValue(String column, Object value) {
		record.put(column, value);
	}
}
