package ish.oncourse.webservices.jobs;

import ish.oncourse.model.Instruction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UpdateAmountOwingJobTest extends ServiceTest {
    private ICayenneService cayenneService;
    private UpdateAmountOwingJob job;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ReplicationTestModule.class);

        new LoadDataSet().dataSetFile("ish/oncourse/webservices/jobs/UpdateAmountOwingJobTest.xml")
                .load(testContext.getDS());

        this.cayenneService = getService(ICayenneService.class);
        this.job = new UpdateAmountOwingJob(cayenneService);
    }


    @Test
    public void test() throws JobExecutionException {
        this.job.execute();

        ObjectContext objectContext = this.cayenneService.newNonReplicatingContext();
        List<Instruction> instructions = ObjectSelect.query(Instruction.class).select(objectContext);

        assertEquals("SQL for invoice loading was changed", 1, instructions.size());
        assertEquals("queue:instructWithRelationships:Invoice:2000", instructions.get(0).getMessage());
    }
}
