package com.axonivy.ivy.supplements.logviewer.knownissues;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssue;
import com.axonivy.ivy.supplements.logviewer.knownissues.webservice.KnownIssuesWrapper;
import com.axonivy.ivy.supplements.logviewer.parser.LogFileParser;
import com.axonivy.ivy.supplements.logviewer.parser.MainLogEntry;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class TestIssuesParser {

	@Test
	public void testBrokenPipe() throws Exception {
		List<MainLogEntry> logEntries = getLogList("src/test/resources/TestBrokenPipeIssueFinder.log");
		List<KnownIssue> knownIssues = getKnownIssues(logEntries);

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

	@Test
	public void testAdditionalJsonField() throws Exception {
		Gson gson = new Gson();
		KnownIssuesWrapper jsonObject = gson.fromJson(
				new JsonReader(new FileReader("src/test/resources/knownIssues.json")), KnownIssuesWrapper.class);
		assertThat(jsonObject.knownIssues).isNotNull();
	}

	@Test
	public void testEmptyStack() throws Exception {
		List<MainLogEntry> logEntries = getLogList("src/test/resources/TestEmptyStackException.log");
		List<KnownIssue> knownIssues = getKnownIssues(logEntries);

		KnownIssue emptyStackIssue = createEmptyStackIssue();
		assertThat(knownIssues).containsOnly(emptyStackIssue);
	}

	private KnownIssue createEmptyStackIssue() {
		return new KnownIssue("XIVY-2538",
				"EmptyStackException occurs if a task is reseted and a user want to submit the form",
				Arrays.asList("6.0.14", "7.0.6", "7.2"), null, Arrays.asList("Caused by: java.util.EmptyStackException",
						"ch.ivyteam.ivy.bpm.engine.internal.core.CallStack.checkEmptyStack"));
	}

	@Test
	public void testEmptyStackAndBrokenPipe() throws Exception {
		List<MainLogEntry> logEntries = getLogList("src/test/resources/TestEmptyStackAndBrokenPipe.log");
		List<KnownIssue> knownIssues = getKnownIssues(logEntries);

		KnownIssue brokenPipeIssue = createBrokenPipeIssue();
		KnownIssue emptyStackIssue = createEmptyStackIssue();
		assertThat(knownIssues).contains(brokenPipeIssue, emptyStackIssue);
	}

	private List<KnownIssue> getKnownIssues(List<MainLogEntry> logEntries) {
		IssuesParser parser = new IssuesParser();
		parser.setLogEntries(logEntries);
		List<KnownIssue> knownIssues = parser.getKnownIssues();
		return knownIssues;
	}

	private List<MainLogEntry> getLogList(String logFile) throws IOException {
		File testLogFile = new File(logFile);
		LogFileParser logFileParser = new LogFileParser(testLogFile);
		return logFileParser.parse();
	}
}