/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.services

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Outcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static junit.framework.Assert.assertEquals
import static org.mockito.Mockito.*

@CompileStatic
class DeduperFactoryTest {

	public List<QueuedRecord> currentBatch

	@BeforeEach
	void setup() throws Exception {

		currentBatch = new LinkedList<>()

		QueuedTransaction transact1 = mock(QueuedTransaction.class)
		when(transact1.getTransactionKey()).thenReturn("transact1")

		QueuedRecord attendanceCretate1 = mock(QueuedRecord.class)
		when(attendanceCretate1.getTableName()).thenReturn(Attendance.class.getSimpleName())
		when(attendanceCretate1.getForeignRecordId()).thenReturn(1L)
		when(attendanceCretate1.getAction()).thenReturn(QueuedRecordAction.CREATE)
		when(attendanceCretate1.getQueuedTransaction()).thenReturn(transact1)


		QueuedRecord attendanceCretate2 = mock(QueuedRecord.class)
		when(attendanceCretate2.getTableName()).thenReturn(Attendance.class.getSimpleName())
		when(attendanceCretate2.getForeignRecordId()).thenReturn(2L)
		when(attendanceCretate2.getAction()).thenReturn(QueuedRecordAction.CREATE)
		when(attendanceCretate2.getQueuedTransaction()).thenReturn(transact1)

		QueuedRecord outcomeCretate1 = mock(QueuedRecord.class)
		when(outcomeCretate1.getTableName()).thenReturn(Outcome.class.getSimpleName())
		when(outcomeCretate1.getForeignRecordId()).thenReturn(1L)
		when(outcomeCretate1.getAction()).thenReturn(QueuedRecordAction.CREATE)
		when(outcomeCretate1.getQueuedTransaction()).thenReturn(transact1)

		QueuedRecord outcomeCretate2 = mock(QueuedRecord.class)
		when(outcomeCretate2.getTableName()).thenReturn(Outcome.class.getSimpleName())
		when(outcomeCretate2.getForeignRecordId()).thenReturn(2L)
		when(outcomeCretate2.getAction()).thenReturn(QueuedRecordAction.CREATE)
		when(outcomeCretate2.getQueuedTransaction()).thenReturn(transact1)

		QueuedTransaction transact2 = mock(QueuedTransaction.class)
		when(transact2.getTransactionKey()).thenReturn("transact2")

		QueuedRecord attendanceDelete1 = mock(QueuedRecord.class)
		when(attendanceDelete1.getTableName()).thenReturn(Attendance.class.getSimpleName())
		when(attendanceDelete1.getForeignRecordId()).thenReturn(1L)
		when(attendanceDelete1.getAction()).thenReturn(QueuedRecordAction.DELETE)
		when(attendanceDelete1.getQueuedTransaction()).thenReturn(transact2)


		QueuedRecord attendanceDelete2 = mock(QueuedRecord.class)
		when(attendanceDelete2.getTableName()).thenReturn(Attendance.class.getSimpleName())
		when(attendanceDelete2.getForeignRecordId()).thenReturn(2L)
		when(attendanceDelete2.getAction()).thenReturn(QueuedRecordAction.DELETE)
		when(attendanceDelete2.getQueuedTransaction()).thenReturn(transact2)

		QueuedRecord outcomeDelete1 = mock(QueuedRecord.class)
		when(outcomeDelete1.getTableName()).thenReturn(Outcome.class.getSimpleName())
		when(outcomeDelete1.getForeignRecordId()).thenReturn(1L)
		when(outcomeDelete1.getAction()).thenReturn(QueuedRecordAction.DELETE)
		when(outcomeDelete1.getQueuedTransaction()).thenReturn(transact2)

		QueuedRecord outcomeDelete2 = mock(QueuedRecord.class)
		when(outcomeDelete2.getTableName()).thenReturn(Outcome.class.getSimpleName())
		when(outcomeDelete2.getForeignRecordId()).thenReturn(2L)
		when(outcomeDelete2.getAction()).thenReturn(QueuedRecordAction.DELETE)
		when(outcomeDelete2.getQueuedTransaction()).thenReturn(transact2)

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
		DedupperFactory factory = DedupperFactory.valueOf(currentBatch, mock(ISHDataContext.class))
		List<DFADedupper> deduppers = factory.assembleDeduppers()

		assertEquals(4, deduppers.size())

		deduppers.each { DFADedupper d ->
			assertEquals(1, d.duplicates().size())
			assertEquals(QueuedRecordAction.CREATE, d.duplicates().get(0).getAction())
			assertEquals(QueuedRecordAction.DELETE, d.deDuppedRecord().getAction())
		}
	}
}
