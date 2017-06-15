package com.epam.scanner.config;

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
		return ".."+ File.separator + "tdp";
	}


	public static String getNewTdpPath() {
		return ".." + File.separator + "tdp";
	}
	
	public static String getGradlePluginsPath() {
		return ".." + File.separator + "gradle-plugins";
	}

}