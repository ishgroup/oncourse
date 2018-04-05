package ish.oncourse.api.cxf

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

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
