package com.example.demo;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		Map<String, String> antoraIdToUrl = new HashMap<>();
		Map<String, String> antoraOriginalIdToUrl = new HashMap<>();
		IdParser antoraIdParser = IdParser.createForAntora();
		String antoraStartUrl = "file:/home/rwinch/code/spring-projects/spring-framework/antora/framework-docs/build/site/framework/index.html";
		WebCrawler webCrawler = new WebCrawler();
		webCrawler.crawl(antoraStartUrl, page -> {
			String url = page.getUrl().toString();
			int lastIndexOfPath = url.lastIndexOf('/');
			String basePath = (lastIndexOfPath > antoraStartUrl.length()) ? url.substring(antoraStartUrl.length(), lastIndexOfPath) : "";
			String baseId = basePath.replaceAll("/", ".") + ".";
			for (String id : antoraIdParser.ids(page)) {
				String urlWithAnchor = url +"#"+id;
				antoraIdToUrl.put(id, urlWithAnchor);
				antoraOriginalIdToUrl.put(baseId + id, urlWithAnchor);
				String h1Id = url.substring(lastIndexOfPath, url.length() - ".html".length()) + ".";
				antoraOriginalIdToUrl.put(baseId + h1Id + id, urlWithAnchor);
			}
		});
		Map<String, String> asciidoctorIdToUrl = new HashMap<>();
		Map<String, String> asciidoctorIdToTrimmedId = new HashMap<>();
		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		String asciidoctorStartUrl = "https://docs.spring.io/spring-framework/docs/current/reference/html/";
		webCrawler.crawl(asciidoctorStartUrl, page -> {
			String url = page.getUrl().toString();
			List<String> ids = asciidoctorIdParser.ids(page);
			String h1Id = ids.isEmpty() ? "" : ids.get(0);
			IdTrimmer trimmer = new IdTrimmer(h1Id);
			for (String id : ids) {
				String trimmedId = trimmer.trim(id);
				asciidoctorIdToUrl.put(id, url);
				asciidoctorIdToTrimmedId.put(id, trimmedId);
			}
		});
		for (String asciidoctorId : asciidoctorIdToUrl.keySet()) {
			if (!antoraIdToUrl.containsKey(asciidoctorId)) {
				if (antoraOriginalIdToUrl.containsKey(asciidoctorId)) {
					String trimmedId = asciidoctorIdToTrimmedId.get(asciidoctorId);
					if (!(antoraOriginalIdToUrl.containsKey(trimmedId) || antoraIdToUrl.containsKey(trimmedId))) {

						System.out.println("Couldn't find mapping for " + asciidoctorId);
					}
				}
			}
		}
		Gson gson = new Gson();
		String json = gson.toJson(antoraIdToUrl);
		System.out.println(json);
	}
}
