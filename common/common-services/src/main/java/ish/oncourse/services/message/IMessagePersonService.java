package ish.oncourse.services.message;

import ish.oncourse.model.MessagePerson;

import java.util.List;

public interface IMessagePersonService {
	List<MessagePerson> smsToSend();
}
