package com.krishagni.participantcsv.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.krishagni.participantcsv.core.ParticipantCsvImporter;

@Controller
@RequestMapping("/participantcsv")
public class ParticipantCsvController {
	@Autowired
	ParticipantCsvImporter participantCsvImporter;

	@RequestMapping(method = RequestMethod.POST)
	public void importCsv() {
			participantCsvImporter.importcsv();
	}
}