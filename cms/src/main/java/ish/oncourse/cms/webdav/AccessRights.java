package ish.oncourse.cms.webdav;

import io.milton.http.Request;

import static io.milton.http.Request.Method.*;

public interface AccessRights {
    /**
     * Allowed request methods for read only dir and which can add child dir (like /cms/webdav/templates)
     */
    public static final Request.Method[] DIR_READ_ONLY_AND_ADD_CHILD = new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND, MKCOL};

    /**
     * Allowed request methods for read only dir (like /cms/webdav)
     */
    public static final Request.Method[] DIR_READ_ONLY = new Request.Method[]{GET, HEAD, OPTIONS, PROPFIND};
}
