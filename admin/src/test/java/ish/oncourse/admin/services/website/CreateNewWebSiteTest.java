package ish.oncourse.admin.services.website;

import ish.oncourse.admin.services.AbstractTest;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static ish.oncourse.admin.AdminProperty.S_ROOT;
import static org.junit.Assert.assertTrue;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateNewWebSiteTest extends AbstractTest {

    private File sRootFile;

    @Override
    protected void configDataSet(ReplacementDataSet rDataSet) {

    }

    @Override
    protected InputStream getDataSource() {

        try {
            sRootFile = Files.createTempDirectory("CreateSiteFileStructureTest").toFile();
            InitialContextFactoryMock.bind(ContextUtil.S_ROOT, sRootFile.getAbsolutePath());

            return CreateNewWebSiteTest.class.getResourceAsStream("CreateNewWebSiteTest.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNewSiteWithoutTemplate() {
        ObjectContext context = cayenneService.newNonReplicatingContext();
        WebSite webSite1 = Cayenne.objectForPK(context, WebSite.class, 1001L);

        CreateNewWebSite createNewWebSite = CreateNewWebSite.valueOf("template2", "template2", "googleTagManager", "coursesRootTagName", null, ContextUtil.getSRoot(),
                webSite1.getCollege(), context,
                getService(IWebSiteVersionService.class), getService(IWebNodeService.class));
        createNewWebSite.create();
        WebSite template2 = createNewWebSite.getWebSite();

        createNewWebSite = CreateNewWebSite.valueOf("webSite2", "webSite2", "googleTagManager", "coursesRootTagName", template2, ContextUtil.getSRoot(),
                webSite1.getCollege(), context,
                getService(IWebSiteVersionService.class), getService(IWebNodeService.class));
        createNewWebSite.create();

        assertTrue(createNewWebSite.getErrors().toString(), createNewWebSite.getErrors().isEmpty());
    }
}
