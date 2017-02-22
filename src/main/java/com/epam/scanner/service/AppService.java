package com.epam.scanner.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.epam.scanner.config.AppConfig;
import com.epam.scanner.utils.DirectoryScanner;
import com.epam.scanner.utils.FileReader;
import com.epam.scanner.utils.FileSaver;
import com.epam.scanner.utils.manager.LibsTdpManager;

public class AppService {

	private static final Logger LOG = LogManager.getLogger(AppService.class);

	public List<File> collectFiles(String mask) {
		List<File> searchfiles;
		searchfiles = DirectoryScanner.listf(new File(AppConfig.TDP_PATH), mask);
		return searchfiles;
	}

	public List<File> collectFiles(String mask, String path) {
		List<File> searchfiles;
		searchfiles = DirectoryScanner.listf(new File(path), mask);
		return searchfiles;
	}

	public Map<String, List<String>> collectDependencies(Map<File, List<File>> umbrellas,
			Map<String, List<String>> groovyPluginProps, String fileType) {
		Map<String, List<String>> propsMap = new HashMap<>();
		for (Entry<File, List<File>> entry : umbrellas.entrySet()) {
			List<String> props = new ArrayList<>();
			for (File file : entry.getValue()) {
				List<File> searchfiles = new ArrayList<>();
				if (file.isDirectory()) {
					searchfiles = DirectoryScanner.listf(file, "*." + fileType);
				}
				if (!searchfiles.isEmpty()) {
					for (File gradleFile : searchfiles) {
						props.addAll(scanFileForProperties(gradleFile, groovyPluginProps));

					}
				}
			}
			Splitter splitter = Splitter.on("\\");
			String path = splitter.splitToList(entry.getKey().getAbsolutePath().replace(AppConfig.TDP_PATH + "\\", ""))
					.get(0).replace(".gradle", "").replace("settings-", "");

			propsMap.put(path, props);
		}
		return propsMap;
	}

	private List<String> scanFileForProperties(File gradleFile, Map<String, List<String>> groovyPluginProps) {
		List<String> props = new ArrayList<>();
		String text = FileReader.read(gradleFile).replaceAll(" ", "").replace("\"", "'").replace("project.property",
				"property");
		Splitter splitter = Splitter.on(CharMatcher.anyOf("\n\r")).omitEmptyStrings();
		List<String> result = splitter.splitToList(text);
		List<String> resultSplit = new ArrayList<>();
		for (String line : result) {
			if (groovyPluginProps != null) {
				splitter = Splitter.on("applyplugin:'");
				resultSplit = splitter.splitToList(line);

				if (resultSplit.size() > 1) {
					splitter = Splitter.on("'");
					String plugin = splitter.splitToList(resultSplit.get(1)).get(0);
					if (groovyPluginProps.containsKey(plugin) && !groovyPluginProps.get(plugin).isEmpty()) {
						LOG.info(gradleFile + "; " + plugin);
						props.addAll(groovyPluginProps.get(plugin));
					}
				}
			}

			splitter = Splitter.on("property('");
			resultSplit = splitter.splitToList(line);

			if (resultSplit.size() > 1) {
				splitter = Splitter.on("')");
				resultSplit = splitter.splitToList(resultSplit.get(1));
				String prop = resultSplit.get(0);
				if (!prop.equals("gradle.plugin.version") && !prop.contains("component.")) {

					splitter = Splitter.on("\\");
					props.add(prop);

				}
			}
		}
		return props;
	}

	public void saveFiles(Map<String, List<String>> properties) {

		Map<String, Map<String, String>> propFilesMap = new TreeMap<>();

		for (Map.Entry<String, List<String>> entry : properties.entrySet()) {

			for (String prop : entry.getValue()) {

				List<String> libsProp = LibsTdpManager.getProperty(prop);

				String propValue = libsProp.get(0);
				String propName = prop;

				if (propValue != null) {
					String fileName = entry.getKey();
					fileName += File.separator + "libs_tdp" + File.separator + libsProp.get(1) + ".properties";

					new File(AppConfig.TDP_NEW_PATH + File.separator + entry.getKey() + File.separator + "libs_tdp")
							.mkdir();

					Map<String, String> propMap = propFilesMap.get(fileName);
					if (propMap != null) {
						propMap.put(propName, propValue);
						propFilesMap.put(fileName, propMap);
					} else {
						Map<String, String> newPropMap = new TreeMap<>();
						newPropMap.put(propName, propValue);
						propFilesMap.put(fileName, newPropMap);
					}
				} else {
					LOG.info(libsProp);
				}
			}
		}

		for (Map.Entry<String, Map<String, String>> entry : propFilesMap.entrySet()) {

			Map<String, String> props = entry.getValue();
			String text = Joiner.on("\n").withKeyValueSeparator("=").join(props);
			String fileName = entry.getKey();
			LOG.info(AppConfig.TDP_NEW_PATH + File.separator + fileName);
			String textFile = AppConfig.TDP_NEW_PATH + File.separator + fileName;
			new File(AppConfig.TDP_NEW_PATH + File.separator + fileName.replace(new File(fileName).getName(),"")).mkdirs();
			FileSaver.save(textFile, text);

		}
	}

	public Map<File, List<File>> findUmbrellas(List<File> searchfiles) {
		Map<File, List<File>> filterFiles = new HashMap<>();
		for (File file : searchfiles) {
			String text = FileReader.read(file);
			Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n")).omitEmptyStrings();

			List<String> result = splitter.splitToList(text);
			List<File> files = new ArrayList<>();
			for (String unbrella : result) {
				if (unbrella.matches("includeBuild(.*)")) {
					splitter = Splitter.on("includeBuild");
					List<String> resultSplit = splitter.splitToList(unbrella);
					splitter = Splitter.on("//");
					if (resultSplit.size() > 1) {
						resultSplit = splitter.splitToList(resultSplit.get(1));
						files.add(new File(AppConfig.TDP_PATH + "/"
								+ resultSplit.get(0).replaceAll("'", "").replaceAll("\"", "").trim()));
					}
				}
			}
			filterFiles.put(file, files);
		}
		return filterFiles;
	}

	public Map<File, List<File>> scanForSettingsGraldeFile(Map<File, List<File>> layers) {
		Map<File, List<File>> umbrellas = new HashMap<>();
		for (Entry<File, List<File>> entry : layers.entrySet()) {
			List<File> files = collectFilesFromSettingsGradle(entry.getValue());
			umbrellas.put(entry.getKey(), files);
		}

		return umbrellas;
	}

	private List<File> collectFilesFromSettingsGradle(List<File> list) {
		List<File> files = new ArrayList<>();
		String text;
		files.addAll(list);
		for (File file : list) {
			File settingsFile = new File(file.getAbsolutePath() + File.separator + "settings.gradle");
			if (settingsFile.exists()) {
				text = FileReader.read(settingsFile);
				Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n")).omitEmptyStrings();
				List<String> result = splitter.splitToList(text);
				for (String line : result) {
					if (line.matches("includeFlat(.*)")) {

						line = line.replace("includeFlat", "").replace("'", "").replace(" ", "").replace("\"", "")
								.replace("	", "");

						splitter = Splitter.on(",").trimResults();
						List<String> paths = splitter.splitToList(line);

						for (String path : paths) {

							files.add(new File(AppConfig.TDP_PATH + File.separator + path));
						}
					}
				}
			}
		}
		return files;
	}

	public void copyFilesByUmbrella(Map<File, List<File>> umbrellas) {
		for (Entry<File, List<File>> entry : umbrellas.entrySet()) {
			String layer =  entry.getKey().getName().replace(".gradle", "").replace("settings-", "");
			File layerPath = new File(AppConfig.TDP_NEW_PATH  + File.separator + layer);
			
			for (File file : entry.getValue()) {
				File destinaton = new File(file.getAbsolutePath().replace(AppConfig.TDP_PATH, layerPath.toString()));
				try {
					FileUtils.copyDirectory(file, destinaton);
					LOG.info("Copy: " + file + " -> " + destinaton);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			try {
				FileUtils.copyFileToDirectory(entry.getKey(),layerPath);
				new File(layerPath + File.separator + entry.getKey().getName()).renameTo(new File(layerPath + File.separator + "settings.gradle"));	
				FileUtils.copyFileToDirectory(new File(AppConfig.TDP_PATH + "/build.gradle"), layerPath);
				FileUtils.copyFileToDirectory(new File(AppConfig.TDP_PATH + "/component_version.properties"), layerPath);
				FileUtils.copyFileToDirectory(new File(AppConfig.TDP_PATH + "/startup.gradle"), layerPath);
				FileUtils.copyFileToDirectory(new File(AppConfig.TDP_PATH + "/gradle.properties"), layerPath);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, String> collectPropertiesFromGroovyPlugins(List<File> pluginPropFiles) {
		Map<String, String> pluginProp = new HashMap<>();
		Map properties = new Properties();
		for (File file : pluginPropFiles) {
			try {
				((Properties) properties).load((InputStream) new FileInputStream(file));
				String path = (AppConfig.GRADLE_PLUGINS_PATH + File.separator + "gradle-plugins" + File.separator
						+ "src" + File.separator + "main" + File.separator + "groovy" + File.separator
						+ (String) properties.get("implementation-class")).replace(".", File.separator) + ".groovy";
				path = pluginProp.put(file.getName().replace(".properties", ""), path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pluginProp;
	}

	public Map<String, List<String>> collectDependencies(Map<String, String> pluginProperties) {
		Map<String, List<String>> propsMap = new HashMap<>();
		Map<String, List<String>> fakeMap = null;
		for (Map.Entry<String, String> entry : pluginProperties.entrySet()) {
			propsMap.put(entry.getKey(), scanFileForProperties(new File(entry.getValue()), fakeMap));
		}
		return propsMap;
	}

}