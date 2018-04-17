package com.axonivy.ivy.supplements.logviewer.parser;

import java.util.List;

public class LogUtil {

	public static String concatDetailEntries(List<DetailLogEntry> detailEntries) {
		StringBuilder sb = new StringBuilder();
		for (DetailLogEntry detailLogEntry : detailEntries) {
			sb.append(detailLogEntry.getDetailText());
			sb.append("\n");
		}
		return sb.toString();
	}
}
