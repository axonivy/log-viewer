package com.axonivy.ivy.supplements.logviewer.parser;

public class MainLogEntry {
	private String originalTitleLine;
	private String time;
	private LogLevel severity;
	private DetailLogEntry detailLogEntry;
	

	public void addDetailLogEntry(String detailLine) {
		if(detailLogEntry == null) {
			detailLogEntry = new DetailLogEntry(this, detailLine);
		}
		else {
			detailLogEntry.addDetailLine(detailLine);
		}
	}

	public MainLogEntry(String originalTitleLine, String time, LogLevel severity) {
		this.originalTitleLine = originalTitleLine;
		this.time = time;
		this.severity = severity;
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
		String details = originalTitleLine + "\n" + LogUtil.concatDetailEntries(getDetailLogEntry());
		return details;
	}

	@Override
	public String toString() {
		return getTitleLine();
	}
}
