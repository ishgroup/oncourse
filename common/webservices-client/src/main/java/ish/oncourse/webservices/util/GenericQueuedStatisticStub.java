package ish.oncourse.webservices.util;

import java.util.Date;

public interface GenericQueuedStatisticStub {
	public static final String QUEUED_STATISTIC_ENTITY = "QueuedStatistic";
	public Boolean isCleanupStub();
	
	public Date getReceivedTimestamp();
	
	public String getStackedEntityIdentifier();
}
