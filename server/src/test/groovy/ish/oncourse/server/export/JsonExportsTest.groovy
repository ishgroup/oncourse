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
import ish.export.ExportParameter
import ish.export.ExportResult
import ish.oncourse.common.ResourceProperty
import ish.oncourse.server.upgrades.DataPopulationUtils
import ish.oncourse.types.OutputType
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/export/jsonExport.xml")
class JsonExportsTest extends TestWithDatabase {
    private static final String LINE_SEPARATOR = StringUtils.LF

    @Test
    void testJsonReportGenerating() {
        Map<String, Object> props = new HashMap<>()
        props.put(ResourceProperty.KEY_CODE.displayName, "ish.onCourse.contact.json")
        props.put(ResourceProperty.NAME.displayName, "Json export")
        props.put(ResourceProperty.ENTITY_CLASS.displayName, "Contact")
        props.put(ResourceProperty.OUTPUT_TYPE.displayName, OutputType.JSON)
        props.put(ResourceProperty.BODY.displayName, "ish.onCourse.contact.json.groovy")
        DataPopulationUtils.updateExport(cayenneContext, props)


        ExportParameter param = new ExportParameter()

        param.setEntity("Contact")
        param.setXslKeyCode("ish.onCourse.contact.json")
        param.setExpression(ExpressionFactory.expTrue())

        ExportService export = injector.getInstance(ExportService.class)

        ExportResult result = export.export(param)
        String resultExportString = StringUtils.replace(new String(result.getResult()), StringUtils.CR, StringUtils.EMPTY)

        byte[] sampleExport = IOUtils.toByteArray(AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/output/jsonExport.json"))

        String sampleExportString = StringUtils.replace(new String(sampleExport), StringUtils.CR, StringUtils.EMPTY)


        String[] sampleExportSplit = sampleExportString.split(LINE_SEPARATOR)
        String[] resultExportSplit = resultExportString.split(LINE_SEPARATOR)

        Assertions.assertEquals(sampleExportSplit.length, resultExportSplit.length)

        for (int i = 0; i < sampleExportSplit.length; i++) {
            if(!(sampleExportSplit[i].contains("createdOn") || sampleExportSplit[i].contains("modifiedOn")))
                Assertions.assertEquals(sampleExportSplit[i].trim(), resultExportSplit[i].trim())
        }
    }
}
