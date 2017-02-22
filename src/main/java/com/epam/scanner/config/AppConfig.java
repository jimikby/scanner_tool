package com.epam.scanner.config;

import com.epam.scanner.utils.manager.ConfigurationManager;

public class AppConfig {
	
	private static String tdpPath  = ConfigurationManager.getProperty("tdp.path");
	private static String newTdpPath = ConfigurationManager.getProperty("tdp.new.path");
	private static String gradlePluginsPath = ConfigurationManager.getProperty("gradle.plugins.path");
	private static String tdpLibsPath  = ConfigurationManager.getProperty("tdp.libs");
	private static String component;
	
	public static String getComponent() {
		return component;
	}
	
	public static void setComponent(String component) {
		AppConfig.component = component;
	}
	
	public static String getTdpPath() {
		return tdpPath;
	}
	
	public static void setTdpPath(String tdpPath) {
		AppConfig.tdpPath = tdpPath;
	}
	
	
	public static String getNewTdpPath() {
		return newTdpPath;
	}
	
	public static void setNewTdpPath(String newTdpPath) {
		AppConfig.newTdpPath = newTdpPath;
	}
	
	public static String getGradlePluginsPath() {
		return gradlePluginsPath;
	}
	
	public static void setGradlePluginsPath(String gradlePluginsPath) {
		AppConfig.gradlePluginsPath = gradlePluginsPath;
	}
	public static String getTdpLibsPath() {
		return tdpLibsPath;
	}
	
	public static void setTdpLibsPath(String tdpLibsPath) {
		AppConfig.tdpLibsPath = tdpLibsPath;
	}
	
	
	
}