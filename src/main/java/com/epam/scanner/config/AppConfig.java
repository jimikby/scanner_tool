package com.epam.scanner.config;

import com.epam.scanner.utils.manager.ConfigurationManager;

public class AppConfig {
	
	public final static String TDP_PATH  = ConfigurationManager.getProperty("tdp.path");
	public final static String TDP_NEW_PATH = ConfigurationManager.getProperty("tdp.new.path");
	public final static String GRADLE_PLUGINS_PATH = ConfigurationManager.getProperty("gradle.plugins.path");
	public final static String TDP_LIBS  = ConfigurationManager.getProperty("tdp.libs");

}