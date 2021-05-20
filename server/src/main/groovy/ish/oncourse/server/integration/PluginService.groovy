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

package ish.oncourse.server.integration

import com.google.inject.Binder
import com.google.inject.Inject
import com.google.inject.Injector
import groovy.transform.CompileStatic
import io.bootique.cayenne.CayenneModule
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner

import java.lang.reflect.Method
import java.util.regex.Pattern

@CompileStatic
class PluginService {

    public static final String PLUGIN_PACKAGE = "ish.oncourse"
    private static final Set<Class<? extends PluginTrait>> PLUGIN_CLASSES =
            (new Reflections(PLUGIN_PACKAGE).getTypesAnnotatedWith(Plugin) as Set<Class<? extends PluginTrait>>)
    private static final String CAYENNE_RESOURCES_FOLDER = 'cayenne/'
    private static final String CAYENNE_FILE_PATTERN = 'cayenne-.*.*.xml'


    private Injector injector

    @Inject
    PluginService(Injector injector) {
        this.injector = injector
    }

    static void configurePlugin(Binder binder) {

        Set<String> cayenneProjects = getPluggableResources(CAYENNE_RESOURCES_FOLDER, CAYENNE_FILE_PATTERN)
        if (cayenneProjects && !cayenneProjects.empty) {
            cayenneProjects.each { CayenneModule.extend(binder).addProject(it) }
        }
        getMethods(OnConfigure)?.each {it.invoke(null, binder)}
    }
    
    /**
     * Check if only one integration instance of certain type can be created
     *
     * @param type type of the integration instance
     * @return Boolean onlyOne property value
     */
    static boolean onlyOne(int type) {
        return getPluginClass(type).getAnnotation(Plugin).oneOnly()
    }

    static Class<? extends PluginTrait> getPluginClass(int type) {
        PLUGIN_CLASSES.find { it.getAnnotation(Plugin).type() == type}
    }

    /**
     * Looking for 'onSave' integration handler
     *
     * @param type type of the integration instance
     * @return Method onSave integration handler
     */
    static Method onSave(int type) {
        return getPluginClass(type)?.getMethods()?.find {it.getAnnotation(OnSave)}
    }
    
    /**
     * Looking for 'getProps' integration getter
     *
     * @param type type of the integration instance
     * @return Method getProps integration method
     */
    static Method getProps(int type) {
        return getPluginClass(type)?.getMethods()?.find {it.getAnnotation(GetProps)}
    }

    /**
     * Looking for class path resources, plugin scripts or additional cayenne files
     *
     * @param resourcePath
     * @param filePattern
     * @return Method getProps integration method
     */
    static Set<String> getPluggableResources(String resourcePath, String filePattern) {
        return new Reflections(resourcePath, new ResourcesScanner()).getResources(Pattern.compile(filePattern))
    }


    /**
     * Looking for 'onStart' plugin handler and invoke them
     */
    List<Method> onStart() {
        getMethods(OnStart)?.each { it.invoke(it.declaringClass.newInstance(injector: injector))}
    }
    
    private static List<Method> getMethods(Class annotation) {
        return (PLUGIN_CLASSES*.methods?.flatten() as List<Method>)
                ?.findAll { Method m -> m.getAnnotation(annotation) }
    }

}
