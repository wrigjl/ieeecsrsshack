package net.thought.ieeecsrsshack;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class ACMDisplayServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		
		formatRss(resp);
	}

	public void formatRss(HttpServletResponse resp) throws IOException {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query();
		//q.addSort("created", SortDirection.DESCENDING);
		resp.getOutputStream().println("<table border=1>");
		for (Entity e : ds.prepare(q).asIterable()) {
			resp.getOutputStream().println("<tr>");
			resp.getOutputStream().println("<td>" + e.getProperty("name") + "</td>");
			resp.getOutputStream().println("<td><a href=\"" + e.getProperty("url") + "\">here</a></td>");
			resp.getOutputStream().println("</tr>");
		}
		resp.getOutputStream().println("</table>");
	}
}
