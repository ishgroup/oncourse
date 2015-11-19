/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.ResourceFactory;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.fs.*;
import io.milton.resource.CollectionResource;
import io.milton.resource.Resource;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.webdav.jscompiler.JSCompiler;
import ish.oncourse.services.mail.EmailBuilder;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.StaticResourcePath;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class StaticResourceFactory implements ResourceFactory {

    private static final Logger logger = LogManager.getLogger();

    private String defaultJsStackPath;
    private String defaultScssPath;

    private static final String SCSS_FILE_PATTERN = "^%s/stylesheets/src/(.*)\\.scss$";
    private static final String JS_FILE_PATTERN = "^%s/js/(.*)\\.js$";
    private static final String JS_TEMP_FILE_PATTERN = "^%s/js/(.*)\\.js\\.(.*)$";


    private IMailService mailService;
    private IWebSiteService webSiteService;
    private IAuthenticationService authenticationService;
    private ExecutorService executorService;
    private ScriptEngine scriptEngine;

    private FileSystemResourceFactory fsResourceFactory;
    private String sRoot;

    public StaticResourceFactory(String sRoot,
                                 IWebSiteService webSiteService,
                                 IAuthenticationService authenticationService,
                                 SecurityManager securityManager,
                                 IMailService mailService) {
        this.webSiteService = webSiteService;
        this.authenticationService = authenticationService;
        this.sRoot = sRoot;
        this.mailService = mailService;


        this.fsResourceFactory = new FileSystemResourceFactory(new File(sRoot), securityManager, sRoot);

        this.executorService = Executors.newCachedThreadPool();
        try {
            this.defaultJsStackPath = URIUtil.decode(getClass().getClassLoader().getResource("ish/oncourse/cms/js").getFile());
            this.defaultScssPath = URIUtil.decode(getClass().getClassLoader().getResource("ish/oncourse/cms/stylesheets/src").getFile());
        } catch (URIException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException {
		String siteKey = webSiteService.getCurrentWebSite().getSiteKey();
		String rootDirName = String.format("%s/%s", sRoot, siteKey);

		fsResourceFactory.setRoot(new File(rootDirName));

		return getFsResource(host, path);
	}

    /**
     * Executes editFile.sh script passing specified file as a parameter.
     * E.g.:
     * /var/onCourse/scripts/editFile.sh -p {file.getAbsolutePath()}
     */
    private void compileResources(File file) {

        ICompiler compiler = getCompiler(file);

        if (compiler != null) {
            long time = System.currentTimeMillis();
            compiler.compile();
            logger.warn("Compilation finished. file: {}, time: {}", file, (System.currentTimeMillis() - time));
            if (compiler.getErrors().isEmpty())
            {
                runEditScript(file);
                runEditScript(compiler.getResult());
                runEditScript(compiler.getGzResult());
            }
            else
            {
                runEditScript(file);
                String userEmail = authenticationService.getUserEmail();
                if (userEmail != null)
                {
                    EmailBuilder emailBuilder = GetEmailBuilder.valueOf(compiler.getErrorEmailTemplate(),
                            userEmail,
                            userEmail,
                            webSiteService.getCurrentWebSite().getSiteKey(),
                            file.getAbsolutePath(),
                            StringUtils.join(compiler.getErrors(), "\n")).get();
                    mailService.sendEmail(emailBuilder, true);
                }
            }
        } else {
            runEditScript(file);
        }
    }

    @Nullable
    private ICompiler getCompiler(File file) {
        if (isJavaScript(file)) {
            return JSCompiler.valueOf(sRoot, defaultJsStackPath, webSiteService.getCurrentWebSite());
//        } if (isSCSSFile(file)) {
//            return SCSSCompiler.valueOf(scriptEngine,
//                    new File(defaultScssPath),
//                    new File(String.format("%s/%s", sRoot, webSiteService.getCurrentWebSite().getSiteKey())));
        } else {
            return null;
        }
    }

    private void runEditScript(File file) {
        RunEditScript.valueOf(file, authenticationService, executorService).run();
    }

    private boolean isSCSSFile(File file) {
        String filePath = file.getAbsolutePath();
        //java script file path looks like /var/onCourse/s-draft/sitekey/stylesheets/src/**/*
        Pattern pattern = Pattern.compile(String.format(SCSS_FILE_PATTERN, fsResourceFactory.getRoot().getAbsolutePath()));
        return pattern.matcher(filePath).matches();
    }

    private boolean isJavaScript(File file) {
        String filePath = file.getAbsolutePath();
        //java script file path looks like /var/onCourse/s-draft/sitekey/js/**/*
        Pattern pattern = Pattern.compile(String.format(JS_FILE_PATTERN, fsResourceFactory.getRoot().getAbsolutePath()));
        return pattern.matcher(filePath).matches();
    }


    private Resource getFsResource(String host, String url) {
		url = stripContext(url);
		File requested = fsResourceFactory.resolvePath(fsResourceFactory.getRoot(), url);
		return resolveFile(host, requested);
	}

	public FsResource resolveFile(String host, File file) {
		FsResource r;
		if (!file.exists()) {
			return null;
		} else if (file.isDirectory()) {
			r = new CmsFsDirectoryResource(host, fsResourceFactory, file, fsResourceFactory.getContentService());
            //the code needs to show "s" root dir name instead of real filesystem file name.
            if (fsResourceFactory.getRoot().equals(file))
                ((CmsFsDirectoryResource)r).setCustomName(TopLevelDir.s.name());
		} else {
			r = new CmsFsFileResource(host, fsResourceFactory, file, fsResourceFactory.getContentService());
		}

		return r;
	}

	private String stripContext(String url) {
		String contextPath = fsResourceFactory.getContextPath();
		if (contextPath != null && contextPath.length() > 0) {
			url = url.replaceFirst('/' + contextPath, "");
			return url;
		} else {
			return url;
		}
	}

	private class CmsFsFileResource extends FsFileResource {

		public CmsFsFileResource(String host, FileSystemResourceFactory factory, File file, FileContentService contentService) {
			super(host, factory, file, contentService);
		}

		@Override
		public void moveTo(CollectionResource newParent, String newName) {
			File oldFile = getFile();
			super.moveTo(newParent, newName);

			compileResources(getFile());
			compileResources(oldFile);
		}

		@Override
		public void delete() {
			super.delete();

			compileResources(getFile());
		}

		@Override
		public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
			super.replaceContent(in, length);

			compileResources(getFile());
		}

		@Override
		protected void doCopy(File dest) {
			super.doCopy(dest);

			compileResources(dest);
		}
	}

	private class CmsFsDirectoryResource extends FsDirectoryResource {
        private String customName;

		public CmsFsDirectoryResource(String host, FileSystemResourceFactory factory, File dir, FileContentService contentService) {
			super(host, factory, dir, contentService);
		}

        @Override
        public void moveTo(CollectionResource newParent, String newName) {
            File oldFile = getFile();
            super.moveTo(newParent, newName);
            compileResources(getFile());
            compileResources(oldFile);
        }

        @Override
        protected void doCopy(File dest) {
            super.doCopy(dest);
            compileResources(dest);
        }

        @Override
        public CollectionResource createCollection(String name) {
            FsDirectoryResource resource = (FsDirectoryResource) super.createCollection(name);
            compileResources(resource.getFile());
            return resource;
        }

        @Override
		public Resource createNew(String name, InputStream in, Long length, String contentType) throws IOException {
			FsResource resource = (FsResource) super.createNew(name, in, length, contentType);

			compileResources(resource.getFile());

			return resource;
		}

		@Override
		public void delete() {
			super.delete();

			compileResources(getFile());
		}

		public String getName()
        {
            return this.customName != null ? customName : super.getName();
        }

        public String getCustomName() {
            return customName;
        }

        public void setCustomName(String customName) {
            this.customName = customName;
		}

        @Override
        public boolean authorise(Request request, Request.Method method, Auth auth) {
            if (fsResourceFactory.getRoot().equals(getFile())) {
                return super.authorise(request,method,auth) && ArrayUtils.contains(TopLevelDir.s.getAllowedMethods(), method);
            } else if (StaticResourcePath.getStaticResourcePathBy(request.getAbsolutePath().replace(RootResourceFactory.WEBDAV_PATH_PREFIX, org.apache.commons.lang3.StringUtils.EMPTY)) != null) {
                return super.authorise(request,method,auth) && ArrayUtils.contains(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD, method);
            } else {
                return super.authorise(request, method, auth);
            }
        }
    }
}
