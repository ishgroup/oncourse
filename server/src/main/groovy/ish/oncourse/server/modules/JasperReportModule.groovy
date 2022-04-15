/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.modules

import com.google.inject.Binder
import com.google.inject.Module
import net.sf.jasperreports.engine.DefaultJasperReportsContext

class JasperReportModule implements Module {

    private static final String JASPER_REPORTS_COMPILER_CLASS_SYSTEM_PROPERTY = "jasper.reports.compiler.class"
    private static final String JR_GROOVY_COMPILER_CLASS_NAME = "net.sf.jasperreports.compilers.JRGroovyCompiler"

    @Override
    void configure(Binder binder) {
        // ensure that Jasper writes temp files to a directory with write permissions
        System.setProperty("jasper.reports.compile.temp", System.getProperty("java.io.tmpdir"))

        // configures default compiler for jasperreport
        System.setProperty(JASPER_REPORTS_COMPILER_CLASS_SYSTEM_PROPERTY, JR_GROOVY_COMPILER_CLASS_NAME)

        // set the location of default Ish jasperreports properties file
        System.setProperty(DefaultJasperReportsContext.PROPERTIES_FILE, "jasperreports.properties")
    }
}
