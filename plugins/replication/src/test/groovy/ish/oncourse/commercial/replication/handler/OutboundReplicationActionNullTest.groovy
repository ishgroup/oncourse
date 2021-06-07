/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.commercial.replication.services.IAngelQueueService
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v23.stubs.replication.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/commercial/replication/handler/actionNullDataSet.xml')
class OutboundReplicationActionNullTest extends TestWithDatabase {
	

	@Test
	void testQueuedRecordWithNullAction() throws Exception {


		ObjectContext dataContext = cayenneService.getSharedContext()
		QueuedTransaction transaction = SelectById.query(QueuedTransaction.class, 3000).selectOne(dataContext)

		assertNotNull( transaction,"Check transaction.")
		List<QueuedRecord> queuedRecords = transaction.getQueuedRecords()
		assertEquals( 5, queuedRecords.size(),"Expecting 5 queuedRecords.")

		ISoapPortLocator soapLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {
					@Override
					ReplicationResult sendRecords(ReplicationRecords records) {
						assertNotNull(records)
						assertEquals(records.getGroups().get(0).getGenericAttendanceOrBinaryDataOrBinaryInfo().size(), 1,"Expecting only one record.")

						List<GenericReplicationStub> stubs = new ArrayList<>()

						for (TransactionGroup group : records.getGroups()) {
							for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
								if (stub instanceof ContactStub) {
									stubs.add(stub)
								}
							}
						}

						assertTrue(stubs.size() == 1,"Excecting only one ContactStub.")

						try {
							List<QueuedRecord> queuedRecordsList = ObjectSelect.query(QueuedRecord)
									.where(QueuedRecord.TABLE_NAME.eq("Contact"))
									.select(dataContext)
							assertEquals(
									1, queuedRecordsList.size(),"Expecting only one queued record.")
							assertEquals(
									QueuedRecordAction.UPDATE, queuedRecordsList.get(0).getAction(),"Expecting action UPDATE.")
						} catch (Exception e) {
							fail("Failed because of database error.")
						}

						return successResponse(records)
					}
				}
			}
		}

		OutboundReplicationHandler handler = new OutboundReplicationHandler(injector.getInstance(IAngelQueueService.class), cayenneService, soapLocator, injector.getInstance(IAngelStubBuilder.class))
		handler.replicate()

		assertEquals(
				0,
				ObjectSelect.query(QueuedRecord)
						.where(QueuedRecord.TABLE_NAME.eq("Contact"))
						.selectCount(dataContext),"All queued records should be deleted.")
	}

	private ReplicationResult successResponse(ReplicationRecords records) {
		ReplicationResult result = new ReplicationResult()

		for (TransactionGroup group : records.getGroups()) {
			for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
				ReplicatedRecord r = new ReplicatedRecord()
				r.setStatus(Status.SUCCESS)

				HollowStub hollowStub = new HollowStub()
				hollowStub.setEntityIdentifier(stub.getEntityIdentifier())
				hollowStub.setAngelId(stub.getAngelId())

				Date today = new Date()

				hollowStub.setModified(today)
				hollowStub.setCreated(today)

				r.setStub(hollowStub)
				r.getStub().setWillowId(1L)
				result.getReplicatedRecord().add(r)
			}
		}

		return result
	}
}
