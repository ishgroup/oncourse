package ish.oncourse.cms.webdav.scss;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SCSSCompilerTest {

	@Ignore
	public void testSite1() throws Exception {

		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("jruby");
		SCSSCompiler scssCompiler = SCSSCompiler.valueOf(scriptEngine,
				new File(getClass().getClassLoader().getResource("ish/oncourse/cms/webdav/scss/default/stylesheets/src").getFile()),
				new File(getClass().getClassLoader().getResource("ish/oncourse/cms/webdav/scss/site1").getFile()));
		scssCompiler.compile();

		assertTrue("Errors is not empty", scssCompiler.getErrors().isEmpty());
		assertTrue("result file exists", scssCompiler.getResult().exists());
		assertTrue("gz result file exists", scssCompiler.getGzResult().exists());

		String value = IOUtils.toString(new FileReader(scssCompiler.getResult()));
		assertTrue(value.contains("#file1"));
		assertTrue(value.contains("#file2"));
		assertTrue(value.contains("#site1"));
		assertTrue(value.contains("#site2"));
	}

	@Ignore
	public void testSite2() throws Exception {

		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("jruby");
		SCSSCompiler scssCompiler = SCSSCompiler.valueOf(scriptEngine,
				new File(getClass().getClassLoader().getResource("ish/oncourse/cms/webdav/scss/default/stylesheets/src").getFile()),
				new File(getClass().getClassLoader().getResource("ish/oncourse/cms/webdav/scss/site2").getFile()));
		scssCompiler.compile();

		assertTrue("Errors is not empty", scssCompiler.getErrors().isEmpty());
		assertTrue("result file exists", scssCompiler.getResult().exists());
		assertTrue("gz result file exists", scssCompiler.getGzResult().exists());

		String value = IOUtils.toString(new FileReader(scssCompiler.getResult()));
		assertTrue(value.contains("#file1"));
		assertTrue(value.contains("#file2"));
	}

}
