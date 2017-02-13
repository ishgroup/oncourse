package ish.oncourse.cxf

import com.google.inject.Module
import io.bootique.BQModuleProvider

import java.lang.reflect.Type

/**
 * Created by akoira on 2/5/17.
 */
class CXFModuleProvider implements BQModuleProvider {
    @Override
    Module module() {
        return new CXFModule()
    }

    @Override
    Map<String, Class> configs() {
        return Collections.singletonMap("cxf", CXFModuleConfig.class)
    }
}
