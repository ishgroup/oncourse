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
	
	/**
	 * @see ish.oncourse.webservices.replication.services.IWillowQueueService#getReplicationQueue(int, int)
	 */
	public List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions) {

		SelectQuery q = new SelectQuery(QueuedTransaction.class);
		q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));
		q.addPrefetch(QueuedTransaction.QUEUED_RECORDS_PROPERTY);
		q.andQualifier(ExpressionFactory.matchExp(QueuedTransaction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		q.setPageSize(numberOfTransactions);
		q.setFetchOffset(fromTransaction);
		
		List<QueuedTransaction> list = cayenneService.sharedContext().performQuery(q);
		List<QueuedTransaction> result = new ArrayList<QueuedTransaction>(numberOfTransactions);

		int maxNumber = (list.size() < numberOfTransactions) ? list.size() : numberOfTransactions;
		int index = 0;

		while (index < maxNumber) {
			result.add(list.get(index++));
		}
		
		return result;
	}

	/**
	 * @see ish.oncourse.webservices.replication.services.IWillowQueueService#cleanEmptyTransactions()
	 */
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
