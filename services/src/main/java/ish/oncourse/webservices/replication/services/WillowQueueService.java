package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

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
		
		String sql = String.format("select count(*) as SIZE from QueuedTransaction t where t.id in (select r.transactionId from QueuedRecord r where r.numberOfAttempts < %s)", QueuedRecord.MAX_NUMBER_OF_RETRY);
		SQLTemplate q = new SQLTemplate(QueuedRecord.class, sql);
		q.setFetchingDataRows(true);
		
		@SuppressWarnings("unchecked")
		List<DataRow> rows = cayenneService.sharedContext().performQuery(q);
		Integer size = (Integer) rows.get(0).get("SIZE");	

		return size;
	}

	/**
	 * @see ish.oncourse.webservices.replication.services.IWillowQueueService#getReplicationQueue(int,
	 *      int)
	 */
	public List<QueuedTransaction> getReplicationQueue(int fromTransaction, int numberOfTransactions) {
		
		Expression qualifier = ExpressionFactory.lessExp(QueuedTransaction.QUEUED_RECORDS_PROPERTY + "." + QueuedRecord.NUMBER_OF_ATTEMPTS_PROPERTY,
				QueuedRecord.MAX_NUMBER_OF_RETRY);
		qualifier = qualifier.andExp(ExpressionFactory.matchExp(QueuedTransaction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		
		SelectQuery q = new SelectQuery(QueuedTransaction.class, qualifier);
		q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));
		q.setPageSize(numberOfTransactions);
		q.setFetchOffset(fromTransaction);

		@SuppressWarnings("unchecked")
		List<QueuedTransaction> list = cayenneService.sharedContext().performQuery(q);
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
