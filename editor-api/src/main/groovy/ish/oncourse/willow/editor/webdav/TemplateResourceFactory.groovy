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
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import ish.oncourse.willow.editor.website.WebTemplateFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
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
    public static final String SYSTEM_TEMP_DIR = '_system'
    
    private ICayenneService cayenneService
    private RequestService requestService
    private SecurityManager securityManager
    
    private Closure<WebTemplate> getTemplate = { String name, WebSiteLayout layout -> WebTemplateFunctions.getTemplateByName(name, layout) }
    public static Closure<WebTemplate> getNull = { String name, WebSiteLayout layout -> null }

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
            return new DirectoryResource(TEMPLATE_DIR_NAME, securityManager,
                { String newName, InputStream inputStream, Long length, String contentType ->
                    return null
                } as Closure<Resource>, 
                { String childName ->
                    return getLayoutResourceByName(childName)
                } as Closure<Resource>, 
                {
                    return listLayouts() as ArrayList
                } as Closure<ArrayList<Resource>>, 
                { Request request, Request.Method method, Auth auth ->
                    return  method in TopLevelDir.templates.allowedMethods
                } as Closure<Boolean>, 
                { String newName ->
                    WebSiteLayout layout = createLayout(newName)
                    return new LayoutDirectoryResource(layout.layoutKey, layout, securityManager, cayenneService, this)
                } as Closure<CollectionResource>)
            
        } else if (path.length == 1) {
            String name = path.name

            return getLayoutResourceByName(name)
        } else {
            String layoutKey = path.first
            String templateName = path.stripFirst.name

            if (SYSTEM_TEMP_DIR == layoutKey) {
                return new WebTemplateResource(defaultTemplatesMap[templateName], null, cayenneService, securityManager, defaultTemplatesMap, getNull)
            } else {
                return getTemplateResourceByName(templateName, getLayoutByName(layoutKey))
            }

        }
    }

    private List<DirectoryResource> listLayouts() {
        ObjectContext context = cayenneService.newContext()

        List<WebSiteLayout> layouts = ObjectSelect.query(WebSiteLayout.class).
                where(WebSiteLayout.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(requestService.request, cayenneService.newContext()))).
                select(context)

        List<DirectoryResource> directoryResources = []
        for (WebSiteLayout layout : layouts) {
            directoryResources.add(new LayoutDirectoryResource(layout.layoutKey, layout, securityManager, cayenneService, this))
        }
        directoryResources.add(new SystemDirectoryResource(securityManager, defaultTemplatesMap, cayenneService))
        return directoryResources
    }

    private List<WebTemplateResource> listTemplates(WebSiteLayout layout) {
        List<WebTemplateResource> templates = []
        WebTemplateFunctions.getTemplatesForLayout(layout)
                .each { templates << new WebTemplateResource(it.name, layout, cayenneService, securityManager, defaultTemplatesMap, getTemplate) }

        return templates
    }

    private DirectoryResource getLayoutResourceByName(String name) {
        if (SYSTEM_TEMP_DIR == name) {
            return new SystemDirectoryResource(securityManager, defaultTemplatesMap, cayenneService)
        }
        WebSiteLayout layout = getLayoutByName(name)
        return layout != null ? new LayoutDirectoryResource(layout.layoutKey, layout, securityManager, cayenneService, this) : null
    }

    private WebSiteLayout getLayoutByName(String name) {
        ObjectContext context = cayenneService.newContext()

        return ObjectSelect.query(WebSiteLayout).
                where(WebSiteLayout.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(requestService.request, cayenneService.newContext()))).
                and(WebSiteLayout.LAYOUT_KEY.eq(name)).
                selectOne(context)
    }

    private WebSiteLayout createLayout(String name) {
        ObjectContext context = cayenneService.newContext()
        WebSiteVersion siteVersion = WebSiteVersionFunctions.getCurrentVersion(requestService.request, context)
        WebSiteLayout layout = context.newObject(WebSiteLayout)
        layout.layoutKey = name
        layout.webSiteVersion = siteVersion
        context.commitChanges()
        return layout
    }

    private WebTemplateResource getTemplateResourceByName(String name, WebSiteLayout layout) {
        return new WebTemplateResource(name, layout, cayenneService, securityManager,defaultTemplatesMap, getTemplate)
    }

    private class SystemDirectoryResource extends DirectoryResource {
        SystemDirectoryResource(SecurityManager securityManager, Map<String, String> systemTemplatesMap, ICayenneService cayenneService) {
            super(SYSTEM_TEMP_DIR, 
                    securityManager,
                    null,
                    { String childName ->  
                    } as Closure<Resource>,
                    {  
                        return systemTemplatesMap.values().collect {  new WebTemplateResource(it, null as WebSiteLayout, cayenneService, securityManager, systemTemplatesMap, getNull as Closure<WebTemplate> ) }
                    } as Closure<ArrayList<Resource>>,
                    { Request request, Request.Method method, Auth auth ->
                        return method in AccessRights.DIR_READ_ONLY
                    } as Closure<Boolean>
            )
        }
    }

    private class LayoutDirectoryResource extends DirectoryResource {

        private WebSiteLayout layout

        LayoutDirectoryResource(String name, WebSiteLayout layout, SecurityManager securityManager, ICayenneService cayenneService, TemplateResourceFactory factory) {
            super(name, securityManager, 
                    { String newName, InputStream inputStream, Long length, String contentType ->
                        ObjectContext context = cayenneService.newContext()
                        WebSiteLayout localLayout = context.localObject(layout)
        
                        StringWriter writer = new StringWriter()
                        IOUtils.copy(inputStream, writer, Charset.defaultCharset())
        
                        WebTemplate template = WebTemplateFunctions.createWebTemplate(newName, writer.toString(), localLayout)
        
                        context.commitChanges()
        
                        return new WebTemplateResource(template.name, localLayout, cayenneService, securityManager, defaultTemplatesMap as Map<String, String> , getTemplate as Closure<WebTemplate>)
                    } as Closure<Resource>, 
                    { String childName ->
                        return factory.getTemplateResourceByName(childName, layout)
                    } as Closure<Resource>,
                    {
                        return factory.listTemplates(layout) as ArrayList
                    } as Closure<ArrayList<Resource>>,
                    { Request request, Request.Method method, Auth auth ->
                        if (layout.layoutKey == WebNodeType.DEFAULT_LAYOUT_KEY) {
                            return method in AccessRights.DIR_READ_ONLY_AND_ADD_CHILD
                        } else {
                            return true
                        }
                    } as Closure<Boolean>)
            this.layout = layout
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
            if (layoutToChange.webNodeTypes.empty) {
                context.deleteObjects(layoutToChange)
                context.commitChanges()
            } else {
                throw new BadRequestException(this, "Remove ${layoutToChange.webNodeTypes.size()} blocks from layout.".toString())
            }
        }
    }
}

