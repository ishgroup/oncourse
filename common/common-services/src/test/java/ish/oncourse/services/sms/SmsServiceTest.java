package ish.oncourse.services.sms;

import ish.common.types.MessageStatus;
import ish.oncourse.model.Pair;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class SmsServiceTest {

    @Test
    public void test() {
        ISMSService service = new TestModeSMSService();

        String sessionId = service.authenticate();
        Pair<MessageStatus, String> status = service.sendSMS(sessionId, "onCourse Test", "0412345678", "This is a sms service test.");

        assertEquals(MessageStatus.SENT, status.getFirst());
        assertTrue(status.getSecond().startsWith("ID:"));
    }
}
