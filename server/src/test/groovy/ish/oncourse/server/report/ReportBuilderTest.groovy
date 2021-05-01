package ish.oncourse.server.report

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Report
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test


@CompileStatic
class ReportBuilderTest {

    
    @Test
    void buildTest() throws Exception {
        String reportFileName = "resources/schema/referenceData/reports/ReportBuilderTest.jrxml"

        String reportXml = IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFileName))
        Report report = ReportBuilder.valueOf(reportXml).build()

        Assertions.assertEquals("Test Report", report.getName())
        Assertions.assertEquals("Test Description", report.getDescription())
        Assertions.assertEquals("Test Entity", report.getEntity())
        Assertions.assertEquals("test.key.code", report.getKeyCode())
        Assertions.assertEquals(true, report.getIsVisible())
        Assertions.assertEquals("test.test;test", report.getSortOn())
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- Created with Jaspersoft Studio version 6.1.0.final using JasperReports Library version 6.1.0  -->\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"TransactionDetailReport\" language=\"groovy\" pageWidth=\"595\" pageHeight=\"842\" columnWidth=\"539\" leftMargin=\"28\" rightMargin=\"28\" topMargin=\"28\" bottomMargin=\"28\" whenResourceMissingType=\"Empty\" uuid=\"bb9b77bc-db0e-4603-9203-7dcd1c50a4fd\">\n" +
                "    <property name=\"name\" value=\"Test Report\"/>\n" +
                "    <property name=\"entity\" value=\"Test Entity\"/>\n" +
                "    <property name=\"isSubreport\" value=\"false\"/>\n" +
                "    <property name=\"isVisible\" value=\"true\"/>\n" +
                "    <property name=\"versionNumber\" value=\"1\"/>\n" +
                "    <property name=\"keyCode\" value=\"test.key.code\"/>\n" +
                "    <property name=\"ish.oncourse.title\" value=\"Transactions\"/>\n" +
                "    <property name=\"ish.oncourse.filePrefix\" value=\"transactionSummary\"/>\n" +
                "    <property name=\"ish.oncourse.description\" value=\"Test Description\"/>\n" +
                "    <property name=\"ish.oncourse.pdfFileName\" value=\"TransactionSummary.pdf\"/>\n" +
                "    <property name=\"ish.oncourse.reports.isObjectOnSeparatePage\" value=\"false\"/>\n" +
                "    <property name=\"sortOn\" value=\"test.test;test\"/>\n" +
                "    <import value=\"net.sf.jasperreports.engine.*\"/>\n" +
                "    <import value=\"java.util.*\"/>\n" +
                "    <import value=\"ish.common.types.*\"/>\n" +
                "    <import value=\"ish.math.Money\"/>\n" +
                "    <import value=\"net.sf.jasperreports.engine.data.*\"/>\n" +
                "    <import value=\"javax.swing.text.DateFormatter\"/>\n" +
                "    <style name=\"default\" isDefault=\"true\" forecolor=\"#000000\" vTextAlign=\"Bottom\" markup=\"none\" fontName=\"Open Sans\" fontSize=\"9\" isBold=\"false\" isItalic=\"false\" isUnderline=\"false\" isStrikeThrough=\"false\" pdfFontName=\"OpenSans\" pdfEncoding=\"Cp1252\" isPdfEmbedded=\"true\">\n" +
                "        <paragraph spacingAfter=\"9\"/>\n" +
                "    </style>\n" +
                "    <style name=\"header 1\" style=\"default\" forecolor=\"#4F0018\" vTextAlign=\"Middle\" fontName=\"Open Sans Light\" fontSize=\"18\" pdfFontName=\"Open Sans Light\"/>\n" +
                "</jasperReport>\n", report.getReport())
    }
}