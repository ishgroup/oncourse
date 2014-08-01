package ish.oncourse.cms.webdav;

import io.milton.http.Request;

public enum TopLevelDir {
    blocks(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    pages(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    s(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD),
    templates(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD);

    private Request.Method[] allowedMethods;

    TopLevelDir(Request.Method[] allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public Request.Method[] getAllowedMethods() {
        return allowedMethods;
    }

    public static boolean has(String name)
    {
        TopLevelDir[] values = TopLevelDir.values();
        for (TopLevelDir value : values) {
            if (value.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
