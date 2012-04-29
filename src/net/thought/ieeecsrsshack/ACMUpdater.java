package net.thought.ieeecsrsshack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.*;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;


@SuppressWarnings("serial")
public abstract class ACMUpdater extends HttpServlet {
	final private String url = "http://dl.acm.org/";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/rss+xml");
		parseXmlFile(resp);
	}

	private void parseXmlFile(HttpServletResponse rsp) throws IOException {
		HtmlCleaner cleaner = new HtmlCleaner();

		CleanerProperties props = cleaner.getProperties();
		props.setNamespacesAware(false);

		TagNode tagnode = null;
		try {
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

		tagnode.traverse(new ACMDLVisitor());
	}
}
