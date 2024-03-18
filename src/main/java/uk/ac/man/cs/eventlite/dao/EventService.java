package uk.ac.man.cs.eventlite.dao;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event saveEvent(Event event);
	
	public Iterable<Event> searchAfter(LocalDate date, String keyword);

	public Iterable<Event> searchBefore(LocalDate date, String keyword);
	
	public Iterable<Event> findAllByDateAfter(LocalDate date);

	public Iterable<Event> findAllByDateBefore(LocalDate date);
	

	public Iterable<Event> findAllByVenue(Venue venue);

	
	public Iterable<Event> findNext3EventsOfVenue(Venue venue, LocalDate date);
	
	public Optional<Event> findById(long id);

	public void delete(Event event);

	public void deleteById(long id);

	public boolean existsById(long id);
	

	public void deleteAll();
	
	public void deleteAll(Iterable<Event>events);

	public void deleteAllById(Iterable<Long> ids);


    public Event update(Event event);


}