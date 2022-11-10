

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
/**
 * <h3>Google places search class</h3>
 * <p>
 * This object uses the wikipedia API to get information about a given search term.
 * The searchWikipedia method uses a google search to find the first wikipedia article title.
 * The getWiki method uses the title for the searchWikipedia method to find the wikipedia page.
 * The getWiki method then reads and parses the wikipedia page and returns the first sentance.
 * </p>
 * 
 * @author Ferdinand Haaben
 */
public class WikiSearch {

	public String wikipediaApi = "https://www.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";

	public WikiSearch() {

	}

	public String searchWikipedia(String input){

		try {
			String encoding = "UTF-8";

			String searchText = input + " wikipedia";

			String connect = "https://www.google.com/search?q=" + URLEncoder.encode(searchText, encoding);

			Document google = Jsoup.connect(connect).get();

			String wikiTitle = google.selectFirst("h3[class=LC20lb DKV0Md]").text().split("-")[0].replace(" ", "_");

			return getWiki(wikiTitle.substring(0, wikiTitle.length() - 1));

		} catch (Exception e) {

			return null;
		}

	}

	public String getWiki(String wikiTitle) throws IOException {

		URL url = new URL(wikipediaApi + wikiTitle);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String response = in.lines().collect(Collectors.joining());

		in.close();

		return response.split("extract\":\"")[1].split("[.]")[0];

	}

}
