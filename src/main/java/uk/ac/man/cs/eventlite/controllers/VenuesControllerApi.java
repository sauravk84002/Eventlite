package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.dao.EventRepository;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": %d }";

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@Autowired
	private EventModelAssembler eventAssembler;
	
	@Autowired
	private VenueModelAssembler venueAssembler;

	@ExceptionHandler(VenueNotFoundException.class)
	public ResponseEntity<?> venueNotFoundHandler(VenueNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
	}

	@GetMapping("/{id}")
	public EntityModel<Venue> getVenue(@PathVariable("id") long id) {
		Optional<Venue> venueOptional = venueService.findById(id);
		
		Venue venue;
		if (venueOptional.isPresent()) {
			venue = venueOptional.get();
		}
		else {
			throw new VenueNotFoundException(id);
		}

		return venueAssembler.toModel(venue);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteVenue(@PathVariable("id") long id) {
		if (!venueService.existsById(id)) {
			throw new VenueNotFoundException(id);
		}

		venueService.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteAllVenues() {
		venueService.deleteAll();

		return ResponseEntity.noContent().build();
	}
	

	@GetMapping
	public CollectionModel<EntityModel<Venue>> getAllVenues() {
		return venueAssembler.toCollectionModel(venueService.findAll())
				.add(linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel(),
				     Link.of("http://localhost:8080/api/profile/venues").withRel("profile"));
	}
	
	@GetMapping("/{id}/events")
	public CollectionModel<EntityModel<Event>> getVenueEvents(@PathVariable("id") long id) {
		Optional<Venue> venueOptional = venueService.findById(id);
		
		Venue venue;
		if (venueOptional.isPresent()) {
			venue = venueOptional.get();
		}
		else {
			throw new VenueNotFoundException(id);
		}
		
		Iterable<Event> venueEvents =  eventService.findAllByVenue(venue);
		
		return eventAssembler.toCollectionModel(venueEvents)
				.add(linkTo(methodOn(VenuesControllerApi.class).getVenueEvents(id)).withSelfRel());
	}
	
	@GetMapping("/{id}/next3events")
	public CollectionModel<EntityModel<Event>> getNext3Events(@PathVariable("id") long id) {
		Optional<Venue> venueOptional = venueService.findById(id);
		
		Venue venue;
		if (venueOptional.isPresent()) {
			venue = venueOptional.get();
		}
		else {
			throw new VenueNotFoundException(id);
		}
		
		LocalDate date = LocalDate.now( ZoneId.of( "Europe/London" ) ) ;
		
		Iterable<Event> venueEvents =  eventService.findNext3EventsOfVenue(venue, date);
		
		return eventAssembler.toCollectionModel(venueEvents)
				.add(linkTo(methodOn(VenuesControllerApi.class).getNext3Events(id)).withSelfRel());
	}


}
