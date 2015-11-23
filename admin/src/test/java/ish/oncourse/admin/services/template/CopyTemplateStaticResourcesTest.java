package ish.oncourse.admin.services.template;

import ish.oncourse.admin.services.CreateSiteFileStructure;
import ish.oncourse.model.WebSite;
import ish.oncourse.util.StaticResourcePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CopyTemplateStaticResourcesTest {

    private File sRootDir;
    @Before
    public void before() throws IOException {
        sRootDir = Files.createTempDirectory("CopyTemplateStaticResourcesTest").toFile();
    }

    @Test
    public void test() throws IOException {

        WebSite template = new WebSite();
        template.setSiteKey("TestTemplate");
        assertTrue(CreateSiteFileStructure.valueOf(template, sRootDir).create());

        File templateFile = new File(new File(sRootDir, template.getSiteKey()), StaticResourcePath.stylesheetsSrc.getFileSystemPath() + "/" + "test.scss");
        OutputStream os = FileUtils.openOutputStream(templateFile);
        IOUtils.closeQuietly(os);


        WebSite webSite = new WebSite();
        webSite.setSiteKey("TestWebSite");
        assertTrue(CreateSiteFileStructure.valueOf(webSite, sRootDir).create());


        assertTrue(CopyTemplateStaticResources.valueOf(template, webSite, sRootDir).copy());
        File webSiteFile = new File(new File(sRootDir, webSite.getSiteKey()), StaticResourcePath.stylesheetsSrc.getFileSystemPath() + "/" + "test.scss");
        assertTrue(webSiteFile.exists());
        assertTrue(webSiteFile.isFile());
    }

    @After
    public void after() throws IOException {
        sRootDir.delete();
    }
}
