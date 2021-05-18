package ish

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import io.bootique.cayenne.CayenneConfigMerger
import io.bootique.cayenne.ServerRuntimeFactory
import io.bootique.cayenne.annotation.CayenneConfigs
import io.bootique.config.ConfigurationFactory
import io.bootique.jdbc.DataSourceFactory

import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.AngelModule
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.modules.TestModule
import ish.oncourse.server.report.JRRuntimeConfig
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.datasource.DriverDataSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.mariadb.jdbc.MariaDbConnection
import org.reflections.Reflections

import javax.sql.DataSource
import java.sql.DatabaseMetaData

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Subclass this when you need injectors to get services for your test
 */
@CompileStatic
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestWithBootique {

    protected static final String ANGEL_NODE = "AngelNode"
    protected static final String MARIADB = "mariadb"


    private static boolean loggingInitialised = false

    public BQRuntime injector

    protected DataSource dataSource
    protected static String databaseType = MARIADB

    protected static final Logger logger = LogManager.getLogger()

    @RegisterExtension
    public static BootiqueTestFactory testFactory = new BootiqueTestFactory()

    @BeforeAll
    @Order(1)
    void setupOnceRoot() throws Exception {
        System.setProperty(DefaultJasperReportsContext.PROPERTIES_FILE, "jasperreports.properties")
        //set JRGroovy compiler as default for tests
        new JRRuntimeConfig().config()

        if (!loggingInitialised) {
            ResourcesUtil.initialiseLogging(false)
            loggingInitialised = true
        }

        createInjectors()
    }

    @AfterAll
    @Order(2)
    private void cleanUpDB() {
        injector.shutdown()
    }

    /**
     * Override this class in the TestWithDatabase subclass
     * @throws Exception
     */
    protected void createInjectors() throws Exception {
        BootiqueTestFactory.Builder builder = testFactory
                .app("--config=classpath:application-test.yml")
                .module(AngelModule.class)
                .module(TestModule.class)
                .module(new Module() {

                    @Provides
                    @Singleton
                    protected ServerRuntime createCayenneRuntime(ConfigurationFactory configFactory,
                                                                 CayenneConfigMerger configMerger,
                                                                 @CayenneConfigs Set<String> injectedCayenneConfigs) {
                        DataSourceFactory factory =  new DataSourceFactory() {
                            @Override
                            DataSource forName(String dataSourceName) {
                                DatabaseMetaData md = mock(DatabaseMetaData)
                                when(md.getDatabaseProductName()).thenReturn("MARIADB")

                                MariaDbConnection c = mock(MariaDbConnection)
                                when(c.getMetaData()).thenReturn(md)

                                DriverDataSource ds = mock(DriverDataSource)
                                when(ds.getConnection()).thenReturn(c)
                                return ds
                            }

                            @Override
                            Collection<String> allNames() {
                                return ['angel-test']
                            }

                            @Override
                            boolean isStarted(String dataSourceName) {
                                return true
                            }
                        } 
                        ServerRuntime runtime = configFactory
                                .config(ServerRuntimeFactory.class, 'cayenne')
                                .createCayenneRuntime(factory,
                                        configMerger,
                                        Collections.EMPTY_LIST as Collection<org.apache.cayenne.di.Module>,
                                        injectedCayenneConfigs)

                        return runtime
                    }


                    @Override
                    void configure(Binder binder) {

                    }
                })

        def testModules = new Reflections(PluginService.PLUGIN_PACKAGE).getTypesAnnotatedWith(ish.TestModule) as Set<Class>
        testModules.each {
            builder.module(it)
        }

        injector = builder.createRuntime()
    }

}
