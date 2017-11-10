package ish.oncourse.willow.editor.webdav

import io.milton.common.ContentTypeUtils
import io.milton.common.Path
import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.CollectionResource
import io.milton.resource.CopyableResource
import io.milton.resource.DeletableResource
import io.milton.resource.GetableResource
import io.milton.resource.MoveableResource
import io.milton.resource.PropFindableResource
import io.milton.resource.ReplaceableResource
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebTemplate
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.website.WebTemplateFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.charset.Charset

class WebTemplateResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

    private static final Logger logger = LogManager.logger

    private WebTemplate webTemplate

    private ICayenneService cayenneService
    private Map<String, String> defaultTemplatesMap

    private WebSiteLayout layout
    private String templateName

    WebTemplateResource(String templateName, WebSiteLayout layout,
                               ICayenneService cayenneService,
                               SecurityManager securityManager, Map<String, String> defaultTemplatesMap) {
        super(securityManager)

        this.cayenneService = cayenneService

        this.templateName = templateName
        this.layout = layout
        this.defaultTemplatesMap = defaultTemplatesMap

        this.webTemplate = WebTemplateFunctions.getTemplateByName(getName(), layout)
    }

    @Override
    void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
    }

    @Override
    void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        if (webTemplate) {
            ObjectContext context = cayenneService.newContext()
            WebTemplate templateToDelete = context.localObject(webTemplate)
            context.deleteObjects(templateToDelete)
            context.commitChanges()
        }
    }

    @Override
    void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
        if (webTemplate) {
            out.write(webTemplate.content.bytes)
        } else {
            InputStream is = Thread.currentThread().contextClassLoader.getResourceAsStream(templateName)
            try {
                IOUtils.copy(is, out)
            } finally {
                IOUtils.closeQuietly(is)
            }
        }
        out.flush()
    }

    @Override
    Long getMaxAgeSeconds(Auth auth) {
        return null
    }

    @Override
    String getContentType(String accepts) {
        return ContentTypeUtils.findAcceptableContentType('text/html', accepts)
    }

    @Override
    Long getContentLength() {
        if (webTemplate) {
            //we should return number of bytes (not chars)
            return webTemplate.content.bytes.length as long
        }
        else
        {
            InputStream is = Thread.currentThread().contextClassLoader.getResourceAsStream(templateName)
            try {
                return (long) is.available()
            } catch (IOException e) {
                logger.catching(e)
                return null
            } finally {
                IOUtils.closeQuietly(is)
            }
        }
    }

    @Override
    void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {


        if (webTemplate) {
            String classPathContent  = null
            try {
                classPathContent = getClassPathContent(name)
            } catch (IOException e) {
                logger.error(e)
            }

            ObjectContext context = cayenneService.newContext()
            WebTemplate localTemplate = context.localObject(webTemplate)

            if (classPathContent && classPathContent == webTemplate.content) {
                context.deleteObjects(localTemplate)
                webTemplate = null
            } else {
                localTemplate.name = name
            }
            context.commitChanges()
        } else {
            //copies content from class path to new db WebTemplate
            InputStream is = Thread.currentThread().contextClassLoader.getResourceAsStream(templateName)
            try {
                (rDest as DirectoryResource).createNew(name, is, 0L, StringUtils.EMPTY)
            } catch (IOException e) {
                throw new BadRequestException(e.message, e)
            }
            finally {
                IOUtils.closeQuietly(is)
            }
        }
    }

    @Override
    Date getCreateDate() {
        if (webTemplate) {
            return webTemplate.created
        }

        return null
    }

    @Override
    String getName() {
        return Path.path(templateName).name
    }

    @Override
    Date getModifiedDate() {
        if (webTemplate) {
            return webTemplate.modified
        }

        return null
    }

    @Override
    void replaceContent(InputStream into, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {

        try {

            WebTemplate template
            StringWriter writer = new StringWriter()
            IOUtils.copy(into, writer, Charset.defaultCharset())

            String content = writer.toString()
            IOUtils.closeQuietly(writer)

            String classPathContent  = null
            try {
                classPathContent = getClassPathContent(templateName)
            } catch (IOException e) {
                logger.error(e)
            }

            ObjectContext context = cayenneService.newContext()
            if (webTemplate) {
                template = context.localObject(webTemplate)
                if (classPathContent && classPathContent == content) {
                    context.deleteObjects(template)
                    context.commitChanges()
                    webTemplate = null
                    return
                }
            } else {
                if (classPathContent && classPathContent == content) {
                    return
                }

                WebSiteLayout localLayout = context.localObject(layout)
                template = WebTemplateFunctions.createWebTemplate(getName(), content, localLayout)
            }
            template.content = content
            context.commitChanges()
        } catch (Exception e) {
            throw new BadRequestException('Can\'t replace template\'s content.', e)
        }
    }

    @Override
    String getUniqueId() {
        return null
    }

    private String getClassPathContent(String templateName) throws IOException {
        InputStream classPathIn = Thread.currentThread().contextClassLoader.getResourceAsStream(templateName)
        if (classPathIn == null) {
            String templatePath = defaultTemplatesMap[templateName]
            if (templatePath == null)
                return null
            classPathIn = Thread.currentThread().contextClassLoader.getResourceAsStream(templatePath)
        }
        if (classPathIn == null)
            return null

        String classPathContent
        StringWriter writer = new StringWriter()
        try {
            IOUtils.copy(classPathIn, writer, Charset.defaultCharset())
            classPathContent = writer.toString()
        } finally {
            IOUtils.closeQuietly(writer)
            IOUtils.closeQuietly(classPathIn)
        }
        return classPathContent
    }
}
