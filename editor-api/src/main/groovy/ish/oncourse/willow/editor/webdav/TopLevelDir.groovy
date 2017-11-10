package ish.oncourse.willow.editor.webdav

import io.milton.http.Request

enum TopLevelDir {
    blocks(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    pages(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    s(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    templates(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD)

    private Request.Method[] allowedMethods

    TopLevelDir(Request.Method[] allowedMethods) {
        this.allowedMethods = allowedMethods
    }

    Request.Method[] getAllowedMethods() {
        return allowedMethods
    }

    static boolean has(String name)
    {
        TopLevelDir[] values = values()
        for (TopLevelDir value : values) {
            if (value.name() == name) {
                return true
            }
        }
        return false
    }

}
