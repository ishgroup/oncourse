package ish.oncourse.admin.services;

import ish.oncourse.model.WebSite;
import ish.oncourse.util.StaticResourcePath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateSiteFileStructureTest {

    private File sRootDir;

    @Before
    public void before() throws IOException {
        sRootDir = Files.createTempDirectory("CreateSiteFileStructureTest").toFile();
    }

    @Test
    public void test() {

        WebSite webSite = new WebSite();
        webSite.setSiteKey("TestWebSite");
        assertTrue(CreateSiteFileStructure.valueOf(webSite, sRootDir).create());
        File webSiteDir = new File(sRootDir, webSite.getSiteKey());
        assertTrue(webSiteDir.exists());
        assertTrue(webSiteDir.isDirectory());

        StaticResourcePath[] paths = StaticResourcePath.values();
        for (StaticResourcePath path : paths) {
            File file = new File(webSiteDir, path.getFileSystemPath());
            assertTrue(file.exists());
            assertTrue(file.isDirectory());
        }
    }

    @After
    public void after() throws IOException {
        sRootDir.delete();
    }

}
