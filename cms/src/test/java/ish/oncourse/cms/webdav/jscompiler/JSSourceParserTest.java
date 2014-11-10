package ish.oncourse.cms.webdav.jscompiler;


import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class JSSourceParserTest {

    @Test
    public void test()
    {
        JSSourceParser sourceParser = new JSSourceParser();
        sourceParser.setDefaultJSPath(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/default").getFile());
        sourceParser.setCustomJSPath(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/site").getFile());
        sourceParser.setErrorHandler(new JSCompilerErrorHandler());
        sourceParser.parse();
        assertTrue("Errors is not empty", sourceParser.getErrorHandler().getErrors().isEmpty());
        assertFalse("jssources is empty", sourceParser.getSources().isEmpty());
        assertEquals(7, sourceParser.getSources().size());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "base.js"),sourceParser.getSources().get(0).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "file1.js"),sourceParser.getSources().get(1).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "file2.js"),sourceParser.getSources().get(2).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "file3.js"),sourceParser.getSources().get(3).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "file4.js"),sourceParser.getSources().get(4).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "site1.js"),sourceParser.getSources().get(5).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "site2.js"),sourceParser.getSources().get(6).getFile());

        assertTrue(sourceParser.isMinify());
    }
}
