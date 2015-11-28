package ish.oncourse.admin.services.website;

import ish.oncourse.admin.services.AdminTestModule;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateNewWebSiteTest extends ServiceTest {

    private ICayenneService cayenneService;
    private File sRootFile;

    @Before
    public void setup() throws Exception {
        InitialContext context = new InitialContext();
        context.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
        InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
        sRootFile = Files.createTempDirectory("CreateSiteFileStructureTest").toFile();
        InitialContextFactoryMock.bind(ContextUtil.S_ROOT, sRootFile.getAbsolutePath());

        initTest("ish.oncourse.admin.services", "", AdminTestModule.class);

        InputStream st = CreateNewWebSiteTest.class.getResourceAsStream("CreateNewWebSiteTest.xml");

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        ReplacementDataSet rDataSet;
        rDataSet = new ReplacementDataSet(dataSet);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), rDataSet);

        this.cayenneService = getService(ICayenneService.class);
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
