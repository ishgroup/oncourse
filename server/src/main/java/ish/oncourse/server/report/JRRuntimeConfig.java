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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.management.ManagementFactory;

/**
 * The class
 * 1. checks what type of jvm (jdk or jre) installed on the computer
 * 2. configures default compiler for jasperreport when installed jdk is version 1.8.*
 * 3. shows warning message if istalled jvm is jre.
 */
public class JRRuntimeConfig {

	private static final String JVM_1_8 = "1.8";
	private static final String JASPER_REPORTS_COMPILER_CLASS_SYSTEM_PROPERTY = "jasper.reports.compiler.class";
    private static final String JR_GROOVY_COMPILER_CLASS_NAME = "net.sf.jasperreports.compilers.JRGroovyCompiler";


	private boolean isJDK = false;
	private boolean is1_8 = false;

	public JRRuntimeConfig() {
		init();
	}

	private void init() {
		var version = ManagementFactory.getRuntimeMXBean().getSpecVersion();

		try {
			var compiler = ToolProvider.getSystemJavaCompiler();
			isJDK = compiler != null;
		} catch (Throwable e) {
			isJDK = false;
		}

		if (version.startsWith(JVM_1_8)) {
			is1_8 = true;
		}
	}


	public void config() {
        System.setProperty(JASPER_REPORTS_COMPILER_CLASS_SYSTEM_PROPERTY, JR_GROOVY_COMPILER_CLASS_NAME);
	}

	public boolean isJDK() {
		return isJDK;
	}
}
