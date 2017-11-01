package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

public class WillowQueueService implements IWillowQueueService {

	private final IWebSiteService webSiteService;

	private final ICayenneService cayenneService;

	@Inject
	public WillowQueueService(IWebSiteService webSiteService, ICayenneService cayenneService) {
		super();
		this.webSiteService = webSiteService;
		this.cayenneService = cayenneService;
	}

	/*
	 * @see ish.oncourse.webservices.replication.services.IWillowQueueService#
	 * getNumberOfTransactions()
	 */
	@Override
	public int getNumberOfTransactions() {
		cleanEmptyTransactions();

		String sql = String.format("select count(distinct t.id) as SIZE from QueuedTransaction t inner join QueuedRecord q on t.id = q.transactionId where q.numberOfAttempts < %s", QueuedRecord.MAX_NUMBER_OF_RETRY);
		SQLTemplate q = new SQLTemplate(QueuedRecord.class, sql);
		q.setFetchingDataRows(true);
		
		List<DataRow> rows = cayenneService.newContext().performQuery(q);
		return  ((Number) rows.get(0).get("SIZE")).intValue();
	}

	/**
	 * @see ish.oncourse.webservices.replication.services.IWillowQueueService#getReplicationQueue(int,
	 *      int)
	 */
	public List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions) {
		List<QueuedTransaction> list = ObjectSelect.query(QueuedTransaction.class)
				.where(QueuedTransaction.QUEUED_RECORDS.dot(QueuedRecord.NUMBER_OF_ATTEMPTS).lt(QueuedRecord.MAX_NUMBER_OF_RETRY))
				.and(QueuedTransaction.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.orderBy(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING))
				.pageSize(numberOfTransactions)
				.offset(fromTransaction)
				.select(cayenneService.sharedContext());

		List<QueuedTransaction> result = new ArrayList<>(numberOfTransactions);

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
		
		@SuppressWarnings("unchecked")
		List<QueuedTransaction> emptyTransactions = objectContext.performQuery(q);

		if (!emptyTransactions.isEmpty()) {
			objectContext.deleteObjects(emptyTransactions);
			objectContext.commitChanges();
		}
	}
}
