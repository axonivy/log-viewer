package com.axonivy.ivy.supplements.logviewer.parser;

public class LogUtil {

	public static String concatDetailEntries(DetailLogEntry detailLogEntry) {
		StringBuilder sb = new StringBuilder();
		sb.append(detailLogEntry.getDetailText());
		sb.append("\n");
		return sb.toString();
	}
}
