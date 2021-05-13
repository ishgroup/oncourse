package ish

import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.AngelModule
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.modules.TestModule
import ish.oncourse.server.report.JRRuntimeConfig
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.reflections.Reflections

import javax.sql.DataSource
/**
 * Subclass this when you need injectors to get services for your test
 */
@CompileStatic
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestWithBootique {

    protected static final String ANGEL_NODE = "AngelNode"
    protected static final String MARIADB = "mariadb"


    private static boolean loggingInitialised = false

    public  BQRuntime injector
    protected ICayenneService cayenneService

    protected  DataSource dataSource
    protected static String databaseType = MARIADB

    private static final Logger logger = LogManager.getLogger()

    @RegisterExtension
    public static BootiqueTestFactory testFactory = new BootiqueTestFactory()

    @BeforeAll
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

    /**
     * Override this class in the TestWithDatabase subclass
     * @throws Exception
     */
    protected void createInjectors() throws Exception {
        BootiqueTestFactory.Builder builder = testFactory
                .app(String.format("--config=classpath:%s", "application-test.yml"))
                .module(AngelModule.class)
                .module(TestModule.class)

        def testModules = new Reflections(PluginService.PLUGIN_PACKAGE).getTypesAnnotatedWith(ish.TestModule) as Set<Class>
        testModules.each {
            builder.module(it)
        }

        injector = builder.createRuntime()
    }

}
