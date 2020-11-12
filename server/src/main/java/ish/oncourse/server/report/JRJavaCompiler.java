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

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRAbstractMultiClassCompiler;
import org.apache.commons.io.IOUtils;

import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class JRJavaCompiler extends JRAbstractMultiClassCompiler {

	public JRJavaCompiler() {
		this(DefaultJasperReportsContext.getInstance());
	}

	public JRJavaCompiler(JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext);
	}

	@Override
	public String compileClasses(File[] sourceFiles, String classpath) throws JRException {

		if (sourceFiles.length == 0) {
			return null;
		}

		try {
			// all source files should be in the same directory to which output should be put, so it is safe to assume that
			// the output directory is the same as parent directory of any of the source files
			var outputDir = sourceFiles[0].getParentFile();

			var compiler = ToolProvider.getSystemJavaCompiler();
			var diagnostics = new DiagnosticCollector<JavaFileObject>();
			var manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);

			manager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(outputDir));

			var units = new ArrayList<JavaFileObject>();
			for (var sourceFile : sourceFiles) {
				units.add(new JRFileObject(sourceFile, JavaFileObject.Kind.SOURCE));
			}
			var task = compiler.getTask(null, manager, diagnostics, null, null, units);
			task.call();
		} catch (Exception e) {
			throw new JRException(e);
		}

		return null;
	}

	private static class JRFileObject extends SimpleJavaFileObject
	{
		private File file;

		private JRFileObject(File file, Kind kind) {
			super(file.toURI(), kind);
			this.file = file;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return new FileInputStream(file);
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return new FileOutputStream(file);
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			var fs = openInputStream();
			return IOUtils.toString(fs, StandardCharsets.UTF_8);
		}
	}
}
