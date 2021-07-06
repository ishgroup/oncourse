/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.scripting.api

import groovy.text.Template
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


@CompileStatic
class MetaclassCleaner {
    private static final Logger logger = LogManager.logger

    @CompileDynamic
    static void clearGroovyCache(final Template template) {
        if (!template) {
            return
        }
        try {
            Script script = template.@script as Script
            Class theClass = script.metaClass.theClass
            ClassLoader classLoader = theClass.classLoader
            if (classLoader.parent &&  classLoader.parent instanceof GroovyClassLoader) {
                (classLoader.parent as GroovyClassLoader).removeClassCacheEntry(theClass.name)
                (classLoader.parent as GroovyClassLoader).removeClassCacheEntry(theClass.name+ '$_run_closure1')
            }
            
        } catch(Exception e) {
            logger.catching(e)
        }
        
    }


}