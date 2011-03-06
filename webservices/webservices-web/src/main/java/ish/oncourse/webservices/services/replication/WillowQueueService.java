package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.webservices.soap.v4.auth.SessionToken;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
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

	public List<QueuedRecord> getReplicationQueue() {

		SortedMap<QueuedKey, QueuedRecord> m = new TreeMap<QueuedKey, QueuedRecord>();

		Session session = request.getSession(false);

		SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);

		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, token.getCollege()));

		q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.DESCENDING));

		return cayenneService.sharedContext().performQuery(q);
	}
	
	@Override
	public Queueable findRelatedEntity(QueuedRecord entity) {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) entity.getObjectContext().getEntityResolver().getObjEntity(entity.getEntityIdentifier())
				.getClass();
		return DataObjectUtils.objectForPK(entity.getObjectContext(), entityClass, entity.getEntityWillowId());
	}

	@Override
	public void confirmRecord(Long willowId, Long angelId, String entityName, boolean isSuccess) {

		ObjectContext ctx = cayenneService.newNonReplicatingContext();

		SelectQuery q = new SelectQuery(QueuedRecord.class);

		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, willowId));
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, entityName));

		QueuedRecord record = (QueuedRecord) DataObjectUtils.objectForQuery(ctx, q);

		if (isSuccess && angelId != null) {
			Class<?> entityClass = (Class<?>) ctx.getEntityResolver().getObjEntity(entityName).getClass();
			Queueable object = (Queueable) DataObjectUtils.objectForPK(ctx, entityClass, willowId);
			object.setAngelId(angelId);
			ctx.deleteObject(record);
		} else {
			int numberAttempts = record.getNumberOfAttempts();
			record.setNumberOfAttempts(numberAttempts + 1);
			record.setLastAttemptTimestamp(new Date());
		}

		ctx.commitChanges();
	}
}
