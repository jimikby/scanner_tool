package com.epam.scanner.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import com.epam.scanner.config.AppConfig;
import com.epam.scanner.service.AppService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;

public class App {

    private static final Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        AppService appService = new AppService();
        List<File> pluginPropFiles = appService.collectFiles("*.properties", AppConfig.getGradlePluginsPath()
                + File.separator + "gradle-plugins" + File.separator
                + "src" + File.separator + "main" + File.separator + "resources"
                + File.separator + "META-INF" + File.separator + "gradle-plugins");

        Map<String, String> pluginProperties = appService.collectPropertiesFromGroovyPlugins(pluginPropFiles);
        Map<String, List<String>> groovyPluginProps = appService.collectDependencies(pluginProperties);

        LOG.info(AppConfig.getComponents());
        List<File> searchFiles = appService.listOfFiles(new File(AppConfig.getTdpPath()),  AppConfig.getComponents());

        LOG.info(searchFiles);

        Map<File, List<File>> layers = appService.findUmbrellas(searchFiles);
        Map<File, List<File>> umbrellas = appService.scanForSettingsGraldeFile(layers);
        Map<String, List<String>> props = appService.collectDependencies(umbrellas, groovyPluginProps,
                "gradle");

        appService.saveFiles(props);
        appService.copyFilesByUmbrella(umbrellas);
    }
}
