/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish

import groovy.transform.CompileStatic
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Helper test rule class populating database with data from dbunit XML.
 */
@CompileStatic
class TestData implements TestRule {

    private String dbUnitXml
    private String resourcePath

    TestData(String dbUnitXml) {
        this.dbUnitXml = dbUnitXml

        String callerClass = Thread.currentThread().getStackTrace()[2].getClassName()
        this.resourcePath = callerClass.substring(0, callerClass.lastIndexOf('.')).replace('.', '/')
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                InputStream st = TestData.class.getClassLoader().getResourceAsStream(dbUnitXml)

                if (st == null) {
                    st = TestData.class.getClassLoader().getResourceAsStream(resourcePath + "/" + dbUnitXml)
                }

                FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
                builder.setColumnSensing(true)
                FlatXmlDataSet dataSet = builder.build(st)

                CayenneIshTestCase.executeDatabaseOperation(dataSet)

                base.evaluate()
            }
        }
    }
}
