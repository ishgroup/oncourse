package ish.oncourse.model;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class BinaryDataTest extends ServiceTest {

    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream binaryDataDS = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/model/BinaryDataTest/binaryDataDS.xml");
        InputStream onCourseDS = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/model/BinaryDataTest/onCourseDS.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(binaryDataDS);
        DataSource dataSource = getDataSource("jdbc/oncourse_binary");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);

        dataSet = new FlatXmlDataSetBuilder().build(onCourseDS);
        dataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);


        this.cayenneService = getService(ICayenneService.class);
    }


    @Test
    public void test() throws UnsupportedEncodingException {
        BinaryInfo binaryInfo = Cayenne.objectForPK(cayenneService.sharedContext(), BinaryInfo.class, 1);
        BinaryData binaryData = binaryInfo.getBinaryData();
        assertNotNull(binaryData);

        ObjectContext objectContext1 = cayenneService.newNonReplicatingContext();
        binaryData = Cayenne.objectForPK(objectContext1, BinaryData.class, 1);
        binaryData.setContent("12345678901234567890".getBytes());
        objectContext1.commitChanges();

        Expression qualifier = ExpressionFactory.matchDbExp(BinaryInfo.ID_PK_COLUMN, 1);
        SelectQuery selectQuery = new SelectQuery(BinaryInfo.class,qualifier);
        //selectQuery.addPrefetch(BinaryInfo.BINARY_DATA_PROPERTY);
        binaryInfo = (BinaryInfo) Cayenne.objectForQuery(cayenneService.sharedContext(), selectQuery);
        BinaryData test = binaryInfo.getBinaryData();
        assertNotNull(test);
        assertEquals(new String(test.getContent(),"UTF-8"),"12345678901234567890" );
    }
}