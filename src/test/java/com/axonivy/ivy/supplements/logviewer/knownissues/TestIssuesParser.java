package com.axonivy.ivy.supplements.logviewer.knownissues;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssue;
import com.axonivy.ivy.supplements.logviewer.parser.LogFileParser;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;

public class TestIssuesParser {

	private List<MainLogEntry> logList;

	@Before
	public void setUp() throws IOException {
		File testLogFile = new File("src/test/resources/TestBrokenPipeIssueFinder.log");

		LogFileParser logFileParser = new LogFileParser(testLogFile);
		logList = logFileParser.parse();
	}

	@Test
	public void testBrokenPipe() throws Exception {
		IssuesParser parser = new IssuesParser();
		parser.setLogEntries(logList);
		List<KnownIssue> knownIssues = parser.getKnownIssues();
		KnownIssue brokenPipeIssue = createBrokenPipeIssue();
		assertThat(knownIssues).containsOnly(brokenPipeIssue);
	}

	private KnownIssue createBrokenPipeIssue() {
		return new KnownIssue("XIVY-2492", "Broken pipe when trying to load web assets like fonts or css files",
				Arrays.asList("7.0.5", "7.2"),
				"It is possible to disable the logging of those exceptions by adding following to the log4jconfig.xml:\r\n  "
						+ "  <!-- Prevent \"ClientAbortException: java.io.IOException: Broken pipe\" from filling the log -->\r\n  "
						+ "  <category name=\"org.apache.myfaces.application.ResourceHandlerImpl\" class=\"ch.ivyteam.log.Logger\">\r\n   "
						+ " <priority value=\"FATAL\"/>\r\n    </category>",
				Arrays.asList("org.apache.catalina.connector.ClientAbortException: java.io.IOException: Broken pipe",
						"at ch.ivyteam.ivy.dialog.execution.jsf.controller.resource.IvyFacesResourceFilter.filter"));
	}
}