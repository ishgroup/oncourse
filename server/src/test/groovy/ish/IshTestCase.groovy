package ish

import com.google.inject.Binder
import com.google.inject.Inject
import com.google.inject.Module
import io.bootique.BQRuntime
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceListener
import io.bootique.jdbc.JdbcModule
import io.bootique.jdbc.managed.ManagedDataSourceStarter
import io.bootique.jdbc.tomcat.JdbcTomcatModule
import io.bootique.test.junit.BQTestFactory
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
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.reflections.Reflections

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

abstract class IshTestCase {

	private static final String ANGEL_NODE = "AngelNode"
    private static final String MARIADB = "mariadb"
    private static final String MYSQL = "mysql"
    private static final String MSSQL = "mssql"
    private static final String DERBY = "derby"

    private static boolean loggingInitialised = false

    protected static String workingDirectory

    protected static BQRuntime injector

    protected static DataSource dataSource
    protected static String databaseType

    private static final Logger logger = LogManager.getLogger()

    @ClassRule
	public static BQTestFactory testFactory = new BQTestFactory()

    @BeforeClass
    static void setupOnceRoot() throws Exception {
		System.setProperty(DefaultJasperReportsContext.PROPERTIES_FILE, "jasperreports.properties")
        System.setProperty("derby.stream.error.method", "ish.IshTestCase.disableDerbyLogFile")
        //set JRGroovy compiler as default for tests
		new JRRuntimeConfig().config()

        workingDirectory = new File(".").getAbsolutePath()
        workingDirectory = workingDirectory.substring(0, workingDirectory.lastIndexOf("."))

        if (!loggingInitialised) {
			ResourcesUtil.initialiseLogging(false)
            loggingInitialised = true
        }

		createInjectors()
        cleanUpMySql()
//        LiquibaseJavaContext.fill(injector);
    }

    static void shutdownCayenne() {
		ICayenneService cayenneService = (ICayenneService) injector.getInstance(ICayenneService.class)
        cayenneService.getServerRuntime().shutdown()
        injector.shutdown()
    }


    static void createInjectors() throws Exception {

        final String yamlTestConfig = System.getProperty("yamlTestConfig")

        databaseType = MARIADB

//        url: jdbc:mariadb://localhost/angelTest_trunk?autocommit=true&useUnicode=true&characterEncoding=utf8

        BQTestFactory.Builder builder = testFactory
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
                                if(dataSourceStarter != null) {
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
        new Reflections(PluginService.PLUGIN_PACKAGE).getTypesAnnotatedWith(ish.TestModule)
    }

    static boolean testEnvMariadb() {
		return databaseType.equalsIgnoreCase(MARIADB)
    }

	protected static IDatabaseConnection getTestDatabaseConnection() throws Exception {

		DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null)

        DatabaseConfig config = dbConnection.getConfig()
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())

		return dbConnection
    }

	/**
	 * cleans up after the test, dropping the database. we might change it in the future, so it only drops once per whole run cycyle
	 */
	@AfterClass
    static void cleanUp() {

		// need to stop stop CayenneService in order to dispose connection pool created for it
		shutdownCayenne()
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
                    e.printStackTrace()
                }
            }
        }
    }

	protected static void cleanUpMySql() {
		Connection connection = null
        try {
            connection = dataSource.getConnection()
            connection.setAutoCommit(true)
            final String databaseName = connection.getCatalog()

            Statement stmt = connection.createStatement()

            ResultSet rs = stmt.executeQuery("SELECT\n" +
                    "    TABLE_NAME\n" +
                    "FROM\n" +
                    "    information_schema.TABLES\n" +
                    "WHERE\n" +
                    "    table_schema = '$databaseName'")

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
			throw new RuntimeException("cleaning mysql database failed", e)
        } finally {
			if (connection != null) {
				try {
					connection.close()
                } catch (SQLException e) {
					e.printStackTrace()
                }
			}
		}
	}

	/**
	 * used to execute statements which can fail, used by mssql cleanup, where we are not sure which tables might have been created/dropped
	 *
	 * @param statement
	 */
	protected static void tryStatement(String statement) {
		Connection connection = null
        try {
			connection = dataSource.getConnection()
            connection.setAutoCommit(true)
            Statement stmt = connection.createStatement()
            stmt.execute(statement)
            stmt.close()

        } catch (Exception e) {
			// nothing
		} finally {
			if (connection != null) {
				try {
					connection.close()
                } catch (SQLException e) {
					e.printStackTrace()
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