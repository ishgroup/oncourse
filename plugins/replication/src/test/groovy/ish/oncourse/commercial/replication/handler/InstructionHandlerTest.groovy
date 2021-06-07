/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import ish.oncourse.webservices.v23.stubs.replication.InstructionStub
import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ejbql.EJBQLException
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectQuery
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/commercial/replication/handler/instructionHandlerTestDataSet.xml')
class InstructionHandlerTest extends TestWithDatabase {

	@Test
	void testNoInstructions() throws Exception {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						return new ArrayList<>()
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						fail("No execution confirmation should appears for empty instructions list")
					}
				}
			}
		}
		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()
		long count = ObjectSelect.query(QueuedRecord).selectCount(cayenneService.getNewContext())
		assertEquals(0, count, "Expecting zero queued records for entity.")
	}

	@Test
	void testInstructAll() {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s", InstructionHandler.QUEUE_ARGUMENT, "*"))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("The ability to replicate ALL the entities is not supported yet", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s", InstructionHandler.QUEUE_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Incorrect format of the Instruction message", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage("qwerty:qwerty")
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("The instruction \"qwerty\"is unsupported", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()
	}

	@Test
	void testInstructAttendancesForClass() throws Exception {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Queue Attendances for Class supported only with defined class angelid.", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test get attendancies for course class with id=1
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		ObjectContext context = this.cayenneService.getNewContext()
		ObjectSelect<QueuedRecord> queuedRecordObjectSelect = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3));
		long count = queuedRecordObjectSelect.selectCount(context)
		assertEquals( 16, count, "Expecting 16 queued attendancies for entity.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test get attendancies for course class with id=2
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 8,
				queuedRecordObjectSelect.selectCount(context), "Expecting 8 queued attendancies for entity.")

		records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test get attendancies for course class with id=3
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:3", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test get attendancies for course class with unexisted id=10
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:10", InstructionHandler.QUEUE_ARGUMENT,
								InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(
							String.format("There is no such record with entityIdentifier = %s with angelid = %s",
								"CourseClass", "10"), arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test get attendancies for course class with incorrect literal id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:qwerty", InstructionHandler.QUEUE_ARGUMENT,
								InstructionHandler.ATTENDANCE_FOR_CLASS_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						fail("EJBQLException should be thrown here")
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		try {
			handler.replicate()
			fail("EJBQLException should be thrown here")
		} catch (CayenneRuntimeException t) {
			assertTrue(t.getMessage().contains("Query exception."))
			assertTrue(t.getCause() instanceof EJBQLException)
			assertTrue(t.getCause().getMessage().contains("Invalid identification variable: qwerty"))
		} catch (Throwable t) {
			fail("CayenneRuntimeException should be thrown here")
		}
	}

	@Test
	void testInstructAttendancesForEnrolment() throws Exception {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Queue Attendances for Enrolment supported only with defined enrolment angelid.",
							arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test get attendancies for active enrolment with id=1
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		ObjectContext context = this.cayenneService.getNewContext()

		ObjectSelect<QueuedRecord> queuedRecordObjectSelect = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
		assertEquals(3,
				queuedRecordObjectSelect.selectCount(context),"Expecting 3 queued attendancies for entity.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//test get attendancies for active enrolment with id=2
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(3,
				queuedRecordObjectSelect.selectCount(context), "Expecting 3 queued attendancies for entity.")

		records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test get attendancies for refunded enrolment with id=22
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:22", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//test get attendancies for enrolment with unexisted id=100
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:100", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(
							String.format("There is no such record with entityIdentifier = %s with angelid = %s",
								InstructionHandler.ENROLMENT_ENTITY_NAME, "100"), arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test get attendancies for enrolment with incorrect literal id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:qwerty", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ATTENDANCE_FOR_ENROLMENT_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						fail("EJBQLException should be thrown here")
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		try {
			handler.replicate()
			fail("EJBQLException should be thrown here")
		} catch (CayenneRuntimeException t) {
			assertTrue(t.getMessage().contains("Query exception."))
			assertTrue(t.getCause() instanceof EJBQLException)
			assertTrue(t.getCause().getMessage().contains("Invalid identification variable: qwerty"))
		} catch (Throwable t) {
			fail("CayenneRuntimeException should be thrown here")
		}
	}

	@Test
	void testInstructWithRelationships() throws Exception {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT, "qwerty"))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Instruct with relationships supported only for PaymentIn,Invoice and Enrolment entities with defined angelids.", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
								InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Instruct with relationships supported only for PaymentIn,Invoice and Enrolment entities with defined angelids.", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
								InstructionHandler.INVOICE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Instruct with relationships supported only for PaymentIn,Invoice and Enrolment entities with defined angelids.", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
								InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("Instruct with relationships supported only for PaymentIn,Invoice and Enrolment entities with defined angelids.", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test valid cases

		//instruct enrolment without payment but with discount
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		ObjectContext context = this.cayenneService.getNewContext()

		ObjectSelect<QueuedRecord> queuedRecordObjectSelect = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
		assertEquals( 13,
				queuedRecordObjectSelect.selectCount(context),"Expecting 13 queued entities for request.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("Contact".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("InvoiceLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("InvoiceLineDiscount".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//instruct enrolment with payment
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
								InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
								InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(15,
				queuedRecordObjectSelect.selectCount(context), "Expecting 15 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(4L) || record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Contact".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("InvoiceLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct enrolment with payment for few invoices
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:3", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(16,
				queuedRecordObjectSelect.selectCount(context), "Expecting 16 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("Contact".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else if ("InvoiceLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct enrolment with invoices with few invoice lines
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:5", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(16,
				queuedRecordObjectSelect.selectCount(context),"Expecting 16 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("Contact".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(3L))
			} else if ("InvoiceLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(5L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(5L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(4L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct invoice with payment for few invoices
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:3", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.INVOICE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(16,
				queuedRecordObjectSelect.selectCount(context), "Expecting 16 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("Contact".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else if ("InvoiceLine".equals(record.getTableName())) {
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct invoice with payment
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.INVOICE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(15,
				queuedRecordObjectSelect.selectCount(context), "Expecting 15 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(4L) || record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Contact".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("InvoiceLine".equals(record.getTableName())) {
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct payment with multiple invoices
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(28,
				queuedRecordObjectSelect.selectCount(context), "Expecting 28 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) ||
					record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L) ||
						record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Contact".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else if ("InvoiceLine".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("Invoice".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("Course".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//instruct payment with single invoice but multiple invoicelines
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s:3", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT,
							InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 25, queuedRecordObjectSelect.selectCount(context),"Expecting 25 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("Room".equals(record.getTableName())) {
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("Session".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L) ||
					record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L) ||
						record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Contact".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(3L))
			} else if ("InvoiceLine".equals(record.getTableName())) {
				assertTrue(record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Student".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("Enrolment".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(5L) || record.getForeignRecordId().equals(6L))
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(5L), record.getForeignRecordId())
			} else if ("Course".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(4L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(3L), record.getForeignRecordId())
			} else if ("CourseClass".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(1L) || record.getForeignRecordId().equals(2L))
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0, queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")
	}

	@Test
	void testInstructIncorrectEntityName() {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT, "qwerty"))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("There are no entity with name \"qwerty\"", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()
	}

	@Test
	void testInstructPaymentInLines() throws Exception {
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(String.format("Illegal angelId %s passed for request %s entity", "*",
							InstructionHandler.PAYMENT_IN_ENTITY_NAME), arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_LINE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(String.format("Illegal angelId %s passed for request %s entity", "*",
							InstructionHandler.PAYMENT_IN_LINE_ENTITY_NAME), arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		//test valid cases
		//test payment with single payment line, invoice and invoice line
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		ObjectContext context = this.cayenneService.getNewContext()

		ObjectSelect<QueuedRecord> queuedRecordObjectSelect = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
		assertEquals(4, queuedRecordObjectSelect.selectCount(context), "Expecting 4 queued entities for request.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("InvoiceLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test payment with multiple invoices
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(7,
				queuedRecordObjectSelect.selectCount(context),"Expecting 7 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("InvoiceLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("Invoice".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test in transaction payment
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:4", InstructionHandler.QUEUE_ARGUMENT,
								InstructionHandler.PAYMENT_IN_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//check that payment line instruction will return the same results

		//test payment with single payment line, invoice and invoice line
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:1", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_LINE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 4,
				queuedRecordObjectSelect.selectCount(context), "Expecting 4 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("InvoiceLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("Invoice".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(1L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test payment with multiple invoices
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_LINE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 7, queuedRecordObjectSelect.selectCount(context), "Expecting 7 queued entities for request.")

		records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			assertEquals(QueuedRecordAction.UPDATE, record.getAction())
			assertEquals(getLastTransaction(context).getId(), record.getTransactionId())
			assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
			if ("InvoiceLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("Invoice".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(3L) || record.getForeignRecordId().equals(4L))
			} else if ("PaymentInLine".equals(record.getTableName())){
				assertTrue(record.getForeignRecordId().equals(2L) || record.getForeignRecordId().equals(3L))
			} else if ("PaymentIn".equals(record.getTableName())){
				assertEquals(Long.valueOf(2L), record.getForeignRecordId())
			} else {
				fail(String.format("Unexpected queued record with type %s and id= %s", record.getTableName(), record.getForeignRecordId()))
			}
		}
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//test in transaction payment
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:5", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.PAYMENT_IN_LINE_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")
	}

	@Test
	void testInstructEntities() throws Exception {
		//test instruct all enrolments
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		ObjectContext context = this.cayenneService.getNewContext()

		ObjectSelect<QueuedRecord> queuedRecordObjectSelect = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
		assertEquals( 58,
				queuedRecordObjectSelect.selectCount(context), "Expecting 58 queued enrolments.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//test instruct all enrolments with * instead of id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
								InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 58,
				queuedRecordObjectSelect.selectCount(context),"Expecting 58 queued enrolments.")

		records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals(0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test instruct active specific enrolment by id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:2", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 1,
				queuedRecordObjectSelect.selectCount(context), "Expecting 1 queued enrolment.")

		records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test instruct refunded specific enrolment by id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:22", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(1,
				queuedRecordObjectSelect.selectCount(context), "Expecting 1 queued enrolment.")

		records = context.select(recordSelectQuery)
		context.deleteObjects(records)
		context.commitChanges()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context), "Expecting zero queued records for entity.")

		//test instruct refunded enrolment by unexisting id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:222", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 0,
				queuedRecordObjectSelect.selectCount(context),"Expecting zero queued records for entity.")

		//test instruct refunded enrolment by incorrect id
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:qwerty", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.ENROLMENT_ENTITY_NAME))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertTrue(arg1.startsWith("Failed"))
						assertTrue(arg1.endsWith("Query exception."))
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()
	}

	@Test
	void testResetRetries() throws Exception {
		ObjectContext context = this.cayenneService.getNewContext()
		QueuedTransaction transaction = context.newObject(QueuedTransaction.class)
		transaction.setTransactionKey("test key")
		QueuedRecord queuedRecord = context.newObject(QueuedRecord.class)
		queuedRecord.setAction(QueuedRecordAction.UPDATE)
		queuedRecord.setForeignRecordId(1L)
		queuedRecord.setNumberOfAttempts(3)
		queuedRecord.setTableName(Enrolment.class.getSimpleName())
		queuedRecord.setQueuedTransaction(transaction)

		transaction = context.newObject(QueuedTransaction.class)
		transaction.setTransactionKey("test key 2")
		queuedRecord = context.newObject(QueuedRecord.class)
		queuedRecord.setAction(QueuedRecordAction.UPDATE)
		queuedRecord.setForeignRecordId(1L)
		queuedRecord.setNumberOfAttempts(3)
		queuedRecord.setTableName(InvoiceLine.class.getSimpleName())
		queuedRecord.setQueuedTransaction(transaction)

		transaction = context.newObject(QueuedTransaction.class)
		transaction.setTransactionKey("test key 3")
		queuedRecord = context.newObject(QueuedRecord.class)
		queuedRecord.setAction(QueuedRecordAction.UPDATE)
		queuedRecord.setForeignRecordId(1L)
		queuedRecord.setNumberOfAttempts(3)
		queuedRecord.setTableName(Invoice.class.getSimpleName())
		queuedRecord.setQueuedTransaction(transaction)

		context.commitChanges()

		ObjectSelect<QueuedRecord> queuedRecordEq3 = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.eq(3))
		assertEquals( 3,
				queuedRecordEq3.selectCount(context),"Expecting zero queued records for entity.")

		//test all queue reset
		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:*", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.RESET_RETRIES_ARGUMENT))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		InstructionHandler handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals(0,
				queuedRecordEq3.selectCount(context),"Expecting zero queued records for entity.")

		ObjectSelect<QueuedRecord> queuedRecordLt3 = ObjectSelect.query(QueuedRecord)
				.where(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
		assertEquals( 3,
				queuedRecordLt3.selectCount(context), "Expecting 3 queued records for entity.")

		SelectQuery<QueuedRecord> recordSelectQuery = SelectQuery.query(QueuedRecord.class)
		List<QueuedRecord> records = context.select(recordSelectQuery)
		for (QueuedRecord record : records) {
			record.setNumberOfAttempts(3)
		}
		context.commitChanges()
		assertEquals( 3,
				queuedRecordEq3.selectCount(context),"Expecting 3 queued records for entity.")

		//test specific table reset
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.RESET_RETRIES_ARGUMENT, Invoice.class.getSimpleName()))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals(InstructionHandler.SUCCEED_RESULT, arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()

		assertEquals( 2,
				queuedRecordEq3.selectCount(context), "Expecting zero queued records for entity.")

		assertEquals( 1,
				queuedRecordLt3.selectCount(context),"Expecting zero queued records for entity.")

		for (QueuedRecord record : records) {
			if (Invoice.class.getSimpleName().equals(record.getTableName())) {
				assertEquals(Integer.valueOf(0), record.getNumberOfAttempts())
				record.setNumberOfAttempts(3)
			}
		}
		context.commitChanges()

		assertEquals( 3,
				queuedRecordEq3.selectCount(context),"Expecting 3 queued records for entity.")

		//test illegal usage
		soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {
				return new AbstractReplicationPortType() {
					@Override
					List<InstructionStub> getInstructions() {
						InstructionStub stub = new InstructionStub()
						stub.setId(1L)
						stub.setMessage(String.format("%s:%s:%s", InstructionHandler.QUEUE_ARGUMENT,
							InstructionHandler.RESET_RETRIES_ARGUMENT, "qwerty"))
						Arrays.asList(stub)
						return Arrays.asList(stub)
					}

					@Override
					void confirmExecution(Long arg0, String arg1) {
						assertEquals("There are no entity with name \"qwerty\"", arg1)
						assertEquals(Long.valueOf(1L), arg0)
					}
				}
			}
		}

		handler = new InstructionHandler(soapPortLocator, cayenneService)
		handler.replicate()
	}

	private QueuedTransaction getLastTransaction(ObjectContext context) {
		SelectQuery<QueuedTransaction> transactionQuery = SelectQuery.query(QueuedTransaction.class)
		transactionQuery.addOrdering(QueuedTransaction.CREATED_ON.desc())

		List<QueuedTransaction> transactions = context.select(transactionQuery)

		return transactions.isEmpty() ? null : transactions.get(0)
	}
}
