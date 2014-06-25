/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.ResourceFactory;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.fs.*;
import io.milton.resource.CollectionResource;
import io.milton.resource.Resource;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.ContextUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class StaticResourceFactory implements ResourceFactory {

	private static final Logger logger = Logger.getLogger(StaticResourceFactory.class);
	
	private static final int EDIT_FILE_SCRIPT_WAIT_TIMEOUT = 15;

    private static final String STATIC_DIR_NAME = "s";

	private IWebSiteService webSiteService;
	private IAuthenticationService authenticationService;
	private ExecutorService executorService;

	private FileSystemResourceFactory fsResourceFactory;
	private String sRoot;

	public StaticResourceFactory(String sRoot, IWebSiteService webSiteService,
								 IAuthenticationService authenticationService, SecurityManager securityManager) {
		this.webSiteService = webSiteService;
		this.authenticationService = authenticationService;
		this.sRoot = sRoot;

		this.executorService = Executors.newCachedThreadPool();
		this.fsResourceFactory = new FileSystemResourceFactory(new File(sRoot), securityManager, sRoot);
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
	 * 		/var/onCourse/scripts/editFile.sh -p {file.getAbsolutePath()}
	 */
	private void executeEditFileScript(File file) {
		String scriptPath = ContextUtil.getCmsEditScriptPath();

		if (StringUtils.trimToNull(scriptPath) == null) {
			logger.error("Edit file script is not defined! Resources haven't been updated!");
			return;
		}

		List<String> scriptCommand = new ArrayList<>();

        String filePath = file.getAbsolutePath();

		scriptCommand.add(scriptPath);
		scriptCommand.add("-p");
		scriptCommand.add(String.format("\"%s\"", filePath));

		String userEmail = authenticationService.getUserEmail();

		if (userEmail != null) {
			scriptCommand.add("-e");
			scriptCommand.add(userEmail);
		}

		ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);

		try {
            logger.debug(String.format("Starting script '%s' for file '%s'", scriptPath, filePath));
            long time = System.currentTimeMillis();
			final Process process = processBuilder.start();

			Future<Integer> scriptCallFuture = executorService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return process.waitFor();
				}
			});

			scriptCallFuture.get(EDIT_FILE_SCRIPT_WAIT_TIMEOUT, TimeUnit.SECONDS);
            time = Math.round( (System.currentTimeMillis() - time) / 1000.0);
            logger.debug(String.format("script '%s' for file '%s' is finished. Time: '%d' sec", scriptPath, filePath, time));
		} catch (Exception e) {
			logger.error(String.format("Error executing script '%s' for file '%s'", scriptPath, filePath), e);
		}
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
                ((CmsFsDirectoryResource)r).setCustomName(STATIC_DIR_NAME);
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
			super.moveTo(newParent, newName);
			
			executeEditFileScript(getFile());
		}

		@Override
		public void delete() {
			super.delete();

			executeEditFileScript(getFile());
		}

		@Override
		public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
			super.replaceContent(in, length);
			
			executeEditFileScript(getFile());
		}

		@Override
		protected void doCopy(File dest) {
			super.doCopy(dest);
			
			executeEditFileScript(dest);
		}
	}
	
	private class CmsFsDirectoryResource extends FsDirectoryResource {
        private String customName;

		public CmsFsDirectoryResource(String host, FileSystemResourceFactory factory, File dir, FileContentService contentService) {
			super(host, factory, dir, contentService);
		}

		@Override
		public Resource createNew(String name, InputStream in, Long length, String contentType) throws IOException {
			FsResource resource = (FsResource) super.createNew(name, in, length, contentType);
			
			executeEditFileScript(resource.getFile());
			
			return resource;
		}

		@Override
		public void delete() {
			super.delete();

			executeEditFileScript(getFile());
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
	}
}
