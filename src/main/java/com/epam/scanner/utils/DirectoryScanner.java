package com.epam.scanner.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DirectoryScanner {

	@SuppressWarnings("unchecked")
	public static List<File> listf(File root, String fileNames) {
		Collection<File> files = new ArrayList<File>();
		try {

			IOFileFilter fileFilter = new WildcardFileFilter(fileNames);
			 files = FileUtils.listFiles(root, fileFilter, TrueFileFilter.TRUE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (List<File>) files;
	}
}