/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish

import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.SessionTest
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.access.DataDomain
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.access.DbGenerator
import org.apache.cayenne.dba.DbAdapter
import org.apache.cayenne.log.JdbcEventLogger
import org.apache.cayenne.map.*
import org.apache.cayenne.validation.ValidationFailure
import org.apache.groovy.internal.util.Function
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.dbunit.operation.CompositeOperation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.*
import org.junit.platform.commons.support.AnnotationSupport

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.function.Supplier

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL

@CompileStatic
class TestWithDatabaseExtension implements
        BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {


    
    private static final Logger logger = LogManager.getLogger()

    private static final String RESET_AUTO_INCREMENT_TEMPLATE_MYSQL = "ALTER TABLE %s AUTO_INCREMENT = %d"
    private static final int NEXT_ID = 10000
    private static final String CUSTOM_FIELD = "CustomField"
    
    private Closure<ICayenneService> cayenneServiceSupplier
    private Closure<DataContext> dataContextSupplier
    private Closure<DataSource> dataSourceSupplier

    TestWithDatabaseExtension(Closure<ICayenneService> cayenneServiceSupplier, Closure<DataContext> dataContextSupplier,  Closure<DataSource> dataSourceSupplier) {

        this.dataSourceSupplier = dataSourceSupplier
        this.dataContextSupplier = dataContextSupplier
        this.cayenneServiceSupplier = cayenneServiceSupplier
    }

    @Override
    void afterAll(ExtensionContext context) throws Exception {

    }

    @Override
    void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    void beforeAll(ExtensionContext context) throws Exception {
        def store = context.root.getStore(GLOBAL)
        store.put("db_data_loaded", false)

    }

    @Override
    void beforeEach(ExtensionContext context) throws Exception {
        def store = context.root.getStore(GLOBAL)
        def previousDataSource = store.get("dataSource")
        

        // do this once for the whole dbunit run
        if (!store.get("db_setup")) {
            dropTablesMariaDB()
            generateTables()
            resetAutoIncrement()
            //        LiquibaseJavaContext.fill(injector);
            store.put("db_setup", true)
        }
        TestWithDatabase databaseTest = (context.getTestInstance().get() as TestWithDatabase)

        DatabaseSetup a = AnnotationSupport.findAnnotation(context.getTestClass(), DatabaseSetup).orElse(null) as DatabaseSetup
        if (a) {
            if (a.readOnly() && store.get("db_data_loaded")) {
                //do nothing, data not changed from tests to test 
            } else if (a.type() == DatabaseOperation.DELETE_ALL) {
                wipeTablesMariadb()             
                for (dataSource in a?.value()) {
                    store.put("dataSource", dataSource)
                    InputStream st = SessionTest.class.getClassLoader().getResourceAsStream(dataSource)
                    FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
                    builder.setColumnSensing(true)
                    FlatXmlDataSet dataSet = builder.build(st)

                    ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
                    databaseTest.dataSourceReplaceValues(rDataSet)

                    org.dbunit.operation.DatabaseOperation.INSERT.execute(testDatabaseConnection, rDataSet)
                }
                store.put("db_data_loaded", true)
            } else {
                //All tests has DELETE_ALL strategy. Need to implement alghorithm for other strategies. Not sure how it hould be right now
                throw new UnsupportedOperationException(" ${a.type()}  Not implemented")
            }
         
        }
        if (a && a.readOnly()) {
            databaseTest.cayenneContext = cayenneServiceSupplier.call().getNewReadonlyContext()
        } else {
            databaseTest.cayenneContext = cayenneServiceSupplier.call().getNewContext()
        }

    }

    private void wipeTablesMariadb() {
        DataDomain domain = cayenneServiceSupplier.call().getSharedContext().getParentDataDomain()
        DataMap dataMap = domain.getDataMap("AngelMap")

        Connection connection = null
        try {
            connection = dataSourceSupplier.call().getConnection()
            connection.setAutoCommit(true)

            executeStatement(connection, "SET foreign_key_checks = 0;")
            for (DbEntity entity : dataMap.getDbEntities()) {
                executeStatement(connection, String.format("TRUNCATE %s;", entity.getName()))
            }
            executeStatement(connection, "SET foreign_key_checks = 1;")

        } catch (Exception e) {
            throw new RuntimeException("Failed to wipe tables.", e)
        } finally {
            connection?.close()
        }
    }

    private static void executeStatement(Connection connection, String statement) throws SQLException {
        Statement stmt = connection.createStatement()
        stmt.execute(statement)
        stmt.close()
    }

    private void dropTablesMariaDB() {
        try {
            def connection = dataSourceSupplier.call().getConnection()
            connection.setAutoCommit(true)
            def databaseName = connection.getCatalog()
            Statement stmt = connection.createStatement()

            ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM information_schema.TABLES\n" +
                    "WHERE  table_schema = '$databaseName'")

            List<String> tables = []
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"))
            }
            rs.close()

            stmt.execute("SET foreign_key_checks = 0")
            tables.each { t ->
                stmt.execute("DROP TABLE $t")
            }
            stmt.execute("SET foreign_key_checks = 1")
            stmt.close()

            connection.setCatalog(databaseName)
        } catch (Exception e) {
            logger.catching(e)
            Assertions.fail("cleaning mysql database failed")
        }
    }


    private void resetAutoIncrement() {
        DataDomain domain = cayenneServiceSupplier.call().getSharedContext().getParentDataDomain()
        DataMap dataMap = domain.getDataMap("AngelMap")

        Connection connection = getTestDatabaseConnection().getConnection()

        for (DbEntity entity : dataMap.getDbEntities()) {
            if ("ACLRoleSystemUser".equals(entity.getName()) || "StudentNumber".equals(entity.getName())) {
                continue
            }
            Statement stmt = connection.createStatement()
            stmt.execute(String.format(RESET_AUTO_INCREMENT_TEMPLATE_MYSQL, entity.getName(), NEXT_ID))
            stmt.close()
        }
    }

    private IDatabaseConnection getTestDatabaseConnection() throws Exception {
        DatabaseConnection dbConnection = new DatabaseConnection(dataSourceSupplier.call().getConnection(), null)

        DatabaseConfig config = dbConnection.getConfig()
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())

        return dbConnection
    }

    void generateTables() throws Exception {
        DataDomain domain = cayenneServiceSupplier.call().getSharedContext().getParentDataDomain()

        DataNode angelNode = domain.getDataNode("AngelNode")
        DbAdapter jdbcAdapter = angelNode.getAdapter()
        JdbcEventLogger jdbcEventLogger = angelNode.getJdbcEventLogger()

        DataMap datamap = domain.getDataMap("AngelMap")

        DbAttribute customField_value = datamap.getDbEntity(CUSTOM_FIELD).getAttribute("value")

        // those relationships do not exist in the real databases, just in cayenne model, they cannot be added to the database model
        List<Relationship> nodeRelationshipsToRemove = new ArrayList<>()
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedContact"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedCourse"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedCourseClass"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedMessage"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedReport"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedRoom"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedSite"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedStudent"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedTutor"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedDocument"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedApplication"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedEnrolment"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedPayslip"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedWaitingList"))
        nodeRelationshipsToRemove.add(datamap.getDbEntity("NodeRelation").getRelationship("taggedAssessment"))
        for (Relationship rel : nodeRelationshipsToRemove) {
            datamap.getDbEntity("NodeRelation").removeRelationship(rel.getName())
        }


        List<Relationship> binaryRelationshipsToRemove = new ArrayList<>()
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedCertificate"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedContact"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedCourse"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedCourseClass"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedEnrolment"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedInvoice"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedPriorLearning"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedRoom"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedSession"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedSite"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedStudent"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedTag"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedTutor"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedApplication"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedAssessment"))
        binaryRelationshipsToRemove.add(datamap.getDbEntity("BinaryRelation").getRelationship("attachedAssessmentSubmission"))
        for (Relationship rel : binaryRelationshipsToRemove) {
            datamap.getDbEntity("BinaryRelation").removeRelationship(rel.getName())
        }

        DbEntity unavailableRuleRelation = datamap.getDbEntity("UnavailableRuleRelation")
        List<Relationship> unavailableRuleRelationsToRemove = new ArrayList<>()
        unavailableRuleRelationsToRemove.add(unavailableRuleRelation.getRelationship("contact"))
        unavailableRuleRelationsToRemove.add(unavailableRuleRelation.getRelationship("course"))
        unavailableRuleRelationsToRemove.add(unavailableRuleRelation.getRelationship("room"))
        unavailableRuleRelationsToRemove.add(unavailableRuleRelation.getRelationship("site"))
        for (Relationship rel : unavailableRuleRelationsToRemove) {
            unavailableRuleRelation.removeRelationship(rel.getName())
        }

        List<Relationship> noteRelationshipsToRemove = new ArrayList<>()
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedApplication"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedContact"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedCourse"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedCourseClass"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedInvoice"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedRoom"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedSite"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedEnrolment"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedAssessment"))
        noteRelationshipsToRemove.add(datamap.getDbEntity("NoteRelation").getRelationship("notedAssessmentSubmission"))
        for (Relationship rel : noteRelationshipsToRemove) {
            datamap.getDbEntity("NoteRelation").removeRelationship(rel.getName())
        }

        List<Relationship> customFieldRelationships = new ArrayList<>()
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedContact"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedEnrolment"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedCourse"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedCourseClass"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedApplication"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedWaitingList"))
        customFieldRelationships.add(datamap.getDbEntity("CustomField").getRelationship("relatedSurvey"))
        for (Relationship rel : customFieldRelationships) {
            datamap.getDbEntity("CustomField").removeRelationship(rel.getName())
        }

        List<Relationship> automationBindingsRelationships = new ArrayList<>()
        automationBindingsRelationships.add(datamap.getDbEntity("AutomationBinding").getRelationship("emailTemplate"))
        automationBindingsRelationships.add(datamap.getDbEntity("AutomationBinding").getRelationship("exportTemplate"))
        automationBindingsRelationships.add(datamap.getDbEntity("AutomationBinding").getRelationship("import"))
        automationBindingsRelationships.add(datamap.getDbEntity("AutomationBinding").getRelationship("report"))
        automationBindingsRelationships.add(datamap.getDbEntity("AutomationBinding").getRelationship("script"))
        automationBindingsRelationships.each { rel ->
            datamap.getDbEntity("AutomationBinding").removeRelationship(rel.getName())
        }

        // avoid generating not null db constraint on BinaryRelation.documentId column - this is needed
        // until we set proper db constraint there and MigrateBinaryInfosToDocuments data upgrade will be removed
        datamap.getDbEntity("BinaryRelation").getAttribute("documentId").setMandatory(false)


        //workaround for tests (clean and insert data to database) to avoid circular dependencies in DB tables.
        DbRelationship circularDependencyRelationship = datamap.getDbEntity("Account").getRelationship("tax")
        datamap.getDbEntity("Account").removeRelationship(circularDependencyRelationship.getName())

        DbGenerator generator = new DbGenerator(jdbcAdapter, datamap, Collections.emptyList()  as Collection<DbEntity>, domain, jdbcEventLogger)
        generator.setShouldCreateTables(true)
        generator.setShouldCreateFKConstraints(true)
        generator.setShouldCreatePKSupport(false)
        generator.runGenerator(dataSourceSupplier.call())
        if (generator.getFailures() != null) {
            Assertions.fail("generation of test database schema out of cayenne model failed:")
            for (ValidationFailure result : generator.getFailures().getFailures()) {
                Assertions.fail(result.toString())
            }
            throw new RuntimeException("generation of test database schema out of cayenne model failed, test terminated.")
        }

        //return circular dependency to dataMap
        datamap.getDbEntity("Account").addRelationship(circularDependencyRelationship)

        // readd the removed relationships
        for (Relationship rel : nodeRelationshipsToRemove) {
            datamap.getDbEntity("NodeRelation").addRelationship(rel)
        }

        for (Relationship rel : binaryRelationshipsToRemove) {
            datamap.getDbEntity("BinaryRelation").addRelationship(rel)
        }
        for (Relationship rel : unavailableRuleRelationsToRemove) {
            unavailableRuleRelation.addRelationship(rel)
        }

        for (Relationship rel : noteRelationshipsToRemove) {
            datamap.getDbEntity("NoteRelation").addRelationship(rel)
        }

        for (Relationship rel : customFieldRelationships) {
            datamap.getDbEntity("CustomField").addRelationship(rel)
        }

        automationBindingsRelationships.each { rel ->
            datamap.getDbEntity("AutomationBinding").addRelationship(rel)
        }

        datamap.getDbEntity("BinaryRelation").getAttribute("documentId").setMandatory(true)
    }

}
