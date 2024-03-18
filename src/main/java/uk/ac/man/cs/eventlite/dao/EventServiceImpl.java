package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	private EventRepository eventRepository; 

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";

	@Override
	public long count() {
		return eventRepository.count();
	}

    @Override
	public Iterable<Event> findAll() {
		Iterable<Event> listEvents = eventRepository.findAll(Sort.by("date").ascending().and(Sort.by("time").ascending()));
		return listEvents;
	}
	
	@Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
	
	@Override
	public Iterable<Event> searchAfter(LocalDate date, String keyword){
		return eventRepository.findAllByDateAfterAndNameContainingIgnoreCaseOrderByDateAscNameAsc(date, keyword);	
	}
	
	@Override
	public Iterable<Event> searchBefore(LocalDate date, String keyword){
		return eventRepository.findAllByDateBeforeAndNameContainingIgnoreCaseOrderByDateAscNameAsc(date, keyword);	
	}
	
	public Iterable<Event> findAllByDateAfter(LocalDate date){
		return eventRepository.findAllByDateAfterOrderByDateAscNameAsc(date);
	}
	
	public Iterable<Event> findAllByDateBefore(LocalDate date){
		return eventRepository.findAllByDateBeforeOrderByDateAscNameAsc(date);
	}
	

	@Override
	public Optional<Event> findById(long id) {
		return eventRepository.findById(id);
	}


	public Iterable<Event> findAllByVenue(Venue venue) {
		return eventRepository.findAllByVenue(venue);
	}
	
    @Override
	public Iterable<Event> findNext3EventsOfVenue(Venue venue, LocalDate date) {
		Iterable<Event> listEvents = eventRepository.findTop3ByVenueAndDateAfterOrderByDateAscTimeAsc(venue, date);
		return listEvents;
	}
	
	@Override
	public Event update(Event event) {
		return eventRepository.save(event);
		
	}
	
	@Override
	public void delete(Event event) {
		// TODO Auto-generated method stub
		eventRepository.delete(event);
		
	}

	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteAll() {
		eventRepository.deleteAll();
	}
	
	@Override
	public void deleteAll(Iterable<Event> events) {
		eventRepository.deleteAll(events);
	}

	@Override
	public void deleteAllById(Iterable<Long> ids){
		eventRepository.deleteAllById(ids);
	}

	@Override
	public boolean existsById(long id) {
		// TODO Auto-generated method stub
		return eventRepository.existsById(id);
	}

}