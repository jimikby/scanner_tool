package com.epam.scanner.app;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.service.AppService;


public class App {
	
	private static final Logger LOG = LogManager.getLogger(App.class);

	public static void main(String[] args) {

		AppService appService = new AppService();
		
		List<File> pluginPropFiles = appService.collectFiles("*.properties",  AppConfig.GRADLE_PLUGINS_PATH + "/gradle-plugins/src/main/resources/META-INF/gradle-plugins");
		Map<String, String> pluginProperties = appService.collectPropertiesFromGroovyPlugins(pluginPropFiles);
		Map<String, List<String>> groovyPluginProps = appService.collectDependencies(pluginProperties);
		
		
		List<File> searchFiles = appService.collectFiles("settings-services.gradle");
		Map<File, List<File>> layers = appService.findUmbrellas(searchFiles);
		Map<File, List<File>> umbrellas = appService.scanForSettingsGraldeFile(layers);
		Map<String, List<String>> props = appService.collectDependencies(umbrellas, groovyPluginProps, "gradle");
		appService.saveFiles(props);
		appService.copyFilesByUmbrella(umbrellas);
	}
}