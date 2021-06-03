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
package ish.oncourse.server.export.avetmiss

import groovy.transform.CompileStatic
import ish.common.types.OutcomeStatus
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.export.avetmiss8.Avetmiss010Line
import ish.oncourse.server.export.avetmiss8.Avetmiss020Line
import ish.oncourse.server.export.avetmiss8.Avetmiss030Line
import ish.oncourse.server.export.avetmiss8.Avetmiss060Line
import ish.oncourse.server.export.avetmiss8.Avetmiss080Line
import ish.oncourse.server.export.avetmiss8.Avetmiss085Line
import ish.oncourse.server.export.avetmiss8.Avetmiss090Line
import ish.oncourse.server.export.avetmiss8.Avetmiss100Line
import ish.oncourse.server.export.avetmiss8.Avetmiss120Line
import ish.oncourse.server.export.avetmiss8.Avetmiss130Line
import ish.oncourse.server.export.avetmiss8.AvetmissLine
import ish.util.StringUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@CompileStatic
class AvetmissExportResult {

    private static final Logger logger = LogManager.getLogger()
    
    ConcurrentMap<String, Avetmiss010Line> avetmiss010Lines = new ConcurrentHashMap<String, Avetmiss010Line>()
    ConcurrentMap<String, Avetmiss020Line> avetmiss020Lines = new ConcurrentHashMap<String, Avetmiss020Line>()
    ConcurrentMap<String, Avetmiss030Line> avetmiss030Lines = new ConcurrentHashMap<String, Avetmiss030Line>()
    ConcurrentMap<String, Avetmiss060Line> avetmiss060Lines = new ConcurrentHashMap<String, Avetmiss060Line>()
    ConcurrentMap<String, Avetmiss080Line> avetmiss080Lines = new ConcurrentHashMap<String, Avetmiss080Line>()
    ConcurrentMap<String, Avetmiss085Line> avetmiss085Lines = new ConcurrentHashMap<String, Avetmiss085Line>()
    ConcurrentMap<String, Avetmiss090Line> avetmiss090Lines = new ConcurrentHashMap<String, Avetmiss090Line>()
    ConcurrentMap<String, Avetmiss100Line> avetmiss100Lines = new ConcurrentHashMap<String, Avetmiss100Line>()
    ConcurrentMap<String, Avetmiss120Line> avetmiss120Lines = new ConcurrentHashMap<String, Avetmiss120Line>()
    ConcurrentMap<String, Avetmiss130Line> avetmiss130Lines = new ConcurrentHashMap<String, Avetmiss130Line>()


    
    private AvetmissExportResultType success
    private Map<String, ByteArrayOutputStream> exportFiles = [:]
    
    LocalDate exportEndDate
    OutcomeStatus defaultOutcome
    Site adminSite
    LocalDate overriddenEndDate
    Boolean ignoreAssessments
    
    boolean has_SKILLSET_LOCAL_TYPE = false

    void setFile(String fileName, ByteArrayOutputStream contents) {
        exportFiles.put(fileName, contents)
    }

    Map<String, ByteArrayOutputStream> getFiles() {
        if (exportFiles.size() == 0) {
            export_all()
        }
        return Collections.unmodifiableMap(exportFiles)
    }

    private void export_all() {
        try {
            fileLines.each { file, lines ->
                setFile(file, export(lines))
            }
            this.success = AvetmissExportResultType.SUCCESS
            logger.debug("AVETMISS export successfully finished.")
        } catch (Exception e) {
            this.success = AvetmissExportResultType.EXPORTED_WITH_ERRORS
            logger.warn("AVETMISS export finished with errors.", e)
        }
    }

    enum AvetmissExportResultType {
        SUCCESS,
        EXPORTED_WITH_ERRORS,
    }

    Map<String, ConcurrentMap<String, AvetmissLine>> getFileLines() {
        [
                "NAT00010.txt" : avetmiss010Lines,
                "NAT00020.txt" : avetmiss020Lines,
                (has_SKILLSET_LOCAL_TYPE ? "NAT00030A.txt" : "NAT00030.txt") : avetmiss030Lines,
                "NAT00060.txt" : avetmiss060Lines,
                "NAT00080.txt" : avetmiss080Lines,
                "NAT00085.txt" : avetmiss085Lines,
                "NAT00090.txt" : avetmiss090Lines,
                "NAT00100.txt" : avetmiss100Lines,
                "NAT00120.txt" : avetmiss120Lines,
                "NAT00130.txt" : avetmiss130Lines,

        ] as Map<String, ConcurrentMap<String, AvetmissLine>>
    }

    /**
     * Export one AVETMISS file
     */
    ByteArrayOutputStream export(ConcurrentMap<String, AvetmissLine> lines) throws IOException {
        def out = new ByteArrayOutputStream()
        //   sort() here to ensure the output lines are in a predictable sort order.
        lines.sort().values().each { outputLine ->
            out.write(StringUtil.normalizeString(outputLine.export()).getBytes())
            out.write("\r\n".getBytes())
        }
        out.flush()
        out.close()
        return out
    }
}
