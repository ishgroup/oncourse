/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.entity.services

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.cayenne.Certificate
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

@CompileStatic
@DatabaseSetup(readOnly = true, value = "ish/oncourse/entity/services/certificateServiceTestDataSet.xml")
class CertificateServiceTest extends TestWithDatabase {

    private CertificateService certificateService = injector.getInstance(CertificateService.class)

    @Test
    void testGetCommencedOn() throws Exception {
        Certificate certificate1 = SelectById.query(Certificate.class, 1).selectOne(cayenneContext)
        Certificate certificate2 = SelectById.query(Certificate.class, 2).selectOne(cayenneContext)

        Assertions.assertEquals(LocalDate.of(2013, Month.FEBRUARY, 1), certificateService.getCommencedOn(certificate1))
        Assertions.assertEquals(LocalDate.of(2012, Month.JANUARY, 1), certificateService.getCommencedOn(certificate2))
    }

    @Test
    void testCompletedOn() throws Exception {
        Certificate certificate1 = SelectById.query(Certificate.class, 1).selectOne(cayenneContext)
        Certificate certificate2 = SelectById.query(Certificate.class, 2).selectOne(cayenneContext)

        Assertions.assertEquals(LocalDate.of(2013, Month.JANUARY, 2), certificateService.getCompletedOn(certificate1))
        Assertions.assertEquals(LocalDate.of(2015, Month.JANUARY, 1), certificateService.getCompletedOn(certificate2))
    }
}
