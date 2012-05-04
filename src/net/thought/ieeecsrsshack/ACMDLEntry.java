package net.thought.ieeecsrsshack;

import java.net.MalformedURLException;
import java.net.URL;

// TODO java doc-ify
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
	
	static public final String KIND = "ACMDLEntry";
	static public final String NAME = "name";
	static public final String URL = "url";
	static public final String CREATED = "created";
}
