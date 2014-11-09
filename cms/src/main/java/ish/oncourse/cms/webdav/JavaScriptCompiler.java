/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.mail.EmailBuilder;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptCompiler {
    private static final Logger LOGGER = LogManager.getLogger(JavaScriptCompiler.class);

    private static final String RESULT_FILE_NAME = "all.js";
    private static final String GZ_RESULT_FILE_NAME = "all.js.gz";

    //the file contains dependencies for default java scripts
    private static final String DEFAULT_BASE_JS = "base.js";

    //the file contains dependencies for default java scripts
    private static final String CUSTOM_SITE_JS = "site.js";

    private static final String REQUIRE_PATTERN = "^(//= require )(.*)";

    private static final String EMAIL_FROM = "support@ish.com.au";
    private static final String EMAIL_SUBJECT = "[onCourse Website] JSP Combiner error";
    private static final String EMAIL_BODY_TEMPLATE = "Hi %s,\n" +
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
            "%s";


    //full path to s-folder
    private String sRoot;
    //college web site key
    private String siteKey;

    //full path to default/js folder
    private String defaultJSPath;
    //full path to custom/js folder
    private String customJSPath;

    private Compiler compiler;
    private CompilerOptions compilerOptions;
    private List<SourceFile> inputFiles = new ArrayList<>();

    private List<String> errors = new ArrayList<>();

    private ByteArrayOutputStream errorsOutputStream;
    private File result;
    private File gzResult;

    public void init() {

        initFileSystem();

        initCompiler();

        File baseJSFile = new File(getDefaultJSPath(), DEFAULT_BASE_JS);
        if (!baseJSFile.exists() || !baseJSFile.isFile()) {
            logError(String.format("File %s does not exist", baseJSFile.getAbsolutePath()));
            return;
        }
        parse(baseJSFile);

        File siteJSFile = new File(getCustomJSPath(), CUSTOM_SITE_JS);
        if (siteJSFile.exists() && siteJSFile.isFile()) {
            parse(siteJSFile);
        }
    }

    private void initFileSystem() {
        customJSPath = String.format("%s/%s/js", sRoot, siteKey);
        defaultJSPath = String.format("%s/default/js", sRoot);

        result = new File(getCustomJSPath(), RESULT_FILE_NAME);
        gzResult = new File(getCustomJSPath(), GZ_RESULT_FILE_NAME);
    }

    private void initCompiler() {
        compiler = new Compiler();
        errorsOutputStream = new ByteArrayOutputStream();
        compiler.setErrorManager(new PrintStreamErrorManager(new PrintStream(errorsOutputStream)));
        compilerOptions = new CompilerOptions();
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
    }

    public void compile() {
        if (inputFiles.isEmpty()) {
            logError("Nothing to compile. Input js files is empty");
            return;
        }

        try {

            compiler.compile(Collections.EMPTY_LIST, inputFiles, compilerOptions);


            if (!compiler.hasErrors()) {
                saveResult(compiler);
                gzResult();
                LOGGER.debug("jsCombiner success");
            } else {
                logError(errorsOutputStream.toString());
                LOGGER.debug(String.format("jsCombiner failed: %s", StringUtils.join(errors, "\n")));
            }
        } catch (Exception e) {
            logError(e);
        } finally {
            IOUtils.closeQuietly(errorsOutputStream);
        }
    }

    private void logError(Exception e) {

        errors.add(String.format("Unexpected exception: %s", ExceptionUtils.getStackTrace(e)));
        LOGGER.error(e.getMessage(), e);
    }

    private void logError(String message) {
        errors.add(message);
        LOGGER.error(message);
    }


    private void parse(File file) {
        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            Pattern pattern = Pattern.compile(REQUIRE_PATTERN);

            while (reader.ready()) {
                String s = reader.readLine();
                Matcher matcher = pattern.matcher(s);
                if (matcher.matches()) {
                    String fileName = matcher.group(2);
                    File fileJS = new File(file.getParent(), fileName);
                    if (fileJS.exists() && fileJS.isFile()) {
                        inputFiles.add(SourceFile.fromFile(fileJS));
                    } else {
                        LOGGER.debug(String.format("File %s does not exist", s));
                    }
                } else {
                    LOGGER.debug(String.format("%s does not match", s));
                }
            }
        } catch (IOException e) {
            logError(e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(fileReader);
        }
    }

    private void gzResult() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        CompressorOutputStream gos = null;

        try {
            fis = new FileInputStream(result);
            fos = new FileOutputStream(gzResult);

            gos = new CompressorStreamFactory()
                    .createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
            IOUtils.copy(fis, gos);
        } catch (Exception e) {
            logError(e);
        } finally {
            IOUtils.closeQuietly(gos);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(fis);
        }

    }

    private void saveResult(Compiler compiler) {
        FileWriter outputFile = null;
        try {
            outputFile = new FileWriter(result);
            outputFile.write(compiler.toSource());
        } catch (IOException e) {
            logError(e);
        } finally {
            IOUtils.closeQuietly(outputFile);
        }
    }

    public boolean hasErrors() {
        return compiler.hasErrors() || !errors.isEmpty();
    }

    public String getSRoot() {
        return sRoot;
    }

    public void setSRoot(String sRoot) {
        this.sRoot = sRoot;
    }

    public String getDefaultJSPath() {
        return defaultJSPath;
    }


    public String getCustomJSPath() {
        return customJSPath;
    }

    public File getResult() {
        return result;
    }

    public File getGzResult() {
        return gzResult;
    }


    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public EmailBuilder buildErrorMail(String emailTo, String fileName) {
        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setFromEmail(EMAIL_FROM);
        emailBuilder.setToEmails(emailTo);
        emailBuilder.setSubject(EMAIL_SUBJECT);
        emailBuilder.setBody(String.format(EMAIL_BODY_TEMPLATE, emailTo, siteKey, fileName, StringUtils.join(errors, "\n")));
        return emailBuilder;
    }

    public static JavaScriptCompiler valueOf(String sRoot, WebSite webSite) {
        JavaScriptCompiler javaScriptCompiler = new JavaScriptCompiler();
        javaScriptCompiler.setSRoot(sRoot);
        javaScriptCompiler.setSiteKey(webSite.getSiteKey());
        javaScriptCompiler.init();
        return javaScriptCompiler;
    }
}
