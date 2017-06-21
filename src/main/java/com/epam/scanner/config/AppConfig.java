package com.epam.scanner.config;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConfig {

	private static String component = System.getProperty("gradle.components");
	
	public static List<String> getComponents() {

		List<String> comps = new ArrayList<>();

		for ( String comp : component.split("\\s*,\\s*")) {
			comps.add ("settings-" + comp + ".gradle");
		}
		return comps;
	}

	public static String getTdpPath() {
		return ".";
	}

	public static String getNewTdpPath() {
		return ".";
	}
	
	public static String getGradlePluginsPath() {
		return ".." + File.separator + "gradle-plugins";
	}
}