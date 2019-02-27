package com.axonivy.ivy.supplements.logviewer.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.axonivy.ivy.supplements.logviewer.MainController;

public class TestLogFileFilter {

	private List<MainLogEntry> initLogList() throws IOException{
		File testLogFile = new File("src/test/resources/testlog-rc.log");
		List<File> testlogfiles = new ArrayList<File>();
		testlogfiles.add(testLogFile);

		LogFileParser logFileParser = new LogFileParser(testlogfiles);
		List<MainLogEntry> logList = logFileParser.parse();
		return logList;
	}

	@Test
	public void filterEntriesFromTestLog() throws IOException {
		List<MainLogEntry> logList = initLogList();
		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:10.195", "14:45:12.494", logList);
		assertEquals(6, filteredList.size());
	}

	@Test
	public void filterEntriesWhenEndTimeBeforeStartTime() throws IOException {
		List<MainLogEntry> logList = initLogList();
		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:12.494","14:45:10.195", logList);
		assertEquals(6, filteredList.size());
	}

	@Test
	public void filterEntriesWithMinimalTimeDesc() throws IOException {
		List<MainLogEntry> logList = initLogList();
		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:12","14:45:10", logList);
		assertEquals(2, filteredList.size());
	}
	@Test
	public void filterEntriesWithMinimalTimeDesc2() throws IOException {
		List<MainLogEntry> logList = initLogList();
		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:11","14:45:09", logList);
		assertEquals(2, filteredList.size());
	}

	@Test
	public void filterMultipleFiles() throws IOException {
		File testLogFile1 = new File("src/test/resources/testlog-rc.log");
		File testLogFile2 = new File("src/test/resources/testlog-rc2.log");
		List<File> testlogfiles = Arrays.asList(testLogFile1, testLogFile2);

		LogFileParser logFileParser = new LogFileParser(testlogfiles);
		List<MainLogEntry> logList = logFileParser.parse();

		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:10", "14:45:11", logList);
		assertEquals(5, filteredList.size());
	}

	@Test
	public void orderFilesByTime() throws IOException {
		File testLogFile1 = new File("src/test/resources/testlog-rc.log");
		File testLogFile2 = new File("src/test/resources/testlog-rc2.log");
		List<File> testlogfiles = Arrays.asList(testLogFile1, testLogFile2);

		LogFileParser logFileParser = new LogFileParser(testlogfiles);
		List<MainLogEntry> logList = logFileParser.parse();

		List<MainLogEntry> filteredList = MainController.filterByTime("14:45:10", "14:45:11", logList);
		List<MainLogEntry> orderedList = MainController.orderEntryListByChronology(filteredList);
		assertEquals("14:45:10.195", orderedList.get(4).getTime());
	}

	@Test
	public void validateDateStringExpectsFalse(){
		assertEquals(false, MainController.validateTimeString("12:33:22", ""));
	}

	@Test
	public void validateDateStringExpectsTrue(){
		assertEquals(true, MainController.validateTimeString("12:33:22", "12:33:24"));
	}

}