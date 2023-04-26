package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class IOUtils {


	static String prompt(String text) {
		System.out.println(text);
		return System.console().readLine();
	}

	static <T> T unchecked(Producer<T> producer) {
		try {
			return producer.produce();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void writeString(Path path, String value) {
		try {
			Files.writeString(path, value, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void delete(Path path) {
		try {
			Files.walk(path)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void copy(Path source, Path destination) {
		try {
			Files.createDirectories(destination.getParent());
			Files.copy(source, destination);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void move(Path source, Path destination) {
		try {
			Files.createDirectories(destination.getParent());
			Files.move(source, destination);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	interface Producer<T> {
		T produce() throws Exception;
	}
}
