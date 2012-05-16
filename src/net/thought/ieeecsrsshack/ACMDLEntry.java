package net.thought.ieeecsrsshack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class ACMDLEntry {
	/**
	 * Construct an ACMDLEntry from a 'name' and 'url'.
	 * 
	 * @param name Name of journal 
	 * @param url URL to citation entry
	 */
	ACMDLEntry(String name, URL url) {
		this.url = url;
		this.name = name;
	}
	
	/**
	 * Construct ACMDLEntry from 'name' and 'url' strings
	 * @param name Name of journal
	 * @param url string URL
	 * 
	 * @throws MalformedURLException if the URL is unparseable
	 */
	ACMDLEntry(String name, String urls) throws MalformedURLException {
		this(name, new URL(urls));
	}
	
	/*
	 * Construct ACMDLEntry with no URL
	 */
	ACMDLEntry(String name) {
		this(name, (URL)null);
	}
	
	/**
	 * (re)set the name of the journal
	 * 
	 * @param name journal name
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * (re)set the URL to the journal
	 * 
	 * @param url URL to journal
	 */
	void setURL(URL url) {
		this.url = url;
	}
	/**
	 * (re)set the URL to the journal (string)
	 * @param url string version of URL
	 * 
	 * @throws MalformedURLException if the URL is unparseable
	 */
	void setURL(String urls) throws MalformedURLException {
		this.url = new URL(urls);
	}
	
	/**
	 * get journal name
	 * @return journal name
	 */
	String getName() {
		return name;
	}
	
	/**
	 * get journal URL
	 * @return URL of journal
	 */
	URL getURL() {
		return url;
	}
	
	/**
	 * create a datastore entity from this ACMDLEntry
	 * @return
	 */
	public Entity createEntity() {
		return createEntity(this);
	}
	
	/**
	 * create a datastore entity from an ACMDLEntry
	 * @param e the ACMDLEntry
	 * @return datastore entity
	 */
	public static Entity createEntity(ACMDLEntry e) {
		Entity ety = new Entity(KIND);
		ety.setProperty(URL, e.getURL().toString());
		ety.setProperty(NAME, e.getName());
		ety.setProperty(CREATED, new Date());
		return ety;
	}

	private URL url;
	private String name;
	
	/** datastore object "kind" */ 
	static public final String KIND = "ACMDLEntry";
	/** field for storing journal name in datastore */
	static public final String NAME = "name";
	/** field for storing URL in datastore */
	static public final String URL = "url";
	/** field for storing creation date in datastore */
	static public final String CREATED = "created";
}