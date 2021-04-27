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

import ish.oncourse.server.cayenne.Report;
import org.apache.cayenne.exp.Property;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ReportBuilder {

    private static final Logger logger = LogManager.getLogger();

    private String reportXml;


    public static ReportBuilder valueOf(String reportXml) {
        var reportBuilder = new ReportBuilder();
        reportBuilder.reportXml = reportXml;

        return reportBuilder;
    }

    public Report build() throws ParserConfigurationException, SAXException, IOException {
        var doc = parseDocument();
        var inputName = getReportName(doc);
        var inputEntity = getReportEntity(doc);
        var inputIsVisible = getReportIsVisible(doc); // optional, default=true
        var inputKeyCode = getReportKeyCode(doc); // mandatory identifier
        var inputSortOn = getReportSortOn(doc); // non-mandatory
        var inputDescription = getReportDescription(doc); // non-mandatory

        var report = new Report();
        report.setReport(reportXml);
        report.setEntity(inputEntity);
        report.setIsVisible(inputIsVisible);
        report.setKeyCode(inputKeyCode);
        report.setName(inputName);
        report.setDescription(inputDescription);
        if (inputSortOn != null) {
            report.setSortOn(inputSortOn);
        } else {
            report.setSortOn(null);
        }

        return report;
    }

    public String readProperty(Property  property) {
        try {

            var s = getPropertyValue(parseDocument(), property.getName(), true);
            if (StringUtils.trimToNull(s) == null) {
                return null;
            } else {
                return s;
            }

        } catch (Exception e) {
            logger.catching(e);
            logger.error("Unable to read property {}, report body: {}",property.getName(), reportXml);
            return null;
        }
    }


    private Document parseDocument() throws IOException, SAXException, ParserConfigurationException {
        Document doc;
        try ( InputStream is = new ByteArrayInputStream(reportXml.getBytes(StandardCharsets.UTF_8)) ) {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
        } catch (Exception e) {
            logger.catching(e);
            logger.error("Unable to parse report: {}", reportXml);
            throw e;
        }

        return doc;
    }

    /**
     * returns value for given 'name' from xml like: <property name="isSubreport" value="false"/>
     * @param doc
     * @param propertyName
     * @return the string
     */
    private String getPropertyValue(Document doc, String propertyName, boolean allowEmptyValue) throws XPathException {
        var xPathfactory = XPathFactory.newInstance();
        var xpath = xPathfactory.newXPath();

        var result = xpath.evaluate("/jasperReport/property[@name='"+propertyName+"']/@value", doc);

        if (result.isEmpty() && !allowEmptyValue) {
            throw new RuntimeException("No '"+propertyName+"' property defined for report");
        }
        return result;
    }

    /**
     *
     * @param report
     * @return report name
     * @throws XPathExpressionException
     */
    private String getReportName(Document report) {
        try {
            return getPropertyValue(report, Report.NAME_PROPERTY, false);
        } catch (XPathException e) {
            throw new RuntimeException("'name' property cannot be parsed");
        }
    }

    /**
     *
     * @param report
     * @return report entity
     * @throws XPathExpressionException
     */
    private String getReportEntity(Document report) {
        try {
            return getPropertyValue(report, Report.ENTITY_PROPERTY, false);
        } catch (XPathException e) {
            throw new RuntimeException("'entity' property cannot be parsed");
        }
    }

    /**
     *
     * @param report
     * @return report keycode
     * @throws XPathExpressionException
     */
    private String getReportKeyCode(Document report) {
        try {
            return getPropertyValue(report, Report.KEY_CODE_PROPERTY, false);
        } catch (XPathException e) {
            throw new RuntimeException("'keycode' property cannot be parsed");
        }
    }

    /**
     *
     * @param report
     * @return report description
     * @throws XPathExpressionException
     */
    private String getReportDescription(Document report) {
        try {
            return getPropertyValue(report, "ish.oncourse."+Report.DESCRIPTION_PROPERTY, true);
        } catch (XPathException e) {
            throw new RuntimeException("'description' property cannot be parsed");
        }
    }

    /**
     *
     * @param report
     * @return report sort on property
     * @throws XPathExpressionException
     */
    private String getReportSortOn(Document report) {
        try {
            return getPropertyValue(report, Report.SORT_ON_PROPERTY, true);
        } catch (XPathException e) {
            throw new RuntimeException("'sortOn' property cannot be parsed");
        }
    }

    /**
     *
     * @param report
     * @return report isVisible property
     * @throws XPathExpressionException
     */
    private boolean getReportIsVisible(Document report) {
        String isVisibleString;
        try {
            isVisibleString = getPropertyValue(report, Report.IS_VISIBLE_PROPERTY, false);
        } catch (XPathException e) {
            throw new RuntimeException("'isVisible' property cannot be parsed");
        }

        try {
            return Boolean.parseBoolean(isVisibleString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("'isVisible' property is not valid :" +isVisibleString, e);
        }
    }
}
