package com.krishagni.importcsv.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.CprErrorCode;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDetail;
import com.krishagni.catissueplus.core.biospecimen.events.VisitDetail;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;
import com.krishagni.catissueplus.core.biospecimen.services.VisitService;
import com.krishagni.catissueplus.core.common.errors.ErrorType;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.importer.domain.ImportJobErrorCode;
import com.krishagni.importcsv.datasource.DataSource;
import com.krishagni.importcsv.datasource.Impl.CsvFileDataSource;

public class CsvImporter {
	private final static String FILE_NAME = "/home/user/Music/participant.csv";
	
	private final static String DATE_FORMAT = "MM/dd/yyyy";
	
	private final static Log logger = LogFactory.getLog(CsvImporter.class);
	
	@Autowired
	private CollectionProtocolRegistrationService cprSvc;
	
	@Autowired
	private VisitService visitService;
	
	private DataSource dataSource;
	
	private int rowCount;

	public void importCsv() {
		dataSource = new CsvFileDataSource(FILE_NAME);
		OpenSpecimenException ose = new OpenSpecimenException(ErrorType.USER_ERROR);
		rowCount = 0;
		try {
		    isHeaderRowValid(dataSource); 
		    while (dataSource.hasNext()) {
		    	Record record = dataSource.nextRecord();
		    	rowCount++;
		    	CollectionProtocolRegistrationDetail cprd = getCPRDetail(record, ose);
		    	cprSvc.createRegistration(new RequestEvent<CollectionProtocolRegistrationDetail>(cprd));
		    	visitService.addVisit(new RequestEvent<VisitDetail>(getVisitDetail(record, cprd)));
		    }
		} catch (Exception e) {
		    logger.error("Error while parsing csv file : " + e.getMessage());
		} finally {
		    if (dataSource != null) {
		    dataSource.close();
		    }
		    ose.checkAndThrow();
		}
	}
	
	private void isHeaderRowValid(DataSource dataSource) throws Exception {
		String[] csvHeaderRow = dataSource.getHeader();
		List<String> expectedHeader = new ArrayList<String>();
		expectedHeader.add("firstName");
		expectedHeader.add("middleName");
		expectedHeader.add("lastName");
		expectedHeader.add("cpId");
		expectedHeader.add("ppId");
		expectedHeader.add("registrationDate");
		expectedHeader.add("name");
		expectedHeader.add("site");

		for (String header : csvHeaderRow) {
			if (!expectedHeader.contains(header)) {
				throw new Exception("Headers of csv file does not match");
			}
		}
	}
	
	private CollectionProtocolRegistrationDetail getCPRDetail(Record record, OpenSpecimenException ose) {
		CollectionProtocolRegistrationDetail cprDetail = new CollectionProtocolRegistrationDetail();
		cprDetail.setParticipant(new ParticipantDetail());
		return populateCPRDetail(record, cprDetail, ose);
	}

	private CollectionProtocolRegistrationDetail populateCPRDetail(Record record, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		cprDetail.getParticipant().setFirstName(record.getValue("firstName"));
		cprDetail.getParticipant().setMiddleName(record.getValue("middleName"));
		cprDetail.getParticipant().setLastName(record.getValue("lastName"));
		setCpId(record.getValue("cpId"), cprDetail, ose);
		setPpID(record.getValue("ppId"), cprDetail, ose);
		setRegistrationDate(record.getValue("registrationDate"), cprDetail, ose);
		return cprDetail;
	}
	
	private VisitDetail getVisitDetail(Record record, CollectionProtocolRegistrationDetail cprd) {
		VisitDetail visitDetail = new VisitDetail();
		visitDetail.setCpId(cprd.getCpId());
		visitDetail.setPpid(cprd.getPpid());
		visitDetail.setName(record.getValue("name"));
		visitDetail.setSite(record.getValue("site"));
		return visitDetail;
	}
	
	private void setCpId(String cpId, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if (StringUtils.isNumeric(cpId)) {
			cprDetail.setCpId(Long.parseLong(cpId));
			return;
		}
		
		logger.error("Invalid CPId at row :" + rowCount);
		ose.addError(CprErrorCode.INVALID_CP_AND_PPID);
	}
	
	private void setPpID(String ppId, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if (StringUtils.isNotBlank(ppId)) {
			cprDetail.setPpid(ppId);
			return;
		}
		
		logger.error("Invalid PPId at row :" + rowCount);
		ose.addError(CprErrorCode.INVALID_CP_AND_PPID);
	}

	private void setRegistrationDate(String registrationDate, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if (StringUtils.isNotBlank(registrationDate)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			try {
			    cprDetail.setRegistrationDate(simpleDateFormat.parse(registrationDate));
			    return;
			} catch (ParseException e) {
			    logger.error("Error while parsing the date at row :" + rowCount);
			    ose.addError(ImportJobErrorCode.RECORD_PARSE_ERROR);
			    return;
			}
		}
		
		logger.error("Registration date is required at row :" + rowCount);
		ose.addError(CprErrorCode.REG_DATE_REQUIRED);
	}
}
