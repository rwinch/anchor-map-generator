package com.example.demo;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		IdParser antorIdParser = IdParser.createForAntora();
		WebCrawler webCrawler = new WebCrawler();
		Map<String, String> antoraIdToUrls = new HashMap<>();
		String antoraStartUrl = "https://rwinch.github.io/spring-framework/";
		webCrawler.crawl(antoraStartUrl, page -> {
			String url = page.getUrl().toString();
			List<String> ids = antorIdParser.ids(page);
			for (String id : ids) {
				String previousUrl = antoraIdToUrls.put(id, url);
				if (previousUrl != null) {
					System.err.println("The id of '" + id +"' cannot be mapped to '" + url +"' because it is already mapped to '" + previousUrl + "'");
				}
			}
		});

		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		String asciidoctorStartUrl = "https://docs.spring.io/spring-framework/docs/current/reference/html/";
		Map<String, String> asciidoctorIdToUrs = new HashMap<>();
		Map<String, String> asciidoctorIdToTrimmedId = new HashMap<>();
		webCrawler.crawl(asciidoctorStartUrl, page -> {
			String url = page.getUrl().toString();
			List<String> ids = asciidoctorIdParser.ids(page);
			IdTrimmer trimmer = new IdTrimmer(ids.isEmpty() ? "" : ids.get(0));
			for (String id : ids) {
				String trimmedId = trimmer.trim(id);
				asciidoctorIdToTrimmedId.put(id, trimmedId);
				String previousUrl = asciidoctorIdToUrs.put(id, url);
				if (previousUrl != null) {
					System.err.println("The id of '" + id +"' cannot be mapped to '" + url +"' because it is already mapped to '" + previousUrl + "'");
				}
			}
		});

		for (String asciidoctorId : asciidoctorIdToUrs.keySet()) {
			if (!antoraIdToUrls.containsKey(asciidoctorId)) {
				String trimmedId = asciidoctorIdToTrimmedId.get(asciidoctorId);
				if (!antoraIdToUrls.containsKey(trimmedId)) {
					System.out.println("Id is not mapped " + asciidoctorId);
				}
			}
		}
//		Gson gson = new Gson();
//		String json = gson.toJson(asciidoctorIdToUrs);
//		System.out.println(json);
	}
}
