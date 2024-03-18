package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesController.class)
@Import(Security.class)
public class VenuesControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;
	
	
	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		verifyNoInteractions(venue);
	}
	
	@Test
	public void getVenueUpcomingDate() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		Event e = new Event();
		LocalDate d = LocalDate.now().plusDays(5);
		LocalTime t = LocalTime.now();
		
		e.setId(0);
		e.setName("Event");
		e.setDescription("Description");
		e.setDate(d);
		e.setTime(t);
		e.setVenue(v);
		
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findAllByVenue(v)).thenReturn(Collections.<Event>singletonList(e));
		
		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/venue-information")).andExpect(handler().methodName("getVenue"));
	}
	
	@Test
	public void getVenueUpcomingTime() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Kilburn Building");
		v.setAddress("Kilburn Building University of Manchester, Oxford Rd, Manchester");
		v.setPostalCode("M13 9PL");
		v.setCapacity(200);
		
		Event e = new Event();
		LocalDate d = LocalDate.now();
		LocalTime t = LocalTime.now().plusMinutes(5);
		
		e.setId(0);
		e.setName("Event");
		e.setDescription("Description");
		e.setDate(d);
		e.setTime(t);
		e.setVenue(v);
		
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findAllByVenue(v)).thenReturn(Collections.<Event>singletonList(e));
		
		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/venue-information")).andExpect(handler().methodName("getVenue"));
	}

	@Test
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenue"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void getVenueCreate() throws Exception {
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());
		
		mvc.perform(get("/venues/venue_create").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/venue_create")).andExpect(handler().methodName("createNewVenue"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postVenueCreate() throws Exception {
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());
		
		mvc.perform(post("/venues/venue_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "Kilburn Building"),
	                    new BasicNameValuePair("address", "Kilburn Building University of Manchester, Oxford Rd, Manchester"),
	                    new BasicNameValuePair("postalCode", "M13 9PL"),
	                    new BasicNameValuePair("capacity", "200")
	            )))))
				.andExpect(view().name("venues/venue_create"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("createNewVenue"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postVenueCreateEmpty() throws Exception {
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());
		
		mvc.perform(post("/venues/venue_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    
	            )))))
				.andExpect(view().name("venues/venue_create"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("createNewVenue"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void getVenueUpdate() throws Exception {
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());
		
		mvc.perform(get("/venues/venue_update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/venue_update")).andExpect(handler().methodName("update"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postVenueUpdate() throws Exception {
		
		mvc.perform(post("/venues/venue_update").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("id", "0"),
	                    new BasicNameValuePair("name", "Kilburn Building"),
	                    new BasicNameValuePair("address", "Kilburn Building University of Manchester, Oxford Rd, Manchester"),
	                    new BasicNameValuePair("postalCode", "M13 9PL"),
	                    new BasicNameValuePair("capacity", "200")
	            )))))
				.andExpect(view().name("venues/venue_update"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("update"));
	}
	
	@Test
	public void searchVenueNotFound() throws Exception{
		String testKeyword = new String("zzzzz");

		when(venueService.searchVenues(testKeyword)).thenReturn(Collections.<Venue> emptyList());
		
		mvc.perform(get("/venues/venues?keyword=zzzzz").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("search"));
		
		verify(venueService).searchVenues(testKeyword);
	}
	
	@Test
	public void searchVenues() throws Exception{
		when(event.getDate()).thenReturn(LocalDate.now().plusDays(1));
		String testKeyword = new String("Venue");
		when(event.getVenue()).thenReturn(venue);
		when(venueService.searchVenues(testKeyword)).thenReturn(Collections.<Venue>singletonList(venue));
		
		mvc.perform(get("/venues/venues?keyword=Venue").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/index")).andExpect(handler().methodName("search"));
		
		verify(venueService).searchVenues(testKeyword);
		
	}
	

}
