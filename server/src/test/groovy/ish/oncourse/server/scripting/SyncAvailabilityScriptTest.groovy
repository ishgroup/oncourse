/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.scripting

import ish.CayenneIshTestCase
import ish.common.types.TriggerType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Script
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals

class SyncAvailabilityScriptTest extends CayenneIshTestCase {

	@BeforeEach
    void setup() throws Exception {
		wipeTables()
        InputStream st = GroovyScriptService.class.getClassLoader().getResourceAsStream("ish/oncourse/server/scripting/syncAvailabilityScriptTest.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[endDate]", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addYears(new Date(), 1)))

        executeDatabaseOperation(rDataSet)
    }

	@Test
    void testSyncAvailabilityEnrolment() throws Exception {
		GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()

        Script script = context.newObject(Script.class)

        script.setScript(IOUtils.toString(
				SyncAvailabilityScriptTest.class.getClassLoader().getResourceAsStream(
						"scripts/ish.script.syncAvailabilityEnrolment.groovy")))
        script.setName("Sync availability enrolment")
        script.setEnabled(true)
        script.setTriggerType(TriggerType.ON_DEMAND)
        script.setEntityClass("Enrolment")

        context.commitChanges()

        Enrolment enrolment = SelectById.query(Enrolment.class, 201).selectOne(context)

        CourseClass class1 = SelectById.query(CourseClass.class, 206).selectOne(context)
        CourseClass class2 = SelectById.query(CourseClass.class, 207).selectOne(context)
        CourseClass class3 = SelectById.query(CourseClass.class, 208).selectOne(context)
        CourseClass class4 = SelectById.query(CourseClass.class, 209).selectOne(context)
        CourseClass class5 = SelectById.query(CourseClass.class, 210).selectOne(context)

        assertEquals(new Integer(10), class1.getMaximumPlaces())
        assertEquals(new Integer(10), class2.getMaximumPlaces())
        assertEquals(new Integer(10), class3.getMaximumPlaces())
        assertEquals(new Integer(10), class4.getMaximumPlaces())
        assertEquals(new Integer(10), class5.getMaximumPlaces())

        scriptService.runAndWait(script, ScriptParameters.from("record", enrolment))

        assertEquals(new Integer(10), class1.getMaximumPlaces())
        assertEquals(new Integer(9), class2.getMaximumPlaces())
        assertEquals(new Integer(10), class3.getMaximumPlaces())
        assertEquals(new Integer(10), class4.getMaximumPlaces())
        assertEquals(new Integer(10), class5.getMaximumPlaces())
    }

	@Test
    void testSyncAvailabilityCancellation() throws Exception {
		GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()

        Script script = context.newObject(Script.class)

        script.setScript(IOUtils.toString(
				SyncAvailabilityScriptTest.class.getClassLoader().getResourceAsStream(
						"scripts/ish.script.syncAvailabilityCancellation.groovy")))
        script.setName("Sync availability cancellation")
        script.setEnabled(true)
        script.setTriggerType(TriggerType.ON_DEMAND)
        script.setEntityClass("Enrolment")

        context.commitChanges()

        Enrolment enrolment = SelectById.query(Enrolment.class, 201).selectOne(context)

        CourseClass class1 = SelectById.query(CourseClass.class, 206).selectOne(context)
        CourseClass class2 = SelectById.query(CourseClass.class, 207).selectOne(context)
        CourseClass class3 = SelectById.query(CourseClass.class, 208).selectOne(context)
        CourseClass class4 = SelectById.query(CourseClass.class, 209).selectOne(context)
        CourseClass class5 = SelectById.query(CourseClass.class, 210).selectOne(context)

        assertEquals(new Integer(10), class1.getMaximumPlaces())
        assertEquals(new Integer(10), class2.getMaximumPlaces())
        assertEquals(new Integer(10), class3.getMaximumPlaces())
        assertEquals(new Integer(10), class4.getMaximumPlaces())
        assertEquals(new Integer(10), class5.getMaximumPlaces())

        scriptService.runAndWait(script, ScriptParameters.from("record", enrolment))

        assertEquals(new Integer(10), class1.getMaximumPlaces())
        assertEquals(new Integer(11), class2.getMaximumPlaces())
        assertEquals(new Integer(10), class3.getMaximumPlaces())
        assertEquals(new Integer(10), class4.getMaximumPlaces())
        assertEquals(new Integer(10), class5.getMaximumPlaces())
    }
}
