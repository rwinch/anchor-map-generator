package com.example.demo;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WriteRedirectsApplication {
	static final String ANTORA_PATH = "/home/rwinch/code/spring-projects/spring-integration/main/src/reference/antora/modules/ROOT/pages";

	static final String HTML_SINGLE_URL = "https://docs.spring.io/spring-integration/docs/6.1.x-SNAPSHOT/reference/html/index-single.html";
	static final String HTML_MULTI_URL = "https://docs.spring.io/spring-integration/docs/6.1.x-SNAPSHOT/reference/html/";

	public static void main(String[] args) throws Exception {
		Map<String, String> antoraOriginalIdToUrl = new HashMap<>();
		Path adocPagesPath = Path.of(ANTORA_PATH);
		Map<String, String> adocFilesIdToPath = new HashMap<>();
		adocFilesIdToPath.putAll(IdToPathMain.getIdToPath(adocPagesPath));
		for (String id : adocFilesIdToPath.keySet()) {
			antoraOriginalIdToUrl.put(id, adocFilesIdToPath.get(id));
		}

		writeRedirectsFor(antoraOriginalIdToUrl, HTML_SINGLE_URL, true);
		writeRedirectsFor(antoraOriginalIdToUrl, HTML_MULTI_URL, false);

	}

	private static void writeRedirectsFor(Map<String, String> antoraOriginalIdToUrl, String asciidoctorStartUrl, boolean htmlSingle) throws Exception {
		WebCrawler webCrawler = new WebCrawler();
		Map<String, List<String>> asciidoctorHtmlPageToIds = new HashMap<>();
		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		webCrawler.crawl(asciidoctorStartUrl, htmlSingle, page -> {
			String url = page.getUrl().toString();
			System.out.println("Crawling " + url);
			for (String id : asciidoctorIdParser.ids(page)) {
				List<String> ids = asciidoctorHtmlPageToIds.computeIfAbsent(url, k -> new ArrayList<>());
				ids.add(id);
			}
		});
		Set<String> notFoundIds = new HashSet<>();
		Path outputPath = Path.of("target/redirects/");
		outputPath.toFile().mkdirs();
		String template = Files.readString(Path.of("src/main/resources/index.html"));
		for (String page : asciidoctorHtmlPageToIds.keySet()) {
			Map<String,String> result = new HashMap<>();
			List<String> ids = asciidoctorHtmlPageToIds.get(page);
			for (String asciidoctorId : ids) {
				String path = getWithHierarchicalKey(antoraOriginalIdToUrl, asciidoctorId);
				if (path == null) {
					notFoundIds.add(asciidoctorId);
				}
				else {
					result.put(asciidoctorId, path);
				}
			}
			String fileName = page.substring(page.lastIndexOf("/") + 1);
			Path outputFilePath = outputPath.resolve(fileName);
			Gson gson = new Gson();
			String json = gson.toJson(result);
			String html = template.replace("%JSON%", json);
			Files.writeString(outputFilePath, html);
		}

		System.out.println("Couldn't find these ids " + notFoundIds);
		System.out.println("total " + notFoundIds.size());
	}

	private static String getWithHierarchicalKey(Map<String, String> map, String hiearchicalKey) {
		String result = map.get(hiearchicalKey);
		if (result != null) {
			return result;
		}
		int indexOfSeparator = hiearchicalKey.indexOf(".");
		if (indexOfSeparator < 0) {
			indexOfSeparator = hiearchicalKey.indexOf("-");
		}
		if (indexOfSeparator < 0) {
			return null;
		}
		return getWithHierarchicalKey(map, hiearchicalKey.substring(indexOfSeparator + 1));
	}
}
