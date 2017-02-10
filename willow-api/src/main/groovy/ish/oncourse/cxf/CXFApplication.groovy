package ish.oncourse.cxf

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.google.inject.Inject
import ish.oncourse.willow.service.ShoppingCartService

import javax.ws.rs.core.Application

/**
 * Created by akoira on 2/5/17.
 */
class CXFApplication extends Application{

    private List<Object> singletons = new ArrayList<>()
    private List<Class> classes = new ArrayList<>()
    private Map<String, Object> props = new HashMap<>()


    CXFApplication(Set<Object> resources, Map<String, Object> props) {
        singletons.addAll(resources)

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
