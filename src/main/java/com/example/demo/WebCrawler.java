package com.example.demo;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

public class WebCrawler {
	private static String INDEX = "index.html";

	public void crawl(String startUrl, Consumer<HtmlPage> action) throws Exception  {
		Set<String> visitedUrls = new HashSet<>();
		Stack<String> urlsToVisit = new Stack<>();
		urlsToVisit.add(startUrl);
		try (final WebClient webClient = new WebClient() ) {
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			while (!urlsToVisit.isEmpty()) {
				String urlToVisit = urlsToVisit.pop();
				System.out.println("Visiting " + urlToVisit);
				visitedUrls.add(urlToVisit);

				final HtmlPage page = webClient.getPage(urlToVisit);
				action.accept(page);
				List<HtmlAnchor> anchors = page.getAnchors();
				for (HtmlAnchor anchor : anchors) {
					String href = anchor.getHrefAttribute();
					try {
						String normlizedUrl = normalizedUrl(absoluteUrl(page.getUrl().toString(), href));
						if (!visitedUrls.contains(normlizedUrl) && !urlsToVisit.contains(normlizedUrl) && normlizedUrl.startsWith(startUrl) && !normlizedUrl.matches(".*?/\\d.*")) {
							urlsToVisit.add(normlizedUrl);
						}
					} catch (URISyntaxException e) {
						continue;
					}
				}
			}
		}
	}

	private static String absoluteUrl(String baseUri, String href) throws MalformedURLException {
		return new URL(new URL(baseUri), href).toString();
	}

	private static String normalizedUrl(String url) throws URISyntaxException {
		URI normalized = URI.create(url).normalize();
		String path = normalized.getPath();
		String noIndexPath = path.endsWith(INDEX) ? path.substring(0, path.length() - INDEX.length()) : path;
		URI noFragmentAndNoIndex = new URI(normalized.getScheme(), normalized.getUserInfo(), normalized.getHost(), normalized.getPort(), noIndexPath, normalized.getQuery(), null);
		return noFragmentAndNoIndex.toString();
	}

}
