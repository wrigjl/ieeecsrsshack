package net.thought.ieeecsrsshack;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

public class ACMDLVisitor implements TagNodeVisitor {
	private List<ACMDLEntry> theList;
	
	ACMDLVisitor() {
		super();
		theList = new LinkedList<ACMDLEntry>();
	}
	
	public List<ACMDLEntry> getList() {
		return theList;
	}
	
	public boolean visit(TagNode tn, HtmlNode htmlNode) {
		if (htmlNode instanceof TagNode) {
			TagNode tag = (TagNode)htmlNode;
			if ("strong".equals(tag.getName())) {
				List<?> tl = tag.getChildren();
				for (int i = 0; i < tl.size(); i++) {
					if (tl.get(i) instanceof ContentNode) {
						ContentNode t = (ContentNode)tl.get(i);
						if ("Recently loaded issues and proceedings:".equals(t.toString())) {
							System.out.println("found it!");
							parseRecent(tag.getParent());
							return (false);
						}
					}
				}
			}
		}
		return (true);
	}
	
	public void parseRecent(TagNode td) {
		TagNode[] divs = td.getElementsByName("div", false);
		for (int i = 0; i < divs.length; i++) {
			parseOuterDiv(divs[i]);
		}
	}

	public void parseOuterDiv(TagNode divo) {
		TagNode[] divs = divo.getElementsByName("div", false);
		for (int i = 0; i < divs.length; i++) {
			parseInnerDiv(divs[i]);
		}
	}

	public void parseInnerDiv(TagNode divi) {
		String name = null;

		List<?> kids = divi.getChildren();
		if (kids.size() > 0) {
			ContentNode cn = (ContentNode)kids.get(0);
			name = cn.toString().trim();
			if ("".equals(name)) {
				name = null;
			}
		}

		if (name == null)
			name = "noname";

		TagNode[] tags = divi.getElementsByName("a", false);
		for (int i = 0; i < tags.length; i++) {
			kids = tags[i].getChildren();
			String subname = "unknown";
			if (kids.size() > 0) {
				ContentNode cn = (ContentNode)kids.get(0);
				subname = cn.toString().trim();
			}
			String href = tags[i].getAttributeByName("href");
			href = href.replaceAll("&CFID=[0-9]+", "");
			href = href.replaceAll("&CFTOKEN=[0-9]+", "");
			href = "http://dl.acm.org/" + href;
			name = name + " (" + subname + ")";
			try {
				theList.add(new ACMDLEntry(name, href));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}