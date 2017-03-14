import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.io.IOUtils
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder

import java.nio.file.Paths
import java.util.regex.Pattern

def run(args) {

    //input params 
    ObjectContext context = args.context
    Long siteId = args.siteId

    //get all default templates
    Map<String, String> defaultTemplatesMap = initDefaultTemplatesMap()


    WebSite webSite = SelectById.query(WebSite, siteId)
            .selectOne(context)

    webSite.versions.each { WebSiteVersion webSiteVersion ->
        webSiteVersion.layouts.each { WebSiteLayout webSiteLayout ->
            defaultTemplatesMap.each { String templateName, String templatePath ->

                Boolean noTemplate = true

                webSiteLayout.templates.each { WebTemplate template ->
                    if (template.name == templateName) {
                        noTemplate = false
                    }
                }

                if (noTemplate) {
                    WebTemplate newTemplate = context.newObject(WebTemplate)
                    newTemplate.name = templateName
                    newTemplate.layout = webSiteLayout
                    newTemplate.content = getContent(templatePath)
                }

            }
        }
    }

    context.commitChanges()
}


private Map<String, String> initDefaultTemplatesMap() {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("ish.oncourse.ui"))
    // exclude resources with *.internal.* in package name
            .filterInputsBy(new FilterBuilder().include(String.format("^%s.*", "ish.oncourse.ui")).exclude(".*(.internal.).*"))
            .setScanners(new ResourcesScanner()))

    Set<String> templates = reflections.getResources(Pattern.compile(".*\\.tml"))

    Map<String, String> defaultTemplatesMap = [:]

    templates.each { templatePath ->
        defaultTemplatesMap.put(Paths.get(templatePath).fileName.toString(), templatePath)
    }

    defaultTemplatesMap

}

private String getContent(String templatePath) throws IOException {
    InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(templatePath)

    StringWriter writer = new StringWriter()
    IOUtils.copy(inputStream, writer)

    String content = writer.toString()
    IOUtils.closeQuietly(writer)

    return content
}

