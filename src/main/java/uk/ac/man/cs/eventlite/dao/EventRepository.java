package uk.ac.man.cs.eventlite.dao;


import java.time.LocalDate;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long>{
	public Iterable<Event> findAll(Sort sort);
	
	public Iterable<Event> findAllByNameContainingOrderByDateAscNameAsc(String keyword);
	
	public Iterable<Event> findAllByDateAfterAndNameContainingIgnoreCaseOrderByDateAscNameAsc(LocalDate date, String keyword);
	
	public Iterable<Event> findAllByDateBeforeAndNameContainingIgnoreCaseOrderByDateAscNameAsc(LocalDate date, String keyword);
	
	public Iterable<Event> findAllByDateAfterOrderByDateAscNameAsc(LocalDate date);

	public Iterable<Event> findAllByDateBeforeOrderByDateAscNameAsc(LocalDate date);
	
	public Iterable<Event> findAllByVenue(Venue venue);
	
	public Iterable<Event> findTop3ByVenueAndDateAfterOrderByDateAscTimeAsc(Venue venue, LocalDate date);
}
