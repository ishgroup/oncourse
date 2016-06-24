package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

public class BinaryInfoRelationUpdaterTest extends ServiceTest {
	private static Logger logger = LogManager.getLogger();

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
		InputStream st = BinaryInfoRelationUpdaterTest.class.getClassLoader().getResourceAsStream(
			"ish/oncourse/webservices/replication/builders/oncourseDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testCorrectRelations(){

		//v10
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), Application.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), Certificate.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), Contact.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), Course.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), CourseClass.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), Enrolment.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), Invoice.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), Room.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), Session.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), Site.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), Student.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), Tag.class);
		testCorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), Tutor.class);

		//v11
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), Application.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), Certificate.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), Contact.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), Course.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), CourseClass.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), Enrolment.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), Invoice.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), Room.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), Session.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), Site.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), Student.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), Tag.class);
		testCorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), Tutor.class);
		
		//v12
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), Application.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), Certificate.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), Contact.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), Course.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), CourseClass.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), Enrolment.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), Invoice.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), Room.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), Session.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), Site.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), Student.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), Tag.class);
		testCorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), Tutor.class);
		
		//v13
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), Application.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), Certificate.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), Contact.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), Course.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), CourseClass.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), Enrolment.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), Invoice.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), Room.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), Session.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), Site.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), Student.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), Tag.class);
		testCorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), Tutor.class);
	}

	@Test
	public void testIncorrectRelations(){

		//V10
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), getMessage(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), getMessage(AbstractWillowUpdater.CONTACT_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), getMessage(AbstractWillowUpdater.INVOICE_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), getMessage(AbstractWillowUpdater.ROOM_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), getMessage(AbstractWillowUpdater.SESSION_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), getMessage(AbstractWillowUpdater.SITE_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.STUDENT_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), getMessage(AbstractWillowUpdater.TUTOR_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), getMessage(AbstractWillowUpdater.TAG_ENTITY_NAME));
		testIncorrectRelation(prepareV10Updater(), prepareV10Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), getMessage(AbstractWillowUpdater.APPLICATION_ENTITY_NAME));
		//illegal entity
		testIncorrectRelation(prepareV10Updater(),
				prepareV10Stub(PaymentOut.class.getSimpleName()),
				getMessageForIllegalEntity(PaymentOut.class.getSimpleName()));

		//V11
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), getMessage(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), getMessage(AbstractWillowUpdater.CONTACT_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), getMessage(AbstractWillowUpdater.INVOICE_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), getMessage(AbstractWillowUpdater.ROOM_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), getMessage(AbstractWillowUpdater.SESSION_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), getMessage(AbstractWillowUpdater.SITE_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.STUDENT_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), getMessage(AbstractWillowUpdater.TUTOR_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), getMessage(AbstractWillowUpdater.TAG_ENTITY_NAME));
		testIncorrectRelation(prepareV11Updater(), prepareV11Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), getMessage(AbstractWillowUpdater.APPLICATION_ENTITY_NAME));
		//illegal entity
		testIncorrectRelation(prepareV11Updater(),
				prepareV11Stub(PaymentOut.class.getSimpleName()),
				getMessageForIllegalEntity(PaymentOut.class.getSimpleName()));

		//V12
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), getMessage(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), getMessage(AbstractWillowUpdater.CONTACT_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), getMessage(AbstractWillowUpdater.INVOICE_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), getMessage(AbstractWillowUpdater.ROOM_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), getMessage(AbstractWillowUpdater.SESSION_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), getMessage(AbstractWillowUpdater.SITE_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.STUDENT_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), getMessage(AbstractWillowUpdater.TUTOR_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), getMessage(AbstractWillowUpdater.TAG_ENTITY_NAME));
		testIncorrectRelation(prepareV12Updater(), prepareV12Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), getMessage(AbstractWillowUpdater.APPLICATION_ENTITY_NAME));
		//illegal entity
		testIncorrectRelation(prepareV12Updater(),
				prepareV12Stub(PaymentOut.class.getSimpleName()),
				getMessageForIllegalEntity(PaymentOut.class.getSimpleName()));

		//V13
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), getMessage(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), getMessage(AbstractWillowUpdater.CONTACT_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), getMessage(AbstractWillowUpdater.INVOICE_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), getMessage(AbstractWillowUpdater.ROOM_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), getMessage(AbstractWillowUpdater.SESSION_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), getMessage(AbstractWillowUpdater.SITE_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.STUDENT_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), getMessage(AbstractWillowUpdater.TUTOR_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), getMessage(AbstractWillowUpdater.TAG_ENTITY_NAME));
		testIncorrectRelation(prepareV13Updater(), prepareV13Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), getMessage(AbstractWillowUpdater.APPLICATION_ENTITY_NAME));
		//illegal entity
		testIncorrectRelation(prepareV13Updater(),
				prepareV13Stub(PaymentOut.class.getSimpleName()),
				getMessageForIllegalEntity(PaymentOut.class.getSimpleName()));
	}
	
	private <U extends AbstractWillowUpdater, S extends GenericReplicationStub> void testCorrectRelation(U updater, S stub, Class eClass) {
		testCorrectRelation(updater, stub, eClass, false);
	}
	
	private <U extends AbstractWillowUpdater, S extends GenericReplicationStub> void testCorrectRelation(U updater, S stub, Class eClass, boolean isV6) {
		ObjectContext context = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation relation = prepareRelation(context);
			updater.updateEntity(stub, relation, prepareRelationShipCallback(context, eClass, isV6));
			assertNotNull("Entity identifier should not be empty", relation.getEntityIdentifier());
			assertNotNull("Entity willowid should not be empty", relation.getEntityWillowId());
			if (isV6) {
				assertNotNull("Binary info should not be empty", relation.getBinaryInfo());
				assertNotNull("Binary info id should not be empty", relation.getBinaryInfo().getId());
			} else {
				assertNotNull("Document should not be empty", relation.getDocument());
				assertNotNull("Document id should not be empty", relation.getDocument().getId());
				assertNotNull("DocumentVersion should not be empty", relation.getDocumentVersion());
				assertNotNull("DocumentVersion id should not be empty", relation.getDocumentVersion().getId());
			}
		} catch (UpdaterException e) {
			logger.catching(e);
			assertTrue(e.getMessage(), false);
		} finally {
			context.rollbackChanges();
		}
	}

	private RelationShipCallback prepareRelationShipCallback(final ObjectContext context, final Class entityClass, final boolean isV6) {
		return new RelationShipCallback() {
			@Override
			public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
				if (clazz == entityClass && entityId == 1l) {
					Object object = ObjectSelect.query(entityClass).selectOne(context);
					assertNotNull(String.format("%s should not be null", clazz.getSimpleName()), object);
					return (M) object;
				}
				if (isV6) {
					if (clazz == BinaryInfo.class && entityId == 1l) {
						BinaryInfo info = ObjectSelect.query(BinaryInfo.class).selectOne(context);
						assertNotNull("BinaryInfo should not be null", info);
						return (M) info;
					}
				} else {
					if (clazz == Document.class && entityId == 1l) {
						Document info = ObjectSelect.query(Document.class).selectOne(context);
						assertNotNull("Document should not be null", info);
						return (M) info;
					}
					if (clazz == DocumentVersion.class && entityId == 1l) {
						DocumentVersion info = ObjectSelect.query(DocumentVersion.class).selectOne(context);
						assertNotNull("DocumentVersion should not be null", info);
						return (M) info;
					}
				}

				return null;
			}
		};
	}

	private <U extends AbstractWillowUpdater, S extends GenericReplicationStub> void testIncorrectRelation(U updater, S stub, String message) {

		final ObjectContext context = getService(ICayenneService.class).newContext();
		try {
			updater.updateEntity(stub, prepareRelation(context), returnDummyRelationShipCallback(context));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase(message));
		} finally {
			context.rollbackChanges();
		}
	}
	
	private BinaryInfoRelation prepareRelation(ObjectContext context) {
		BinaryInfoRelation entity = context.newObject(BinaryInfoRelation.class);
		College college = context.newObject(College.class);
		entity.setCollege(college);
		return entity;
	}
	
	private ish.oncourse.webservices.replication.v10.updaters.BinaryInfoRelationUpdater prepareV10Updater() {
		return new ish.oncourse.webservices.replication.v10.updaters.BinaryInfoRelationUpdater();
	}

	private ish.oncourse.webservices.v10.stubs.replication.BinaryInfoRelationStub prepareV10Stub(String entityName) {
		ish.oncourse.webservices.v10.stubs.replication.BinaryInfoRelationStub stub = new ish.oncourse.webservices.v10.stubs.replication.BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setDocumentId(1l);
		stub.setDocumentVersionId(1l);
		stub.setEntityName(entityName);
		return stub;
	}


	private ish.oncourse.webservices.replication.v11.updaters.BinaryInfoRelationUpdater prepareV11Updater() {
		return new ish.oncourse.webservices.replication.v11.updaters.BinaryInfoRelationUpdater();
	}

	private ish.oncourse.webservices.v11.stubs.replication.BinaryInfoRelationStub prepareV11Stub(String entityName) {
		ish.oncourse.webservices.v11.stubs.replication.BinaryInfoRelationStub stub = new ish.oncourse.webservices.v11.stubs.replication.BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setDocumentId(1l);
		stub.setDocumentVersionId(1l);
		stub.setEntityName(entityName);
		return stub;
	}

	private ish.oncourse.webservices.replication.v12.updaters.BinaryInfoRelationUpdater prepareV12Updater() {
		return new ish.oncourse.webservices.replication.v12.updaters.BinaryInfoRelationUpdater();
	}

	private ish.oncourse.webservices.v12.stubs.replication.BinaryInfoRelationStub prepareV12Stub(String entityName) {
		ish.oncourse.webservices.v12.stubs.replication.BinaryInfoRelationStub stub = new ish.oncourse.webservices.v12.stubs.replication.BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setDocumentId(1l);
		stub.setDocumentVersionId(1l);
		stub.setEntityName(entityName);
		return stub;
	}

	private ish.oncourse.webservices.replication.v13.updaters.BinaryInfoRelationUpdater prepareV13Updater() {
		return new ish.oncourse.webservices.replication.v13.updaters.BinaryInfoRelationUpdater();
	}

	private ish.oncourse.webservices.v13.stubs.replication.BinaryInfoRelationStub prepareV13Stub(String entityName) {
		ish.oncourse.webservices.v13.stubs.replication.BinaryInfoRelationStub stub = new ish.oncourse.webservices.v13.stubs.replication.BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setDocumentId(1l);
		stub.setDocumentVersionId(1l);
		stub.setEntityName(entityName);
		return stub;
	}


	private RelationShipCallback returnDummyRelationShipCallback(final ObjectContext objectContext) {
		return new RelationShipCallback() {
			@Override
			public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
				return null;
			}
		};
	}
	
	private String getMessage(String simpleName) {
		return String.format("Unable to load related entity %s for angelid 1 or this entity not presented in transaction group", simpleName);
	}
	
	private String getMessageForIllegalEntity(String simpleName) {
		return String.format("Unexpected related entity with type %S and angelid 1", simpleName);
	}
}
