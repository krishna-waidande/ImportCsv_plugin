package com.krishagni.participantcsv.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.participantcsv.core.ParticipantCsvImporter;

@Controller
@RequestMapping("/participantcsv")
public class ParticipantCsvController {
	@Autowired
	ParticipantCsvImporter participantCsvImporter;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String importCsv() {
		participantCsvImporter.importcsv();
		return "Participants are added to OS";
	}
}
