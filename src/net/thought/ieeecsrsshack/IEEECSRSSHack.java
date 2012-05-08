package net.thought.ieeecsrsshack;

import java.io.IOException;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * The IEEE Computer Society library feeds are broken. Fortunately,
 * they are fixable. This class grabs the appropriate document and
 * subclasses do the fixes specific to the kind of breakage.
 * 
 * @author Jason L. Wright (jason@thought.net)
 *
 */
@SuppressWarnings("serial")
public abstract class IEEECSRSSHack extends HttpServlet {
	private String url;
	
	public IEEECSRSSHack(String url) {
		this.url = url;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/rss+xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom;
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(url);
		} catch (ParserConfigurationException pce) {
			resp.getWriter().println("failed in parser configuration");
			return;
		} catch (SAXException se) {
			resp.getWriter().println("failed in sax");
			return;
		} catch (IOException ioe) {
			resp.getWriter().println("failed in i/o");
			return;
		}

		Element doc = dom.getDocumentElement();
		NodeList chans = doc.getElementsByTagName("channel");
		for (int i = 0; i < chans.getLength(); i++) {
			Node chann = chans.item(i);
			
			if (chann.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element chan = (Element)chann;
			NodeList items = chan.getElementsByTagName("item");
			
			for (int j = 0; j < items.getLength(); j++) {
				Node itemn = items.item(j);
				if (itemn.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				fix_item(dom, resp, (Element)itemn);
			}
		}
		
		try {
			TransformerFactory xformf = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = xformf.newTransformer();
			DOMSource src = new DOMSource(doc);
			StreamResult dst = new StreamResult(resp.getWriter());
			transformer.transform(src, dst);
		} catch (TransformerConfigurationException e) {
			resp.getWriter().println("Messed up transformerconfigurationexception");
		} catch (TransformerException e) {
			resp.getWriter().println("Messed up transformerexception");
		}
	}
	
	
	/**
	 * Called for each RSS/channel/item to be fixed.
	 *  
	 * @param dom Document object for creating new nodes
	 * @param rsp Servlet response (for error reporting)
	 * @param item document item to be fixed
	 * 
	 * @throws DOMException on errors from the DOM object (eg. removeChild)
	 */
	abstract void fix_item(Document dom, HttpServletResponse rsp, Element item) throws DOMException;
	
	public boolean isWhiteSpace(char c) {
		switch (c) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			return (true);
		}
		return (false);
	}
	
	/**
	 * For a given node list, concatenate the values of all text nodes.
	 * 
	 * @param nl list of nodes
	 * @return resulting concenated string
	 * @throws DOMException
	 */
	String get_text(NodeList nl) throws DOMException {
		String r = "";

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.TEXT_NODE) {
				Text t = (Text)n;
				r = r.concat(t.getData());
			}
		}

		int i, len = r.length();
		for (i = 0; i < len; i++) {
			if (!isWhiteSpace(r.charAt(i)))
					break;
		}
		if (i == len)
			r = "";
		else
			r = r.substring(i);

		for (i = r.length() - 1; i > 0; i--) {
			if (!isWhiteSpace(r.charAt(i)))
				break;
		}
		if (i == -1)
			r = "";
		else
			r = r.substring(0, i + 1);
		
		return (r);
	}
}
