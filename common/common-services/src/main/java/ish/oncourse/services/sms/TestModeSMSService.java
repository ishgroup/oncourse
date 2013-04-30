package ish.oncourse.services.sms;

import ish.common.types.MessageStatus;
import ish.oncourse.model.Pair;

public class TestModeSMSService implements ISMSService {

	@Override
	public String authenticate() {
		return "123456";
	}

	@Override
	public Pair<MessageStatus, String> sendSMS(String sessionId, String from, String to, String text) {
		return new Pair<>(MessageStatus.SENT, "message was sent");
	}
}
