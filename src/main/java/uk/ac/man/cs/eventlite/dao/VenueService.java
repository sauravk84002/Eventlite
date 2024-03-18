package uk.ac.man.cs.eventlite.dao;

import java.time.LocalDate;
import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue saveVenue(Venue venue);
	
	public Venue findById(Long id);
	
	public Optional<Venue> findById(long id);
	
	public Iterable<Venue> searchVenues(String keyword);
	
	public Venue update(Venue venue);
	
	public void delete(Venue venue);

	public void deleteById(long id);

	public void deleteAll();

	public void deleteAll(Iterable<Venue> venues);

	public void deleteAllById(Iterable<Long> ids);
	
	public boolean existsById(long id);

	
}
