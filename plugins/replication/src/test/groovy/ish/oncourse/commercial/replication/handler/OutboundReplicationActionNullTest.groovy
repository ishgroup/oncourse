/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.commercial.replication.services.IAngelQueueService
import ish.oncourse.server.ICayenneService
import ish.oncourse.webservices.soap.v22.ReplicationPortType
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v22.stubs.replication.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

/**
 */
@CompileStatic
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
				"ish/oncourse/commercial/replication/handler/actionNullDataSet.xml")

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
