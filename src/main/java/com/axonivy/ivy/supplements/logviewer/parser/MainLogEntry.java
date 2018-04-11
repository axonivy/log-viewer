package com.axonivy.ivy.supplements.logviewer.parser;

import java.util.ArrayList;
import java.util.List;

public class MainLogEntry {
	private String originalTitleLine;
	private String time;
	private LogLevel severity;
	private List<DetailLogEntry> detailLogEntries;
	

	public void addDetailLogEntry(String detailLogEntry) {
		if(detailLogEntries == null) {
			detailLogEntries = new ArrayList<DetailLogEntry>();
		}
		detailLogEntries.add(new DetailLogEntry(this, detailLogEntry));
	}

	public MainLogEntry(String originalTitleLine, String time, LogLevel severity) {
		this.originalTitleLine = originalTitleLine;
		this.time = time;
		this.severity = severity;
	}

	public List<DetailLogEntry> getDetailLogEntry() {
		return detailLogEntries;
	}
	
	public String getOriginalTitleLine() {
		return originalTitleLine;
	}

	public String getTitleLine() {
		String detailTitle = "";
		if (detailLogEntries != null) {
			detailTitle = detailLogEntries.get(0).getDetailText().split("[\\r\\n]+")[0];
		}
		return time + " " + severity + ": " + detailTitle.trim();
	}

	public String getTime() {
		return time;
	}

	public LogLevel getSeverity() {
		return severity;
	}

	public String getDetails() {
		String details = originalTitleLine;
		for(DetailLogEntry detailLogEntry : detailLogEntries) {
			details.concat("\n").concat(detailLogEntry.getDetailText());
		}
		return details;
	}

	@Override
	public String toString() {
		return getTitleLine();
	}
}
