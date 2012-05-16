package net.thought.ieeecsrsshack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import javax.servlet.http.*;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * The ACM Digital Library main web page has a nice list of new items
 * added to the library.  I have asked they produce an RSS feed (more than
 * a year ago), and I'm tired of waiting.
 * 
 * So, this class pulls their web page once a day and parses it into
 * the google datastore.
 * 
 * @author Jason L. Wright (jason@thought.net)
 *
 */
@SuppressWarnings("serial")
public class ACMUpdaterServlet extends HttpServlet {
	/**
	 * The URL to fetch.
	 */
	final private String url = "http://dl.acm.org/";

	/**
	 * the "get" here just sets off the fetch of the ACM library
	 * page: parse and store the entries for display.
	 *  
	 * @param req Servlet request object
	 * @param rsp Servlet response
	 * 
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		HtmlCleaner cleaner = new HtmlCleaner();

		// step 1: grab the current web page
		CleanerProperties props = cleaner.getProperties();
		props.setNamespacesAware(false);
		TagNode tagnode = null;
		try {
			tagnode = cleaner.clean(new URL(url));
		} catch (MalformedURLException e) {
			resp.sendError(500, "bad URL: " + url);
			return;
		} catch (IOException e) {
			resp.sendError(500, "fetching ACM DL page: " + e.getMessage());
			return;
		}
		if (tagnode == null) {
			resp.sendError(500, "error parsing DL file (no root)");
			return;
		}

		// Step 2: traverse the page and build entries
		ACMDLVisitor visitor = new ACMDLVisitor(); 
		tagnode.traverse(visitor);		

		// Step 3: update datastore (see which entries are new) 
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Iterator<ACMDLEntry>itr = visitor.getList().iterator();
		while (itr.hasNext()) {
			ACMDLEntry entry = itr.next();
			Query q = new Query(ACMDLEntry.KIND).setKeysOnly();
			q.addFilter(ACMDLEntry.URL, Query.FilterOperator.EQUAL, entry.getURL().toString());
			q.addFilter(ACMDLEntry.NAME, Query.FilterOperator.EQUAL, entry.getName());
			
			PreparedQuery pq = datastore.prepare(q);
			if (pq.countEntities(FetchOptions.Builder.withDefaults()) != 0) {
				// This one is already in the store, chuck it.
				itr.remove();
				continue;
			}
			datastore.put(entry.createEntity());
		}
	}
}
