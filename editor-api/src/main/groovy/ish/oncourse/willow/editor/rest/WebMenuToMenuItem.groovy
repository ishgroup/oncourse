package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebMenu
import ish.oncourse.willow.editor.model.MenuItem
import ish.oncourse.willow.editor.model.menuitem.Errors
import ish.oncourse.willow.editor.website.WebMenuFunctions
import org.apache.commons.lang3.StringUtils

class WebMenuToMenuItem {

    private WebMenu webMenu

    private WebMenuToMenuItem(){}

    static WebMenuToMenuItem valueOf(WebMenu webMenu) {
        WebMenuToMenuItem serializer = new WebMenuToMenuItem()
        serializer.webMenu = webMenu
        serializer
    }

    MenuItem getMenuItem() {
        translate(webMenu)
    }
    
    private MenuItem translate(WebMenu webMenu) {
        return new MenuItem().with { menu ->
            menu.id = webMenu.id.intValue()
            menu.title = webMenu.name
            menu.url = WebMenuFunctions.getUrlPath(webMenu)
            String error = StringUtils.trimToNull(webMenu.warning)
            if (error) {
                menu.errors = new Errors(title: error)
            }
            webMenu.childrenMenus.sort { m -> m.weight }.each { m -> menu.children << translate(m) }
            menu
        }
    }
}
