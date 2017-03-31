package com.epam.scanner.utils.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.epam.scanner.config.AppConfig;

public class ComponentVersionManager {

	private final static Properties componentVerisons = new Properties();
	
	static {
		try {
			componentVerisons.load((InputStream) new FileInputStream(
					new File(AppConfig.getTdpPath() + File.separator + "component_version.properties")));
	
		} catch (IOException e5) {
			e5.printStackTrace();
		}
	}

	private ComponentVersionManager() {
		
	}

	public static String getProperty(String key) {
		return componentVerisons.getProperty(key);
	}
	
}