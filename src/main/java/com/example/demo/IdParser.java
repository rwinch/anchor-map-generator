package com.example.demo;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdParser {
	public List<String> ids(HtmlPage page) {
		List<String> ids = new ArrayList<>();
		List<HtmlElement> elementsWithId = page.getByXPath("//article//*[@id][@id!=\"\"]");
		for (HtmlElement element : elementsWithId) {
			ids.add(element.getId());
		}
		return ids;
	}
}
