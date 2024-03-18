package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


@Controller
@RequestMapping(value = "/homepage", produces = { MediaType.TEXT_HTML_VALUE })
public class HomeController{
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	@GetMapping
	public String getHomeEvents(Model model) {
		List<Event> upcomingEventsHome = new ArrayList<Event>();
		Iterable<Event> events = eventService.findAll();
		Integer MAX = 10000;
		Integer counter = 0;
		
		for (Event event : events) {
			if(counter < 3) {
				if( event.getDate().compareTo(LocalDate.now()) > 0) {
					upcomingEventsHome.add(event);
					counter = counter + 1;
				}
				else if((event.getDate().compareTo(LocalDate.now()) == 0) &&
						event.getTime().compareTo(LocalTime.now()) >= 0) {
						upcomingEventsHome.add(event);
						counter = counter + 1;}
			}
		}
		model.addAttribute("eventsHome", upcomingEventsHome);
		
		Iterable<Venue> venues = venueService.findAll();
		List<Venue> maxVenues = new ArrayList<Venue>();
		List<Venue> venuesCopy = new ArrayList<Venue>();
		List<Integer> count = new ArrayList<Integer>();
		
		for (Venue venue: venues) {
			venuesCopy.add(venue);
			events = eventService.findAllByVenue( venue);
			counter = 0;
			for (@SuppressWarnings("unused") Event event: events) {
				counter = counter + 1;
			}
			count.add(counter);
			
		}
		counter = count.size()-1;
		Integer maxi = 0;
		Integer ind = -1;
		Integer imaxi = 0;
		for (int i = 0; i<3; i++) {
			for (Integer value : count) {
				if(value > maxi && value<=MAX) {
					maxi = value;
					imaxi = ind;
				}
				ind = ind + 1;
			}
			if(counter >= i && imaxi>-1)
				maxVenues.add(venuesCopy.get(imaxi));
			count.set(imaxi, -1);
			ind = 0;
			MAX = maxi;
			maxi = 0;
		}
		
		model.addAttribute("venuesEvents", maxVenues);
		
		
		
		
		return "homepage/index";
	}
	
}