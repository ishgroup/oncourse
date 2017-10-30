package ish.oncourse.webservices.jobs;

import ish.oncourse.model.Instruction;
import ish.oncourse.services.ServiceModule;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UpdateAmountOwingJobTest extends ServiceTest {
    private ICayenneService cayenneService;
    private UpdateAmountOwingJob job;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = PaymentInExpireJobTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/webservices/jobs/UpdateAmountOwingJobTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource refDataSource = testContext.getDS();
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        this.cayenneService = getService(ICayenneService.class);
        this.job = new UpdateAmountOwingJob(cayenneService);
    }


    @Test
    public void test() {
        this.job.execute();

        ObjectContext objectContext = this.cayenneService.newNonReplicatingContext();
        List<Instruction> instructions = ObjectSelect.query(Instruction.class).select(objectContext);
        assertEquals(1, instructions.size());
    }
}
