package ish.oncourse.usi;

import com.sun.xml.wss.XWSSecurityException;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;

import java.text.ParseException;

public class USITest {

    @Test
    @Ignore
    public void verifyUsi() throws XMLStreamException, XWSSecurityException, ParseException {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");


        USIService usiService = new USIServiceBuilder().buildService();


        USIVerificationResult result = usiService.verifyUsi("Simone",
                "Stockl",
                "1988-02-05",
                "DB5PW8SXU6",
                "90054" , "53136500313", "5176926411" );
        System.out.println(result);
    }
}
