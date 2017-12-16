package ish.oncourse.services.cache;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.junit.Before;
import org.junit.Test;

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
        new LoadDataSet().dataSetFile("ish/oncourse/services/cache/EHCacheTest.xml").load(testContext.getDS());

        cayenneService = getService(ICayenneService.class);
        service = getService(IWebContentService.class);
        webSiteService = getService(IWebSiteService.class);
    }

    @Test
    public void test() {
        ObjectSelect.query(WebSite.class).cacheStrategy(QueryCacheStrategy.SHARED_CACHE, WebSite.class.getSimpleName())
                .select(cayenneService.newContext());
    }
}
