/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.db

import ish.IshTestCase
import ish.util.RuntimeUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.Assume
import org.junit.Before

//TODO Refactoring

/**
 */
class DerbyDatabaseServiceTest extends IshTestCase {
	private static final Logger logger = LogManager.getLogger()

    @Before
    void setUp() throws Exception {
		Assume.assumeTrue(databaseType.equalsIgnoreCase("derby"))

        String workingDirectory = new File(".").getAbsolutePath()
        workingDirectory = workingDirectory.substring(0, workingDirectory.lastIndexOf("."))

        System.out.println("Working directory:" + workingDirectory)

        ArrayList<URI> uriList = new ArrayList<>()
        if (RuntimeUtil.isRunningOnWindows()) {
			uriList.add(new URI("jdbc:derby:" + workingDirectory.replaceAll("\\\\", "/")))
            uriList.add(new URI("jdbc:derby:" + workingDirectory.replaceAll("\\\\", "/") + "onCourse.iocdata"))
            uriList.add(new URI("jdbc:derby:" + workingDirectory.replaceAll("\\\\", "/") + "onCourse.iocdata/onCourse"))
            uriList.add(new URI("file://" + workingDirectory.replaceAll("\\\\", "/")))
            uriList.add(new URI("file://" + workingDirectory.replaceAll("\\\\", "/") + "onCourse.iocdata"))
            uriList.add(new URI("file://" + workingDirectory.replaceAll("\\\\", "/") + "onCourse.iocdata/onCourse"))
        } else {
			uriList.add(new URI("jdbc:derby:" + workingDirectory))
            uriList.add(new URI("jdbc:derby:" + workingDirectory + "onCourse.iocdata"))
            uriList.add(new URI("jdbc:derby:" + workingDirectory + "onCourse.iocdata/onCourse"))
            uriList.add(new URI("file://" + workingDirectory))
            uriList.add(new URI("file://" + workingDirectory + "onCourse.iocdata"))
            uriList.add(new URI("file://" + workingDirectory + "onCourse.iocdata/onCourse"))
        }

	}

//	/**
//	 * @throws Exception
//	 */
//	@Test
//	public void testAdapter() throws Exception {
//		for (URI uri : this.uriList) {
//			Injector injector = Guice.createInjector(new DatabaseModule(uri, IshTestCase.ANGEL_VERSION));
//			DatabaseService dbService = injector.getInstance(DatabaseService.class);
//			try {
//				DatabaseAdapter adapter = dbService.getDBAdapter();
//				assertEquals("Checking adapter class", DerbyAdapter.class, adapter.getClass());
//				assertEquals("Checking file location", this.workingDirectory + "onCourse.iocdata", System.getProperty("derby.system.home"));
//			} catch (Exception e) {
//				fail(String.format("Failed to instantiate database adapter for URI: %s", uri));
//			}
//		}
//	}

//	/**
//	 * @throws Exception
//	 */
//	@Test
//	public void testDatabaseConnection() throws Exception {
//		for (URI uri : this.uriList) {
//			Injector injector = Guice.createInjector(new DatabaseModule(uri, IshTestCase.ANGEL_VERSION));
//			DatabaseService dbService = injector.getInstance(DatabaseService.class);
//			try {
//				dbService.getDBAdapter();
//				dbService.testConnection();
//				dbService.stop();
//			} catch (Exception e) {
//				fail(String.format("Failed to connect to database for URI: %s", uri));
//			}
//		}
//	}

//	@Test
//	public void testFailingStartDatabaseService() throws URISyntaxException {
//		// if wrong url
//		try {
//			Injector injector = Guice.createInjector(new DatabaseModule(new URI(""), IshTestCase.ANGEL_VERSION));
//			DatabaseService ds = injector.getInstance(DatabaseService.class);
//			ds.start();
//			fail("DatabaseService is expected to fail when passed an empty uri.");
//		} catch (Exception e) {
//			assertNotNull(e);
//		}
//	}

//	@After
//	public void cleanup() {
//		SystemPreferences.userPreferences().put(DatabaseUtils.DB_URI_PREF, "");
//		File f = new File(this.workingDirectory + "onCourse.iocdata");
//		delete(f);
//	}

	private static void delete(File f) {
		if (f.exists()) {
			File[] files = f.listFiles()
            for (File file : files) {
				if (file.isDirectory()) {
					delete(file)
                } else {
					file.delete()
                }
			}
			f.delete()
        }
	}

//	@Test
//	public void testDatabaseSchemaIntegrity() throws Exception {
//		DatabaseService dbService = injector.getInstance(DatabaseService.class);
//		SchemaUpdateService schemaUpdateService = injector.getInstance(SchemaUpdateService.class);
//		CayenneService cayenneService = injector.getInstance(CayenneService.class);
//		dbService.getDBAdapter();
//
//		DbAdapter adapter = cayenneService.getSharedContext().getParentDataDomain().getDataNode("AngelNode").getAdapter();
//
//        schemaUpdateService.createSchema(dbService.getDBAdapter().createConnection());
//		schemaUpdateService.applySchemaUpdates(dbService.getDBAdapter().createConnection());
//
//		DbLoaderDelegate delegate = new DbLoaderDelegate() {
//
//			public boolean overwriteDbEntity(DbEntity ent) throws CayenneException {
//				return false;
//			}
//
//			public void objEntityRemoved(ObjEntity ent) {}
//
//			public void objEntityAdded(ObjEntity ent) {}
//
//			public void dbEntityRemoved(DbEntity ent) {}
//
//			public void dbEntityAdded(DbEntity ent) {}
//
//			@Override
//			public boolean dbRelationship(DbEntity entity) {
//				return false;
//			}
//
//			@Override
//			public boolean dbRelationshipLoaded(DbEntity entity, DbRelationship relationship) {
//				return false;
//			}
//		};
//
//		DbLoader loader = new DbLoader(adapter, dbService.getDBAdapter().createConnection(), new DbLoaderConfiguration(), delegate, new DefaultObjectNameGenerator());
//		DataMap dm = loader.load();
//
//		assertTrue("finding student", dm.getDbEntity("STUDENT") != null);
//		assertTrue("finding non existing entity", dm.getDbEntity("DOOOH") == null);
//
//		CayenneService cayService = (CayenneService) injector.getInstance(ICayenneService.class);
//
//		DataMap cayMap = cayService.getSharedContext().getParentDataDomain().getDataMap("AngelMap");
//
//		for (DbEntity cayenneModelDbEntity : cayMap.getDbEntities()) {
//			DbEntity reverseEngineeredDbEntity = dm.getDbEntity(cayenneModelDbEntity.getName().toUpperCase());
//
//			assertFalse("the entity " + cayenneModelDbEntity.getName() + " is not present in the reverse engineered cayenne model",
//					reverseEngineeredDbEntity == null);
//
//			if (reverseEngineeredDbEntity != null) {
//				for (DbAttribute cayenneModelAttibute : cayenneModelDbEntity.getAttributes()) {
//					logger.warn("checking {}:{}", cayenneModelDbEntity.getName(), cayenneModelAttibute.getName());
//
//					Attribute reverseEngineeredAttribute = reverseEngineeredDbEntity.getAttribute(cayenneModelAttibute.getName().toUpperCase());
//
//					assertFalse("the attribute " + cayenneModelDbEntity.getName() + ":" + cayenneModelAttibute.getName() +
//							" is not present in the reverse engineered cayenne model", reverseEngineeredAttribute == null);
//
//					if (reverseEngineeredAttribute instanceof DbAttribute) {
//						assertEquals("checking attribute precision, from caynne", cayenneModelAttibute.getAttributePrecision(),
//								((DbAttribute) reverseEngineeredAttribute).getAttributePrecision());
//						// assertEquals("checking attribute max length", cayenneModelAttibute.getMaxLength(),
//						// ((DbAttribute) reverseEngineeredAttribute).getMaxLength());
//						// assertEquals("checking attribute type", cayenneModelAttibute.getType(), ((DbAttribute) reverseEngineeredAttribute).getType());
//						// assertEquals("checking attribute scale", cayenneModelAttibute.getScale(), ((DbAttribute) reverseEngineeredAttribute).getScale());
//						// assertEquals("checking attribute is mandatory, from cayenne model", cayenneModelAttibute.isMandatory(),
//						// ((DbAttribute) reverseEngineeredAttribute).isMandatory());
//						// assertEquals("checking attribute is generated, from cayenne model", cayenneModelAttibute.isGenerated(),
//						// ((DbAttribute) reverseEngineeredAttribute).isGenerated());
//						// assertEquals("checking attribute is primary, from cayenne model", cayenneModelAttibute.isPrimaryKey(),
//						// ((DbAttribute) reverseEngineeredAttribute).isPrimaryKey());
//						// assertEquals("checking attribute is foreign", cayenneModelAttibute.isForeignKey(),
//						// ((DbAttribute) reverseEngineeredAttribute).isForeignKey());
//					}
//				}
//			}
//		}
//	}
}
