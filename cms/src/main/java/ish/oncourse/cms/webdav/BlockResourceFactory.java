/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.Path;
import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.Resource;
import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BlockResourceFactory implements ResourceFactory {
	
	private static final String BLOCK_DIR_NAME = "blocks";
	
	@Inject
	private IWebContentService webContentService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private ITextileConverter textileConverter;
	
	private SecurityManager securityManager;
	
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

		Path path = Path.path(url);

		if (path.isRoot()) {
			return new DirectoryResource(BLOCK_DIR_NAME, securityManager) {
				@Override
				public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
					return getBlockByName(childName);
				}

				@Override
				public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
					return listBlocks();
				}

				@Override
				public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) 
						throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
					
					StringWriter writer = new StringWriter();
					IOUtils.copy(inputStream, writer);
					
					String content = writer.toString();

					// check if there is an existing block with similar name
					WebContent block = webContentService.getWebContent(WebContent.NAME_PROPERTY, newName);
					
					if (block != null) {
						return changeBlock(block, newName, content);
					}
					
					return createNewBlock(newName, content);
				}
			};
		} else if (path.getLength() == 1) {
			String name = path.getName();
			
			return getBlockByName(name);
		}
		
		return null;
	}
	
	public List<WebContentResource> listBlocks() {
		List<WebContentResource> blocks = new ArrayList<>();
		
		for (WebContent block : webContentService.getBlocks()) {
			blocks.add(new WebContentResource(block, cayenneService, webContentService, textileConverter, securityManager));
		}
		
		return blocks;
	}
	
	public WebContentResource getBlockByName(String name) {
		WebContent block = webContentService.getWebContent(WebContent.NAME_PROPERTY, name);
		
		if (block != null) {
			return new WebContentResource(block, cayenneService, webContentService, textileConverter, securityManager);
		}
		
		return null;
	}
	
	public WebContentResource changeBlock(WebContent blockToChange, String name, String content) {
		
		ObjectContext context = cayenneService.newContext();
		
		WebContent block = context.localObject(blockToChange);
		
		block.setName(name);
		block.setContentTextile(content);
		block.setContent(textileConverter.convertCoreTextile(content));
		
		context.commitChanges();
		
		return new WebContentResource(block, cayenneService, webContentService, textileConverter, securityManager);
	}
	
	public WebContentResource createNewBlock(String name, String content) {
		ObjectContext ctx = cayenneService.newContext();
		
		WebContent block = ctx.newObject(WebContent.class);
		block.setName(name);
		block.setContentTextile(content);
		block.setContent(textileConverter.convertCoreTextile(content));

		WebContentVisibility visibility = ctx.newObject(WebContentVisibility.class);
		visibility.setRegionKey(RegionKey.unassigned);
		visibility.setWebContent(block);

		block.setWebSiteVersion(
				webSiteVersionService.getCurrentVersion(ctx.localObject(webSiteService.getCurrentWebSite())));
		
		ctx.commitChanges();
		
		return new WebContentResource(block, cayenneService, webContentService, textileConverter, securityManager);
	}
}
