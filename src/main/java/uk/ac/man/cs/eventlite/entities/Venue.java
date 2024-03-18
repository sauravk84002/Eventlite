package uk.ac.man.cs.eventlite.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
@Table(name = "venues")
public class Venue {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	
	private String address;
	
	private String postalCode;

	private double longitude;
	
	private double latitude;
	
	private int capacity;

	public Venue() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
		if(this.postalCode != null) {
			setLocation(this);
		}
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		if(this.address != null) {
			setLocation(this);
		}
	}
	
	public void setLongitude(double value) {
		this.longitude = value;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLatitude(double value) {
		this.latitude = value;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void setLocation(Venue venue) {
		String address = venue.getAddress();
		String postcode = venue.getPostalCode();
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1IjoiZDNubmlzdXR1IiwiYSI6ImNsMm0xYmpyYTBpcnAzYm11N3JuY2k0c3MifQ.xKR2OruW0ljKvKjBnusWvg")
				.query(address+", "+postcode)
				.build();
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
		 
				List<CarmenFeature> results = response.body().features();
		 
				if (results.size() > 0) {
				  // Log the first results Point.
				  Point firstResultPoint = results.get(0).center();
				  venue.setLongitude(firstResultPoint.longitude());
				  venue.setLatitude(firstResultPoint.latitude());
		 
				} else {
				  // No result for your request were found.
				  venue.setLongitude(0.0);
				  venue.setLatitude(0.0);
				}
			}
		 
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		try {
		    Thread.sleep(5000L);
		} catch(InterruptedException e) {
		    System.out.println("got interrupted!");
		}
	}

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}
}
