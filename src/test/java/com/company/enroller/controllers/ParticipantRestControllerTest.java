package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeetingService meetingService;

	@MockBean
	// @Autowired - test integracyjny z serwisem
	private ParticipantService participantService;

	@Test
	public void getParticipants() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		Collection<Participant> allParticipants = singletonList(participant);
		given(participantService.getAll()).willReturn(allParticipants);

		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}

	@Test
	public void getParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		given(participantService.findByLogin("testlogin")).willReturn(participant);

		// mvc.perform(get("/participants/testlogin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		// .andExpect(content().string("TUTAJ WPISZ OCZEKIWANEGO JSONa"));

		// rozne mozliwosci sprawdzenie oczekiwanych danych - ten wydaje sie
		// najfajniszy automatycznie zmianie obiekt na JSona
		mvc.perform(get("/participants/testlogin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(new ObjectMapper().writeValueAsString(participant)));

	}

	@Test
	public void addParticipantTest() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		
		String participantJson = new ObjectMapper().writeValueAsString(participant);

		given(participantService.add(participant)).willReturn(null);

		mvc.perform(post("/participants").content(participantJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string(participantJson));
		
	}
	
	@Test
	public void addParticipantExistiongTest() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		
		String participantJson = new ObjectMapper().writeValueAsString(participant);

		given(participantService.findByLogin("testlogin")).willReturn(participant);

		mvc.perform(post("/participants").content(participantJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
		
	}
	
//	@Test
//	public void deleteParticipantExistiongTest() throws Exception {
//		Participant participant = new Participant();
//		participant.setLogin("testlogin");
//		participant.setPassword("testpassword");
//		
//		String participantJson = new ObjectMapper().writeValueAsString(participant);
//
//		given(participantService.findByLogin("testlogin")).willReturn(participant);
//		veryfi(participantService, never()).finfbyLogin("testk");
//		veryfi(participantService, atLeastOnce()).finfbyLogin("testk");
//
//		mvc.perform(delete("/participants/testlogin").content(participantJson).contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isNoContent());
//		
//	}

}
