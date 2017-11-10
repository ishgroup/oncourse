package ish.oncourse.willow.editor.webdav.jscompiler

import org.apache.commons.io.IOUtils
import org.apache.commons.io.LineIterator
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.regex.Matcher
import java.util.regex.Pattern

class JSSourceParser {

    private static final Logger logger = LogManager.logger

    //the file contains dependencies for default java scripts
    private static final String DEFAULT_BASE_JS = 'base.js'

    //the file contains dependencies for default java scripts
    private static final String CUSTOM_SITE_JS = 'site.js'

    private static final Pattern REQUIRE_PATTERN = Pattern.compile('^(//= require )(.*)')
    private static final Pattern MINIFY_PATTERN = Pattern.compile('^(//= minify )(off|on)')
    private static final Pattern OVERRIDE_PATTERN = Pattern.compile('^(//= )(.*)')

    private JSCompilerErrorHandler errorHandler

    //full path to default/js folder
    private File defaultJSPath
    //full path to custom/js folder
    private File customJSPath
    //internal
    private JSSource fileJS
    //result
    private boolean minify = false
    private List<JSSource> sources = []

    private void init() {
        fileJS = JSSource.valueOf(CUSTOM_SITE_JS, new File(getCustomJSPath(), CUSTOM_SITE_JS), null)
        if (!fileJS.exists()) {
            fileJS = JSSource.valueOf(DEFAULT_BASE_JS, new File(getDefaultJSPath(), DEFAULT_BASE_JS), null)
        }
    }

    void parse() {
        init()
        if (!fileJS.exists()) {
            errorHandler.logError(logger, "File ${fileJS.file.absolutePath} does not exist")
            return
        }

        FileIterator fileIterator = new FileIterator(fileJS.getFile()) {
            @Override
            protected void callback(String line) {
                if (parseMinify(line))
                    return
                parseRequire(fileJS, line)
            }
        }
        try {
            fileIterator.run()
        } catch (IOException e) {
            errorHandler.logError(logger, e)
        }
    }

    private boolean parseRequire(JSSource parentJS, String line) {

        String fileName = null
        boolean isOverride = false

        Matcher matcher = REQUIRE_PATTERN.matcher(line)
        if (matcher.matches()) {
            //regexp group with index 2 contains filename see REQUIRE_PATTERN
            fileName = StringUtils.trimToEmpty(matcher.group(2))
        } else {
            matcher = OVERRIDE_PATTERN.matcher(line)
            if (matcher.matches()) {
                //regexp group with index 2 contains filename see OVERRIDE_PATTERN
                fileName = StringUtils.trimToEmpty(matcher.group(2))
                isOverride = true
            }
        }

        if (fileName) {
            File file = new File(parentJS.file.parent, fileName)
            JSSource source = JSSource.valueOf(fileName, file, parentJS)
            if (source.exists()) {
                parseSource(source, isOverride)
            } else if (fileName.endsWith(DEFAULT_BASE_JS)){
                file = new File(defaultJSPath, fileName)
                logger.warn('base.js file path: {}', file)
                if (!file.exists()) {
                    file = new File(defaultJSPath, "v1/$fileName")
                    logger.warn('base.js file path: {}', file)
                }
                if (file.exists()) {
                    source = JSSource.valueOf(fileName, file, parentJS)
                    parseSource(source, isOverride)
                }
            }

            if (!source.exists()) {
                errorHandler.logError(logger, "File ${source.fileName} does not exist")
            }
            return true
        }
        return false
    }

    private Verify verify(JSSource source) {
        Verify result = new Verify()
        for (int i = 0; i < sources.size(); i++) {
            JSSource jsSource = sources[i]
            if (jsSource.isSameFile(source)) {
                result.theSame = jsSource
                break
            } else if (source.fileName == jsSource.fileName) {
                result.replacingIndex = i
            }
        }
        return result
    }

    private void parseSource(final JSSource source, boolean isOverride) {
        Verify verify = verify(source)
        if (verify.theSame) {
            errorHandler.logError(logger, "File ${source.file} is included in more then once. Usages:\n${verify.theSame.path}\n${source.path}")
            return
        }

        addSource(source, verify, isOverride)
        FileIterator fileIterator = new FileIterator(source.getFile()) {
            @Override
            protected void callback(String line) {
                parseRequire(source, line)
            }
        }
        try {
            fileIterator.run()
        } catch (IOException e) {
            errorHandler.logError(logger, e)
        }
    }


    private boolean parseMinify(String s) {
        Matcher matcher = MINIFY_PATTERN.matcher(s)
        if (matcher.matches()) {
            //regexp group with index 2 contains minify value
            String value = StringUtils.trimToEmpty(matcher.group(2))
            if (value == 'on') {
                minify = true
            }
            return true
        }
        return false
    }

    private void addSource(JSSource source, Verify verify, boolean isOverride) {
        if (verify.replacingIndex > -1 && isOverride) {
            sources.remove(verify.replacingIndex)
            sources.add(verify.replacingIndex, source)
        } else {
            sources.add(source)
        }
    }

    File getDefaultJSPath() {
        return defaultJSPath
    }

    void setDefaultJSPath(File defaultJSPath) {
        this.defaultJSPath = defaultJSPath
    }

    File getCustomJSPath() {
        return customJSPath
    }

    void setCustomJSPath(File customJSPath) {
        this.customJSPath = customJSPath
    }

    JSCompilerErrorHandler getErrorHandler() {
        return errorHandler
    }

    void setErrorHandler(JSCompilerErrorHandler errorHandler) {
        this.errorHandler = errorHandler
    }

    List<JSSource> getSources() {
        return sources
    }

    boolean isMinify() {
        return minify
    }

    static abstract class FileIterator {

        private File file

        FileIterator(File file) {
            this.file = file
        }

        void run() throws IOException {
            FileReader fileReader = null
            try {
                fileReader = new FileReader(file)
                LineIterator lineIterator = new LineIterator(fileReader)
                while (lineIterator.hasNext()) {
                    callback(lineIterator.nextLine())
                }
            } finally {
                IOUtils.closeQuietly(fileReader)
            }
        }

        protected abstract void callback(String line)

        File getFile() {
            return file
        }

        void setFile(File file) {
            this.file = file
        }
    }

    private static class Verify
    {
        private JSSource theSame
        private int replacingIndex = -1
    }

}
