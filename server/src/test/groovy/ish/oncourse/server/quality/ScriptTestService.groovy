package ish.oncourse.server.quality

import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.QualityRule
import org.apache.cayenne.access.DataContext
import org.apache.commons.io.IOUtils

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by akoiro on 17/03/2016.
 */
@CompileStatic
class ScriptTestService {
    DataContext context
    ICayenneService cayenneService
    PreferenceController preferenceController
    QualityService qualityService

    ScriptTestService() {
        init()
    }

    private QualityService init() {
        context = mock(DataContext)
        preferenceController = mock(PreferenceController)
        cayenneService = mock(ICayenneService)
        when(cayenneService.newContext).thenReturn(context)
        qualityService = new QualityService(cayenneService, preferenceController)
    }

    QualityRule getQualityRuleBy(String name) {
        QualityRule qualityRule = mock(QualityRule)
        def scriptPath = "quality/${name}.groovy"
        def propertiesPath = "quality/${name}.properties"

        def properties = new Properties()
        properties.load(QualityServiceTest.classLoader.getResourceAsStream(propertiesPath))

        when(qualityRule.keyCode).thenReturn(properties.keyCode as String)
        when(qualityRule.groupName).thenReturn(properties.groupName as String)

        when(qualityRule.script).thenReturn(IOUtils.toString(
                QualityServiceTest.classLoader.getResourceAsStream(scriptPath)
                , "UTF-8"))


        return qualityRule
    }
}
