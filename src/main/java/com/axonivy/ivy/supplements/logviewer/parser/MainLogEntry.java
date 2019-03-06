package com.axonivy.ivy.supplements.logviewer.parser;

import java.time.LocalTime;

public class MainLogEntry implements Comparable<MainLogEntry>{
	private String originalTitleLine;
	private String time;
	private LogLevel severity;
	private DetailLogEntry detailLogEntry;

	public MainLogEntry(String originalTitleLine, String time, LogLevel severity) {
		this.originalTitleLine = originalTitleLine;
		this.time = time;
		this.severity = severity;
	}

	public void addDetailLogEntry(String detailLine) {
		if(detailLogEntry == null) {
			detailLogEntry = new DetailLogEntry(this, detailLine);
		}
		else {
			detailLogEntry.addDetailLine(detailLine);
		}
	}

	public DetailLogEntry getDetailLogEntry() {
		return detailLogEntry;
	}

	public String getOriginalTitleLine() {
		return originalTitleLine;
	}

	public String getTitleLine() {
		String detailTitle = "";
		if (detailLogEntry != null) {
			detailTitle = detailLogEntry.getDetailText().split("[\\r\\n]+")[0];
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
		String details = originalTitleLine + "\n" + getDetailLogEntry();
		return details;
	}

	@Override
	public String toString() {
		return getTitleLine();
	}

	@Override
	public int compareTo(MainLogEntry o) {
		LocalTime t2 = LocalTime.parse(this.getTime());
		LocalTime t1 = LocalTime.parse(o.getTime());
		return t2.compareTo(t1);
	}
}
