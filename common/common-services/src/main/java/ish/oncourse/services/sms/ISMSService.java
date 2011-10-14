package ish.oncourse.services.sms;

import ish.common.types.MessageStatus;
import ish.oncourse.model.Pair;

/**
 * Common interface for sms for sms sending. Implementing services are supposed to contact remote sms gateway.
 */
public interface ISMSService {
	
	String authenticate();
	
    Pair<MessageStatus, String> sendSMS(String sessionId, String from, String to, String text);
}
