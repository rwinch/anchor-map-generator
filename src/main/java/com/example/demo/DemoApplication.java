package com.example.demo;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DemoApplication {

	public static void main(String[] args) throws Exception {
		WebCrawler webCrawler = new WebCrawler();
		Map<String, String> antoraOriginalIdToUrl = new HashMap<>();
		Path adocPagesPath = Path.of("/home/rwinch/code/spring-projects/spring-graphql/antora/spring-graphql-docs/modules/ROOT/pages");
		Map<String, String> adocFilesIdToPath = new HashMap<>();
//		adocFilesIdToPath.put("beans-factory-ctor-arguments-index", "core/beans/dependencies/factory-collaborators.html#beans-factory-ctor-arguments-index");
//		adocFilesIdToPath.put("beans-factory-ctor-arguments-name", "core/beans/dependencies/factory-collaborators.html#beans-factory-ctor-arguments-names");
//		adocFilesIdToPath.put("beans-factory-ctor-arguments-type", "core/beans/dependencies/factory-collaborators.html#beans-factory-ctor-arguments-type");
//		adocFilesIdToPath.put("beans-java-combining-xml-centric-component-scan", "core/beans/java/composing-configuration-classes.html#beans-java-combining-xml-centric-component-scan");
//		adocFilesIdToPath.put("mvc.web-uribuilder", "web/webmvc/mvc-uri-building.html#uribuilder");
//		adocFilesIdToPath.put("mvc.web-uri-encoding", "web/webmvc/mvc-uri-building.html#uri-encoding");
//		adocFilesIdToPath.put("mvc.web-uricomponents", "web/webmvc/mvc-uri-building.html#uricomponents");
//
//		adocFilesIdToPath.put("webflux.web-uri-encoding", "web/webflux/uri-building.html#uri-encoding");
//		adocFilesIdToPath.put("webflux.web-uribuilder", "web/webflux/uri-building.html#uribuilder");
//		adocFilesIdToPath.put("webflux.web-uricomponents", "web/webflux/uri-building.html#uricomponents");
//
//		adocFilesIdToPath.put("webflux.websocket-intro", "web/webflux-websocket.html#introduction-to-websocket");
//		adocFilesIdToPath.put("webflux.websocket-intro-architecture", "web/webflux-websocket.html#http-versus-websocket");
//		adocFilesIdToPath.put("webflux.websocket-intro-when-to-use", "web/webflux-websocket.html#when-to-use-websockets");
//
//		adocFilesIdToPath.put("mvc.websocket-intro", "web/websocket.html#introduction-to-websocket");
//		adocFilesIdToPath.put("mvc.websocket-intro-architecture", "web/websocket.html#http-versus-websocket");
//		adocFilesIdToPath.put("mvc.websocket-intro-when-to-use", "web/websocket.html#when-to-use-websockets");
		adocFilesIdToPath.putAll(IdToPathMain.getIdToPath(adocPagesPath));
		for (String id : adocFilesIdToPath.keySet()) {
			antoraOriginalIdToUrl.put(id, adocFilesIdToPath.get(id));
		}
		Map<String, List<String>> asciidoctorHtmlPageToIds = new HashMap<>();
		IdParser asciidoctorIdParser = IdParser.createForAsciidoctor();
		String asciidoctorStartUrl = "https://docs.spring.io/spring-graphql/docs/current-SNAPSHOT/reference/html/index.html";
		webCrawler.crawl(asciidoctorStartUrl, page -> {
			String url = page.getUrl().toString();
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

//		System.out.println(json);
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
