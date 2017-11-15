package ish.oncourse.willow.editor.webdav.jscompiler

import ish.oncourse.model.WebSite
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Test
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class JSCompilerTest {
    private JSCompiler jsCompiler

    @Test
    void testV1() {
        String userDir = System.getProperty('user.dir')
        jsCompiler = JSCompiler.valueOf("$userDir/src/test/resources/ish/oncourse/editor/webdav/jscompiler",
                "$userDir/src/test/resources/ish/oncourse/editor/webdav/jscompiler/js",
                [getSiteKey: 'site1'] as WebSite )
        compileAndAssert()
    }

    @Test
     void testV2() {
        String userDir = System.getProperty('user.dir')
        jsCompiler = JSCompiler.valueOf("$userDir/src/test/resources/ish/oncourse/editor/webdav/jscompiler",
                "$userDir/src/test/resources/ish/oncourse/editor/webdav/jscompiler/js",
                [getSiteKey: 'siteV2'] as WebSite )
        compileAndAssert()

    }

    private void compileAndAssert() {
        jsCompiler.compile()
        assertFalse('Errors is not empty', jsCompiler.hasErrors())
        assertTrue('result file exists', jsCompiler.result.exists())
        assertTrue('gz result file exists', jsCompiler.gzResult.exists())
    }
    
    @After
    void after() throws Exception {
        FileUtils.forceDelete(jsCompiler.result)
        FileUtils.forceDelete(jsCompiler.gzResult)
    }
}
