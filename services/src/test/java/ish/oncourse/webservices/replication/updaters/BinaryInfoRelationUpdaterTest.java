package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BinaryInfoRelationUpdaterTest extends ServiceTest {
	private static Logger logger = LogManager.getLogger();

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
		InputStream st = BinaryInfoRelationUpdaterTest.class.getClassLoader().getResourceAsStream(
			"ish/oncourse/webservices/replication/builders/oncourseDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource();
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testCorrectRelations(){

		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), Application.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), Certificate.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), Contact.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), Course.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), CourseClass.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), Enrolment.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), Invoice.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), Room.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), Session.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), Site.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), Student.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), Tag.class);
		testCorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), Tutor.class);
	}

	@Test
	public void testIncorrectRelations(){

		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME), getMessage(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.CONTACT_ENTITY_NAME), getMessage(AbstractWillowUpdater.CONTACT_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.COURSE_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME), getMessage(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.INVOICE_ENTITY_NAME), getMessage(AbstractWillowUpdater.INVOICE_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.ROOM_ENTITY_NAME), getMessage(AbstractWillowUpdater.ROOM_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.SESSION_ENTITY_NAME), getMessage(AbstractWillowUpdater.SESSION_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.SITE_ENTITY_NAME), getMessage(AbstractWillowUpdater.SITE_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.STUDENT_ENTITY_NAME), getMessage(AbstractWillowUpdater.STUDENT_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.TUTOR_ENTITY_NAME), getMessage(AbstractWillowUpdater.TUTOR_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.TAG_ENTITY_NAME), getMessage(AbstractWillowUpdater.TAG_ENTITY_NAME));
		testIncorrectRelation(prepareV21Updater(), prepareV21Stub(AbstractWillowUpdater.APPLICATION_ENTITY_NAME), getMessage(AbstractWillowUpdater.APPLICATION_ENTITY_NAME));
		//illegal entity
		testIncorrectRelation(prepareV21Updater(),
				prepareV21Stub(PaymentOut.class.getSimpleName()),
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

	private ish.oncourse.webservices.replication.v21.updaters.BinaryInfoRelationUpdater prepareV21Updater() {
		return new ish.oncourse.webservices.replication.v21.updaters.BinaryInfoRelationUpdater();
	}

	private ish.oncourse.webservices.v21.stubs.replication.BinaryInfoRelationStub prepareV21Stub(String entityName) {
		ish.oncourse.webservices.v21.stubs.replication.BinaryInfoRelationStub stub = new ish.oncourse.webservices.v21.stubs.replication.BinaryInfoRelationStub();
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
