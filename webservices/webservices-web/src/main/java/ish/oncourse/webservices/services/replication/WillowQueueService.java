package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.webservices.soap.v4.auth.SessionToken;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowQueueService implements IWillowQueueService {

	@Inject
	@Autowired
	private Request request;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	public SortedMap<QueuedKey, QueuedRecord> getReplicationQueue() {
		
		SortedMap<QueuedKey, QueuedRecord> m = new TreeMap<QueuedKey, QueuedRecord>();
		
		Session session = request.getSession(false);

		SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);

		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, token.getCollege()));	
		
		List<QueuedRecord> list = cayenneService.sharedContext().performQuery(q);
		
		for (QueuedRecord r : list) {
			m.put(new QueuedKey(r.getEntityWillowId(), r.getEntityIdentifier()), r);
		}
		
		return m;
	}

	@Override
	public QueuedRecord find(Long willowId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(QueuedRecord.class);
		
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, willowId));
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, entityIdentifier));
		
		return (QueuedRecord) DataObjectUtils.objectForQuery(cayenneService.sharedContext(), q);
	}
}
