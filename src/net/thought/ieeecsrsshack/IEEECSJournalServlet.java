package net.thought.ieeecsrsshack;

import javax.servlet.http.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Journal list has a useless &lt;guid isPermaLink="true"&gt; object. Delete it and
 * create a new one with the contents of the &lt;link&gt; node on the fly.
 * 
 * @author Jason L. Wright (jason@thought.net)
 */
@SuppressWarnings("serial")
public class IEEECSJournalServlet extends IEEECSRSSHack {
	/**
	 * Sets the URL for the xml to parse/modify
	 */	
	public IEEECSJournalServlet() {
		super("http://csdl.computer.org/rss/journalList.xml");
	}
	
	/**
	 * Journal list has a useless &lt;guid isPermaLink="true"&gt; object. Delete it and
	 * create a new one with the contents of the &lt;link&gt; node.
	 * 
	 * @param dom Document object for creating new nodes
	 * @param rsp Servlet response (for error reporting)
	 * @param item document item to be fixed
	 * 
	 * @throws DOMException on errors from the DOM object (e.g. removeChild)
	 */
	void fix_item(Document dom, HttpServletResponse rsp, Element item) throws DOMException {
		NodeList links = item.getElementsByTagName("link");
		String link = null;
		
		for (int i = 0; i < links.getLength(); i++) {
			if (links.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element l = (Element)links.item(i);
			link = get_text(l.getChildNodes());
		}
		
		if (link == null) {
			// Hrmph, didn't find a link, let's pray that everything is ok.
			return;
		}
		
		NodeList guids = item.getElementsByTagName("guid");
		for (int i = 0; i < guids.getLength(); i++) {
			if (guids.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element guid = (Element)guids.item(i);
			if ("true".equals(guid.getAttribute("isPermaLink"))) {
				item.removeChild(guid);
				guids = item.getElementsByTagName("guid");
			}
		}

		Element newLink = dom.createElement("guid");
		newLink.setAttribute("isPermaLink", "true");
		newLink.appendChild(dom.createTextNode(link));
		item.appendChild(newLink);
	}	
}
