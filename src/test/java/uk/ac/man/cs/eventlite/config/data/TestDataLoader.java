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
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			// Build and save test events and venues here.
			// The test database is configured to reside in memory, so must be initialized
			// every time.
			if (eventService.count() > 0) {
                log.info("Database already populated with venues. Skipping venue initialization.");
            } else {
                Venue first_venue = new Venue();
                first_venue.setId(1);
                first_venue.setName("Kilburn Building");
                first_venue.setCapacity(120);

                Venue second_venue = new Venue();
                second_venue.setId(2);
                second_venue.setName("Online");
                second_venue.setCapacity(100000);

                venueService.saveVenue(first_venue);
                venueService.saveVenue(second_venue);

                // Build and save initial events here.
                Event first_event = new Event();
                first_event.setId(1);
                first_event.setName("First event");
                first_event.setVenue(first_venue);
                first_event.setDate(LocalDate.now());
                first_event.setTime(LocalTime.now());

                Event second_event = new Event();
                second_event.setId(2);
                second_event.setName("Second event");
                second_event.setVenue(first_venue);
                second_event.setDate(LocalDate.now());
                second_event.setTime(LocalTime.now());

                Event third_event = new Event();
                third_event.setId(3);
                third_event.setName("Third event");
                third_event.setVenue(first_venue);
                third_event.setDate(LocalDate.now());
                third_event.setTime(LocalTime.now());

                eventService.saveEvent(first_event);
                eventService.saveEvent(second_event);
                eventService.saveEvent(third_event);
            }

		};
	}
}
