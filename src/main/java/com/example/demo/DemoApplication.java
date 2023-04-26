package com.example.demo;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		Map<String, String> antoraIdToUrl = new HashMap<>();
		Map<String, String> antoraOriginalIdToUrl = new HashMap<>();
		IdParser antoraIdParser = IdParser.createForAntora();
		String antoraStartUrl = "file:/home/rwinch/code/spring-projects/spring-framework/antora/framework-docs/build/site/framework/index.html";;// "https://rwinch.github.io/spring-framework/";//
		WebCrawler webCrawler = new WebCrawler();
		BaseUrl baseAntoraStartUrl = new BaseUrl(antoraStartUrl);
		webCrawler.crawl(antoraStartUrl, page -> {
			String url = page.getUrl().toString();
			String basePath = baseAntoraStartUrl.relative(url);
			for (String id : antoraIdParser.ids(page)) {
				String urlWithAnchor = basePath +"#"+id;
				antoraIdToUrl.put(id, urlWithAnchor);
			}
		});
		Map<String, String> adocFilesIdToPath = IdToPathMain.getIdToPath();
		for (String id : adocFilesIdToPath.keySet()) {
			antoraOriginalIdToUrl.put(id, adocFilesIdToPath.get(id));
		}
		Map<String, String> asciidoctorIdToUrl = new HashMap<>();
		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		String asciidoctorStartUrl = "https://docs.spring.io/spring-framework/docs/current/reference/html/";
		webCrawler.crawl(asciidoctorStartUrl, page -> {
			String url = page.getUrl().toString();
			for (String id : asciidoctorIdParser.ids(page)) {
				asciidoctorIdToUrl.put(id, url);
			}
		});
		Set<String> notFoundIds = new HashSet<>();
		Map<String,String> result = new HashMap<>();
		for (String asciidoctorId : asciidoctorIdToUrl.keySet()) {
			String path = antoraIdToUrl.getOrDefault(asciidoctorId, antoraOriginalIdToUrl.get(asciidoctorId));
			if (path == null) {
				notFoundIds.add(asciidoctorId);
			}
			else {
				result.put(asciidoctorId, path);
			}
		}
		System.out.println("Couldn't find these ids " + notFoundIds);
		System.out.println("total " + notFoundIds.size());
		Gson gson = new Gson();
		String json = gson.toJson(result);
//		System.out.println(json);
	}

	static class BaseUrl {
		private final String baseUrl;

		private final int indexOfLastPath;

		BaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			this.indexOfLastPath = baseUrl.lastIndexOf("/");
		}

		String relative(String url) {
			return url.substring(indexOfLastPath + 1);
		}
	}
}
