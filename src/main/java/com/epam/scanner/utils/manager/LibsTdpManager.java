package com.epam.scanner.utils.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.epam.scanner.config.AppConfig;

public class LibsTdpManager {

	private final static Properties libsTdp = new Properties();
	private final static Properties libsTdp_1_3 = new Properties();
	private final static Properties libsTdp_3_0 = new Properties();
	private final static Properties libsTdp_3_1 = new Properties();
	private final static Properties libsTdp_4_0 = new Properties();
	
	static {
		try {
			libsTdp.load((InputStream) new FileInputStream(
					new File(AppConfig.getTdpPath() + File.separator + "libs_tdp" + File.separator + "libs_tdp.properties")));
			libsTdp_1_3.load((InputStream) new FileInputStream(new File(
					AppConfig.getTdpPath() + File.separator + "libs_tdp" + File.separator + "libs_tdp_1_3.properties")));
			libsTdp_3_0.load((InputStream) new FileInputStream(new File(
					AppConfig.getTdpPath() + File.separator + "libs_tdp" + File.separator + "libs_tdp_3_0.properties")));
			libsTdp_3_1.load((InputStream) new FileInputStream(new File(
					AppConfig.getTdpPath() + File.separator + "libs_tdp" + File.separator + "libs_tdp_3_1.properties")));
			libsTdp_4_0.load((InputStream) new FileInputStream(new File(
					AppConfig.getTdpPath() + File.separator + "libs_tdp" + File.separator + "libs_tdp_4_0.properties")));
		} catch (IOException e5) {
			e5.printStackTrace();
		}
	}
	
	private LibsTdpManager() {
	}
	

	public static List<String> getProperty(String key) {

		List<String> values = new ArrayList<>();
	
		 values.add(libsTdp.getProperty(key));
		 values.add("libs_tdp");
		 
		 if (values.get(0) == null) {
			values.set(0,libsTdp_1_3.getProperty(key));
			values.set(1,"libs_tdp_1_3");
		 }
		 
		 if (values.get(0) == null) {
			values.set(0,libsTdp_3_0.getProperty(key));
			values.set(1,"libs_tdp_3_0");
		 }
		 
		 if (values.get(0) == null) {
			values.set(0,libsTdp_3_1.getProperty(key));
			values.set(1,"libs_tdp_3_1");
		 }
		
		 if (values.get(0) == null) {
			values.set(0,libsTdp_4_0.getProperty(key));
			values.set(1,"libs_tdp_4_0");
		 }
		 	
		return values;

	}
}