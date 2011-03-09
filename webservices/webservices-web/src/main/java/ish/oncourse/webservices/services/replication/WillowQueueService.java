package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.webservices.util.SoapUtil;

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

		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, (College) request.getAttribute(SoapUtil.REQUESTING_COLLEGE)));

		q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));

		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public Queueable findEntityByWillowId(String entityIdentifier, Long willowId) {
		return DataObjectUtils.objectForPK(cayenneService.newNonReplicatingContext(), getEntityClass(entityIdentifier), willowId);
	}
	
	@Override
	public Queueable findEntityByAngelId(String entityIdentifier, Long angelId) {
		SelectQuery q = new SelectQuery(getEntityClass(entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", angelId));
		return (Queueable) DataObjectUtils.objectForQuery(cayenneService.newNonReplicatingContext(), q);
	}

	@Override
	public <T extends Queueable> T createNew(Class<T> clazz) {
		ObjectContext ctx = cayenneService.newNonReplicatingContext();
		return ctx.newObject(clazz);
	}

	@Override
	public Class<? extends Queueable> getEntityClass(String entityIdentifier) {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) cayenneService.sharedContext().getEntityResolver()
				.getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}

	@Override
	public void confirmRecord(Long willowId, Long angelId, String entityName, boolean isSuccess) {

		ObjectContext ctx = cayenneService.newNonReplicatingContext();

		SelectQuery q = new SelectQuery(QueuedRecord.class);

		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, willowId));
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, entityName));

		QueuedRecord record = (QueuedRecord) DataObjectUtils.objectForQuery(ctx, q);

		if (isSuccess && angelId != null) {
			Queueable object = (Queueable) DataObjectUtils.objectForPK(ctx, getEntityClass(entityName), willowId);
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
