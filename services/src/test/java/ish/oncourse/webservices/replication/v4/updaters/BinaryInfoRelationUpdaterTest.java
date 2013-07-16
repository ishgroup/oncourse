package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v5.stubs.replication.BinaryInfoRelationStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class BinaryInfoRelationUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(BinaryInfoRelationUpdaterTest.class.getName());

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
		InputStream st = DiscountUpdaterTest.class.getClassLoader().getResourceAsStream(
			"ish/oncourse/webservices/replication/builders/oncourseDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testCorrectTutorRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.TUTOR_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Tutor.class && entityId == 1l) {
						List<Tutor> list = objectContext.performQuery(new SelectQuery(Tutor.class));
						assertFalse("Tutors should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectStudentRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.STUDENT_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Student.class && entityId == 1l) {
						List<Student> list = objectContext.performQuery(new SelectQuery(Student.class));
						assertFalse("Students should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectSiteRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.SITE_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Site.class && entityId == 1l) {
						List<Site> list = objectContext.performQuery(new SelectQuery(Site.class));
						assertFalse("Sites should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectSessionRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.SESSION_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Session.class && entityId == 1l) {
						List<Session> list = objectContext.performQuery(new SelectQuery(Session.class));
						assertFalse("Sessions should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectRoomRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.ROOM_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Room.class && entityId == 1l) {
						List<Room> list = objectContext.performQuery(new SelectQuery(Room.class));
						assertFalse("Rooms should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectInvoiceRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.INVOICE_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Invoice.class && entityId == 1l) {
						List<Invoice> list = objectContext.performQuery(new SelectQuery(Invoice.class));
						assertFalse("Invoices should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectEnrolmentRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Enrolment.class && entityId == 1l) {
						List<Enrolment> list = objectContext.performQuery(new SelectQuery(Enrolment.class));
						assertFalse("Enrolments should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectCourseClassRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == CourseClass.class && entityId == 1l) {
						List<CourseClass> list = objectContext.performQuery(new SelectQuery(CourseClass.class));
						assertFalse("CourseClasses should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectCertificateRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Certificate.class && entityId == 1l) {
						List<Certificate> list = objectContext.performQuery(new SelectQuery(Certificate.class));
						assertFalse("Certificates should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectCourseRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.COURSE_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Course.class && entityId == 1l) {
						List<Course> list = objectContext.performQuery(new SelectQuery(Course.class));
						assertFalse("Courses should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testCorrectContactRelation() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
			new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.CONTACT_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					if (clazz == Contact.class && entityId == 1l) {
						List<Contact> list = objectContext.performQuery(new SelectQuery(Contact.class));
						assertFalse("Contacts should not be empty", list.isEmpty());
						assertTrue(list.size() == 1);
						return (M) list.get(0);
					}
					if (clazz == BinaryInfo.class && entityId == 1l) {
						List<BinaryInfo> binaryInfos = objectContext.performQuery(new SelectQuery(BinaryInfo.class));
						assertFalse("BinaryInfos should not be empty", binaryInfos.isEmpty());
						assertTrue(binaryInfos.size() == 1);
						return (M) binaryInfos.get(0);
					}
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertNotNull("Entity identifier should not be empty", entity.getEntityIdentifier());
			assertNotNull("Binary info should not be empty", entity.getBinaryInfo());
			assertNotNull("Binary info id should not be empty", entity.getBinaryInfo().getId());
			assertNotNull("Entity willowid should not be empty", entity.getEntityWillowId());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testInCorrectRelations() {
		ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater updater =
				new ish.oncourse.webservices.replication.v5.updaters.BinaryInfoRelationUpdater();
		BinaryInfoRelationStub stub = new BinaryInfoRelationStub();
		stub.setAngelId(1l);
		stub.setEntityName(AbstractWillowUpdater.CONTACT_ENTITY_NAME);
		stub.setEntityAngelId(1l);
		stub.setEntityWillowId(-1l);
		stub.setBinaryInfoId(1l);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Contact for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}

		stub.setEntityName(AbstractWillowUpdater.COURSE_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Course for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.CERTIFICATE_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Certificate for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.COURSE_CLASS_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity CourseClass for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.ENROLMENT_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Enrolment for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.INVOICE_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Invoice for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.ROOM_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Room for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.SESSION_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Session for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.SITE_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Site for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.STUDENT_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Student for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.TUTOR_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Tutor for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}
		stub.setEntityName(AbstractWillowUpdater.TAG_ENTITY_NAME);
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			assertTrue(e.getMessage().equalsIgnoreCase("Unable to load related entity Tag for angelid 1 or this entity have null willowId"));
		} finally {
			objectContext.rollbackChanges();
		}

		//test illegal entity
		stub.setEntityName(PaymentOut.class.getSimpleName());
		try {
			BinaryInfoRelation entity = objectContext.newObject(BinaryInfoRelation.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			updater.updateEntity(stub, entity, returnDummyRelationShipCallback(objectContext));
			assertTrue("Updater should throw and exception for invalid data",false);
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage().equalsIgnoreCase("Unexpected related entity with type PaymentOut and angelid 1"));
		} finally {
			objectContext.rollbackChanges();
		}
	}

	private RelationShipCallback returnDummyRelationShipCallback(final ObjectContext objectContext) {
		return new RelationShipCallback() {
			@Override
			public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
				if (clazz == Contact.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Course.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Certificate.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == CourseClass.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Enrolment.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Invoice.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Room.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Session.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Site.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Student.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Tutor.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == Tag.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == BinaryInfo.class && entityId == 1l) return objectContext.newObject(clazz);
				if (clazz == PaymentOut.class && entityId == 1l) {
					logger.warn("Illegal entity requested");
					return objectContext.newObject(clazz);
				}
				return null;
			}
		};
	}
}
