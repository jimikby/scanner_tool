package com.epam.scanner.utils.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

	private final static Properties config = new Properties();

	private ConfigurationManager() {
	}

	public static String getProperty(String key) {
		File configFile = new File("config.properties");
		if (configFile.exists()) {
			try {
				config.load((InputStream) new FileInputStream(configFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return config.getProperty(key);
	}

	public static void saveProperties(String tdpPath, String gradlePluginsPath, String newTdpPath) {
		Properties newConfig = new Properties();
		newConfig.setProperty("tdp.path", tdpPath.replace("\\", File.separator));
		newConfig.setProperty("tdp.new.path", newTdpPath.replace("\\", File.separator));
		newConfig.setProperty("gradle.plugins.path", gradlePluginsPath.replace("\\", File.separator));

		try {
			newConfig.store(new FileOutputStream(new File("config.properties")), "Scanner config");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}