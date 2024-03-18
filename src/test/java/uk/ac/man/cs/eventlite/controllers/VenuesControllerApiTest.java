package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesControllerApi.class)
@Import({ Security.class, EventModelAssembler.class, VenueModelAssembler.class })
public class VenuesControllerApiTest {
	
	@Mock
	private Venue venue;
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;
	
	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));
	}
	
	@Test
	public void getIndexWithVenues() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)));

		verify(venueService).findAll();
	}
	
	@Test
	public void getVenueById() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		
		mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", equalTo("Kilburn Building")))
				.andExpect(jsonPath("$.address", equalTo("Kilburn Building University of Manchester, Oxford Rd, Manchester")))
				.andExpect(jsonPath("$.postalCode", equalTo("M13 9PL")))
				.andExpect(jsonPath("$.capacity", equalTo(200)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
				.andExpect(handler().methodName("getVenue"));
		
		verify(venueService).findById(0);
	}
	
	@Test
	public void getVenueByIdWhenNotFound() throws Exception {
		
		mvc.perform(get("/api/venues/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getVenue"));
	}
	
	@Test
	public void getVenueEvents() throws Exception {
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
		
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findAllByVenue(v)).thenReturn(Collections.<Event>singletonList(e));
		
		mvc.perform(get("/api/venues/0/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)))
				.andExpect(jsonPath("$._embedded.events[0].name", equalTo("Event")))
				.andExpect(jsonPath("$._embedded.events[0].description", equalTo("Description")))
				.andExpect(jsonPath("$._embedded.events[0].date", equalTo(d.toString())))
				.andExpect(jsonPath("$._embedded.events[0].time", containsString(t.toString())))
				.andExpect(jsonPath("$._embedded.events[0]._links.self.href", endsWith("/api/events/0")))
				.andExpect(jsonPath("$._embedded.events[0]._links.event.href", endsWith("/api/events/0")))
				.andExpect(jsonPath("$._embedded.events[0]._links.venue.href", endsWith("/api/events/0/venue")))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/events")))
				.andExpect(handler().methodName("getVenueEvents"));
		
		verify(venueService).findById(0);
		verify(eventService).findAllByVenue(v);
	}
	
	@Test
	public void getVenueEventsWhenNotFound() throws Exception {
		
		mvc.perform(get("/api/venues/99/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getVenueEvents"));
	}
	
	@Test
	public void getVenueNext3Events() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		ArrayList<Event> elist = new ArrayList<Event>(2);
		
		for (int i = 0; i < 2; i++) {
			Event e = new Event();
			LocalDate d = LocalDate.now();
			LocalTime t = LocalTime.now();
			
			e.setId(i);
			e.setName("Event");
			e.setDescription("Description");
			e.setDate(d);
			e.setTime(t);
			e.setVenue(v);
			
			elist.add(e);
		}
		
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findNext3EventsOfVenue(v, LocalDate.now())).thenReturn(elist);
		
		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.events.length()", lessThanOrEqualTo(3)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")))
				.andExpect(handler().methodName("getNext3Events"));
		
		verify(venueService).findById(0);
		verify(eventService).findNext3EventsOfVenue(v, LocalDate.now());
	}
	
	@Test
	public void getVenueNext3EventsWhenNotFound() throws Exception {
		
		mvc.perform(get("/api/venues/99/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getNext3Events"));
	}
	
}
