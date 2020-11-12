/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.handler

import ish.CayenneIshTestCase
import ish.oncourse.cayenne.QueuedRecordAction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.QueuedRecord
import ish.oncourse.server.cayenne.QueuedTransaction
import ish.oncourse.server.modules.ISoapPortLocator
import ish.oncourse.server.replication.builders.IAngelStubBuilder
import ish.oncourse.server.replication.services.IAngelQueueService
import ish.oncourse.webservices.soap.v22.ReplicationPortType
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v22.stubs.replication.ContactStub
import ish.oncourse.webservices.v22.stubs.replication.HollowStub
import ish.oncourse.webservices.v22.stubs.replication.ReplicatedRecord
import ish.oncourse.webservices.v22.stubs.replication.ReplicationRecords
import ish.oncourse.webservices.v22.stubs.replication.ReplicationResult
import ish.oncourse.webservices.v22.stubs.replication.Status
import ish.oncourse.webservices.v22.stubs.replication.TransactionGroup
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.ITable
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 */
class OutboundReplicationActionNullTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

	private IAngelQueueService queueService

	private IAngelStubBuilder stubBuilder

	@Before
	void setup() throws Exception {
		wipeTables()
		this.cayenneService = injector.getInstance(ICayenneService.class)
		this.stubBuilder = injector.getInstance(IAngelStubBuilder.class)
		this.queueService = injector.getInstance(IAngelQueueService.class)
	}

	@Test
	void testQueuedRecordWithNullAction() throws Exception {
		InputStream st = OutboundReplicationHandlerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/server/replication/handler/actionNullDataSet.xml")

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
		executeDatabaseOperation(dataSet)

		ObjectContext dataContext = cayenneService.getSharedContext()
		QueuedTransaction transaction = SelectById.query(QueuedTransaction.class, 3000).selectOne(dataContext)

		assertNotNull("Check transaction.", transaction)
		List<QueuedRecord> queuedRecords = transaction.getQueuedRecords()
		assertEquals("Expecting 5 queuedRecords.", 5, queuedRecords.size())

		ISoapPortLocator soapLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {
					@Override
					ReplicationResult sendRecords(ReplicationRecords records) {
						assertNotNull(records)
						assertEquals("Expecting only one record.", records.getGroups().get(0).getGenericAttendanceOrBinaryDataOrBinaryInfo().size(), 1)

						List<GenericReplicationStub> stubs = new ArrayList<>()

						for (TransactionGroup group : records.getGroups()) {
							for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
								if (stub instanceof ContactStub) {
									stubs.add(stub)
								}
							}
						}

						assertTrue("Excecting only one ContactStub.", stubs.size() == 1)

						try {
							List<QueuedRecord> queuedRecordsList = ObjectSelect.query(QueuedRecord)
									.where(QueuedRecord.TABLE_NAME.eq("Contact"))
									.select(dataContext)
							assertEquals("Expecting only one queued record.",
									1, queuedRecordsList.size())
							assertEquals("Expecting action UPDATE.",
									QueuedRecordAction.UPDATE, queuedRecordsList.get(0).getAction())
						} catch (Exception e) {
							fail("Failed because of database error.")
						}

						return successResponse(records)
					}
				}
			}
		}

		OutboundReplicationHandler handler = new OutboundReplicationHandler(queueService, cayenneService, soapLocator, stubBuilder)
		handler.replicate()

		assertEquals("All queued records should be deleted.",
				0,
				ObjectSelect.query(QueuedRecord)
						.where(QueuedRecord.TABLE_NAME.eq("Contact"))
						.selectCount(dataContext))
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
