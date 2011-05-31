package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowQueueService implements IWillowQueueService {

	@Inject
	@Autowired
	private IWebSiteService webSiteService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	private static final int FETCH_LIMIT = 30;

	public List<QueuedRecord> getReplicationQueue(int limit) {

		List<QueuedRecord> queue = new ArrayList<QueuedRecord>();
		ObjectContext ctx = cayenneService.sharedContext();

		boolean hasMoreRecords = true;
		int fetchOffset = 0;

		while (hasMoreRecords) {

			SelectQuery q = new SelectQuery(QueuedTransaction.class);

			q.andQualifier(ExpressionFactory.matchExp(QueuedTransaction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));

			q.addPrefetch(QueuedTransaction.QUEUED_RECORDS_PROPERTY);
			q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));

			q.setFetchLimit(FETCH_LIMIT);
			q.setFetchOffset(fetchOffset);

			List<QueuedTransaction> transactions = ctx.performQuery(q);

			int fetchedSize = transactions.size();

			if (fetchedSize < FETCH_LIMIT) {
				hasMoreRecords = false;
			} else {
				fetchOffset += fetchedSize;
			}

			for (QueuedTransaction t : transactions) {

				int numberOfRecords = t.getQueuedRecords().size();

				if (queue.size() + numberOfRecords >= limit) {
					hasMoreRecords = false;
					break;
				}

				queue.addAll(t.getQueuedRecords());
			}
		}

		return queue;
	}

	@Override
	public void cleanEmptyTransactions() {
		
		ObjectContext objectContext = cayenneService.newNonReplicatingContext();

		EJBQLQuery q = new EJBQLQuery("select qt from QueuedTransaction qt left join qt.queuedRecords qr where qr.entityWillowId is null");

		List<QueuedTransaction> emptyTransactions = objectContext.performQuery(q);

		if (!emptyTransactions.isEmpty()) {
			objectContext.deleteObjects(emptyTransactions);
			objectContext.commitChanges();
		}
	}
}
