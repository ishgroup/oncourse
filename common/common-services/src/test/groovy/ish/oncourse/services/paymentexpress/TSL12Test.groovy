package ish.oncourse.services.paymentexpress

import ish.oncourse.services.ServiceModule
import org.apache.commons.io.FileUtils
import org.junit.Test
import org.testng.annotations.AfterTest


/**
 * User: akoiro
 * Date: 17/8/17
 */
class TSL12Test {
    private static final String URL_USI = "https://portal.usi.gov.au/Service/v2/UsiService.wsdl"

    private File file = new File("check.html")

    @Test
    void testUsi() {
        System.setProperty(ServiceModule.JAVA_PARAM_HTTPS_PROTOCOLS, ServiceModule.TLS_V12)
        URL url = new URL(URL_USI)
        FileUtils.copyInputStreamToFile(url.openStream(), file)
    }

    @Test
    void testPaymentExpress() {
        System.setProperty(ServiceModule.JAVA_PARAM_HTTPS_PROTOCOLS, ServiceModule.TLS_V12)
        URL url = new URL(PaymentExpressGatewayService.URL)
        FileUtils.copyInputStreamToFile(url.openStream(), file)
    }


    @AfterTest
    void afterTest() {
        file.deleteOnExit()
    }
}
