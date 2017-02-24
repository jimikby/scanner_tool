package com.epam.scanner.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.service.AppService;
import com.epam.scanner.utils.manager.ConfigurationManager;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;

public class AppWindow {

	private JFrame frame;
	private JTextField pathToTdpField;
	private JTextField pathToGradlePluginsField;
	private JTextField pathToNewTdp;
	private JProgressBar progressBar;
	private JLabel msgLabel;
	private JButton startButton;
	private SwingWorker<Boolean, Integer> worker;

	private static AppWindow instance;
	private JLabel lblSelectLa;
	private JTextField componentField;
	
	private static final Logger LOG = LogManager.getLogger(AppWindow.class);

	public static synchronized AppWindow getInstance() {
		if (instance == null) {
			instance = new AppWindow();
		}
		return instance;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					AppWindow.getInstance().pathToTdpField.setText(AppConfig.getTdpPath());
					AppWindow.getInstance().pathToGradlePluginsField.setText(AppConfig.getGradlePluginsPath());
					AppWindow.getInstance().pathToNewTdp.setText(AppConfig.getNewTdpPath());
					AppWindow.getInstance().frame.setVisible(true);	
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 463, 426);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		pathToTdpField = new JTextField();
		pathToTdpField.setBounds(35, 48, 383, 20);
		frame.getContentPane().add(pathToTdpField);
		pathToTdpField.setColumns(10);

		progressBar = new JProgressBar();
		progressBar.setBounds(35, 271, 383, 14);
		frame.getContentPane().add(progressBar);

		msgLabel = new JLabel("");
		msgLabel.setBounds(35, 301, 383, 14);
		frame.getContentPane().add(msgLabel);

		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				AppConfig.setGradlePluginsPath(pathToGradlePluginsField.getText());
				AppConfig.setTdpPath(pathToTdpField.getText());
				AppConfig.setNewTdpPath(pathToNewTdp.getText());
				AppConfig.setComponent(componentField.getText());
				
				ConfigurationManager.saveProperties(AppConfig.getTdpPath(),AppConfig.getGradlePluginsPath(),AppConfig.getNewTdpPath());
				
				startButton.setVisible(false);
				worker = new SwingWorker<Boolean, Integer>() {
					 @Override
					 protected Boolean doInBackground() throws Exception {
				
						 AppService appService = new AppService();

							LOG.info(AppConfig.getComponent());

							setProgressBarValue(0);

							List<File> pluginPropFiles = appService.collectFiles("*.properties", AppConfig.getGradlePluginsPath()
									+ "/gradle-plugins/src/main/resources/META-INF/gradle-plugins");

							Map<String, String> pluginProperties = appService.collectPropertiesFromGroovyPlugins(pluginPropFiles);
							Map<String, List<String>> groovyPluginProps = appService.collectDependencies(pluginProperties);

							setProgressBarValue(5);

							setLabelValue("Collecting settings files");
							
							List<File> searchFiles = appService.listOfFiles(new File(AppConfig.getTdpPath()) ,"settings-" + AppConfig.getComponent() + ".gradle");
							
							LOG.info(searchFiles);
							
							setProgressBarValue(10);

							setLabelValue("Finding umbrellas");

							Map<File, List<File>> layers = appService.findUmbrellas(searchFiles);

							setProgressBarValue(15);

							setLabelValue("Scanning umbrellas for settings files");

							Map<File, List<File>> umbrellas = appService.scanForSettingsGraldeFile(layers);

							setProgressBarValue(20);

							setLabelValue("Collecting dependencies");

							Map<String, List<String>> props = appService.collectDependencies(umbrellas, groovyPluginProps,
									"gradle");

							setLabelValue("Saving tdp libs files");

							appService.saveFiles(props);

							setProgressBarValue(60);

							setLabelValue("Copying files");

							appService.copyFilesByUmbrella(umbrellas);

							setProgressBarValue(100);
						
					  return true;
					 }

					 protected void done() {
					  boolean status;
					  try {
					   // Retrieve the return value of doInBackground.
					   status = get();
					   msgLabel.setText("Completed");
					  } catch (InterruptedException e) {
					   // This is thrown if the thread's interrupted.
					  } catch (ExecutionException e) {
					   // This is thrown if we throw an exception
					   // from doInBackground.
					  }
					 }

					 @Override
					 protected void process(List<Integer> chunks) {
		
					 }

					};

					worker.execute();

			}
		});
		startButton.setBounds(329, 338, 89, 23);
		frame.getContentPane().add(startButton);

		JLabel pathToTdpLabel = new JLabel("Path to TDP");
		pathToTdpLabel.setBounds(35, 24, 383, 23);
		frame.getContentPane().add(pathToTdpLabel);

		pathToGradlePluginsField = new JTextField();
		pathToGradlePluginsField.setColumns(10);
		pathToGradlePluginsField.setBounds(35, 103, 383, 20);
		frame.getContentPane().add(pathToGradlePluginsField);

		JLabel lblPathToGradle = new JLabel("Path to Gradle plugins");
		lblPathToGradle.setBounds(35, 79, 383, 23);
		frame.getContentPane().add(lblPathToGradle);

		JLabel lblPathForNew = new JLabel("Path for new TDP");
		lblPathForNew.setBounds(35, 134, 383, 23);
		frame.getContentPane().add(lblPathForNew);

		pathToNewTdp = new JTextField();
		pathToNewTdp.setColumns(10);
		pathToNewTdp.setBounds(35, 158, 383, 20);
		frame.getContentPane().add(pathToNewTdp);

		lblSelectLa = new JLabel("Select component");
		lblSelectLa.setBounds(35, 200, 383, 23);
		frame.getContentPane().add(lblSelectLa);

		componentField = new JTextField();
		componentField.setText("*");
		componentField.setColumns(10);
		componentField.setBounds(35, 224, 383, 20);
		frame.getContentPane().add(componentField);

	}

	public void setProgressBarValue(int value) {
		progressBar.setValue(value);

		if (value == 100) {
			msgLabel.setText("Done");
			progressBar.setValue(0);
			startButton.setVisible(true);
		}
	}

	public void setLabelValue(String value) {
		msgLabel.setText(value);
	}

	public int getProgressBarValue() {
		return progressBar.getValue();
	}
}
