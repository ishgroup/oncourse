package ish.oncourse.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class StaticResourcePathTest {

    @Test
    public void test() {
        assertEquals("/s/stylesheets/", StaticResourcePath.stylesheets.getWebDavPath());
        assertEquals("stylesheets", StaticResourcePath.stylesheets.getFileSystemPath());

        assertEquals("/s/stylesheets/css/", StaticResourcePath.stylesheetsCss.getWebDavPath());
        assertEquals("stylesheets/css", StaticResourcePath.stylesheetsCss.getFileSystemPath());

        assertEquals("/s/stylesheets/src/", StaticResourcePath.stylesheetsSrc.getWebDavPath());
        assertEquals("stylesheets/src", StaticResourcePath.stylesheetsSrc.getFileSystemPath());

        assertEquals("/s/js/", StaticResourcePath.js.getWebDavPath());
        assertEquals("js", StaticResourcePath.js.getFileSystemPath());
    }
}
