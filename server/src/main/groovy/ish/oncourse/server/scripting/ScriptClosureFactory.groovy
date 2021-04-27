/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.scripting

import com.google.inject.Injector
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
/**
 *
 */
@CompileStatic
class ScriptClosureFactory {

    public ICayenneService cayenneService
    public Injector injector

    void setSpecClass(Class<? extends ScriptClosureTrait> specClass) {
        this.specClass = specClass
        this.integrationClass = specClass.getAnnotation(ScriptClosure).integration()
        this.type = integrationClass.getAnnotation(Plugin).type()
    }
    private Class<? extends ScriptClosureTrait> specClass
    private Class<? extends PluginTrait> integrationClass
    private int type

    private static Logger logger = LogManager.logger


    Object execute(Closure cl) {
        def spec = this.specClass.getDeclaredConstructor().newInstance()

        def build = cl.rehydrate(spec, cl, this)
        build.resolveStrategy = Closure.DELEGATE_FIRST
        build()

        List<Object> result = new ArrayList<>()
        getIntegrationConfiguration(spec, this.type).each {integrationConfig ->
            def i = integrationClass.newInstance(injector:injector,
                    configuration: integrationConfig)
            try {
                Object closureResult = spec.execute(i)
                if (closureResult) {
                    result.add(closureResult)
                }
            } catch (Exception e) {
                logger.catching(e)
                throw e
            }
        }
        
        if (result.empty) {
            return null
        } else if (result.size() == 1) {
            return result[0]
        } else {
            return result
        }
        
    }

    private List<IntegrationConfiguration> getIntegrationConfiguration(ScriptClosureTrait spec, int type) {
        if (spec.name) {
            def config = ObjectSelect.query(IntegrationConfiguration)
                    .where(IntegrationConfiguration.NAME.eq(spec.name))
                    .and(IntegrationConfiguration.TYPE.eq(type))
                    .selectOne(cayenneService.newContext)
            if (config == null) {
                throw new IllegalArgumentException("No integration with name '${spec.name}' was found.")
            }
            return [config]

        } else {
            return ObjectSelect.query(IntegrationConfiguration)
                    .where(IntegrationConfiguration.TYPE.eq(type))
                    .select(cayenneService.newContext)
        }
    }


}
