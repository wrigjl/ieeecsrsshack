package net.thought.ieeecsrsshack;

import java.net.MalformedURLException;
import java.net.URL;

public class ACMDLEntry {
	ACMDLEntry(String name, URL url) {
		this.url = url;
		this.name = name;
	}
	
	ACMDLEntry(String name, String urls) throws MalformedURLException {
		this(name, new URL(urls));
	}
	
	void setName(String name) {
		this.name = name;
	}

	void setURL(URL url) {
		this.url = url;
	}
	void setURL(String urls) throws MalformedURLException {
		this.url = new URL(urls);
	}
	
	String getName() {
		return name;
	}
	
	URL getURL() {
		return url;
	}
	
	private URL url;
	private String name;
}
