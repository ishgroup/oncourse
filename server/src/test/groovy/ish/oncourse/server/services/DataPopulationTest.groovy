/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.common.ResourceType
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test

@CompileStatic
class DataPopulationTest extends CayenneIshTestCase {

    private static final Logger logger = LogManager.getLogger()

    
    @Test
    void testImportResources() {

        for (ResourceType type : ResourceType.values()) {
            def count = PluginService.getPluggableResources(type.resourcePath, type.filePattern).size()
            assertNotEquals(0, count)
        }

        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        try {
            // can only really test export templates, the other imports require window server...
            dataPopulation.run()
        } catch (Exception e) {
            logger.warn("fail", e)
            fail("could not import one of the resources " + e)
        }
    }
}
