package ish.oncourse.cms.webdav.jscompiler;

import ish.oncourse.model.WebSite;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class JSCompilerTest {
    private JSCompiler jsCompiler;

    @Test
    public void testV1()
    {
        jsCompiler = JSCompiler.valueOf(String.format("%s/src/test/resources/ish/oncourse/cms/webdav/jscompiler", System.getProperty("user.dir")),
                String.format("%s/src/test/resources/ish/oncourse/cms/webdav/jscompiler/js", System.getProperty("user.dir")),
                new WebSite() {
                    @Override
                    public String getSiteKey() {
                        return "site1";
                    }
                }
        );
        jsCompiler.compile();
        assertFalse("Errors is not empty", jsCompiler.hasErrors());
        assertTrue("result file exists", jsCompiler.getResult().exists());
        assertTrue("gz result file exists", jsCompiler.getGzResult().exists());
    }

    @Test
    public void testV2()
    {
        jsCompiler = JSCompiler.valueOf(String.format("%s/src/test/resources/ish/oncourse/cms/webdav/jscompiler", System.getProperty("user.dir")),
                String.format("%s/src/test/resources/ish/oncourse/cms/webdav/jscompiler/js", System.getProperty("user.dir")),
                new WebSite() {
                    @Override
                    public String getSiteKey() {
                        return "siteV2";
                    }
                }
        );
        jsCompiler.compile();
        assertFalse("Errors is not empty", jsCompiler.hasErrors());
        assertTrue("result file exists", jsCompiler.getResult().exists());
        assertTrue("gz result file exists", jsCompiler.getGzResult().exists());
    }

    @After
    public void after() throws Exception
    {
        FileUtils.forceDelete(jsCompiler.getResult());
        FileUtils.forceDelete(jsCompiler.getGzResult());
    }
}
