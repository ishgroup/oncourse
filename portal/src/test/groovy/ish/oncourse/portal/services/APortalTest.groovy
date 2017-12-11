package ish.oncourse.portal.services

import ish.oncourse.portal.service.TestModule
import ish.oncourse.services.paymentexpress.INewPaymentGatewayService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.ServiceTest
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint
import org.apache.cayenne.ObjectContext
import org.junit.Before

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
        return this.getClass().getName().replace('.', '/') + ".xml"
    }

    @Before
    public void setup() throws Exception {
        System.setProperty(TestUSIServiceEndpoint.USI_TEST_MODE, "true");

        initTest("ish.oncourse.portal", "portal", "src/main/resources/desktop/ish/oncourse/portal/pages", TestModule.class);


        new LoadDataSet().dataSetFile(getDataSetName())
                .replacements(replacements())
                .load(getDataSource("jdbc/oncourse"))

        cayenneService = getService(ICayenneService)
        portalService = getService(IPortalService)
        paymentGatewayService = getService(INewPaymentGatewayService)
        objectContext = cayenneService.newContext()

    }

    protected Map<String, Object> replacements() {
        return Collections.emptyMap()
    }
}
