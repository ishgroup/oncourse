package ish.oncourse.webservices.replication.updaters.v16;

import ish.oncourse.model.Course;
import ish.oncourse.model.Field;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v16.stubs.replication.*;
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

import static ish.oncourse.webservices.util.SupportedVersions.V16;
import static ish.oncourse.webservices.v16.stubs.replication.Status.SUCCESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class FieldConfigurationTransactionTest extends ServiceTest {
    
    private ITransactionGroupProcessor transactionGroupProcessor;
    private ICayenneService cayenneService;
    
    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/updaters/fieldConfigurationTransaction.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DatabaseConnection dbConnection = new DatabaseConnection(testContext.getDS().getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

        transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
        cayenneService = getService(ICayenneService.class);
    }


    @Test
    public void test() {
       // FieldConfigurationUpdater updater = new FieldConfigurationUpdater();
        FieldConfigurationStub configurationStub1 = new FieldConfigurationStub();
        configurationStub1.setAngelId(1L);
        configurationStub1.setWillowId(null);
        configurationStub1.setName("Test configuration 1");
        configurationStub1.setModified(new Date());
        configurationStub1.setCreated(new Date());
        configurationStub1.setEntityIdentifier(FieldConfiguration.class.getSimpleName());

        FieldConfigurationStub configurationStub2 = new FieldConfigurationStub();
        configurationStub2.setAngelId(2L);
        configurationStub2.setWillowId(null);
        configurationStub2.setName("Test configuration 2");
        configurationStub2.setModified(new Date());
        configurationStub2.setCreated(new Date());
        configurationStub2.setEntityIdentifier(FieldConfiguration.class.getSimpleName());
        
        FieldConfigurationSchemeStub schemeStub = new FieldConfigurationSchemeStub();
        schemeStub.setName("Test scheme");
        schemeStub.setAngelId(1L);
        schemeStub.setWaitingListFieldConfigurationId(1L);
        schemeStub.setApplicationFieldConfigurationId(2L);
        schemeStub.setEnrolFieldConfigurationId(2L);
        schemeStub.setModified(new Date());
        schemeStub.setCreated(new Date());
        schemeStub.setEntityIdentifier(FieldConfigurationScheme.class.getSimpleName());

        FieldStub fieldStub = new FieldStub();
        fieldStub.setAngelId(1L);
        fieldStub.setFieldConfigurationId(1L);
        fieldStub.setName("Test Field");
        fieldStub.setMandatory(false);
        fieldStub.setOrder(1);
        fieldStub.setProperty("prop");
        fieldStub.setModified(new Date());
        fieldStub.setCreated(new Date());
        fieldStub.setEntityIdentifier(Field.class.getSimpleName());

        CourseStub courseStub = new CourseStub();
        courseStub.setAngelId(1L);
        courseStub.setWillowId(1L);
        courseStub.setName("Test Course");
        courseStub.setCode("code");
        courseStub.setEnrolmentType(1);
        courseStub.setModified(new Date());
        courseStub.setCreated(new Date());
        courseStub.setEntityIdentifier(Course.class.getSimpleName());
        
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V16);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(courseStub);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(fieldStub);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStub1);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStub2);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(schemeStub);
        
        
        List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
        assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

        ObjectContext context = cayenneService.newContext();

        FieldConfiguration configuration1 = ObjectSelect.query(FieldConfiguration.class)
                .where(FieldConfiguration.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(configuration1);


        FieldConfiguration configuration2 = ObjectSelect.query(FieldConfiguration.class)
                .where(FieldConfiguration.ANGEL_ID.eq(2L))
                .selectOne(context);
        assertNotNull(configuration2);

        FieldConfigurationScheme scheme = ObjectSelect.query(FieldConfigurationScheme.class)
                .where(FieldConfigurationScheme.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(scheme);

        Field field = ObjectSelect.query(Field.class)
                .where(Field.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(field);

        Course course = ObjectSelect.query(Course.class)
                .where(Course.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(course);
        assertEquals(courseStub.getName(), course.getName());
    }
}
