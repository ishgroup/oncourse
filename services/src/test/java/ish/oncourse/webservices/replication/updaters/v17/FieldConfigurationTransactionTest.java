package ish.oncourse.webservices.replication.updaters.v17;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v17.stubs.replication.*;
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

import static ish.oncourse.webservices.util.SupportedVersions.V17;
import static ish.oncourse.webservices.v17.stubs.replication.Status.SUCCESS;
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
        configurationStub1.setEntityIdentifier(EnrolmentFieldConfiguration.class.getSimpleName());
        configurationStub1.setType(1);
        
        FieldConfigurationStub configurationStub2 = new FieldConfigurationStub();
        configurationStub2.setAngelId(2L);
        configurationStub2.setWillowId(null);
        configurationStub2.setName("Test configuration 2");
        configurationStub2.setModified(new Date());
        configurationStub2.setCreated(new Date());
        configurationStub2.setEntityIdentifier(ApplicationFieldConfiguration.class.getSimpleName());
        configurationStub2.setType(2);

        FieldConfigurationStub configurationStub3 = new FieldConfigurationStub();
        configurationStub3.setAngelId(3L);
        configurationStub3.setWillowId(null);
        configurationStub3.setName("Test configuration 2");
        configurationStub3.setModified(new Date());
        configurationStub3.setCreated(new Date());
        configurationStub3.setEntityIdentifier(WaitingListFieldConfiguration.class.getSimpleName());
        configurationStub3.setType(3);
        
        FieldConfigurationSchemeStub schemeStub = new FieldConfigurationSchemeStub();
        schemeStub.setName("Test scheme");
        schemeStub.setAngelId(1L);
        
        
        schemeStub.setModified(new Date());
        schemeStub.setCreated(new Date());
        schemeStub.setEntityIdentifier(FieldConfigurationScheme.class.getSimpleName());

        FieldConfigurationLinkStub linkStub1 = new FieldConfigurationLinkStub();
        linkStub1.setModified(new Date());
        linkStub1.setCreated(new Date());
        linkStub1.setAngelId(1L);
        linkStub1.setEntityIdentifier(FieldConfigurationLink.class.getSimpleName());
        linkStub1.setSchemeId(1L);
        linkStub1.setConfigurationId(1L);
        linkStub1.setConfigurationType(1);

        FieldConfigurationLinkStub linkStub2 = new FieldConfigurationLinkStub();
        linkStub2.setModified(new Date());
        linkStub2.setCreated(new Date());
        linkStub2.setAngelId(2L);
        linkStub2.setEntityIdentifier(FieldConfigurationLink.class.getSimpleName());
        linkStub2.setSchemeId(1L);
        linkStub2.setConfigurationId(2L);
        linkStub2.setConfigurationType(2);

        FieldConfigurationLinkStub linkStub3 = new FieldConfigurationLinkStub();
        linkStub3.setModified(new Date());
        linkStub3.setCreated(new Date());
        linkStub3.setAngelId(3L);
        linkStub3.setEntityIdentifier(FieldConfigurationLink.class.getSimpleName());
        linkStub3.setSchemeId(1L);
        linkStub3.setConfigurationId(3L);
        linkStub3.setConfigurationType(3);


        FieldStub fieldStub1= new FieldStub();
        fieldStub1.setAngelId(1L);
        fieldStub1.setFieldConfigurationId(1L);
        fieldStub1.setFieldHeadingId(1L);
        fieldStub1.setName("Test Field");
        fieldStub1.setMandatory(false);
        fieldStub1.setOrder(2);
        fieldStub1.setProperty("prop");
        fieldStub1.setModified(new Date());
        fieldStub1.setCreated(new Date());
        fieldStub1.setEntityIdentifier(Field.class.getSimpleName());
        fieldStub1.setConfigurationType(1);

        FieldStub fieldStub2 = new FieldStub();
        fieldStub2.setAngelId(2L);
        fieldStub2.setFieldConfigurationId(2L);
        fieldStub2.setFieldHeadingId(null);
        fieldStub2.setName("Test Field");
        fieldStub2.setMandatory(false);
        fieldStub2.setOrder(1);
        fieldStub2.setProperty("prop");
        fieldStub2.setModified(new Date());
        fieldStub2.setCreated(new Date());
        fieldStub2.setEntityIdentifier(Field.class.getSimpleName());
        fieldStub2.setConfigurationType(2);

        FieldStub fieldStub3 = new FieldStub();
        fieldStub3.setAngelId(3L);
        fieldStub3.setFieldConfigurationId(3L);
        fieldStub3.setFieldHeadingId(null);
        fieldStub3.setName("Test Field");
        fieldStub3.setMandatory(false);
        fieldStub3.setOrder(1);
        fieldStub3.setProperty("prop");
        fieldStub3.setModified(new Date());
        fieldStub3.setCreated(new Date());
        fieldStub3.setEntityIdentifier(Field.class.getSimpleName());
        fieldStub3.setConfigurationType(3);

        FieldHeadingStub fieldHeadingStub = new FieldHeadingStub();
        fieldHeadingStub.setName("Test heading");
        fieldHeadingStub.setAngelId(1L);
        fieldHeadingStub.setOrder(0);
        fieldHeadingStub.setFieldConfigurationId(1L);
        fieldHeadingStub.setModified(new Date());
        fieldHeadingStub.setCreated(new Date());
        fieldHeadingStub.setEntityIdentifier(FieldHeading.class.getSimpleName());
        fieldHeadingStub.setConfigurationType(1);

        CourseStub courseStub = new CourseStub();
        courseStub.setAngelId(1L);
        courseStub.setWillowId(1L);
        courseStub.setName("Test Course");
        courseStub.setCode("code");
        courseStub.setEnrolmentType(1);
        courseStub.setModified(new Date());
        courseStub.setCreated(new Date());
        courseStub.setEntityIdentifier(Course.class.getSimpleName());
        
        GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V17);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(schemeStub);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(courseStub);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(fieldStub1);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(fieldStub2);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(fieldStub3);

        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStub1);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStub2);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStub3);

        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(linkStub1);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(linkStub2);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(linkStub3);
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(fieldHeadingStub);
        
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

        Field field2 = ObjectSelect.query(Field.class)
                .where(Field.ANGEL_ID.eq(2L))
                .selectOne(context);
        assertNotNull(field2);

        FieldHeading fieldHeading = ObjectSelect.query(FieldHeading.class)
                .where(FieldHeading.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(fieldHeading);
        
        Course course = ObjectSelect.query(Course.class)
                .where(Course.ANGEL_ID.eq(1L))
                .selectOne(context);
        assertNotNull(course);
        assertEquals(courseStub.getName(), course.getName());
    }
}
