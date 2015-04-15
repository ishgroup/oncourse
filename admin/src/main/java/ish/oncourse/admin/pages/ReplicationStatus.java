package ish.oncourse.admin.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

	/**
	 * Number of records in the queue for this college older than one hour
	 * @return
	 */
	public String getNumberOfRecordsInQueue() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -1);
			Date oneHourAgo = cal.getTime();
			
			List<QueuedRecord> records = ObjectSelect.query(QueuedRecord.class).
					where(QueuedRecord.COLLEGE.eq(college).
							andExp(QueuedRecord.QUEUED_TRANSACTION.dot(QueuedTransaction.CREATED).lt(oneHourAgo))).
					select(context);
			return String.valueOf(records.size());
		}
		return "0";
	}
	
	private QueuedRecord getOldestQueuedItem() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {

			return ObjectSelect.query(QueuedRecord.class).
					where(QueuedRecord.COLLEGE.eq(college)).
					orderBy(QueuedRecord.LAST_ATTEMPT_TIMESTAMP.desc()).
					limit(1).
					selectFirst(context);
		}
		return null;
	}
	
	private QueuedRecord getLastQueuedItem() {
		ObjectContext context = cayenneService.sharedContext();
		
		College college = context.localObject(currentCollege);
		if (college != null) {
			return ObjectSelect.query(QueuedRecord.class).
					where(QueuedRecord.COLLEGE.eq(college)).
					orderBy(QueuedRecord.LAST_ATTEMPT_TIMESTAMP.asc()).
					limit(1).
					selectFirst(context);
		}
		return null;
	}

}
