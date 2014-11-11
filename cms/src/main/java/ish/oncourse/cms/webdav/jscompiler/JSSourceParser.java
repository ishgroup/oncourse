/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.cms.webdav.jscompiler;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSSourceParser {

    private static final Logger LOGGER = LogManager.getLogger(JSSourceParser.class);

    //the file contains dependencies for default java scripts
    private static final String DEFAULT_BASE_JS = "base.js";

    //the file contains dependencies for default java scripts
    private static final String CUSTOM_SITE_JS = "site.js";

    private static final Pattern REQUIRE_PATTERN = Pattern.compile("^(//= require )(.*)");
    private static final Pattern MINIFY_PATTERN = Pattern.compile("^(//= minify )(off|on)");
    private static final Pattern OVERRIDE_PATTERN = Pattern.compile("^(//= )(.*)");


    private JSCompilerErrorHandler errorHandler;

    //full path to default/js folder
    private String defaultJSPath;
    //full path to custom/js folder
    private String customJSPath;


    //internal
    private JSSource fileJS;


    //result
    private boolean minify = false;
    private List<JSSource> sources = new ArrayList<>();

    private void init() {
        fileJS = JSSource.valueOf(CUSTOM_SITE_JS, new File(getCustomJSPath(), CUSTOM_SITE_JS), null);
        if (!fileJS.exists()) {
            fileJS = JSSource.valueOf(DEFAULT_BASE_JS, new File(getDefaultJSPath(), DEFAULT_BASE_JS), null);
        }
    }

    public void parse() {
        init();
        if (!fileJS.exists()) {
            errorHandler.logError(LOGGER, String.format("File %s does not exist", fileJS.getFile().getAbsolutePath()));
            return;
        }

        FileIterator fileIterator = new FileIterator(fileJS.getFile()) {
            @Override
            protected void callback(String line) {
                if (parseMinify(line))
                    return;
                parseRequire(fileJS, line);
            }
        };
        try {
            fileIterator.run();
        } catch (IOException e) {
            errorHandler.logError(LOGGER, e);
        }
    }

    private boolean parseRequire(JSSource parentJS, String line) {

        String fileName = null;
        boolean isOverride = false;

        Matcher matcher = REQUIRE_PATTERN.matcher(line);
        if (matcher.matches()) {
            //regexp group with index 2 contains filename see REQUIRE_PATTERN
            fileName = matcher.group(2);
        } else {
            matcher = OVERRIDE_PATTERN.matcher(line);
            if (matcher.matches()) {
                //regexp group with index 2 contains filename see OVERRIDE_PATTERN
                fileName = matcher.group(2);
                isOverride = true;
            }
        }

        if (fileName != null) {
            File file = new File(parentJS.getFile().getParent(), fileName);
            JSSource source = JSSource.valueOf(fileName, file, parentJS);
            if (source.exists()) {
                parseSource(source, isOverride);
            //
            } else if (fileName.equals(DEFAULT_BASE_JS)){
                file = new File(defaultJSPath, fileName);
                if (file.exists()) {
                    source = JSSource.valueOf(fileName, file, parentJS);
                    parseSource(source, isOverride);
                }
            }

            if (!source.exists())
            {
                errorHandler.logError(LOGGER, String.format("File %s does not exist", source.getFile()));
            }
            return true;
        }
        return false;
    }

    private Verify verify(JSSource source)
    {
        Verify result = new Verify();
        for (int i = 0; i < sources.size(); i++) {
            JSSource jsSource = sources.get(i);
            if (jsSource.isSameFile(source)) {
                result.theSame = jsSource;
                break;
            } else if (source.getFileName().equals(jsSource.getFileName())) {
                result.replacingIndex = i;
            }
        }
        return result;
    }

    private void parseSource(final JSSource source, boolean isOverride) {
        Verify verify = verify(source);
        if (verify.theSame != null)
        {
            errorHandler.logError(LOGGER, String.format("File %s is included in more then once. Usages:\n%s\n%s",
                    source.getFile(),
                    verify.theSame.getPath(),
                    source.getPath()));
            return;
        }

        addSource(source, verify, isOverride);
        FileIterator fileIterator = new FileIterator(source.getFile()) {
            @Override
            protected void callback(String line) {
                parseRequire(source, line);
            }
        };
        try {
            fileIterator.run();
        } catch (IOException e) {
            errorHandler.logError(LOGGER, e);
        }
    }


    private boolean parseMinify(String s) {
        Matcher matcher = MINIFY_PATTERN.matcher(s);
        if (matcher.matches()) {
            //regexp group with index 2 contains minify value
            String value = matcher.group(2);
            if (StringUtils.trimToEmpty(value).equals("on")) {
                minify = true;
            }
            return true;
        }
        return false;
    }

    private void addSource(JSSource source, Verify verify, boolean isOverride) {
        if (verify.replacingIndex > -1 && isOverride) {
            sources.remove(verify.replacingIndex);
            sources.add(verify.replacingIndex, source);
        } else {
            sources.add(source);
        }
    }

    public String getDefaultJSPath() {
        return defaultJSPath;
    }

    public void setDefaultJSPath(String defaultJSPath) {
        this.defaultJSPath = defaultJSPath;
    }

    public String getCustomJSPath() {
        return customJSPath;
    }

    public void setCustomJSPath(String customJSPath) {
        this.customJSPath = customJSPath;
    }

    public JSCompilerErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(JSCompilerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public List<JSSource> getSources() {
        return sources;
    }

    public boolean isMinify() {
        return minify;
    }

    public static abstract class FileIterator {

        private File file;

        public FileIterator(File file) {
            this.file = file;
        }

        public void run() throws IOException {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                LineIterator lineIterator = new LineIterator(fileReader);
                while (lineIterator.hasNext()) {
                    callback(lineIterator.nextLine());
                }
            } finally {
                IOUtils.closeQuietly(fileReader);
            }
        }

        protected abstract void callback(String line);

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    private static class Verify
    {
        private JSSource theSame;
        private int replacingIndex = -1;
    }

}
