package ish.oncourse.willow.editor.webdav

import com.google.inject.Module
import io.bootique.BQModuleProvider 

class WebdavModuleProvider implements BQModuleProvider {
    @Override
    Module module() {
        return new WebdavModule()
    }
}