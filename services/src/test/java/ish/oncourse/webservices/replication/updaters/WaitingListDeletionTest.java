package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v21.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v21.stubs.replication.ReplicatedRecord;
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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class WaitingListDeletionTest extends ServiceTest {

    private ITransactionGroupProcessor transactionGroupProcessor;
    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/updaters/WaitingListDeletionTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DatabaseConnection dbConnection = new DatabaseConnection(testContext.getDS().getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

        transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
        cayenneService = getService(ICayenneService.class);
    }

    @Test
    public void processDeleteWaitingListTransactionTest() {
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);

        DeletedStub deletedWaitingList = new DeletedStub();
        deletedWaitingList.setEntityIdentifier("WaitingList");
        deletedWaitingList.setWillowId(1l);
        deletedWaitingList.setAngelId(1l);
        deletedWaitingList.setCreated(new Date());
        deletedWaitingList.setModified(new Date());
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedWaitingList);

        DeletedStub deletedTaggable = new DeletedStub();
        deletedTaggable.setEntityIdentifier("Taggable");
        deletedTaggable.setWillowId(1l);
        deletedTaggable.setAngelId(1l);
        deletedTaggable.setCreated(new Date());
        deletedTaggable.setModified(new Date());
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedTaggable);

        List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

        ObjectContext context = cayenneService.newContext();

        Taggable taggable = ObjectSelect.query(Taggable.class)
                .where(Taggable.ENTITY_WILLOW_ID.eq(1L))
                .selectOne(context);
        assertNull(taggable);

        WaitingList waitingLis = ObjectSelect.query(WaitingList.class)
                .where(WaitingList.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNull(waitingLis);

        TaggableTag taggableTag = ObjectSelect.query(TaggableTag.class)
                .where(TaggableTag.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNull(taggableTag);
    }
}

