package ish.oncourse.usi;

import com.sun.xml.wss.XWSSecurityException;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;

import java.text.ParseException;

import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE;
import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE_PASSWORD;

public class USITest {

    @Test
    public void verifyUsi() throws XMLStreamException, XWSSecurityException, ParseException {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        System.setProperty(CREDENTIAL_STORE.getSystemProperty(), "oncourse-usi-keystore.xml");
        System.setProperty(CREDENTIAL_STORE_PASSWORD.getSystemProperty(), "oncourse-usi-keystore.pass");

        USIService usiService = new USIServiceBuilder().buildService();
        USIVerificationRequest request = new USIVerificationRequest();

        request.setStudentBirthDate(DateUtils.parseDate("1988-05-02", "yyyy-MM-dd"));
        request.setStudentFirstName("Simone");
        request.setStudentLastName("Claudia Stockl");
        request.setUsiCode("DB5PW8SXU6");
        request.setOrgCode("90054");

        USIVerificationResult result = usiService.verifyUsi("Simone",
                "Stockl",
                "1988-02-05",
                "DB5PW8SXU6",
                "90054" , "53136500313", "5176926411" );
        System.out.println(result);
    }
}
