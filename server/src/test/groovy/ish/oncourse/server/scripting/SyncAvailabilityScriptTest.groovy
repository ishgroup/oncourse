/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.scripting

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.common.types.TriggerType
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Script
import org.apache.cayenne.query.SelectById
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/scripting/syncAvailabilityScriptTest.xml")
class SyncAvailabilityScriptTest extends CayenneIshTestCase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        rDataSet.addReplacementObject("[endDate]", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addYears(new Date(), 1)))
    }
    
    @Test
    void testSyncAvailabilityEnrolment() throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        Script script = cayenneContext.newObject(Script.class)

        script.setScript(IOUtils.toString(
                SyncAvailabilityScriptTest.class.getClassLoader().getResourceAsStream(
                        "scripts/ish.script.syncAvailabilityEnrolment.groovy")))
        script.setName("Sync availability enrolment")
        script.setEnabled(true)
        script.setTriggerType(TriggerType.ON_DEMAND)
        script.setEntityClass("Enrolment")

        cayenneContext.commitChanges()

        Enrolment enrolment = SelectById.query(Enrolment.class, 201).selectOne(cayenneContext)

        CourseClass class1 = SelectById.query(CourseClass.class, 206).selectOne(cayenneContext)
        CourseClass class2 = SelectById.query(CourseClass.class, 207).selectOne(cayenneContext)
        CourseClass class3 = SelectById.query(CourseClass.class, 208).selectOne(cayenneContext)
        CourseClass class4 = SelectById.query(CourseClass.class, 209).selectOne(cayenneContext)
        CourseClass class5 = SelectById.query(CourseClass.class, 210).selectOne(cayenneContext)

        Assertions.assertEquals(new Integer(10), class1.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class2.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class3.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class4.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class5.getMaximumPlaces())

        scriptService.runAndWait(script, ScriptParameters.from("record", enrolment))

        Assertions.assertEquals(new Integer(10), class1.getMaximumPlaces())
        Assertions.assertEquals(new Integer(9), class2.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class3.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class4.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class5.getMaximumPlaces())
    }

    
    @Test
    void testSyncAvailabilityCancellation() throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService.class)
        Script script = cayenneContext.newObject(Script.class)

        script.setScript(IOUtils.toString(
                SyncAvailabilityScriptTest.class.getClassLoader().getResourceAsStream(
                        "scripts/ish.script.syncAvailabilityCancellation.groovy")))
        script.setName("Sync availability cancellation")
        script.setEnabled(true)
        script.setTriggerType(TriggerType.ON_DEMAND)
        script.setEntityClass("Enrolment")

        cayenneContext.commitChanges()

        Enrolment enrolment = SelectById.query(Enrolment.class, 201).selectOne(cayenneContext)

        CourseClass class1 = SelectById.query(CourseClass.class, 206).selectOne(cayenneContext)
        CourseClass class2 = SelectById.query(CourseClass.class, 207).selectOne(cayenneContext)
        CourseClass class3 = SelectById.query(CourseClass.class, 208).selectOne(cayenneContext)
        CourseClass class4 = SelectById.query(CourseClass.class, 209).selectOne(cayenneContext)
        CourseClass class5 = SelectById.query(CourseClass.class, 210).selectOne(cayenneContext)

        Assertions.assertEquals(new Integer(10), class1.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class2.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class3.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class4.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class5.getMaximumPlaces())

        scriptService.runAndWait(script, ScriptParameters.from("record", enrolment))

        Assertions.assertEquals(new Integer(10), class1.getMaximumPlaces())
        Assertions.assertEquals(new Integer(11), class2.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class3.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class4.getMaximumPlaces())
        Assertions.assertEquals(new Integer(10), class5.getMaximumPlaces())
    }
}
