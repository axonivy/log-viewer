package com.axonivy.ivy.supplements.logviewer.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestLogFileParser {
	/*
	 * This test tests the current state of parsing, which is not perfect yet.
	 */
	@Test
	public void parseLogFile() throws IOException {
		File testLogFile = new File("src/test/resources/testlog-rc.log");

		LogFileParser logFileParser = new LogFileParser(testLogFile);
		List<MainLogEntry> logList = logFileParser.parse();

		assertThat(logList).hasSize(891);

		List<MainLogEntry> fatals = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.FATAL))
				.collect(Collectors.toList());
		assertThat(fatals).hasSize(1);

		List<MainLogEntry> errors = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.ERROR))
				.collect(Collectors.toList());
		assertThat(errors).hasSize(1);

		MainLogEntry error = errors.get(0);
		assertThat(error.getSeverity()).isEqualTo(LogLevel.ERROR);
		assertThat(error.getTime()).isEqualTo("14:47:07.860");
		assertThat(error.getOriginalTitleLine()).isEqualTo(
				"14:47:07.860 ERROR [ch.ivyteam.ivy.webserver.internal.exception] [http-nio-8080-exec-5] [requestId=229, executionContext=SYSTEM] ");
		assertThat(error.getTitleLine()).isEqualTo("14:47:07.860 ERROR: Special Test ERROR");

		List<MainLogEntry> warnings = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.WARN))
				.collect(Collectors.toList());
		assertThat(warnings).hasSize(4);

		List<MainLogEntry> information = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.INFO))
				.collect(Collectors.toList());
		assertThat(information).hasSize(884);

		List<MainLogEntry> debugs = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.DEBUG))
				.collect(Collectors.toList());
		assertThat(debugs).hasSize(1);
	}
	
	
	@Test
	public void testDetailLogEntry() throws IOException {
		File testLogFile = new File("src/test/resources/testlog-rc.log");

		LogFileParser logFileParser = new LogFileParser(testLogFile);
		List<MainLogEntry> logList = logFileParser.parse();
		
		List<MainLogEntry> warnings = logList.stream().filter(e -> e.getSeverity().equals(LogLevel.WARN))
				.collect(Collectors.toList());
		assertThat(warnings).hasSize(4);
		
		MainLogEntry mainEntry = warnings.get(0);
		assertThat(mainEntry.getDetailLogEntry().get(0).getDetailText()).isEqualTo("  Problem while processing request 'http://localhost:8080/ivy/pro/TestAPP/AngularWfDemo$1/1614CC1E96512AEA/createTestTask.ivp'");
		assertThat(mainEntry.getDetailLogEntry().get(1).getDetailText()).isEqualTo("    [errorId=161519F22B42A867, requestId=297, executionContext=SYSTEM, client=127.0.0.1]");
		assertThat(mainEntry.getDetailLogEntry()).hasSize(70);
	}
}
