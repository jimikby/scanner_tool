package com.epam.scanner.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileSaver {
	private static final Logger LOG = LogManager.getLogger(FileSaver.class);

    public static void save(String fileName, String text) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
        	LOG.fatal("RuntimeException " + fileName +  ";"+ e);
            throw new RuntimeException(e);
        }
    }
}