package com.krishagni.importcsv.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.krishagni.importcsv.core.ParticipantCsvImporter;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;

@Controller
@RequestMapping("/participantcsv")
public class ParticipantCsvController {
	@Autowired
	ParticipantCsvImporter participantCsvImporter;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<CollectionProtocolRegistrationDetail> importCsv() {
		return participantCsvImporter.importcsv();
	}
}