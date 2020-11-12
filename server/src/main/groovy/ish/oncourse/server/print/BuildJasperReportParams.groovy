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

package ish.oncourse.server.print

import net.sf.jasperreports.engine.JRParameter

class BuildJasperReportParams {
    static final String REPORT_NAME = "ReportName"
    static final String REPORT_LOCALE = "REPORT_LOCALE"
    private Map<String, Object> params

    private BuildJasperReportParams(){}

    static BuildJasperReportParams valueOf(String reportName, Locale locale = Locale.default){
        BuildJasperReportParams getParams = new BuildJasperReportParams()
        getParams.initParams(reportName, locale)

        getParams
    }

    void initParams(String reportName, Locale locale) {
        params = new HashMap<>()
        params.put(REPORT_NAME, reportName)
        params.put(JRParameter.REPORT_FORMAT_FACTORY, new AngelFormatFactory())
        params.put(REPORT_LOCALE, locale)
    }

    Map<String, Object> get(){
        params
    }
}
