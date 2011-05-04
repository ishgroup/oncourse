package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.services.ICollegeRequestService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowQueueService implements IWillowQueueService {

	@Inject
	@Autowired
	private ICollegeRequestService collegeRequestService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	public List<QueuedRecord> getReplicationQueue(int limit) {

		int size = 0;
		int fetchOffset = 0;

		List<QueuedRecord> queue = new ArrayList<QueuedRecord>();
		ObjectContext ctx = cayenneService.sharedContext();

		while (size < limit) {

			SelectQuery q = new SelectQuery(QueuedTransaction.class);
			q.andQualifier(ExpressionFactory.matchExp(QueuedTransaction.COLLEGE_PROPERTY, collegeRequestService.getRequestingCollege()));
			q.addPrefetch(QueuedTransaction.QUEUED_RECORDS_PROPERTY);
			q.addOrdering(new Ordering(QueuedTransaction.CREATED_PROPERTY, SortOrder.ASCENDING));
			q.setFetchLimit(30);
			q.setFetchOffset(fetchOffset);

			List<QueuedTransaction> transactions = ctx.performQuery(q);

			size += transactions.size();
			fetchOffset += transactions.size();

			for (QueuedTransaction t : transactions) {
				queue.addAll(t.getQueuedRecords());
			}
		}

		return queue;
	}
}
