/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.export

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.AutomationStatus
import ish.export.ExportParameter
import ish.export.ExportResult
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import ish.scripting.ScriptResult
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ish.oncourse.server.integration.PluginService

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/export/jsonExport.xml")
class JsonExportsTest extends TestWithDatabase {
    private static final String LINE_SEPARATOR = StringUtils.LF

    @Test
    void testJsonReportGenerating() {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        def scriptName = PluginService.getPluggableResources(ResourceType.EXPORT.getResourcePath(), "ish.onCourse.contact.json.groovy")
        String scriptText = ResourcesUtil.readFile((scriptName as LinkedHashSet).first() as String)

        Script script = cayenneContext.newObject(Script.class)
        script.setAutomationStatus(AutomationStatus.ENABLED)
        script.setScript(scriptText)

        def records = ObjectSelect.query(Contact).select(cayenneContext)


        ScriptResult result = scriptService
                .runScript(script,
                        ScriptParameters.empty().add("records", records),
                        cayenneService.getNewContext())

        byte[] sampleExport = IOUtils.toByteArray(AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/output/jsonExport.json"))

        String sampleExportString = StringUtils.replace(new String(sampleExport), StringUtils.CR, StringUtils.EMPTY)
        //String resultExportString = StringUtils.replace(new String(result.getResult()), StringUtils.CR, StringUtils.EMPTY)

        String[] sampleExportSplit = sampleExportString.split(LINE_SEPARATOR)
        //String[] resultExportSplit = resultExportString.split(LINE_SEPARATOR)

/*
        Assertions.assertEquals(sampleExportSplit.length, resultExportSplit.length)

        for (int i = 0; i < sampleExportSplit.length; i++) {
            Assertions.assertEquals(sampleExportSplit[i].trim(), resultExportSplit[i].trim())
        }*/
    }
}
