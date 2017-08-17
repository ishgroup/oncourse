package ish.oncourse.services.paymentexpress

import ish.oncourse.util.CommonUtils
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

import javax.net.ssl.SSLHandshakeException

/**
 * User: akoiro
 * Date: 17/8/17
 */
class TLS12Test {
    private static final String URL_USI = "https://portal.usi.gov.au/Service/v2/UsiService.wsdl"
    private static
    final String URL_TRAINING = "https://ws.training.gov.au/Deewr.Tga.WebServices/ClassificationService.svc/Classification12"

    private File file = new File("check.html")

    @Before
    void before() {
        CommonUtils.configureTLSProtocols()
    }

    @Test
    void testUsi() {
        URL url = new URL(URL_USI)
        FileUtils.copyInputStreamToFile(url.openStream(), file)
    }

    @Test
    void testPaymentExpress() {
        URL url = new URL(PaymentExpressGatewayService.URL)
        FileUtils.copyInputStreamToFile(url.openStream(), file)
    }

    @Test
    void testTraining() {
        URL url = new URL(URL_TRAINING)
        try {
            FileUtils.copyInputStreamToFile(url.openStream(), file)
        } catch (SSLHandshakeException e) {
            throw e
        } catch (Exception e) {
        }
    }


    @After
    void after() {
        file.deleteOnExit()
    }
}
