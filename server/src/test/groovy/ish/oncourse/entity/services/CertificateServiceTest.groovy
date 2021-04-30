/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.entity.services

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Certificate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static org.junit.Assert.assertEquals

class CertificateServiceTest extends CayenneIshTestCase {
	
	private ICayenneService cayenneService
    private CertificateService certificateService

    @BeforeEach
    void setup() throws Exception {
		wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)
        this.certificateService = injector.getInstance(CertificateService.class)

        InputStream st = CertificateServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/entity/services/certificateServiceTestDataSet.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)
        executeDatabaseOperation(dataSet)
    }
	
	@Test
    void testGetCommencedOn() throws Exception {
		ObjectContext context = cayenneService.getNewContext()

        Certificate certificate1 = SelectById.query(Certificate.class, 1).selectOne(context)
        Certificate certificate2 = SelectById.query(Certificate.class, 2).selectOne(context)

        assertEquals(LocalDate.of(2013, Month.FEBRUARY, 1), certificateService.getCommencedOn(certificate1))
        assertEquals(LocalDate.of(2012, Month.JANUARY, 1), certificateService.getCommencedOn(certificate2))
    }

	@Test
    void testCompletedOn() throws Exception {
		ObjectContext context = cayenneService.getNewContext()

        Certificate certificate1 = SelectById.query(Certificate.class, 1).selectOne(context)
        Certificate certificate2 = SelectById.query(Certificate.class, 2).selectOne(context)

        assertEquals(LocalDate.of(2013, Month.JANUARY, 2), certificateService.getCompletedOn(certificate1))
        assertEquals(LocalDate.of(2015, Month.JANUARY, 1), certificateService.getCompletedOn(certificate2))
    }
}
