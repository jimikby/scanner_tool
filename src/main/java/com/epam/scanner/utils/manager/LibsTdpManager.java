package com.epam.scanner.utils.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.epam.scanner.config.AppConfig;

public class LibsTdpManager {

	private final static Properties libsTdp = new Properties();
	private final static Properties libsTdp_1_3  = new Properties(); 
	private final static Properties libsTdp_3_0  = new Properties(); 
	private final static Properties libsTdp_3_1  = new Properties(); 
	private final static Properties libsTdp_4_0 = new Properties(); 
	
	private LibsTdpManager() {
	}

	public static List<String> getProperty(String key) {
		try {
			libsTdp.load((InputStream) new FileInputStream(new File(AppConfig.getTdpLibsPath() + "/" + "libs_tdp.properties")));
			libsTdp_1_3.load((InputStream) new FileInputStream(new File(AppConfig.getTdpLibsPath()  + "/" + "libs_tdp_1_3.properties")));
			libsTdp_3_0.load((InputStream) new FileInputStream(new File(AppConfig.getTdpLibsPath()  + "/" + "libs_tdp_3_0.properties")));
			libsTdp_3_1.load((InputStream) new FileInputStream(new File(AppConfig.getTdpLibsPath() + "/" + "libs_tdp_3_1.properties")));
			libsTdp_4_0.load((InputStream) new FileInputStream(new File(AppConfig.getTdpLibsPath() + "/" + "libs_tdp_4_0.properties")));
		} catch (IOException e5) {
			e5.printStackTrace();
		}

		String prop;
		String rep;
	
			prop = libsTdp.getProperty(key);
			rep = "libs_tdp";
		if (prop == null) {
				prop = libsTdp_1_3.getProperty(key);
				rep = "libs_tdp_1_3";
		} 
		
		if (prop == null) {
					prop = libsTdp_3_0.getProperty(key);
					rep = "libs_tdp_3_0";
		}
		 if (prop == null) {
						prop = libsTdp_3_1.getProperty(key);
						rep = "libs_tdp_3_1";
						}
		 
		if (prop == null) {
							prop = libsTdp_4_0.getProperty(key);
							rep = "libs_tdp_4_0";}
		
		 if (prop == null) {
							prop = null;
							rep = null;
							System.out.println(key);
						}


		List<String> values = new ArrayList<>();

		values.add(prop);
		values.add(rep);
		return values;

	}
}