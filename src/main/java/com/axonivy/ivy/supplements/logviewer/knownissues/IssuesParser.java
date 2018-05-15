package com.axonivy.ivy.supplements.logviewer.knownissues;

import java.util.ArrayList;
import java.util.List;

import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssue;
import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssuesLoader;
import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssuesWrapper;
import com.axonivy.ivy.supplements.logviewer.parser.DetailLogEntry;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;

public class IssuesParser {
	List<MainLogEntry> logEntries;

	public List<KnownIssue> getKnownIssues() {
		KnownIssuesWrapper issueWrapper = KnownIssuesLoader.getIssues();
		List<KnownIssue> issues = new ArrayList<KnownIssue>();
		if (logEntries == null || issueWrapper == null) {
			return issues;
		}
		for (MainLogEntry logEntry : logEntries) {
			for (KnownIssue issue : issueWrapper.knownIssues) {
				if (matchException(logEntry, issue)) {
					if (!issues.contains(issue)) {
						issues.add(issue);
					}
				}
			}
		}
		return issues;
	}

	private boolean matchException(MainLogEntry logEntry, KnownIssue issue) {
		for (String match : issue.matchExceptionParts) {
			DetailLogEntry detailLogEntry = logEntry.getDetailLogEntry();
			if (detailLogEntry == null) {
				return false;
			}
			if (!detailLogEntry.getDetailText().contains(match)) {
				return false;
			}
		}
		return true;
	}

	public void setLogEntries(List<MainLogEntry> logEntries) {
		this.logEntries = logEntries;
	}
}