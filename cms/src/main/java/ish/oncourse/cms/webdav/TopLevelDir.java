package ish.oncourse.cms.webdav;

import io.milton.http.Request;

import static io.milton.http.Request.Method.*;

public enum TopLevelDir {
    blocks(new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND, MKCOL}),
    pages(new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND, MKCOL}),
    s(new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND, MKCOL}),
    templates(new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND, MKCOL});

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
