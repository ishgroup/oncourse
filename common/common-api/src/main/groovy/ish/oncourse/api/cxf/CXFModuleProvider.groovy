package ish.oncourse.api.cxf

import com.google.inject.Module
import io.bootique.BQModuleProvider

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
