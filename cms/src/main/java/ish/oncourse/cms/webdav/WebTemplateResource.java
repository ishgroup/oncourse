/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.ContentTypeUtils;
import io.milton.common.Path;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.*;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.templates.IWebTemplateService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

public class WebTemplateResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

    private static final Logger logger = LogManager.getLogger();

	private WebTemplate webTemplate;

	private ICayenneService cayenneService;
	private IWebTemplateService webTemplateService;
    private Map<String, String> defaultTemplatesMap;

	private WebSiteLayout layout;
    private String templateName;

	public WebTemplateResource(String templateName, WebSiteLayout layout,
							   ICayenneService cayenneService, IWebTemplateService webTemplateService,
							   SecurityManager securityManager, Map<String, String> defaultTemplatesMap) {
		super(securityManager);

		this.cayenneService = cayenneService;
		this.webTemplateService = webTemplateService;

		this.templateName = templateName;
		this.layout = layout;
        this.defaultTemplatesMap = defaultTemplatesMap;

        this.webTemplate = webTemplateService.getTemplateByName(getName(), layout);
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		if (webTemplate != null) {
			ObjectContext context = cayenneService.newContext();
			WebTemplate templateToDelete = context.localObject(webTemplate);
			context.deleteObjects(templateToDelete);
			context.commitChanges();
		}
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		if (webTemplate != null) {
			out.write(webTemplate.getContent().getBytes());
		} else {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
            try {
                IOUtils.copy(is, out);
            } finally {
                IOUtils.closeQuietly(is);
            }
		}
		out.flush();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return ContentTypeUtils.findAcceptableContentType("text/html", accepts);
	}

	@Override
	public Long getContentLength() {
		if (webTemplate != null) {
            //we should return number of bytes (not chars)
			return (long) webTemplate.getContent().getBytes().length;
		}
        else
        {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
            try {
                return (long) is.available();
            } catch (IOException e) {
                logger.catching(e);
                return null;
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
	}

	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {


		if (webTemplate != null) {
            String classPathContent  = null;
            try {
                classPathContent = getClassPathContent(name);
            } catch (IOException e) {
                logger.error(e);
            }

            ObjectContext context = cayenneService.newContext();
            WebTemplate localTemplate = context.localObject(webTemplate);

            if (classPathContent != null && classPathContent.equals(webTemplate.getContent()))
            {
                context.deleteObjects(localTemplate);
                webTemplate = null;
            } else
            {
                localTemplate.setName(name);
            }
			context.commitChanges();
		}
        else
        {
            //copies content from class path to new db WebTemplate
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
            try {
                ((DirectoryResource)rDest).createNew(name, is, 0L, StringUtils.EMPTY);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage(), e);
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }
	}

	@Override
	public Date getCreateDate() {
		if (webTemplate != null) {
			return webTemplate.getCreated();
		}

		return null;
	}

	@Override
	public String getName() {
		return Path.path(templateName).getName();
	}

	@Override
	public Date getModifiedDate() {
		if (webTemplate != null) {
			return webTemplate.getModified();
		}

		return null;
	}

	@Override
	public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {

			try {

				WebTemplate template;

				StringWriter writer = new StringWriter();
				IOUtils.copy(in, writer);

				String content = writer.toString();
                IOUtils.closeQuietly(writer);

                String classPathContent  = null;
                try {
                    classPathContent = getClassPathContent(templateName);
                } catch (IOException e) {
                    logger.error(e);
                }

                ObjectContext context = cayenneService.newContext();
                if (webTemplate != null) {
                    template = context.localObject(webTemplate);
                    if (classPathContent != null && classPathContent.equals(content)) {
                        context.deleteObjects(template);
                        context.commitChanges();
                        webTemplate = null;
                        return;
                    }
                } else {
                    if (classPathContent != null && classPathContent.equals(content)) {
                        return;
                    }

                    WebSiteLayout localLayout = context.localObject(layout);

                    template = webTemplateService.createWebTemplate(getName(), content, localLayout);
                }
                template.setContent(content);
                context.commitChanges();
			} catch (Exception e) {
				throw new BadRequestException("Can't replace template's content.", e);
			}
	}

    @Override
    public String getUniqueId() {
        return null;
    }

    private String getClassPathContent(String templateName) throws IOException {
        InputStream classPathIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
        if (classPathIn == null)
        {
            String templatePath = defaultTemplatesMap.get(templateName);
            if (templatePath == null)
                return null;
            classPathIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        }

        if (classPathIn == null)
            return null;

        String classPathContent;
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(classPathIn, writer);
            classPathContent = writer.toString();
        } finally {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(classPathIn);
        }
        return classPathContent;
    }
}
