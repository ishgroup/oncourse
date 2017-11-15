package ish.oncourse.willow.editor.webdav

import io.milton.common.Path
import io.milton.http.Auth
import io.milton.http.Request
import io.milton.http.ResourceFactory
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.resource.CollectionResource
import io.milton.resource.Resource
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebTemplate
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import ish.oncourse.willow.editor.website.WebTemplateFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder

import java.nio.charset.Charset
import java.util.regex.Pattern

class TemplateResourceFactory implements ResourceFactory {

    private static final Logger logger = LogManager.logger

    private static final String TEMPLATE_DIR_NAME = 'templates'
    private static final String DEFAULT_TEMPLATES_PACKAGE = 'ish.oncourse.ui'
    
    private ICayenneService cayenneService
    private RequestService requestService
    private SecurityManager securityManager

    private Map<String, String> defaultTemplatesMap

    TemplateResourceFactory(ICayenneService cayenneService, RequestService requestService, SecurityManager securityManager) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.securityManager = securityManager
    }

    void initDefaultResources() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(DEFAULT_TEMPLATES_PACKAGE))
        // exclude resources with *.internal.* in package name
                .filterInputsBy(new FilterBuilder().include("^$DEFAULT_TEMPLATES_PACKAGE.*").exclude('.*(.internal.).*'))
                .setScanners(new ResourcesScanner()))

        Set<String> templates = reflections.getResources(Pattern.compile('.*\\.tml'))
        
        this.defaultTemplatesMap = [:]
        templates.each { defaultTemplatesMap.put(Path.path(it).name, it) }
    }

    @Override
    Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

        Path path = Path.path(url)

        if (path.root) {
            return new DirectoryResource(TEMPLATE_DIR_NAME, securityManager) {
                @Override
                Resource child(String childName) throws NotAuthorizedException, BadRequestException {
                    return getLayoutResourceByName(childName)
                }

                @Override
                List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
                    return listLayouts()
                }

                @Override
                Resource createNew(String newName, InputStream inputStream, Long length, String contentType)
                        throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
                    return null
                }

                @Override
                CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
                    WebSiteLayout layout = createLayout(newName)

                    return new LayoutDirectoryResource(layout.layoutKey, layout, securityManager)
                }

                @Override
                boolean authorise(Request request, Request.Method method, Auth auth) {
                    return super.authorise(request,method,auth) && ArrayUtils.contains(TopLevelDir.templates.allowedMethods, method)
                }
            }
        } else if (path.length == 1) {
            String name = path.name

            return getLayoutResourceByName(name)
        } else {
            String layoutKey = path.first
            String templateName = path.stripFirst.name

            return getTemplateResourceByName(templateName, getLayoutByName(layoutKey))
        }
    }

    private List<DirectoryResource> listLayouts() {
        ObjectContext context = cayenneService.newContext()

        List<WebSiteLayout> layouts = ObjectSelect.query(WebSiteLayout.class).
                where(WebSiteLayout.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(requestService.request, cayenneService.sharedContext()))).
                select(context)

        List<DirectoryResource> directoryResources = []
        for (WebSiteLayout layout : layouts) {
            directoryResources.add(new LayoutDirectoryResource(layout.layoutKey, layout, securityManager))
        }

        return directoryResources
    }

    private List<WebTemplateResource> listTemplates(WebSiteLayout layout) {
        List<WebTemplateResource> templates = []

        for (String templateFileName : defaultTemplatesMap.values()) {
            templates.add(new WebTemplateResource(templateFileName, layout, cayenneService, securityManager, defaultTemplatesMap))
        }

        WebTemplateFunctions.getTemplatesForLayout(layout)
                .findAll {!defaultTemplatesMap.containsKey(it.name)}
                .each { templates << new WebTemplateResource(it.name, layout, cayenneService, securityManager, defaultTemplatesMap) }

        return templates
    }

    private DirectoryResource getLayoutResourceByName(String name) {
        WebSiteLayout layout = getLayoutByName(name)
        return layout != null ? new LayoutDirectoryResource(layout.layoutKey, layout, securityManager) : null
    }

    private WebSiteLayout getLayoutByName(String name) {
        ObjectContext context = cayenneService.newContext()

        return ObjectSelect.query(WebSiteLayout).
                where(WebSiteLayout.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(requestService.request, cayenneService.sharedContext()))).
                and(WebSiteLayout.LAYOUT_KEY.eq(name)).
                selectOne(context)
    }

    private WebSiteLayout createLayout(String name) {
        ObjectContext context = cayenneService.newContext()
        WebSiteVersion siteVersion = context.localObject(WebSiteVersionFunctions.getCurrentVersion(requestService.request, cayenneService.sharedContext()))
        WebSiteLayout layout = context.newObject(WebSiteLayout)
        layout.layoutKey = name
        layout.webSiteVersion = siteVersion
        context.commitChanges()
        return layout
    }

    private WebTemplateResource getTemplateResourceByName(String name, WebSiteLayout layout) {
        String templateName = defaultTemplatesMap[name]

        if (templateName == null) {
            templateName = name
        }

        return new WebTemplateResource(templateName, layout, cayenneService, securityManager,defaultTemplatesMap)
    }

    private class LayoutDirectoryResource extends DirectoryResource {

        private WebSiteLayout layout

        LayoutDirectoryResource(String name, WebSiteLayout layout, SecurityManager securityManager) {
            super(name, securityManager)
            this.layout = layout
        }

        @Override
        Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
            ObjectContext context = cayenneService.newContext()
            WebSiteLayout localLayout = context.localObject(layout)

            StringWriter writer = new StringWriter()
            IOUtils.copy(inputStream, writer, Charset.defaultCharset())

            WebTemplate template = WebTemplateFunctions.createWebTemplate(newName, writer.toString(), localLayout)

            context.commitChanges()

            return new WebTemplateResource(template.name, localLayout, cayenneService, securityManager, defaultTemplatesMap)
        }

        @Override
        Resource child(String childName) throws NotAuthorizedException, BadRequestException {
            return getTemplateResourceByName(childName, layout)
        }

        @Override
        List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
            return listTemplates(layout)
        }

        @Override
        void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
            ObjectContext context = cayenneService.newContext()
            WebSiteLayout layoutToChange = context.localObject(layout)
            layoutToChange.layoutKey = name
            context.commitChanges()
        }

        @Override
        void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
            ObjectContext context = cayenneService.newContext()
            WebSiteLayout layoutToChange = context.localObject(layout)
            context.deleteObjects(layoutToChange)
            context.commitChanges()
        }

        @Override
        boolean authorise(Request request, Request.Method method, Auth auth) {
            if (layout.layoutKey == WebNodeType.DEFAULT_LAYOUT_KEY) {
                return super.authorise(request,method,auth) && method in AccessRights.DIR_READ_ONLY_AND_ADD_CHILD
            } else {
                return super.authorise(request,method,auth)
            }
        }
    }
}

