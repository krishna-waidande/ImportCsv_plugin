package com.krishagni.participantcsv.datasource;

import com.krishagni.participantcsv.core.Record;

public interface DataSource {
	Record nextRecord();
	
	boolean hasNext();
	
	void close();
}