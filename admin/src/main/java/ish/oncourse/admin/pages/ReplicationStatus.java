package ish.oncourse.admin.pages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ReplicationStatus {
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Property
	private List<College> colleges;
	
	@Property
	private College currentCollege;
	
	private DateFormat dateFormat;
	
	@SetupRender
	void setupRender() {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		this.colleges = collegeService.allColleges();
		
		Ordering.orderList(colleges, Arrays.asList(new Ordering(College.NAME_PROPERTY, SortOrder.ASCENDING_INSENSITIVE)));
	}
	
	public String getLastReplicationDate() {
		return dateFormat.format(currentCollege.getLastRemoteAuthentication());
	}
	
	public String getOldestQueuedItemTimestamp() {
		QueuedRecord oldestRecord = getOldestQueuedItem();
		if (oldestRecord != null) {
			return dateFormat.format(oldestRecord.getLastAttemptTimestamp());
		}
		return "";
	}
	
	public String getLastError() {
		QueuedRecord lastRecord = getLastQueuedItem();
		if (lastRecord != null) {
			return lastRecord.getErrorMessage();
		}
		return "";
	}
	
	public String getNumberOfRecordsInQueue() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -1);
			Date oneHourBefore = cal.getTime();

			Expression exp = ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, college).andExp(
					ExpressionFactory.lessExp(
							QueuedRecord.QUEUED_TRANSACTION_PROPERTY + "." + QueuedTransaction.CREATED_PROPERTY,
							oneHourBefore));
			SelectQuery query = new SelectQuery(QueuedRecord.class, exp);
			
			List<QueuedRecord> records = context.performQuery(query);
			return String.valueOf(records.size());
		}
		return "0";
	}
	
	private QueuedRecord getOldestQueuedItem() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {
			Expression exp = ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, college);
			SelectQuery query = new SelectQuery(QueuedRecord.class, exp);
			query.addOrdering(QueuedRecord.LAST_ATTEMPT_TIMESTAMP_PROPERTY, SortOrder.DESCENDING);
			query.setFetchLimit(1);
			
			QueuedRecord record = (QueuedRecord) Cayenne.objectForQuery(context, query);
			
			return record;
		}
		return null;
	}
	
	private QueuedRecord getLastQueuedItem() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {
			Expression exp = ExpressionFactory.matchExp(QueuedRecord.COLLEGE_PROPERTY, college);
			SelectQuery query = new SelectQuery(QueuedRecord.class, exp);
			query.addOrdering(QueuedRecord.LAST_ATTEMPT_TIMESTAMP_PROPERTY, SortOrder.ASCENDING);
			query.setFetchLimit(1);
			
			QueuedRecord record = (QueuedRecord) Cayenne.objectForQuery(context, query);
			
			return record;
		}
		return null;
	}

}
