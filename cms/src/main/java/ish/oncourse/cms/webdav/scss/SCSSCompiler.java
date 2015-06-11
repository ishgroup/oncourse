package ish.oncourse.cms.webdav.scss;

import ish.oncourse.cms.webdav.ErrorEmailTemplate;
import ish.oncourse.cms.webdav.GetEmailBuilder;
import ish.oncourse.cms.webdav.GzipFile;
import ish.oncourse.cms.webdav.ICompiler;
import ish.oncourse.services.mail.EmailBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SCSSCompiler implements ICompiler {
	private static final Logger logger = LogManager.getLogger();

	public static final ErrorEmailTemplate ERROR_EMAIL_TEMPLATE = ErrorEmailTemplate.valueOf("support@ish.com.au",
			"[onCourse Website] SCSS compilation error",
			"Hi %s,\n" +
					"\n" +
					"\n" +
					"\n" +
					"This is the onCourse Website: %s\n" +
					"\n" +
					"\n" +
					"\n" +
					"I've found a problem with the SCSS file you uploaded. This means that the CSS will not be updated until you fix this error.\n" +
					"\n" +
					"\n" +
					"\n" +
					"File: %s\n" +
					"\n" +
					"\n" +
					"\n" +
					"Error:\n" +
					"%s");

	private static final String PROPERY_additional_import_paths = "additional_import_paths";
	private static final String PROPERY_project_path = "project_path";
	private static final String PROPERY_sass_path = "sass_path";
	private static final String PROPERY_css_path = "css_path";
	private static final String PROPERY_images_path = "images_path";
	private static final String PROPERY_sass_file = "sass_file";
	private static final String PROPERY_css_file = "css_file";


	private ScriptEngine rubyEngine;
	private File defaultScssPath;
	private File sitePath;
	private String siteScssFileName;

	private File siteScssPath;
	private File siteCssPath;
	private File siteScssFile;
	private File imagesPath;
	private File result;
	private File gzResult;

	private String script;

	private List<String> errors = new ArrayList<>();

	public void compile() {
		Bindings bindings = rubyEngine.createBindings();
		bindings.put(PROPERY_additional_import_paths, defaultScssPath.getAbsolutePath());
		bindings.put(PROPERY_project_path, sitePath.getAbsolutePath());
		bindings.put(PROPERY_sass_path, siteScssPath.getAbsolutePath());
		bindings.put(PROPERY_css_path, siteCssPath.getAbsolutePath());
		bindings.put(PROPERY_sass_file, siteScssFile.getAbsolutePath());
		bindings.put(PROPERY_css_file, result.getAbsolutePath());
		bindings.put(PROPERY_images_path, result.getAbsolutePath());

		try {
			rubyEngine.eval(script, bindings);
			gzResult();
		} catch (ScriptException e) {
			logger.error(e.getMessage(), e);
			errors.add(ExceptionUtils.getMessage(e));
		}
	}

	public File getResult() {
		return result;
	}

	public File getGzResult() {
		return gzResult;
	}

	public List<String> getErrors() {
		 return errors;
	}

	@Override
	public ErrorEmailTemplate getErrorEmailTemplate() {
		return ERROR_EMAIL_TEMPLATE;
	}


	private void gzResult() {
		try {
			GzipFile.valueOf(result, gzResult).gzip();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			errors.add(ExceptionUtils.getMessage(e));
		}
	}

	private void init() throws Exception {
		siteScssPath = new File(sitePath, "stylesheets/src");
		if (!siteScssPath.exists()) {
			siteScssPath.mkdirs();
		}

		siteCssPath  = new File(sitePath, "stylesheets/css");
		if (!siteCssPath.exists()) {
			siteCssPath.mkdirs();
		}

		siteScssFile = new File(siteScssPath, siteScssFileName);
		if (!siteScssFile.exists()) {
			FileUtils.writeStringToFile(siteScssFile, "@import \"base\";\n");
		}

		imagesPath = new File(sitePath, "img");

		result = new File(siteCssPath, FilenameUtils.getBaseName(siteScssFileName) + ".css");
		gzResult = new File(siteCssPath, result.getName() + ".gz");

		initScript();
	}

	private void initScript(){
		InputStream stream = null;
		try {
			stream = getClass().getResourceAsStream("compiler.rb");
			if (stream != null) {
				script = IOUtils.toString(stream);
			} else {
				throw new IllegalArgumentException();
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}


	public static SCSSCompiler valueOf(ScriptEngine scriptEngine,
	                                   File defaultScssPath,
	                                   File sitePath)
	{
		SCSSCompiler compiler = new SCSSCompiler();

		compiler.rubyEngine = scriptEngine;
		compiler.sitePath = sitePath;
		compiler.defaultScssPath = defaultScssPath;
		compiler.siteScssFileName = "site.scss";
		try {
			compiler.init();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return compiler;
	}
}
