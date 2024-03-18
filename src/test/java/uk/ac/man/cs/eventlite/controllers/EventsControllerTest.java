package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

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
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

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
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyNoInteractions(event);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(venue.getName()).thenReturn("Kilburn Building");

		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getUpcomingEvents() throws Exception {
		when(event.getDate()).thenReturn(LocalDate.now().plusDays(1));
		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAllByDateAfter(LocalDate.now())).thenReturn(Collections.<Event>singletonList(event));
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));
		
		verify(eventService).findAllByDateAfter(LocalDate.now());
		
	}
	
	@Test
	public void getPreviousEvents() throws Exception {
		when(event.getDate()).thenReturn(LocalDate.now().minusDays(1));
		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAllByDateBefore(LocalDate.now())).thenReturn(Collections.<Event>singletonList(event));
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));
		
		verify(eventService).findAllByDateBefore(LocalDate.now());
		
	}
	
	@Test
	public void searchEventNotFound() throws Exception{
		String testKeyword = new String("zzzzz");
		when(eventService.searchBefore(LocalDate.now(), testKeyword)).thenReturn(Collections.<Event> emptyList());
		when(eventService.searchAfter(LocalDate.now(), testKeyword)).thenReturn(Collections.<Event> emptyList());
		
		mvc.perform(get("/events/events?keyword=zzzzz").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/not_found")).andExpect(handler().methodName("search"));
		
		verify(eventService).searchBefore(LocalDate.now(), testKeyword);
		verify(eventService).searchAfter(LocalDate.now(), testKeyword);
		
	}
	
	@Test
	public void searchUpcomingEvent() throws Exception{
		when(event.getDate()).thenReturn(LocalDate.now().plusDays(1));
		String testKeyword = new String("Event");
		when(event.getVenue()).thenReturn(venue);
		when(eventService.searchAfter(LocalDate.now(), testKeyword)).thenReturn(Collections.<Event>singletonList(event));
		
		mvc.perform(get("/events/events?keyword=Event").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("search"));
		
		verify(eventService).searchAfter(LocalDate.now(), testKeyword);
		
	}
	
	@Test
	public void searchPreviousEvent() throws Exception{
		when(event.getDate()).thenReturn(LocalDate.now().minusDays(1));
		String testKeyword = new String("Event");
		when(event.getVenue()).thenReturn(venue);
		when(eventService.searchBefore(LocalDate.now(), testKeyword)).thenReturn(Collections.<Event>singletonList(event));
		
		mvc.perform(get("/events/events?keyword=Event").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("search"));
		
		verify(eventService).searchBefore(LocalDate.now(), testKeyword);
		
	}
	
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreate() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(view().name("events/event_create"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("createNewEvent"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateInvalidName() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "opejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejijk"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateInvalidVenue() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	            		new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "0"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateInvalidTime() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	            		new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj"),
	                    new BasicNameValuePair("time", "32:30")
	            )))))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateInvalidDate() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	            		new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj"),
	                    new BasicNameValuePair("date", "2020-06-15")
	            )))))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateInvalidDescription() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	            		new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "opejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejijopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopejfpiejfopej")
	            )))))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="Admin")
	public void postEventCreateNoAuth() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(status().isForbidden());;
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateNocrsf() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create")
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(status().isForbidden());;
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventCreateEmpty() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(post("/events/event_create").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	            )))))
				.andExpect(view().name("events/event_create"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("createNewEvent"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})

	public void getEventUpdate() throws Exception {
		
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		
		mvc.perform(get("/events/event_update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/event_update")).andExpect(handler().methodName("update"));
	}
	
	@Test
	@WithMockUser(username="Admin", roles= {"ADMINISTRATOR"})
	public void postEventUpdate() throws Exception {
		
		mvc.perform(post("/events/event_update").with(csrf())
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
	                    new BasicNameValuePair("name", "Event1"),
	                    new BasicNameValuePair("venue.id", "1"),
	                    new BasicNameValuePair("time", "12:30"),
	                    new BasicNameValuePair("date", "2025-06-15"),
	                    new BasicNameValuePair("description", "onoijpij  oppoj")
	            )))))
				.andExpect(view().name("events/event_update"))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("update"));
	}


}
