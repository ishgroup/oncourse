/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav.jscompiler;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import ish.oncourse.cms.webdav.ErrorEmailTemplate;
import ish.oncourse.cms.webdav.GetEmailBuilder;
import ish.oncourse.cms.webdav.GzipFile;
import ish.oncourse.cms.webdav.ICompiler;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.mail.EmailBuilder;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSCompiler implements ICompiler {
    private static final Logger logger = LogManager.getLogger();

    private static final String RESULT_FILE_NAME = "all.js";
    private static final String GZ_RESULT_FILE_NAME = "all.js.gz";

    public static final ErrorEmailTemplate ERROR_EMAIL_TEMPLATE = ErrorEmailTemplate.valueOf("support@ish.com.au",
            "[onCourse Website] JSP Combiner error",
            "Hi %s,\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "This is the onCourse Website: %s\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "I've found a problem with the JS file you uploaded. This means that the JS will not be updated until you fix this error.\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "File: %s\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "Error:\n" +
                    "%s");


    //full path to s-folder
    private String sRoot;
    //college web site key
    private String siteKey;

    //full path to default/js folder
    private File defaultJSPath;
    //full path to custom/js folder
    private File customJSPath;

    private Compiler compiler;
    private CompilerOptions compilerOptions;
    private List<SourceFile> inputFiles = new ArrayList<>();
    private JSCompilerErrorHandler errorHandler = new JSCompilerErrorHandler();

    private boolean minify = false;

    private ByteArrayOutputStream errorsOutputStream;
    private File result;
    private File gzResult;

    public void init() {

        initFileSystem();

        JSSourceParser sourceParser = new JSSourceParser();
        sourceParser.setErrorHandler(errorHandler);
        sourceParser.setCustomJSPath(customJSPath);
        sourceParser.setDefaultJSPath(defaultJSPath);
        sourceParser.parse();

        this.minify = sourceParser.isMinify();
        List<JSSource> sources = sourceParser.getSources();
        for (JSSource source : sources) {
            inputFiles.add(source.getSourceFile());
        }
        initCompiler();
    }

    private void initFileSystem() {
        customJSPath = new File(String.format("%s/%s/js", sRoot, siteKey));

        result = new File(customJSPath, RESULT_FILE_NAME);
        gzResult = new File(customJSPath, GZ_RESULT_FILE_NAME);
    }

    private void initCompiler() {
        compiler = new Compiler();
        errorsOutputStream = new ByteArrayOutputStream();
        compiler.setErrorManager(new PrintStreamErrorManager(new PrintStream(errorsOutputStream)));
        compilerOptions = new CompilerOptions();
        if (minify) {
            CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
        } else {
            CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(compilerOptions);
        }
        compiler.initOptions(compilerOptions);
    }

    public void compile() {
        if (!errorHandler.getErrors().isEmpty())
            return;
        if (inputFiles.isEmpty()) {
            errorHandler.logError(logger, "Nothing to compile. Input js files is empty");
            return;
        }

        try {
            compiler.compile(Collections.EMPTY_LIST, inputFiles, compilerOptions);
            if (!compiler.hasErrors()) {
                saveResult(compiler);
                gzResult();
                logger.debug("jsCombiner success");
            } else {
                errorHandler.logError(logger, errorsOutputStream.toString());
                logger.error("jsCombiner failed: {}", StringUtils.join(errorHandler.getErrors(), "\n"));
            }
        } catch (Exception e) {
            errorHandler.logError(logger, e);
        } finally {
            IOUtils.closeQuietly(errorsOutputStream);
        }
    }

    private void gzResult() {
        try {
            GzipFile.valueOf(result, gzResult).gzip();
        } catch (Exception e) {
            errorHandler.logError(logger, e);
        }
    }

    private void saveResult(Compiler compiler) {
        FileWriter outputFile = null;
        try {
            outputFile = new FileWriter(result);
            outputFile.write(compiler.toSource());
        } catch (IOException e) {
            errorHandler.logError(logger, e);
        } finally {
            IOUtils.closeQuietly(outputFile);
        }
    }

    public boolean hasErrors() {
        return compiler.hasErrors() || !errorHandler.getErrors().isEmpty();
    }

    public File getResult() {
        return result;
    }

    public File getGzResult() {
        return gzResult;
    }

    public List<String> getErrors() {
        return errorHandler.getErrors();
    }

    @Override
    public ErrorEmailTemplate getErrorEmailTemplate() {
        return ERROR_EMAIL_TEMPLATE;
    }

    public static JSCompiler valueOf(String sRoot, String defaultJSPath, WebSite webSite) {
        JSCompiler javaScriptCompiler = new JSCompiler();
        javaScriptCompiler.sRoot = sRoot;
        javaScriptCompiler.defaultJSPath = new File(defaultJSPath);
        javaScriptCompiler.siteKey = webSite.getSiteKey();
        javaScriptCompiler.init();
        return javaScriptCompiler;
    }
}
