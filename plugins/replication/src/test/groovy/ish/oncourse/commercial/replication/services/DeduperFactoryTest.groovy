/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.services

import ish.oncourse.cayenne.QueuedRecordAction
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.QueuedRecord
import ish.oncourse.server.cayenne.QueuedTransaction
import org.junit.Before
import org.junit.Test

import static junit.framework.Assert.assertEquals

class DeduperFactoryTest {

	public List<QueuedRecord> currentBatch

	@Before
	void setup() throws Exception {

		currentBatch = new LinkedList<>()

		QueuedTransaction transact1 = org.mockito.Mockito.mock(QueuedTransaction.class)
		org.mockito.Mockito.when(transact1.getTransactionKey()).thenReturn("transact1")

		QueuedRecord attendanceCretate1 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(attendanceCretate1.getTableName()).thenReturn(Attendance.class.getSimpleName())
		org.mockito.Mockito.when(attendanceCretate1.getForeignRecordId()).thenReturn(1L)
		org.mockito.Mockito.when(attendanceCretate1.getAction()).thenReturn(QueuedRecordAction.CREATE)
		org.mockito.Mockito.when(attendanceCretate1.getQueuedTransaction()).thenReturn(transact1)


		QueuedRecord attendanceCretate2 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(attendanceCretate2.getTableName()).thenReturn(Attendance.class.getSimpleName())
		org.mockito.Mockito.when(attendanceCretate2.getForeignRecordId()).thenReturn(2L)
		org.mockito.Mockito.when(attendanceCretate2.getAction()).thenReturn(QueuedRecordAction.CREATE)
		org.mockito.Mockito.when(attendanceCretate2.getQueuedTransaction()).thenReturn(transact1)

		QueuedRecord outcomeCretate1 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(outcomeCretate1.getTableName()).thenReturn(Outcome.class.getSimpleName())
		org.mockito.Mockito.when(outcomeCretate1.getForeignRecordId()).thenReturn(1L)
		org.mockito.Mockito.when(outcomeCretate1.getAction()).thenReturn(QueuedRecordAction.CREATE)
		org.mockito.Mockito.when(outcomeCretate1.getQueuedTransaction()).thenReturn(transact1)

		QueuedRecord outcomeCretate2 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(outcomeCretate2.getTableName()).thenReturn(Outcome.class.getSimpleName())
		org.mockito.Mockito.when(outcomeCretate2.getForeignRecordId()).thenReturn(2L)
		org.mockito.Mockito.when(outcomeCretate2.getAction()).thenReturn(QueuedRecordAction.CREATE)
		org.mockito.Mockito.when(outcomeCretate2.getQueuedTransaction()).thenReturn(transact1)

		QueuedTransaction transact2 = org.mockito.Mockito.mock(QueuedTransaction.class)
		org.mockito.Mockito.when(transact2.getTransactionKey()).thenReturn("transact2")

		QueuedRecord attendanceDelete1 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(attendanceDelete1.getTableName()).thenReturn(Attendance.class.getSimpleName())
		org.mockito.Mockito.when(attendanceDelete1.getForeignRecordId()).thenReturn(1L)
		org.mockito.Mockito.when(attendanceDelete1.getAction()).thenReturn(QueuedRecordAction.DELETE)
		org.mockito.Mockito.when(attendanceDelete1.getQueuedTransaction()).thenReturn(transact2)


		QueuedRecord attendanceDelete2 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(attendanceDelete2.getTableName()).thenReturn(Attendance.class.getSimpleName())
		org.mockito.Mockito.when(attendanceDelete2.getForeignRecordId()).thenReturn(2L)
		org.mockito.Mockito.when(attendanceDelete2.getAction()).thenReturn(QueuedRecordAction.DELETE)
		org.mockito.Mockito.when(attendanceDelete2.getQueuedTransaction()).thenReturn(transact2)

		QueuedRecord outcomeDelete1 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(outcomeDelete1.getTableName()).thenReturn(Outcome.class.getSimpleName())
		org.mockito.Mockito.when(outcomeDelete1.getForeignRecordId()).thenReturn(1L)
		org.mockito.Mockito.when(outcomeDelete1.getAction()).thenReturn(QueuedRecordAction.DELETE)
		org.mockito.Mockito.when(outcomeDelete1.getQueuedTransaction()).thenReturn(transact2)

		QueuedRecord outcomeDelete2 = org.mockito.Mockito.mock(QueuedRecord.class)
		org.mockito.Mockito.when(outcomeDelete2.getTableName()).thenReturn(Outcome.class.getSimpleName())
		org.mockito.Mockito.when(outcomeDelete2.getForeignRecordId()).thenReturn(2L)
		org.mockito.Mockito.when(outcomeDelete2.getAction()).thenReturn(QueuedRecordAction.DELETE)
		org.mockito.Mockito.when(outcomeDelete2.getQueuedTransaction()).thenReturn(transact2)

		currentBatch.add(attendanceCretate1)
		currentBatch.add(attendanceCretate2)
		currentBatch.add(outcomeCretate1)
		currentBatch.add(outcomeCretate2)
		currentBatch.add(attendanceDelete1)
		currentBatch.add(attendanceDelete2)
		currentBatch.add(outcomeDelete1)
		currentBatch.add(outcomeDelete2)
	}
	
	@Test
	void testAttendancesDedupp() {
		DedupperFactory factory = DedupperFactory.valueOf(currentBatch, org.mockito.Mockito.mock(ISHDataContext.class))
		List<DFADedupper> deduppers = factory.assembleDeduppers()

		assertEquals(4, deduppers.size())

		deduppers.each { DFADedupper d ->
			assertEquals(1, d.duplicates().size())
			assertEquals(QueuedRecordAction.CREATE, d.duplicates().get(0).getAction())
			assertEquals(QueuedRecordAction.DELETE, d.deDuppedRecord().getAction())
		}
	}
}
