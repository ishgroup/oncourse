package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.webservices.soap.v4.auth.SessionToken;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class QueuedRecordService implements IQueuedRecordService {

	@Inject
	@Autowired
	private Request request;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	@Override
	public List<QueuedRecord> getRecords() {

		Session session = request.getSession(false);

		SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);

		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, token.getCollege()));

		return cayenneService.sharedContext().performQuery(q);
	}

}
