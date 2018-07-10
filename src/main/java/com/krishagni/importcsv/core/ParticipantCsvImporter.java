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
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;
import com.krishagni.catissueplus.core.common.errors.ErrorType;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.importer.domain.ImportJobErrorCode;
import com.krishagni.importcsv.datasource.DataSource;
import com.krishagni.importcsv.datasource.Impl.CsvFileDataSource;

public class ParticipantCsvImporter {
	@Autowired
	private CollectionProtocolRegistrationService cprSvc;
	
	private DataSource dataSource;
	
	private String filename = "/home/user/Music/participant.csv";
	
	private final static Log logger = LogFactory.getLog(ParticipantCsvImporter.class);
	
	static int rowCount;

	public void importcsv() {
		dataSource = new CsvFileDataSource(filename);
		OpenSpecimenException ose = new OpenSpecimenException(ErrorType.USER_ERROR);
		rowCount = 0;
		
		if (isValideHeaders(dataSource)) {
			while (dataSource.hasNext()) {
				Record record = dataSource.nextRecord();
				rowCount++;
				cprSvc.createRegistration(new RequestEvent<CollectionProtocolRegistrationDetail>(getCPRDetail(record, ose)));
			}
		} else {
			logger.error("Headers of csv file not matched");
			ose.addError(ImportJobErrorCode.RECORD_PARSE_ERROR); 
		}
		ose.checkAndThrow();
		dataSource.close();
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
		validateCpId(record.getValue("cpId"), record, cprDetail, ose);
		validatePpID(record.getValue("ppId"), record, cprDetail, ose);
		validateRegistrationDate(record.getValue("registrationDate"), record, cprDetail, ose);
		return cprDetail;
	}
	
	private void validateCpId(String value, Record record, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if (StringUtils.isNumeric(value)) {
			cprDetail.setCpId(Long.parseLong(record.getValue("cpId")));
			return;
		}
		logger.error("Invalide CP Id for record :"+ rowCount);
		ose.addError(CprErrorCode.INVALID_CP_AND_PPID);
	}
	
	private void validatePpID(String value, Record record, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if (StringUtils.isNotBlank(value)) {
			cprDetail.setPpid(record.getValue("ppId"));
			return;
		}
		logger.error("Invalide pp Id for record :"+ rowCount);
		ose.addError(CprErrorCode.INVALID_CP_AND_PPID);
	}

	private void validateRegistrationDate(String value, Record record, CollectionProtocolRegistrationDetail cprDetail, OpenSpecimenException ose) {
		if(StringUtils.isNotBlank(value)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			try {
				 cprDetail.setRegistrationDate(sdf.parse(value.toString()));
				 return;
			} catch (ParseException e) {
				logger.error("Error while parsing the date of record :"+ rowCount);
				ose.addError(ImportJobErrorCode.RECORD_PARSE_ERROR);
				return;
			}
		}
		logger.error("Registration date is required :"+ rowCount);
		ose.addError(CprErrorCode.REG_DATE_REQUIRED);
	}
	
	private boolean isValideHeaders(DataSource dataSource) {
		String[] header = dataSource.getHeader();
		List<String> headers = new ArrayList<String>();
		headers.add("firstName");
		headers.add("middleName");
		headers.add("lastName");
		headers.add("cpId");
		headers.add("ppId");
		headers.add("registrationDate");
		
		for (int i=0; i < header.length ; i++) {
			if (!headers.contains(header[i])) {
				return false;
			}
		}
		return true;	
	}
}
