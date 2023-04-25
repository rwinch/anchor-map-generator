package com.example.demo;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		Map<String, String> idToUrl = new HashMap<>();
		IdParser idParser = new IdParser();
		String startUrl = "https://docs.spring.io/spring-security/reference/";
		WebCrawler webCrawler = new WebCrawler();
		webCrawler.crawl(startUrl, page -> {
			String url = page.getUrl().toString();
			System.out.println(url);
			for (String id : idParser.ids(page)) {
				idToUrl.put(id, url);
			}
		});
		Gson gson = new Gson();
		String json = gson.toJson(idToUrl);
		System.out.println(json);
	}
}
