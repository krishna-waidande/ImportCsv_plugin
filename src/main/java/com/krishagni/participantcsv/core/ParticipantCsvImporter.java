package com.krishagni.participantcsv.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDetail;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.participantcsv.datasource.DataSource;
import com.krishagni.participantcsv.datasource.Impl.CsvFileDataSource;

public class ParticipantCsvImporter {
	@Autowired
	private CollectionProtocolRegistrationService cprSvc;
	
	private DataSource dataSource;
	
	private final static Log logger = LogFactory.getLog(ParticipantCsvImporter.class);
	
	private String filename = "/home/user/Music/participant.csv";
	
	
	public void importcsv() {
		dataSource = new CsvFileDataSource(filename);
		
		try {
		    while (dataSource.hasNext()) {
			Record record = dataSource.nextRecord();
			cprSvc.createRegistration(new RequestEvent<CollectionProtocolRegistrationDetail>(getCPRDetail(record)));
		    }
		} catch (Exception e) {
		    logger.error("Error while processing: " + e.getMessage());
		} finally {
		    if (dataSource != null) {
			dataSource.close();
		    }
		}
	}
	
	private CollectionProtocolRegistrationDetail getCPRDetail(Record record) {
		CollectionProtocolRegistrationDetail cprDetail = new CollectionProtocolRegistrationDetail();
		cprDetail.setParticipant(new ParticipantDetail());
		return populateCPRDetail(record, cprDetail);
	}

	private CollectionProtocolRegistrationDetail populateCPRDetail(Record record, CollectionProtocolRegistrationDetail cpRegistrationDetail) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");  
		
		cpRegistrationDetail.setCpId(Long.parseLong(record.getValue("cpId")));
		cpRegistrationDetail.getParticipant().setFirstName(record.getValue("firstName"));
		cpRegistrationDetail.getParticipant().setMiddleName(record.getValue("middleName"));
		cpRegistrationDetail.getParticipant().setLastName(record.getValue("lastName"));
		cpRegistrationDetail.setPpid(record.getValue("ppId"));
		
		try {  
	        	Date registrationDate = formatter.parse(record.getValue("registrationDate"));
	            	cpRegistrationDetail.setRegistrationDate(registrationDate);
	    	} catch (ParseException e) {
	    		e.printStackTrace();
	    	}
		return cpRegistrationDetail;
	}
}
