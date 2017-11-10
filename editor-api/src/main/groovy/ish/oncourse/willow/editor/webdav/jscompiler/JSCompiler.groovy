package ish.oncourse.willow.editor.webdav.jscompiler

import com.google.javascript.jscomp.*
import com.google.javascript.jscomp.Compiler
import ish.oncourse.model.WebSite
import ish.oncourse.willow.editor.webdav.ErrorEmailTemplate
import ish.oncourse.willow.editor.webdav.GzipFile
import ish.oncourse.willow.editor.webdav.ICompiler
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class JSCompiler implements ICompiler {
    
    private static final Logger logger = LogManager.logger
    private static final String RESULT_FILE_NAME = 'all.js'
    private static final String GZ_RESULT_FILE_NAME = 'all.js.gz'

    static final ErrorEmailTemplate ERROR_EMAIL_TEMPLATE = ErrorEmailTemplate.valueOf('support@ish.com.au',
            '[onCourse Website] JSP Combiner error',
            'Hi %s,\n' +
                    '\n' +
                    '\n' +
                    '\n' +
                    'his is the onCourse Website: %s\n' +
                    '\n' +
                    '\n' +
                    '\n' +
                    've found a problem with the JS file you uploaded. This means that the JS will not be updated until you fix this error.\n' +
                    '\n' +
                    '\n' +
                    '\n' +
                    'ile: %s\n' +
                    '\n' +
                    '\n' +
                    '\n' +
                    'rror:\n' +
                    '%s')


    //full path to s-folder
    private String sRoot
    //college web site key
    private String siteKey

    //full path to default/js folder
    private File defaultJSPath
    //full path to custom/js folder
    private File customJSPath

    private Compiler compiler
    private CompilerOptions compilerOptions
    private List<SourceFile> inputFiles = new ArrayList<>()
    private JSCompilerErrorHandler errorHandler = new JSCompilerErrorHandler()

    private boolean minify = false

    private ByteArrayOutputStream errorsOutputStream
    private File result
    private File gzResult

    void init() {

        initFileSystem()

        JSSourceParser sourceParser = new JSSourceParser()
        sourceParser.errorHandler = errorHandler
        sourceParser.customJSPath = customJSPath
        sourceParser.defaultJSPath = defaultJSPath
        sourceParser.parse()

        this.minify = sourceParser.minify
        sourceParser.sources.each { inputFiles << it.sourceFile }
        initCompiler()
    }

    private void initFileSystem() {
        customJSPath = new File("$sRoot/$siteKey/js" )
        result = new File(customJSPath, RESULT_FILE_NAME)
        gzResult = new File(customJSPath, GZ_RESULT_FILE_NAME)
    }

    private void initCompiler() {
        compiler = new Compiler()
        errorsOutputStream = new ByteArrayOutputStream()
        compiler.setErrorManager(new PrintStreamErrorManager(new PrintStream(errorsOutputStream)))
        compilerOptions = new CompilerOptions()
        if (minify) {
            CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions)
        } else {
            CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(compilerOptions)
        }
        compiler.initOptions(compilerOptions)
    }

    void compile() {
        if (!errorHandler.errors.empty)
            return
        if (inputFiles.empty) {
            errorHandler.logError(logger, 'Nothing to compile. Input js files is empty')
            return
        }

        try {
            compiler.compile(Collections.EMPTY_LIST, inputFiles, compilerOptions)
            if (!compiler.hasErrors()) {
                saveResult(compiler)
                gzResult()
                logger.debug('jsCombiner success')
            } else {
                errorHandler.logError(logger, errorsOutputStream.toString())
                logger.error('jsCombiner failed: {}', StringUtils.join(errorHandler.getErrors(), '\n'))
            }
        } catch (Exception e) {
            errorHandler.logError(logger, e)
        } finally {
            IOUtils.closeQuietly(errorsOutputStream)
        }
    }

    private void gzResult() {
        try {
            GzipFile.valueOf(result, gzResult).gzip()
        } catch (Exception e) {
            errorHandler.logError(logger, e)
        }
    }

    private void saveResult(Compiler compiler) {
        FileWriter outputFile = null
        try {
            outputFile = new FileWriter(result)
            outputFile.write(compiler.toSource())
        } catch (IOException e) {
            errorHandler.logError(logger, e)
        } finally {
            IOUtils.closeQuietly(outputFile)
        }
    }

    boolean hasErrors() {
        return compiler.hasErrors() || !errorHandler.errors.empty
    }

    File getResult() {
        return result
    }

    File getGzResult() {
        return gzResult
    }

    List<String> getErrors() {
        return errorHandler.errors
    }

    @Override
    ErrorEmailTemplate getErrorEmailTemplate() {
        return ERROR_EMAIL_TEMPLATE
    }

    static JSCompiler valueOf(String sRoot, String defaultJSPath, WebSite webSite) {
        JSCompiler javaScriptCompiler = new JSCompiler()
        javaScriptCompiler.sRoot = sRoot
        javaScriptCompiler.defaultJSPath = new File(defaultJSPath)
        javaScriptCompiler.siteKey = webSite.siteKey
        javaScriptCompiler.init()
        return javaScriptCompiler
    }
}
