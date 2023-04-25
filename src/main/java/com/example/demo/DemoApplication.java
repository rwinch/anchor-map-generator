package com.example.demo;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		Map<String, String> antoraIdToUrl = new HashMap<>();
		IdParser antoraIdParser = IdParser.createForAntora();
		String antoraStartUrl = "https://rwinch.github.io/spring-framework/";
		WebCrawler webCrawler = new WebCrawler();
		webCrawler.crawl(antoraStartUrl, page -> {
			String url = page.getUrl().toString();
			for (String id : antoraIdParser.ids(page)) {
				antoraIdToUrl.put(id, url +"#"+id);
			}
		});
		Map<String, String> asciidoctorIdToUrl = new HashMap<>();
		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		String asciidoctorStartUrl = "https://docs.spring.io/spring-framework/docs/current/reference/html/";
		webCrawler.crawl(asciidoctorStartUrl, page -> {
			String url = page.getUrl().toString();
			for (String id : asciidoctorIdParser.ids(page)) {
				asciidoctorIdToUrl.put(id, url);
			}
		});
		for (String asciidoctorId : asciidoctorIdToUrl.keySet()) {
			if (!antoraIdToUrl.containsKey(asciidoctorId)) {
				System.out.println("Couldn't find mapping for " + asciidoctorId);
			}
		}
		Gson gson = new Gson();
		String json = gson.toJson(antoraIdToUrl);
		System.out.println(json);
	}
}
