package ish.oncourse.webservices.usi;

import ish.oncourse.webservices.soap.RealWSTransportTest;
import ish.oncourse.webservices.soap.v22.PaymentPortType;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v22.stubs.replication.ParametersMap;
import org.junit.Test;

public class V22USIVerificationServiceTest extends RealWSTransportTest {
    @Test
    public void testUSI() throws Exception {

        new USITest(new USITest.Parent() {
            @Override
            public GenericParametersMap verifyUSI(GenericParametersMap var1) throws Exception {
                return ((PaymentPortType) testEnv.getTestEnv().getTransportConfig().getPaymentPortType()).verifyUSI((ParametersMap) var1);
            }

            @Override
            public void authenticate() throws Exception {
                testEnv.getTestEnv().authenticate();
            }

            @Override
            public SupportedVersions getVersion() {
                return SupportedVersions.V22;
            }
        }).testUSI();
    }
}
