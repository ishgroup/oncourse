package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class WebTemplateFunctions {

    static List<WebTemplate> getTemplatesForLayout(WebSiteLayout layout) {
        return ObjectSelect.query(WebTemplate)
                .where(WebTemplate.LAYOUT.eq(layout))
                .select(layout.objectContext)
    }

    static WebTemplate createWebTemplate(String name, String content, WebSiteLayout layout) {
        ObjectContext context = layout.objectContext

        WebTemplate template = context.newObject(WebTemplate)
        template.name = name
        template.layout = layout
        template.content = content

        return template
    }

    static WebTemplate getTemplateByName(String name, WebSiteLayout layout) {
        return (ObjectSelect.query(WebTemplate)
                .where(WebTemplate.LAYOUT.eq(layout)) 
                & WebTemplate.NAME.eq(name))
                .selectOne(layout.objectContext)
    }
    
}
