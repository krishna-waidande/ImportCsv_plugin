package com.krishagni.participantcsv.core;

import com.krishagni.participantcsv.datasource.DataSource;
import com.krishagni.participantcsv.datasource.Impl.CsvFileDataSource;

public class ParticipantCsvImporter {

	private String filename = "/home/user/Music/participant.csv"; 

	private DataSource dataStore; 

	public void importcsv() {
		dataStore = new CsvFileDataSource(filename);
		while(dataStore.hasNext()) {
			//Record record;
			dataStore.nextRecord();
		}
		dataStore.close();
	}
}