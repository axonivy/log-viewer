package com.axonivy.ivy.supplements.logviewer.knownissues.webservice;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import com.axonivy.ivy.supplements.logviewer.ExceptionDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KnownIssuesLoader {
	private static String jsonContent;

	public static KnownIssuesWrapper getIssues() {
		jsonContent = getJsonFileFromServer();
		Gson gson = new GsonBuilder().create();
		KnownIssuesWrapper knownIssues = gson.fromJson(jsonContent, KnownIssuesWrapper.class);
		return knownIssues;
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