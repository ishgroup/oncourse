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
package ish.oncourse.server.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


/**
 */
public class ReportService {

	public static byte[] compileReport(String xmlReport) throws UnsupportedEncodingException, JRException {
		var outputStream = new ByteArrayOutputStream();
		JasperCompileManager.compileReportToStream(new ByteArrayInputStream(xmlReport.getBytes(StandardCharsets.UTF_8)), outputStream);
		return outputStream.toByteArray();
	}

}
