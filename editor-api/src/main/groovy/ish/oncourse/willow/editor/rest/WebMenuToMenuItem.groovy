package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebMenu
import ish.oncourse.willow.editor.v1.model.MenuItem
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
            String warning = StringUtils.trimToNull(webMenu.warning)
            if (warning) {
                menu.warning = warning
                
            }
            webMenu.childrenMenus.sort { m -> m.weight }.each { m -> menu.children << translate(m) }
            menu
        }
    }
}
