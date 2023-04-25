package com.example.demo;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdParser {
	private final String idXpath;

	private IdParser(String idXpath) {
		this.idXpath = idXpath;
	}

	public List<String> ids(HtmlPage page) {
		List<String> ids = new ArrayList<>();
		List<HtmlElement> elementsWithId = page.getByXPath(this.idXpath);
		for (HtmlElement element : elementsWithId) {
			String id = element.getId();
			if (!Set.of("page-title", "preamble").contains(id) && !id.startsWith("_tabs_")) {
				ids.add(element.getId());
			}
		}
		return ids;
	}

	public static IdParser createForAntora() {
		return new IdParser("//article//*[@id][@id!=\"\"]");
	}

	public static IdParser createForAsciidoctor() {
		return new IdParser("//div[@id=\"content\"]//*[@id]");
	}
}
