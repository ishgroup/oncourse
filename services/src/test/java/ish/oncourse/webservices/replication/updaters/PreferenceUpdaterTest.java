package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v21.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v21.stubs.replication.PreferenceStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static ish.oncourse.webservices.util.SupportedVersions.V21;
import static ish.oncourse.webservices.v21.stubs.replication.Status.SUCCESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PreferenceUpdaterTest extends ServiceTest {

    private ITransactionGroupProcessor transactionGroupProcessor;
    private ICayenneService cayenneService;

    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/updaters/preferenceUpdaterTestDataSet.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DatabaseConnection dbConnection = new DatabaseConnection(testContext.getDS().getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

        transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
        cayenneService = getService(ICayenneService.class);
    }

    @Test
    public void testCreate() {
        PreferenceStub stub = new PreferenceStub();
        stub.setAngelId(1L);
        stub.setWillowId(null);
        stub.setName("Test pref");
        stub.setValueString("value?");
        stub.setModified(new Date());
        stub.setCreated(new Date());
        stub.setEntityIdentifier(Preference.class.getSimpleName());

        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stub);

        List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

        ObjectContext context = cayenneService.newContext();

        Preference preference = ObjectSelect.query(Preference.class)
                .where(Preference.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(preference);
    }

}
