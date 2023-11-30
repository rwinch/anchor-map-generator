package com.example.demo;


import com.example.demo.AsciidocIdScanner.Id;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.demo.IOUtils.unchecked;

public class IdToPathMain {
	public static Map<String,String> getIdToPath(Path path) throws Exception{
		AsciidocIdScanner idScanner = new AsciidocIdScanner();
		Map<String, String> result = new HashMap<>();
		try (Stream<Path> paths = Files.walk(path)
				.filter(p -> p.toFile().isFile() && p.toFile().getName().endsWith(".adoc"))) {
			paths.forEach(p -> {
				Path relativePath = path.relativize(p);
				List<String> lines = unchecked(() -> Files.readAllLines(p));
				List<Id> ids = idScanner.findIds(lines);
				if (!ids.isEmpty()) {
					Id firstId = ids.remove(0);
					result.put(firstId.getId(), relativePath.toString().replace(".adoc", ".html"));
				}
				ids.forEach(id -> {
					result.put(id.getId(), relativePath.toString().replace(".adoc", ".html") +"#" + id.getId());
				});

			});
		}
		return result;
	}
}
