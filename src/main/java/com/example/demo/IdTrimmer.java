package com.example.demo;

public class IdTrimmer {
	private final String firstId;

	private final int indexOfLastIdSeparator;

	public IdTrimmer(String firstId) {
		this.firstId = firstId;
		this.indexOfLastIdSeparator = firstId.lastIndexOf(".");
	}

	public String trim(String id) {
		int lastPeriod = id.indexOf(".");
		return lastPeriod > -1 ? id.substring(lastPeriod) : id;
//		if (id.startsWith(firstId)) {
//			return id.substring(indexOfLastIdSeparator + 1);
//		}
//		return id;
	}
}
