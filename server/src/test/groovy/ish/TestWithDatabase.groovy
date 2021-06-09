package ish

import com.google.inject.Binder
import com.google.inject.Inject
import com.google.inject.Module
import groovy.transform.CompileStatic
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceListener
import io.bootique.jdbc.JdbcModule
import io.bootique.jdbc.managed.ManagedDataSourceStarter
import io.bootique.jdbc.tomcat.JdbcTomcatModule
import ish.common.types.PaymentType
import ish.oncourse.server.AngelModule
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.db.SanityCheckService
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.modules.ApiCayenneLayerModule
import ish.util.AccountUtil
import org.apache.cayenne.Persistent
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.access.DataDomain
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.configuration.server.ServerModule
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.dbunit.operation.DatabaseOperation
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.RegisterExtension
import org.reflections.Reflections

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
/**
 * Subclass this when you want to set up a database for your test.
 */
@CompileStatic
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestWithDatabase extends TestWithBootique {

    protected DataContext cayenneContext

    ICayenneService getCayenneService() {
        if (!cayenneService) {
            cayenneService = injector.getInstance(ICayenneService)
        }
        return cayenneService
    }
    protected ICayenneService cayenneService



    @RegisterExtension
    protected dbExtension = new TestWithDatabaseExtension({getCayenneService()}, {cayenneContext}, {dataSource})

    /**
     * Replace values in the dbUnit data source. But default we handle null, but override this method to provide additional values
     * @param rDataSet
     */
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        rDataSet.addReplacementObject("[null]", null)
        rDataSet.addReplacementObject("[NULL]", null)
    }

    @BeforeEach
    @Order(2)
    private void setup() throws Exception {
        validateAccountAndTaxDefaults()
        checkPaymentMethods()
    }

    protected void createInjectors() throws Exception {
        BootiqueTestFactory.Builder builder = testFactory
                .app("--config=classpath:application-test.yml")
                .module(AngelModule.class)
                .module(JdbcModule.class)
                .module(new Module() {
                    @Override
                    void configure(Binder binder) {
                        JdbcModule.extend(binder).addDataSourceListener(new DataSourceListener() {

                            @Inject
                            Map<String, ManagedDataSourceStarter> starters

                            @Override
                            void beforeStartup(String name, String jdbcUrl) {
                                ManagedDataSourceStarter dataSourceStarter = starters.get("angel-test-creation")
                                if (dataSourceStarter != null) {
                                    createMariaDbSchema(dataSourceStarter)
                                }
                            }
                        })
                    }
                })
                .module(JdbcTomcatModule.class)
                .module(CayenneModule.class)
                .module(new Module() {
                    @Override
                    void configure(Binder binder) {
                        CayenneModule.extend(binder).addModule(new org.apache.cayenne.di.Module() {
                            @Override
                            void configure(org.apache.cayenne.di.Binder binderCayenne) {
                                ServerModule.contributeProperties(binderCayenne)
                                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(true))
                            }
                        })
                    }
                })
                .module(ish.oncourse.server.modules.TestModule.class)
                .module(ApiCayenneLayerModule.class)

        def testModules = new Reflections(PluginService.PLUGIN_PACKAGE).getTypesAnnotatedWith(ish.TestModule) as Set<Class>
        testModules.each {
            builder.module(it)
        }

        injector = builder.createRuntime()
        DataDomain domain = getCayenneService().getSharedContext().getParentDataDomain()
        dataSource = domain.getDataNode(ANGEL_NODE).getDataSource()
    }

    private static void createMariaDbSchema(ManagedDataSourceStarter dataSourceStarter) {
        Connection connection = null
        final String createSchema =
                "CREATE DATABASE IF NOT EXISTS angelTest_trunk DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;"
        DataSource currDataSource = dataSourceStarter.start().getDataSource()
        try {
            connection = currDataSource.getConnection()
            connection.setAutoCommit(true)

            final Statement stmt = connection.createStatement()
            final ResultSet rs = stmt.executeQuery(createSchema)
            rs.close()
            stmt.close()
        } catch (Exception e) {
            logger.catching(e)
            throw new RuntimeException("Can't create mariadb/mysql schema.")
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

    void validateAccountAndTaxDefaults() throws Exception {
        SanityCheckService sanity = injector.getInstance(SanityCheckService)
        sanity.performCheck()
    }

    private void checkPaymentMethods() {
        DataContext newContext = injector.getInstance(ICayenneService).getNewNonReplicatingContext()

        List<PaymentMethod> methods = ObjectSelect.query(PaymentMethod).select(newContext)
        Account bankAccount = AccountUtil.getDefaultBankAccount(newContext, Account)

        for (PaymentType type : PaymentType.values()) {
            if (PaymentMethod.NAME.eq(type.getDisplayName()).filterObjects(methods).isEmpty()) {


                PaymentMethod paymentMethod = newContext.newObject(PaymentMethod)
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

    // -------- util methods --------
    protected <T extends Persistent> T getRecordWithId(DataContext context, Class<T> clazz, Long id) {
        return SelectById.query(clazz, id).selectOne(context)
    }

    @Deprecated
    private IDatabaseConnection getTestDatabaseConnection() throws Exception {
        DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null)

        DatabaseConfig config = dbConnection.getConfig()
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())

        return dbConnection
    }

    @Deprecated
    void executeDatabaseOperation(IDataSet dataSet) throws Exception {
        IDatabaseConnection testDatabaseConnection = getTestDatabaseConnection()
        DatabaseOperation.CLEAN_INSERT.execute(testDatabaseConnection, dataSet)
    }
}
