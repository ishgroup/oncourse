package ish.oncourse.willow.editor.webdav.jscompiler

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class JSSourceParserTest {
    @Test
    void testSite() {
        JSSourceParser sourceParser = new JSSourceParser()
        sourceParser.setDefaultJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/js/v1').file))
        sourceParser.setCustomJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/site/js').file))
        sourceParser.setErrorHandler(new JSCompilerErrorHandler())
        sourceParser.parse()
        assertTrue('Errors is not empty', sourceParser.errorHandler.errors.empty)
        assertFalse('jssources is empty', sourceParser.sources.empty)
        assertEquals(7, sourceParser.sources.size())
        assertEquals(new File(sourceParser.defaultJSPath, 'base.js'),sourceParser.sources[0].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file1.js'),sourceParser.sources[1].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file2.js'),sourceParser.sources[2].file)
        assertEquals(new File(sourceParser.customJSPath, 'file3.js'),sourceParser.sources[3].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file4.js'),sourceParser.sources[4].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra1.js'),sourceParser.sources[5].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra2.js'),sourceParser.sources[6].file)

        assertTrue(sourceParser.minify)
    }

    @Test
    void testSite1() {
        JSSourceParser sourceParser = new JSSourceParser()
        sourceParser.setDefaultJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/js/v1').file))
        sourceParser.setCustomJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/site1/js').file))
        sourceParser.setErrorHandler(new JSCompilerErrorHandler())
        sourceParser.parse()
        assertTrue('Errors is not empty', sourceParser.errorHandler.errors.empty)
        assertFalse('jssources is empty', sourceParser.sources.empty)
        assertEquals(10, sourceParser.sources.size())
        assertEquals(new File(sourceParser.defaultJSPath, 'base.js'), sourceParser.sources[0].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file1.js'),sourceParser.sources[1].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file2.js'),sourceParser.sources[2].file)
        assertEquals(new File(sourceParser.customJSPath, 'file3.js'),sourceParser.sources[3].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'file4.js'),sourceParser.sources[4].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra.nested.js'),sourceParser.sources[5].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra1.js'),sourceParser.sources[6].file)
        assertEquals(new File(sourceParser.customJSPath, 'sub/extra2.js'),sourceParser.sources[7].file)
        assertEquals(new File(sourceParser.customJSPath, 'sub/extra.nested.js'),sourceParser.sources[8].file)
        assertEquals(new File(sourceParser.customJSPath, 'sub/extra1.js'), sourceParser.sources[9].file)
    }

    @Test
    void testSiteV2() {
        JSSourceParser sourceParser = new JSSourceParser()
        sourceParser.setDefaultJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/js').file))
        sourceParser.setCustomJSPath(new File(JSSourceParserTest.classLoader.getResource('ish/oncourse/editor/webdav/jscompiler/siteV2/js').file))
        sourceParser.setErrorHandler(new JSCompilerErrorHandler())
        sourceParser.parse()
        assertTrue('Errors is not empty', sourceParser.errorHandler.errors.empty)
        assertFalse('jssources is empty', sourceParser.sources.empty)
        assertEquals(10, sourceParser.sources.size())
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/base.js'),sourceParser.sources[0].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/sub1/base.js'),sourceParser.sources[1].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/sub1/file1.js'),sourceParser.sources[2].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/sub1/file2.js'),sourceParser.sources[3].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/sub2/base.js'),sourceParser.sources[4].file)
        assertEquals(new File(sourceParser.customJSPath, 'file3.js'),sourceParser.sources[5].file)
        assertEquals(new File(sourceParser.defaultJSPath, 'v2/sub2/file4.js'),sourceParser.sources[6].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra.nested.js'),sourceParser.sources[7].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra1.js'),sourceParser.sources[8].file)
        assertEquals(new File(sourceParser.customJSPath, 'extra2.js'),sourceParser.sources[9].file)
    }

}
