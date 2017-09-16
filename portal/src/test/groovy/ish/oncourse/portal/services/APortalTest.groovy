package ish.oncourse.portal.services

import ish.oncourse.portal.service.TestModule
import ish.oncourse.services.paymentexpress.INewPaymentGatewayService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.test.ServiceTest
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint
import org.apache.cayenne.ObjectContext
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.operation.DatabaseOperation
import org.junit.Before

import javax.sql.DataSource

/**
 * User: akoiro
 * Date: 2/07/2016
 */
abstract class APortalTest extends ServiceTest {
    def ICayenneService cayenneService;
    def IPortalService portalService
    def INewPaymentGatewayService paymentGatewayService
    def ObjectContext objectContext

    protected String getDataSetName() {
        return this.getClass().getSimpleName() + ".xml";
    }

    @Before
    public void setup() throws Exception {
        System.setProperty(TestUSIServiceEndpoint.USI_TEST_MODE, "true");

        initTest("ish.oncourse.portal", "portal", "src/main/resources/desktop/ish/oncourse/portal/pages", TestModule.class);

        InputStream st = this.getClass().getResourceAsStream(getDataSetName());
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet fileDataSet = builder.build(st);

        IDataSet dataSet = adjustDataSet(fileDataSet);

        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService)
        portalService = getService(IPortalService)
        paymentGatewayService = getService(INewPaymentGatewayService)
        objectContext = cayenneService.newContext()

    }

    protected IDataSet adjustDataSet(IDataSet dataSet) {
        return dataSet;
    }
}
