/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 */
public class ZipUtil {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Deletes the given directory or file from the file system. It will immediately abort at the point of failure. No undo!
	 *
	 * @param aFile - the directory or file to (recursively) delete
	 * @return true if the file was successfully deleted
	 */
	public static boolean deleteFile(final File aFile) {
		if (aFile != null && aFile.exists()) {
			if (aFile.isDirectory()) {
				for (final File fileInFolder : aFile.listFiles()) {
					if (!deleteFile(fileInFolder)) {
						return false;
					}
				}
			}
			return aFile.delete();
		}
		return true;
	}

	public static void compress(final File aFile) throws IOException {
		if (aFile != null) {
			if (aFile.exists()) {

				// Create the ZIP file
				FileOutputStream fout = new FileOutputStream(aFile.getAbsolutePath() + ".zip");
				ZipOutputStream zipOut = new ZipOutputStream(fout);

				compressFile("", aFile, zipOut);

				// Complete the ZIP file
				zipOut.close();
				fout.close();
			} else {
				logger.debug("File doesn't exist: {}", aFile.getAbsolutePath());
			}
		}
	}

	public static void compressFile(final String pathTo, final File aFile, final ZipOutputStream zipOut) throws IOException {
		if (aFile != null && aFile.exists()) {
			String anEntry = pathTo + aFile.getName() + (aFile.isDirectory() && !aFile.getName().endsWith("/") ? "/" : "");
			zipOut.putNextEntry(new ZipEntry(anEntry));

			logger.debug("zipping file: {}", aFile.getAbsolutePath());
			if (aFile.isDirectory()) {
				for (final File fileInFolder : aFile.listFiles()) {
					compressFile(anEntry + "/", fileInFolder, zipOut);
				}
			} else {
				FileInputStream in;
				byte[] buf;
				int len;

				// Compress the file
				in = new FileInputStream(aFile);

				// Transfer bytes from the file to the ZIP file
				buf = new byte[1024];
				while ((len = in.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}
				in.close();
			}
			zipOut.closeEntry();
			logger.debug("zipped file");
		}
	}

}
