package ish.oncourse.willow.editor.webdav


import io.milton.http.Auth
import io.milton.http.Request
import io.milton.http.ResourceFactory
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.fs.FileContentService
import io.milton.http.fs.FileSystemResourceFactory
import io.milton.http.fs.FsDirectoryResource
import io.milton.http.fs.FsFileResource
import io.milton.http.fs.FsResource
import io.milton.resource.CollectionResource
import io.milton.resource.Resource
import ish.oncourse.services.mail.EmailBuilder
import ish.oncourse.services.mail.SendEmail
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.StaticResourcePath
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.services.access.UserService
import ish.oncourse.willow.editor.webdav.jscompiler.JSCompiler
import ish.oncourse.willow.editor.website.WebSiteFunctions
import org.apache.commons.httpclient.URIException
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

class StaticResourceFactory  implements ResourceFactory {

    private static final Logger logger = LogManager.logger

    private String defaultJsStackPath

    private ICayenneService cayenneService
    private RequestService requestService

    private static final String SCSS_FILE_PATTERN = '^%s/stylesheets/src/(.*)\\.scss$'
    private static final String JS_FILE_PATTERN = '^%s/js/(.*)\\.js$'


    private UserService userService
    private ExecutorService executorService

    private FileSystemResourceFactory fsResourceFactory
    private String sRoot

    StaticResourceFactory(String sRoot, UserService userService,
                                 SecurityManager securityManager, ICayenneService cayenneService, RequestService requestService) {
        this.userService = userService
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.sRoot = sRoot
        this.fsResourceFactory = new FileSystemResourceFactory(new File(sRoot), securityManager, sRoot)
        this.executorService = Executors.newCachedThreadPool()
        
        try {
            this.defaultJsStackPath = null
            //this.defaultJsStackPath = URIUtil.decode(this.class.classLoader.getResource("ish/oncourse/cms/js").file)
        } catch (URIException e) {
            throw new RuntimeException(e)
        }
    }

    @Override
    Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException {
        String siteKey = WebSiteFunctions.getCurrentWebSite(requestService.request, cayenneService.newContext()).siteKey
        String rootDirName = "$sRoot/$siteKey"

        fsResourceFactory.root = new File(rootDirName)

        return getFsResource(host, path)
    }

    /**
     * Executes editFile.sh script passing specified file as a parameter.
     * E.g.:
     * /var/onCourse/scripts/editFile.sh -p {file.getAbsolutePath()}*/
    private void compileResources(File file) {

        ICompiler compiler = getCompiler(file)

        if (compiler) {
            long time = System.currentTimeMillis()
            compiler.compile()
            logger.warn("Compilation finished. file: {}, time: {}", file, (System.currentTimeMillis() - time))
            if (compiler.errors.empty) {
                runEditScript(file)
                runEditScript(compiler.result)
                runEditScript(compiler.gzResult)
            } else {
                runEditScript(file)
                String userEmail = userService.userEmail
                if (userEmail != null) {
                    EmailBuilder emailBuilder = GetEmailBuilder.valueOf(compiler.errorEmailTemplate,
                            userEmail,
                            userEmail,
                            WebSiteFunctions.getCurrentWebSite(requestService.request, cayenneService.newContext()).siteKey,
                            file.absolutePath,
                            compiler.errors.join('\n')).get()
                    SendEmail.valueOf(emailBuilder, true).send()
                }
            }
        } else {
            runEditScript(file)
        }
    }
    
    private ICompiler getCompiler(File file) {
        if (isJavaScript(file)) {
            return JSCompiler.valueOf(sRoot, defaultJsStackPath, WebSiteFunctions.getCurrentWebSite(requestService.request, cayenneService.newContext()))
        } else {
            return null
        }
    }

    private void runEditScript(File file) {
        RunEditScript.valueOf(file, userService, executorService).run()
    }

    private boolean isSCSSFile(File file) {
        String filePath = file.absolutePath
        //java script file path looks like /var/onCourse/s-draft/sitekey/stylesheets/src/**/*
        Pattern pattern = Pattern.compile(String.format(SCSS_FILE_PATTERN, fsResourceFactory.root.absolutePath))
        return pattern.matcher(filePath).matches()
    }

    private boolean isJavaScript(File file) {
        String filePath = file.absolutePath
        //java script file path looks like /var/onCourse/s-draft/sitekey/js/**/*
        Pattern pattern = Pattern.compile(String.format(JS_FILE_PATTERN, fsResourceFactory.root.absolutePath))
        return pattern.matcher(filePath).matches()
    }


    private Resource getFsResource(String host, String url) {
        url = stripContext(url)
        File requested = fsResourceFactory.resolvePath(fsResourceFactory.root, url)
        return resolveFile(host, requested)
    }

    FsResource resolveFile(String host, File file) {
        FsResource r
        if (!file.exists()) {
            return null
        } else if (file.directory) {
            r = new CmsFsDirectoryResource(host, fsResourceFactory, file, fsResourceFactory.contentService)
            //the code needs to show "s" root dir name instead of real filesystem file name.
            if (fsResourceFactory.root == file)
                ((CmsFsDirectoryResource) r).customName =  TopLevelDir.s.name()
        } else {
            r = new CmsFsFileResource(host, fsResourceFactory, file, fsResourceFactory.contentService)
        }

        return r
    }

    private String stripContext(String url) {
        String contextPath = fsResourceFactory.contextPath
        if (contextPath && contextPath.length() > 0) {
            url = url.replaceFirst('/' + contextPath, '')
            return url
        } else {
            return url
        }
    }

    private class CmsFsFileResource extends FsFileResource {

        CmsFsFileResource(String host, FileSystemResourceFactory factory, File file, FileContentService contentService) {
            super(host, factory, file, contentService)
        }

        @Override
        void moveTo(CollectionResource newParent, String newName) {
            File oldFile = getFile()
            super.moveTo(newParent, newName)

            compileResources(getFile())
            compileResources(oldFile)
        }

        @Override
        void delete() {
            super.delete()
            compileResources(getFile())
        }

        @Override
        void replaceContent(InputStream into, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
            super.replaceContent(into, length)
            compileResources(getFile())
        }

        @Override
        protected void doCopy(File dest) {
            super.doCopy(dest)
            compileResources(dest)
        }
    }

    private class CmsFsDirectoryResource extends FsDirectoryResource {
        private String customName

        CmsFsDirectoryResource(String host, FileSystemResourceFactory factory, File dir, FileContentService contentService) {
            super(host, factory, dir, contentService)
        }

        @Override
        void moveTo(CollectionResource newParent, String newName) {
            File oldFile = getFile()
            super.moveTo(newParent, newName)
            compileResources(getFile())
            compileResources(oldFile)
        }

        @Override
        protected void doCopy(File dest) {
            super.doCopy(dest)
            compileResources(dest)
        }

        @Override
        CollectionResource createCollection(String name) {
            FsDirectoryResource resource = (FsDirectoryResource) super.createCollection(name)
            compileResources(resource.getFile())
            return resource
        }

        @Override
        Resource createNew(String name, InputStream into, Long length, String contentType) throws IOException {
            FsResource resource = (FsResource) super.createNew(name, into, length, contentType)
            compileResources(resource.file)
            return resource
        }

        @Override
        void delete() {
            super.delete()
            compileResources(getFile())
        }

        String getName() {
            return this.customName != null ? customName : super.getName()
        }

        String getCustomName() {
            return customName
        }

        void setCustomName(String customName) {
            this.customName = customName
        }

        @Override
        boolean authorise(Request request, Request.Method method, Auth auth) {
            if (fsResourceFactory.root == getFile()) {
                return super.authorise(request, method, auth) && ArrayUtils.contains(TopLevelDir.s.allowedMethods, method)
            } else if (StaticResourcePath.getStaticResourcePathBy(request.absolutePath.replace(RootResourceFactory.WEBDAV_PATH_PREFIX, StringUtils.EMPTY))) {
                return super.authorise(request, method, auth) && ArrayUtils.contains(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD, method)
            } else {
                return super.authorise(request, method, auth)
            }
        }
    }
}
