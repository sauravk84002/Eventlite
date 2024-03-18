package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


@Service
public class VenueServiceImpl implements VenueService {

	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";
	
	@Autowired
	private venueRepository venueRepository;

	@Override
	public long count() {

		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		
		Iterable<Venue> listVenues = venueRepository.findAll(Sort.by("name").ascending());
		return listVenues;

	}
	
	@Override
	public Venue saveVenue(Venue venue) {
		return venueRepository.save(venue);
	}
	
	public Venue findById(Long id) {
		return venueRepository.findById(id).get();
		
	}
	
	/*
	public void deleteAll() {
		venueRepository.deleteAll();
		
	}
	*/
	
	@Override
	public void delete(Venue venue) {
		venueRepository.delete(venue);
	}
	
	@Override
	public void deleteById(long id) {
		venueRepository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		venueRepository.deleteAll();
	}

	@Override
	public void deleteAll(Iterable<Venue> venues) {
		venueRepository.deleteAll(venues);
	}

	@Override
	public void deleteAllById(Iterable<Long> ids) {
		venueRepository.deleteAllById(ids);
	}

	@Override
	public boolean existsById(long id) {
		return venueRepository.existsById(id);
	}

	
	
	
	@Override
	public Optional<Venue> findById(long id){
		return venueRepository.findById(id);
	}

	@Override
	public Iterable<Venue> searchVenues(String keyword){
		return venueRepository.findAllByNameContainingIgnoreCaseOrderByNameAsc(keyword);	
	}
	
	@Override
	public Venue update(Venue venue) {
		return venueRepository.save(venue);
		
	}

}

