package ish.oncourse.services.cache;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class EHCacheTest extends ServiceTest {
    private ICayenneService cayenneService;
    private IWebContentService service;
    private IWebSiteService webSiteService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
        InputStream st = EHCacheTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/cache/EHCacheTest.xml");

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService.class);
        service = getService(IWebContentService.class);
        webSiteService = getService(IWebSiteService.class);
    }

    @Test
    public void test() {
        ObjectSelect.query(WebSite.class).cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebSite.class.getSimpleName())
                .select(cayenneService.newContext());
    }
}
