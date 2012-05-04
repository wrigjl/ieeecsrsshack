package net.thought.ieeecsrsshack;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

// TODO java doc-ify
@SuppressWarnings("serial")
public class ACMDisplayServlet extends HttpServlet {
	
	public ACMDisplayServlet() {
		dateformat = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/rss+xml");
		
		formatRss(resp);
	}

	static public Element createSimpleText(Document doc, Element root, String name, String value) {
		Element e = doc.createElement(name);
		root.appendChild(e);
		e.appendChild(doc.createTextNode(value));
		return e;
	}
	
	public void formatRss(HttpServletResponse resp) throws IOException {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		Document doc = docBuilder.newDocument();
		
		Element rss = doc.createElement("rss");
		doc.appendChild(rss);
		rss.setAttribute("version", "2.0");
		
		Element channel = doc.createElement("channel");
		rss.appendChild(channel);

		createSimpleText(doc, channel, "title", "ACM Digital Library recent additions");
		createSimpleText(doc, channel, "link", "http://dl.acm.org");
		createSimpleText(doc, channel, "description", "Recently uploaded publications to the ACM digital library");
		
		// TODO deal with pubDate/lastBuildDate
		Date dd = new Date();
		createSimpleText(doc, channel, "pubDate", dateformat.format(dd));
		createSimpleText(doc, channel, "lastBuildDate", dateformat.format(dd));
		
		Query q = new Query(ACMDLEntry.KIND);
		q.addSort(ACMDLEntry.CREATED, SortDirection.DESCENDING);
		for (Entity e : ds.prepare(q).asIterable()) {
			Element item = doc.createElement("item");
			channel.appendChild(item);
			
			createSimpleText(doc, item, "link", (String) e.getProperty(ACMDLEntry.URL));
			createSimpleText(doc, item, "title", (String) e.getProperty(ACMDLEntry.NAME));
			
			Element guid = createSimpleText(doc, item, "guid", (String) e.getProperty(ACMDLEntry.URL));
			guid.setAttribute("isPermaLink", "true");
			
			dd = (Date) e.getProperty(ACMDLEntry.CREATED);
			createSimpleText(doc, item, "pubDate", dateformat.format(dd));
		}
		
		try {
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			StringWriter sw = new StringWriter();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(sw);
			trans.transform(source, result);
			resp.getOutputStream().print(sw.toString());
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private SimpleDateFormat dateformat;
}
