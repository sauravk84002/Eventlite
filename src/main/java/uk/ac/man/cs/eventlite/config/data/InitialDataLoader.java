package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				Venue kilburn = new Venue();
				kilburn.setCapacity(50);
				kilburn.setId(1);
				kilburn.setName("Kilburn");
				kilburn.setAddress("Oxford Road");
				kilburn.setPostalCode("M13 9PL");
				venueService.saveVenue(kilburn);
			}
			
			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				Venue kilburn = new Venue();
				kilburn.setCapacity(50);
				kilburn.setId(2);
				kilburn.setName("KilburnE");
				kilburn.setAddress("Oxford Road");
				kilburn.setPostalCode("M13 9PL");
				venueService.saveVenue(kilburn);
				Venue mecd = new Venue();
				mecd.setCapacity(50);
				mecd.setId(1);
				mecd.setName("MECD");
				mecd.setAddress("Booth Street");
				mecd.setPostalCode("M13 9XH");
				venueService.saveVenue(mecd);
				Event showcase = new Event();
				showcase.setId(1);
				showcase.setName("COMP23412 showcase G18");
				showcase.setTime(LocalTime.of(15, 00));
				showcase.setDate(LocalDate.of(2022, 3, 4));
				showcase.setVenue(mecd);
				eventService.saveEvent(showcase);
				Event event1 = new Event();
				showcase.setId(3);
				showcase.setName("Event Alpha");
				showcase.setTime(LocalTime.of(15, 00));
				showcase.setDate(LocalDate.of(2022, 6, 4));
				showcase.setVenue(mecd);
				eventService.saveEvent(showcase);
				Event event2 = new Event();
				showcase.setId(4);
				showcase.setName("Event Beta");
				showcase.setTime(LocalTime.of(15, 00));
				showcase.setDate(LocalDate.of(2022, 6, 4));
				showcase.setVenue(mecd);
				eventService.saveEvent(showcase);
				Event event3 = new Event();
				showcase.setId(5);
				showcase.setName("Event Alpha");
				showcase.setTime(LocalTime.of(15, 00));
				showcase.setDate(LocalDate.of(2022, 3, 4));
				showcase.setVenue(mecd);
				eventService.saveEvent(showcase);
				Event groupProject = new Event();
				groupProject.setId(2);
				groupProject.setName("Group meeting");
				groupProject.setTime(LocalTime.of(15, 00));
				groupProject.setDate(LocalDate.of(2022, 3, 4));
				groupProject.setVenue(mecd);
				eventService.saveEvent(groupProject);
			}
		};
	}
}