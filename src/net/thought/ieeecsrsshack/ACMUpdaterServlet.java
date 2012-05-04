package net.thought.ieeecsrsshack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import javax.servlet.http.*;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;


@SuppressWarnings("serial")
public class ACMUpdaterServlet extends HttpServlet {
	final private String url = "http://dl.acm.org/";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		parseXmlFile(resp);
	}

	private void parseXmlFile(HttpServletResponse rsp) throws IOException {
		HtmlCleaner cleaner = new HtmlCleaner();

		CleanerProperties props = cleaner.getProperties();
		props.setNamespacesAware(false);

		TagNode tagnode = null;
		try {
			System.setProperty("http.proxyHost", "webbalance.inel.gov");
			System.setProperty("http.proxyPort", "8080");
			tagnode = cleaner.clean(new URL(url));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tagnode == null) {
			System.out.println("not parse file!");
			System.exit(1);
		}

		ACMDLVisitor visitor = new ACMDLVisitor(); 
		tagnode.traverse(visitor);		
		updateDataStore(visitor.getList());
	}
	
	Entity createEntity(ACMDLEntry e) {
		Entity ety = new Entity("ACMDLEntry");
		ety.setProperty(ACMDLEntry.URL, e.getURL().toString());
		ety.setProperty(ACMDLEntry.NAME, e.getName());
		ety.setProperty(ACMDLEntry.CREATED, new Date());
		return ety;
	}
	
	void updateDataStore(List<ACMDLEntry> lst) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Iterator<ACMDLEntry>itr = lst.iterator();
		while (itr.hasNext()) {
			ACMDLEntry entry = itr.next();
			Query q = new Query(ACMDLEntry.KIND).setKeysOnly();
			q.addFilter(ACMDLEntry.URL, Query.FilterOperator.EQUAL, entry.getURL().toString());
			q.addFilter(ACMDLEntry.NAME, Query.FilterOperator.EQUAL, entry.getName());
			
			PreparedQuery pq = datastore.prepare(q);
			if (pq.countEntities(FetchOptions.Builder.withDefaults()) != 0) {
				// This one is already in the store, chuck it.
				itr.remove();
				System.out.println("Skipped (exists): " + entry.getName());
				continue;
			}

			datastore.put(createEntity(entry));
		}
		
	}
}
