package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Sort;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface venueRepository extends CrudRepository<Venue, Long> {
	public Iterable<Venue> findAll(Sort sort);
	
	public Iterable<Venue> findAllByNameContainingIgnoreCaseOrderByNameAsc(String keyword);
}
