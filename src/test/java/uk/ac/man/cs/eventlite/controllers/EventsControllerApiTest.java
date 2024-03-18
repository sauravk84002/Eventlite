package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsControllerApi.class)
@Import({ Security.class, EventModelAssembler.class, VenueModelAssembler.class })
public class EventsControllerApiTest {
	
	@Mock
	private Venue venue;
	
	@Mock
	private Event event;
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private EventService eventService;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")));

		verify(eventService).findAll();
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		Event e = new Event();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(new Venue());
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));

		mvc.perform(get("/api/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllEvents")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)));

		verify(eventService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/api/events/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("event 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getEventById() throws Exception {
		Event e = new Event();
		LocalDate d = LocalDate.now();
		LocalTime t = LocalTime.now();
		
		e.setId(0);
		e.setName("Event");
		e.setDescription("Description");
		e.setDate(d);
		e.setTime(t);
		e.setVenue(new Venue());
		
		when(eventService.findById(0)).thenReturn(Optional.of(e));
		
		mvc.perform(get("/api/events/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", equalTo("Event")))
				.andExpect(jsonPath("$.description", equalTo("Description")))
				.andExpect(jsonPath("$.date", equalTo(d.toString())))
				.andExpect(jsonPath("$.time", containsString(t.toString())))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/events/0")))
				.andExpect(jsonPath("$._links.event.href", endsWith("/api/events/0")))
				.andExpect(jsonPath("$._links.venue.href", endsWith("/api/events/0/venue")))
				.andExpect(handler().methodName("getEvent"));
		
		verify(eventService).findById(0);
	}
	
	@Test
	public void getEventVenueWhenNotFound() throws Exception {
		
		mvc.perform(get("/api/events/99/venue").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("event 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getEventVenue"));
	}
	
	@Test
	public void getEventVenue() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		Event e = new Event();
		LocalDate d = LocalDate.now();
		LocalTime t = LocalTime.now();
		
		e.setId(0);
		e.setName("Event");
		e.setDescription("Description");
		e.setDate(d);
		e.setTime(t);
		e.setVenue(v);
		
		when(eventService.findById(0)).thenReturn(Optional.of(e));
		
		mvc.perform(get("/api/events/0/venue").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", equalTo("Kilburn Building")))
				.andExpect(jsonPath("$.address", equalTo("Kilburn Building University of Manchester, Oxford Rd, Manchester")))
				.andExpect(jsonPath("$.postalCode", equalTo("M13 9PL")))
				.andExpect(jsonPath("$.capacity", equalTo(200)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
				.andExpect(handler().methodName("getEventVenue"));
		
		verify(eventService).findById(0);
	}
	
//	@Test
//	public void searchUpcomingEvent() throws Exception{
//		Venue v = new Venue();
//		v.setId(0);
//		v.setName("Kilburn Building");
//		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
//		v.setPostalCode("M13 9PL");
//		v.setCapacity(200);
//		
//		Event e = new Event();
//		LocalDate d = LocalDate.now().plusDays(1);
//		LocalTime t = LocalTime.now();
//		
//		e.setId(0);
//		e.setName("Event");
//		e.setDescription("Description");
//		e.setDate(d);
//		e.setTime(t);
//		e.setVenue(v);
//		
//		String testKeyword = new String("Event");
//		
//		when(eventService.searchAfter(LocalDate.now(), testKeyword)).thenReturn(Collections.<Event>singletonList(e));
//		
//		mvc.perform(get("/events/events?keyword=Event").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
//		.andExpect(view().name("events/index"))
//		.andExpect(handler().methodName("search"))
//		.andExpect(jsonPath("$.name", equalTo("Event")))
//		.andExpect(jsonPath("$.descritpion", equalTo("Description")))
//		.andExpect(jsonPath("$._links.self.href", endsWith("/api/events")));
//		
//		verify(eventService).searchAfter(LocalDate.now(), testKeyword);
//		
//	}
	
}
