package ish.oncourse.cms.webdav.jscompiler;


import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class JSSourceParserTest {

    @Test
    public void testSite()
    {
        JSSourceParser sourceParser = new JSSourceParser();
        sourceParser.setDefaultJSPath(new File(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/default/js").getFile()));
        sourceParser.setCustomJSPath(new File(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/site/js").getFile()));
        sourceParser.setErrorHandler(new JSCompilerErrorHandler());
        sourceParser.parse();
        assertTrue("Errors is not empty", sourceParser.getErrorHandler().getErrors().isEmpty());
        assertFalse("jssources is empty", sourceParser.getSources().isEmpty());
        assertEquals(9, sourceParser.getSources().size());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "base.js"),sourceParser.getSources().get(0).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/base.js"),sourceParser.getSources().get(1).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/file1.js"),sourceParser.getSources().get(2).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/file2.js"),sourceParser.getSources().get(3).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub2/base.js"),sourceParser.getSources().get(4).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "file3.js"),sourceParser.getSources().get(5).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub2/file4.js"),sourceParser.getSources().get(6).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "extra1.js"),sourceParser.getSources().get(7).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "extra2.js"),sourceParser.getSources().get(8).getFile());

        assertTrue(sourceParser.isMinify());
    }

    @Test
    public void testSite1()
    {
        JSSourceParser sourceParser = new JSSourceParser();
        sourceParser.setDefaultJSPath(new File(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/default/js").getFile()));
        sourceParser.setCustomJSPath(new File(JSSourceParserTest.class.getClassLoader().getResource("ish/oncourse/cms/webdav/jscompiler/site1/js").getFile()));
        sourceParser.setErrorHandler(new JSCompilerErrorHandler());
        sourceParser.parse();
        assertTrue("Errors is not empty", sourceParser.getErrorHandler().getErrors().isEmpty());
        assertFalse("jssources is empty", sourceParser.getSources().isEmpty());
        assertEquals(12, sourceParser.getSources().size());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "base.js"),sourceParser.getSources().get(0).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/base.js"),sourceParser.getSources().get(1).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/file1.js"),sourceParser.getSources().get(2).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub1/file2.js"),sourceParser.getSources().get(3).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub2/base.js"),sourceParser.getSources().get(4).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "file3.js"),sourceParser.getSources().get(5).getFile());
        assertEquals(new File(sourceParser.getDefaultJSPath(), "sub2/file4.js"),sourceParser.getSources().get(6).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "extra.nested.js"),sourceParser.getSources().get(7).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "extra1.js"),sourceParser.getSources().get(8).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "sub/extra2.js"),sourceParser.getSources().get(9).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "sub/extra.nested.js"),sourceParser.getSources().get(10).getFile());
        assertEquals(new File(sourceParser.getCustomJSPath(), "sub/extra1.js"),sourceParser.getSources().get(11).getFile());
    }
}
