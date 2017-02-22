package com.epam.scanner.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileReader {
	private static final Logger LOGGER = LogManager.getLogger(FileReader.class);

    public static String read(File file) {
        String text = null;
        try {
            @SuppressWarnings("resource")
			FileInputStream inFile = new FileInputStream(file);
            byte[] str = new byte[inFile.available()];
            inFile.read(str);
            text = new String(str);
        } catch (IOException c) {
            LOGGER.fatal("File " + file + " not found.");
            throw new RuntimeException(c);
        }
        return text;
    }
    
    public static String read(String file) {
    	return read(new File(file)); 

    }
}