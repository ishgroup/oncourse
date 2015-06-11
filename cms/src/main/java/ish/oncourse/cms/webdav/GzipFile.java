package ish.oncourse.cms.webdav;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GzipFile {
	private File source;
	private File target;

	public void gzip() throws Exception{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		CompressorOutputStream gos = null;

		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);

			gos = new CompressorStreamFactory()
					.createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
			IOUtils.copy(fis, gos);
		} finally {
			IOUtils.closeQuietly(gos);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(fis);
		}
	}

	public static GzipFile valueOf(File source, File target) {
		GzipFile result = new GzipFile();
		result.source = source;
		result.target = target;
		return result;
	}
}
