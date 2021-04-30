package ish.oncourse.server.scripting

import ish.oncourse.server.cayenne.Script
import org.apache.cayenne.access.DataContext
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock

import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

/**
 * Created by akoiro on 7/04/2016.
 */

abstract class AbstractGroovyScriptTest {
    MockInjector mockInjector

    GroovyScriptService groovyScriptService

    @Mock
    DataContext objectContext

    @Mock
    Script script

    @BeforeEach
    void before() {
        mockInjector = new MockInjector()
        mockInjector.init()

        initMocks(this)

        when(mockInjector.cayenneService.newContext).thenReturn(objectContext)

        groovyScriptService = new GroovyScriptService(mockInjector.cayenneService,
                mockInjector.schedulerService, mockInjector.preferenceController,
                mockInjector.injector)

        when(objectContext.localObject(script)).thenReturn(script)
    }

    /**
     * This method is demo how to use the AbstractGroovyScriptTest
     */
    @Ignore
    void test() {
        when(script.script).thenReturn("def run(args) {println 'Hello AbstractGroovyScriptTest'}")
        when(script.script).thenReturn(getScriptContent("scripts/creation-vet-certificates.groovy"))
        groovyScriptService.runAndWait(script, ScriptParameters.empty())
    }

    String getScriptContent(String path) {
        return this.class.classLoader.getResourceAsStream(path).getText("UTF-8")
    }
}
