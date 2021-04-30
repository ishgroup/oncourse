package ish

import ish.common.types.PaymentType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.db.SanityCheckService
import ish.util.AccountUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.access.DataDomain
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.access.DbGenerator
import org.apache.cayenne.dba.DbAdapter
import org.apache.cayenne.log.JdbcEventLogger
import org.apache.cayenne.map.*
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.validation.ValidationFailure
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.operation.DatabaseOperation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

abstract class CayenneIshTestCase extends IshTestCase {
	private static final Logger logger = LogManager.getLogger()

	private static final String RESET_AUTO_INCREMENT_TEMPLATE_MYSQL = "ALTER TABLE %s AUTO_INCREMENT = %d"
	private static final int NEXT_ID = 10000

	private static final String CUSTOM_FIELD = "CustomField"

	@BeforeAll
	static void setUpOnce() throws Exception {
		generateTables()
		resetAutoIncrement()
	}

	@BeforeEach
	void setup() throws Exception {
		validateAccountAndTaxDefaults()
		checkPaymentMethods()
	}

	void validateAccountAndTaxDefaults() throws Exception {
		SanityCheckService sanity = injector.getInstance(SanityCheckService.class)
		sanity.performCheck()
	}

	private void checkPaymentMethods() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

		List<PaymentMethod> methods = ObjectSelect.query(PaymentMethod.class).select(newContext)
		Account bankAccount = AccountUtil.getDefaultBankAccount(newContext, Account.class)

		for (PaymentType type : PaymentType.values()) {
			if (PaymentMethod.NAME.eq(type.getDisplayName()).filterObjects(methods).isEmpty()) {


				PaymentMethod paymentMethod = newContext.newObject(PaymentMethod.class)
				paymentMethod.setName(type.getDisplayName())
				paymentMethod.setAccount(bankAccount)
				paymentMethod.setActive(true)
				paymentMethod.setBankedAutomatically(PaymentType.EFT.equals(type) || PaymentType.EFTPOS.equals(type) || PaymentType.CREDIT_CARD.equals(type))

				if (PaymentType.EFT.equals(type) || PaymentType.EFTPOS.equals(type) || PaymentType.BPAY.equals(type) || PaymentType.CASH.equals(type) ||
						PaymentType.PAYPAL.equals(type)) {

					paymentMethod.setType(PaymentType.OTHER)

				} else {
					paymentMethod.setType(type)
				}
				paymentMethod.setReconcilable(true)
				paymentMethod.setUndepositedFundsAccount(bankAccount)
			}
		}

		newContext.commitChanges()

	}

	static void generateTables() throws Exception {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

		DataDomain domain = cayenneService.getSharedContext().getParentDataDomain()

		DataNode angelNode =  domain.getDataNode("AngelNode")
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

		DbGenerator generator = new DbGenerator(jdbcAdapter, datamap, Collections.emptyList(), domain, jdbcEventLogger)
		generator.setShouldCreateTables(true)
		generator.setShouldCreateFKConstraints(true)
		generator.setShouldCreatePKSupport(false)
		generator.runGenerator(dataSource)
		if (generator.getFailures() != null) {
			logger.warn("generation of test database schema out of cayenne model failed:")
			for (ValidationFailure result : generator.getFailures().getFailures()) {
				logger.warn(result.toString())
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

	// -------- util methods --------
	protected <T extends Persistent> T getRecordWithId(DataContext context, Class<T> clazz, Long id) {
		return SelectById.query(clazz, id).selectOne(context)
	}

	/**
	 * Remove all records from db tables.
	 */
	protected static void wipeTables() {
		if (testEnvMariadb()) {
			wipeTablesMariadb()
		} else {
			fail("Not recognised database: "+databaseType)
		}
	}

	protected static void resetAutoIncrement() {

		String template
		if (testEnvMariadb()) {
			template = RESET_AUTO_INCREMENT_TEMPLATE_MYSQL
		} else {
			return
		}

		try {
			ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
			DataDomain domain = cayenneService.getSharedContext().getParentDataDomain()
			DataMap dataMap = domain.getDataMap("AngelMap")

			Connection connection = getTestDatabaseConnection().getConnection()

			for (DbEntity entity : dataMap.getDbEntities()) {
				if ("ACLRoleSystemUser".equals(entity.getName()) || "StudentNumber".equals(entity.getName())) {
					continue
				}
				Statement stmt = connection.createStatement()
				stmt.execute(String.format(template, entity.getName(), NEXT_ID))
				stmt.close()
			}
		} catch(Exception e) {
			logger.catching(e)
		}
	}

	private static void wipeTablesMariadb() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

		DataDomain domain = cayenneService.getSharedContext().getParentDataDomain()
		DataMap dataMap = domain.getDataMap("AngelMap")

		Connection connection = null
		try {

			connection = dataSource.getConnection()
			connection.setAutoCommit(true)

			executeStatement(connection, "SET foreign_key_checks = 0;")
			for (DbEntity entity : dataMap.getDbEntities()) {
				executeStatement(connection, String.format("TRUNCATE %s;", entity.getName()))
			}

			executeStatement(connection, "SET foreign_key_checks = 1;")

//			shutdownCayenne()
//			createInjectors()
		} catch (Exception e) {
			throw new RuntimeException("Failed to wipe tables.", e)
		} finally {
			if (connection != null) {
				try {
					connection.close()
				} catch (SQLException e) {
					logger.warn("Failed to close connection.", e)
				}
			}
		}
	}

	private static void wipeTablesDerby() {
		Connection connection = null
		try {
			final String clean = """SELECT
'ALTER TABLE '||S.SCHEMANAME||'.'||T.TABLENAME||' DROP CONSTRAINT '||C.CONSTRAINTNAME 
FROM
    SYS.SYSCONSTRAINTS C,
    SYS.SYSSCHEMAS S,
    SYS.SYSTABLES T
WHERE
    C.SCHEMAID = S.SCHEMAID
AND
    C.TABLEID = T.TABLEID
AND
S.SCHEMANAME = 'APP'
UNION
SELECT 'TRUNCATE TABLE ' || schemaname ||'.' || tablename 
FROM SYS.SYSTABLES
INNER JOIN SYS.SYSSCHEMAS ON SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID
where schemaname='APP'"""


			connection = dataSource.getConnection()
			connection.setAutoCommit(true)

			final List<String> qrList = new ArrayList<>()
			final Statement stmt = connection.createStatement()
			final ResultSet rs = stmt.executeQuery(clean)

			while (rs.next()) {
				qrList.add(rs.getString(1))
			}
			rs.close()
			stmt.close()

			if (qrList.isEmpty()) {
				return
			}

			for (final String sql : qrList) {
				tryStatement(sql)
			}

			// for some reason those 5 tables do not drop by themselves in 10% cases:
			tryStatement(qrList.find{ it.contains("SYSTEMUSER")})
			tryStatement(qrList.find{ it.contains("ACLROLE")})
			tryStatement(qrList.find{ it.contains("SITE")})
			tryStatement(qrList.find{ it.contains("COUNTRY")})
			tryStatement(qrList.find{ it.contains("TAX")})
			tryStatement(qrList.find{ it.contains("ACCOUNT")})
			tryStatement(qrList.find{ it.contains("ACCOUNTTRANSACTION")})
			//
//			tryStatement("DROP FUNCTION IFNULL");

		} catch (Exception e) {
			throw new RuntimeException("cleaning database failed", e)
		} finally {
			if (connection != null) {
				try {
					connection.close()
				} catch (SQLException e) {
					logger.catching(e)
				}
			}
		}
	}

	private static void wipeTablesMsSQL() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

		DataDomain domain = cayenneService.getSharedContext().getParentDataDomain()
		DataMap dataMap = domain.getDataMap("AngelMap")

		Connection connection = null
		try {

			connection = dataSource.getConnection()
			connection.setAutoCommit(true)

			executeStatement(connection, "EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\"")

			for (DbEntity entity : dataMap.getDbEntities()) {
				executeStatement(connection, String.format("DELETE FROM %s;", entity.getName()))
			}

			executeStatement(connection, "EXEC sp_msforeachtable @command1=\"print '?'\", @command2=\"ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all\"")

			shutdownCayenne()
			createInjectors()
		} catch (Exception e) {
			throw new RuntimeException("Failed to wipe tables.", e)
		} finally {
			if (connection != null) {
				try {
					connection.close()
				} catch (SQLException e) {
					logger.warn("Filed to close connection.", e)
				}
			}
		}
	}

	private static void executeStatement(Connection connection, String statement) throws SQLException {
		Statement stmt = connection.createStatement()
		stmt.execute(statement)
		stmt.close()
	}

	protected Account getAccountWithId(ObjectContext context, Long id) {
		return AccountUtil.getAccountWithId(id, context, Account.class)
	}

	static void executeDatabaseOperation(IDataSet dataSet) throws Exception {
		IDatabaseConnection testDatabaseConnection = getTestDatabaseConnection()
		DatabaseOperation.CLEAN_INSERT.execute(testDatabaseConnection, dataSet)
	}
}
