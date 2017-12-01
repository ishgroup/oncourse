package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebMenu
import ish.oncourse.model.WebNode
import ish.oncourse.utils.ResourceNameValidator
import ish.oncourse.willow.editor.model.MenuItem
import ish.oncourse.willow.editor.website.WebMenuFunctions
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.server.Request


class UpdateMenu extends AbstractUpdate<List<MenuItem>> {

    List<String> getErrors() {
        return errors
    }

    void setErrors(List<String> errors) {
        this.errors = errors
    }
    
    private List<String> errors
    
    private UpdateMenu() {}
    
    static UpdateMenu valueOf(List<MenuItem> menusToSave, ObjectContext context, Request request) {
        UpdateMenu updater = new UpdateMenu()
        updater.init(menusToSave, context, request)
        updater.errors = new ArrayList<>()
        return updater
    }


    @Override
    UpdateMenu update() {
        WebMenu root = WebMenuFunctions.getRootMenu(request, context)
        context.deleteObjects(root.childrenMenus)
        updateMenus(resourceToSave, root)
        return this
    }

    void updateMenus(List<MenuItem> menusToSave, WebMenu root) {

        menusToSave.eachWithIndex { MenuItem menuItem, int i ->
            WebMenu menu = null
            menuItem.title = StringUtils.trimToEmpty(menuItem.title)
            menuItem.url = StringUtils.trimToEmpty(menuItem.url)
            String error = new ResourceNameValidator().validate(menuItem.title)
            if (error) {
                errors << "Menu name: $menuItem.title is wrong. $error".toString()
            } else if (root.childrenMenus.find { child -> child.name == menuItem.title }.any()) {
                errors << "Menu name: $menuItem.title is wrong. The name is already used.".toString()
            }
            
            if (!menuItem.url.startsWith('http://') && !menuItem.url.startsWith('https://') && !menuItem.url.startsWith('www.')
                    && !menuItem.url.startsWith('/')) {
                menuItem.url = "/$menuItem.url"
            }
            
            if (errors.empty) {
                menu = context.newObject(WebMenu)
                menu.parentWebMenu = root
                menu.webSiteVersion = root.webSiteVersion
                menu.weight = i

                WebNode node = WebNodeFunctions.getNodeByPath(menuItem.url, request, context)
                if (node) {
                    menu.webNode = node
                } else {
                    menu.url = menuItem.url
                }
                menuItem.errors.title = menu.warning
            }

            updateMenus(menuItem.children, menu)
        }
    }
}
