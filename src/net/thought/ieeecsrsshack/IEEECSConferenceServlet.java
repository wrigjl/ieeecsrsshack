package net.thought.ieeecsrsshack;

import javax.servlet.http.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author jason
 *
 */
@SuppressWarnings("serial")
public class IEEECSConferenceServlet extends IEEECSRSSHack {	
	/**
	 * Sets the URL for the xml to parse/modify
	 */
	public IEEECSConferenceServlet() {
		super("http://csdl.computer.org/rss/proceedingsList.xml");
	}
	
	/**
	 * Conference proceedings have two &lt;guid isPermaLink="true"&gt; objects. We have to delete
	 * the useless one so that readers can use the "real" permalink.
	 * 
	 * @param dom Document object for creating new nodes
	 * @param rsp Servlet response (for error reporting)
	 * @param item document item to be fixed
	 * 
	 * @throws DOMException on errors from the DOM object (eg. removeChild)
	 */
	void fix_item(Document dom, HttpServletResponse rsp, Element item) throws DOMException {
		NodeList guids = item.getElementsByTagName("guid");
		for (int i = 0; i < guids.getLength(); i++) {
			if (guids.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element guid = (Element)guids.item(i);
			if ("true".equals(guid.getAttribute("isPermaLink")) &&
					"http://www.computer.org/publications/dlib/".equals(get_text(guid.getChildNodes()))) {
				item.removeChild(guid);
				guids = item.getElementsByTagName("guid");
			}
		}
	}
}
