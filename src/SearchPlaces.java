

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;
/**
 * <h3>Google places search class</h3>
 * <p>
 * This object uses the google places API to get information about a given search term.
 * The search method returns the place_id of the search result and passes it to the details.
 * The details method uses the place_id to get the address and coordinates of the given place_id.
 * Retrieving the desired information is accomplished by reading the returned Json object and parsing it with the split() method.
 * </p>
 * 
 * @param String
 * @return String
 * @author Ferdinand Haaben
 */
public class SearchPlaces {

	public static final String API_KEY =  "AIzaSyAM128clPaigy48Jioynb75QPVof_savbY";
	
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	
	
	public SearchPlaces() {
		
	}
	
	
	public String details(String place_id, String searchTerm) throws IOException {
		
		URL url = new URL(PLACES_API_BASE + "/details/json?place_id="+ place_id + "&fields=formatted_address,geometry&key=" + API_KEY);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String response = in.lines().collect(Collectors.joining());
		
		in.close();
		
		String address = response.split("formatted_address\" : \"")[1].split("\"")[0];
		
		String lat = response.split("lat\" : ")[1].split("[,]")[0];
				
		String lng = response.split("lng\" : ")[1].split("[}]")[0];
		
		lat = "Latidude: " + lat;
		
		lng = "Longitude: " + lng;
		
		return "The address of " + searchTerm + " is " + address + " and the geographic coordinates are " + lat + " " + lng;
	}
	
	
	public String search(String searchTerm){
		try {
			String searchTerm2 = searchTerm.replace(" ","%20");
			
			URL url = new URL(PLACES_API_BASE + "/findplacefromtext/json?input=" + searchTerm2 + "&inputtype=textquery&fields=place_id&key=" + API_KEY);
		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String response = in.lines().collect(Collectors.joining());
			
			in.close();
			
			String place_id = response.split("place_id\" : \"")[1].split("\"")[0];
			
			return details(place_id,searchTerm);
			
		}catch(Exception e){
			
			return null;
		}
		
	}
	
}
