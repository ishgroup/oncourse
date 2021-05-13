package ish

import com.google.inject.Binder
import com.google.inject.Inject
import com.google.inject.Module
import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceListener
import io.bootique.jdbc.JdbcModule
import io.bootique.jdbc.managed.ManagedDataSourceStarter
import io.bootique.jdbc.tomcat.JdbcTomcatModule
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.AngelModule
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.modules.ApiCayenneLayerModule
import ish.oncourse.server.modules.TestModule
import ish.oncourse.server.report.JRRuntimeConfig
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import org.apache.cayenne.access.DataDomain
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.configuration.server.ServerModule
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.reflections.Reflections

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
/**
 * Subclass this when you need injectors to get services for your test
 */
@CompileStatic
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestWithBootique {

    protected static final String ANGEL_NODE = "AngelNode"
    protected static final String MARIADB = "mariadb"
    protected static final String MYSQL = "mysql"
    protected static final String MSSQL = "mssql"

    private static boolean loggingInitialised = false

    public static BQRuntime injector

    protected static DataSource dataSource
    protected static String databaseType = MARIADB

    private static final Logger logger = LogManager.getLogger()

    @RegisterExtension
    public static BootiqueTestFactory testFactory = new BootiqueTestFactory()

    @BeforeAll
    static void setupOnceRoot() throws Exception {
        System.setProperty(DefaultJasperReportsContext.PROPERTIES_FILE, "jasperreports.properties")
        //set JRGroovy compiler as default for tests
        new JRRuntimeConfig().config()

        if (!loggingInitialised) {
            ResourcesUtil.initialiseLogging(false)
            loggingInitialised = true
        }

        createInjectors()
//        LiquibaseJavaContext.fill(injector);
    }

    static void createInjectors() throws Exception {
        final String yamlTestConfig = System.getProperty("yamlTestConfig")

        BootiqueTestFactory.Builder builder = testFactory
                .app(String.format("--config=classpath:%s", yamlTestConfig))
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
                .module(TestModule.class)
                .module(ApiCayenneLayerModule.class)

        testModules.each {
            builder.module(it)
        }

        injector = builder.createRuntime()

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        DataDomain domain = cayenneService.getSharedContext().getParentDataDomain()

        dataSource = domain.getDataNode(ANGEL_NODE).getDataSource()
    }

    static Set<Class> getTestModules() {
        new Reflections(PluginService.PLUGIN_PACKAGE).getTypesAnnotatedWith(ish.TestModule) as Set<Class>
    }

    protected static IDatabaseConnection getTestDatabaseConnection() throws Exception {

        DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null)

        DatabaseConfig config = dbConnection.getConfig()
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())

        return dbConnection
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

    protected static File getResourceAsFile(final String relativePath) {
        logger.entry(relativePath)

        URL resourceURL = ClassLoader.getSystemClassLoader().getResource(relativePath)
        try {
            return new File(resourceURL.toURI())
        } catch (URISyntaxException e) {
            logger.warn("resource not found, searched in paths:\n {}", ResourcesUtil.getClasspathString())
        }
        return null
    }
}
