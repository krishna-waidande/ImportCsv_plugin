package com.krishagni.participantcsv.datasource;

public interface DataSource {
	void nextRecord();
	
	boolean hasNext();
	
	void close();
}