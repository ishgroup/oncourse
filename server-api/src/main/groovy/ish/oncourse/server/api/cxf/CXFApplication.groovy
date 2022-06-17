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

package ish.oncourse.server.api.cxf

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.jaxrs.provider.JavaTimeTypesParamConverterProvider

import javax.ws.rs.core.Application

/**
 * Created by akoira on 2/5/17.
 */
class CXFApplication extends Application{

    private Set<Object> singletons = new HashSet<>()
    private Set<Class> classes = new HashSet<>()
    private Map<String, Object> props = new HashMap<>()


    CXFApplication(Set<Object> resources, Set<Object> features, Map<String, String> props) {
        singletons.addAll(resources)
        singletons.addAll(features)
        classes.add(JacksonJsonProvider.class)
        classes.add(JacksonJaxbJsonProvider.class)
        classes.add(ObjectMapperContextResolver.class)
        props.putAll(props)
    }

    @Override
    Set<Class<?>> getClasses() {
        return classes
    }

    @Override
    Set<Object> getSingletons() {
        return singletons
    }

    @Override
    Map<String, Object> getProperties() {
        return props
    }
}
