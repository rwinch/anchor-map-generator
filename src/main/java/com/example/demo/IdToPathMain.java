package com.example.demo;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static com.example.demo.IOUtils.unchecked;

public class IdToPathMain {
	public static List<IdentifiableDocument> getIdentifiableDocuments() throws Exception{
		AsciidocIdScanner idScanner = new AsciidocIdScanner();
		Path path = Path.of("/home/rwinch/code/spring-projects/spring-framework/antora/framework-docs/modules/ROOT/pages");
		List<IdentifiableDocument> result = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(path)
				.filter(p -> p.toFile().isFile() && p.toFile().getName().endsWith(".adoc"))) {
			paths.forEach(p -> {
				Path relativePath = path.relativize(p);
				IdentifiableDocument identifiableDocument = new IdentifiableDocument(relativePath);
				List<String> lines = unchecked(() -> Files.readAllLines(p));
				idScanner.findIds(lines).forEach(id -> {
					identifiableDocument.addId(id.getId());
				});
				result.add(identifiableDocument);

			});
		}
		return result;
	}

	static class IdentifiableDocument {
		private final Set<String> ids = new HashSet<>();

		private final Path documentPath;

		private final String baseHtmlAnchorUrl;

		IdentifiableDocument(Path documentPath) {
			this.documentPath = documentPath;
			this.baseHtmlAnchorUrl = documentPath.toString().replace(".adoc", ".html") + "#";
		}

		public void addId(String id) {
			this.ids.add(id);
		}

		public Map<String, String> getIdToHtmlUrl() {
			Map<String, String> result = new HashMap<>();
			for (String id : this.ids) {
				result.put(id, this.baseHtmlAnchorUrl + id);
			}
			return result;
		}

		public Path getDocumentPath() {
			return this.documentPath;
		}
	}
}
