package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueueKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.services.ICollegeRequestService;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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

	public List<QueuedRecord> getReplicationQueue() {

		SortedMap<QueueKey, QueuedRecord> m = new TreeMap<QueueKey, QueuedRecord>();

		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, collegeRequestService.getRequestingCollege()));

		q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));

		return cayenneService.sharedContext().performQuery(q);
	}
}
