package com.krishagni.participantcsv.datasource.Impl;

import com.krishagni.catissueplus.core.common.util.CsvFileReader;
import com.krishagni.participantcsv.datasource.DataSource;

public class CsvFileDataSource implements DataSource {
	private CsvFileReader csvReader;
	
	public CsvFileDataSource(String filename) {
		this.csvReader = CsvFileReader.createCsvFileReader(filename, true);
	}
	
	@Override
	public void nextRecord() {
		getRecord(csvReader.getColumnNames(), csvReader.getRow());
	}
	
	private void getRecord(String[] columnNames, String[] row) {
		/*Record record = new Record();
		for (int i = 0; i <row.length; i++) {
			record.addValue(columnNames[i], row[i]);
		}
		return record;*/
		for(int i=0; i< columnNames.length ; i++) {
			System.out.println(columnNames[i]+":"+row[i]);
		}
	}
	
	@Override
	public boolean hasNext() {
		return csvReader.next();
	}
	
	@Override
	public void close() {
		csvReader.close();
	}
}