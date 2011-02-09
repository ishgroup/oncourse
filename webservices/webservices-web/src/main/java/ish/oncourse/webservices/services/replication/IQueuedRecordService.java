package ish.oncourse.webservices.services.replication;

import java.util.List;

import ish.oncourse.model.QueuedRecord;

public interface IQueuedRecordService {
	List<QueuedRecord> getRecords();
}
