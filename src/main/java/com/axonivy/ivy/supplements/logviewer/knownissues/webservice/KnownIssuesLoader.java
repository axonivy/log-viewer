package com.axonivy.ivy.supplements.logviewer.knownissues.webservice;

import java.io.ByteArrayOutputStream;
import java.net.URL;
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
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			URL jsonUrl = new URL("https://log.axonivy.rocks/knownIssues.json");
			byte[] bytes = new byte[jsonUrl.openStream().available()];

			jsonUrl.openStream().read(bytes);
			baos.write(bytes);
			content = baos.toString();
		} catch (Exception e) {
			new ExceptionDialog().showException(e);
		}
		return content;
	}

}