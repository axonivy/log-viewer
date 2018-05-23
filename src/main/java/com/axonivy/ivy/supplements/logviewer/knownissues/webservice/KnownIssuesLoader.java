package com.axonivy.ivy.supplements.logviewer.knownissues.webservice;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.axonivy.ivy.supplements.logviewer.ExceptionDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KnownIssuesLoader {
	private static Future<String> futureJsonContent;

	public static KnownIssuesWrapper getIssues() {
		setupExecutor();

		try {
			Gson gson = new GsonBuilder().create();
			KnownIssuesWrapper knownIssues = gson.fromJson(futureJsonContent.get(), KnownIssuesWrapper.class);
			return knownIssues;
		} catch (Exception e) {
			new ExceptionDialog().showException(e);
		}
		return null;
	}

	private static void setupExecutor() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<String> task = () -> getJsonFileFromServer();
		futureJsonContent = executor.submit(task);
	}

	private static String getJsonFileFromServer() {
		String content = "";
		try {
			content = readStringFromURL("https://log.axonivy.rocks/knownIssues.json");
		} catch (Exception e) {
			new ExceptionDialog().showException(e);
		}
		return content;
	}

	public static String readStringFromURL(String requestURL) throws IOException
	{
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}

}