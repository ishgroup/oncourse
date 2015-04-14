package ish.oncourse.services.message;

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

public class MessagePersonService implements IMessagePersonService {

	@Inject
	private ICayenneService cayenneService;

	@SuppressWarnings("unchecked")
	@Override
	public List<MessagePerson> smsToSend(int limit) {
		SelectQuery q = new SelectQuery(MessagePerson.class, dequeueingQualifier());
		q.addPrefetch(MessagePerson.MESSAGE_PROPERTY);
		q.addPrefetch(MessagePerson.CONTACT_PROPERTY);
		q.setFetchLimit(limit);
		return cayenneService.sharedContext().performQuery(q);
	}

	private Expression dequeueingQualifier() {
		List<Expression> exprArray = new ArrayList<>();
		exprArray.add(ExpressionFactory.matchExp(MessagePerson.TYPE_PROPERTY, MessageType.SMS));
		exprArray.add(ExpressionFactory.matchExp(MessagePerson.STATUS_PROPERTY, MessageStatus.QUEUED));
		exprArray.add(ExpressionFactory.noMatchExp(MessagePerson.MESSAGE_PROPERTY, null));
		exprArray.add(ExpressionFactory.noMatchExp(MessagePerson.MESSAGE_PROPERTY + "." + Message.SMS_TEXT_PROPERTY, null));
		exprArray.add(ExpressionFactory.noMatchExp(MessagePerson.DESTINATION_ADDRESS_PROPERTY, null));
		exprArray.add(ExpressionFactory.noMatchExp(MessagePerson.CONTACT_PROPERTY, null));
		return ExpressionFactory.joinExp(Expression.AND, exprArray);
	}
}
