package com.epam.scanner.app;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.service.AppService;
import com.epam.scanner.ui.AppWindow;


public class App implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(App.class);
	
		private static App instance;
		private static AppWindow window = AppWindow.getInstance();
		
		public static synchronized App getInstance() {
			if (instance == null) {
				instance = new App();
			}
			return instance;
		}
	

	@Override
	public void run() {
		AppService appService = new AppService();
		
		window.setProgressBarValue(0);
		
		List<File> pluginPropFiles = appService.collectFiles("*.properties",  AppConfig.getGradlePluginsPath() + "/gradle-plugins/src/main/resources/META-INF/gradle-plugins");
		
		window.setProgressBarValue(1);
		
		Map<String, String> pluginProperties = appService.collectPropertiesFromGroovyPlugins(pluginPropFiles);
		
		window.setProgressBarValue(2);
		
		Map<String, List<String>> groovyPluginProps = appService.collectDependencies(pluginProperties);
		
		window.setProgressBarValue(3);
		
		window.setLabelValue("Collecting settings files");
		
		List<File> searchFiles = appService.collectFiles("settings-"+ AppConfig.getComponent() +".gradle");
		
		window.setProgressBarValue(23);
		
		window.setLabelValue("Finding umbrellas");
		
		Map<File, List<File>> layers = appService.findUmbrellas(searchFiles);
		
		window.setProgressBarValue(34);
		
		window.setLabelValue("Scanning umbrellas for settings files");
		
		Map<File, List<File>> umbrellas = appService.scanForSettingsGraldeFile(layers);
		
		window.setProgressBarValue(45);
		
		window.setLabelValue("Collecting dependencies");
		
		Map<String, List<String>> props = appService.collectDependencies(umbrellas, groovyPluginProps, "gradle");
		
		window.setProgressBarValue(55);
		
		window.setLabelValue("Saving tdp libs files");
		
		appService.saveFiles(props);
		
		window.setProgressBarValue(64);
		
		window.setLabelValue("Copying files");
		
		appService.copyFilesByUmbrella(umbrellas);
		
		window.setProgressBarValue(100);
		
	}
	
	
}