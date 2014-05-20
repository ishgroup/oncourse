package ish.oncourse.ui.components;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Title {

    @Parameter
    @Property
    private String pageName;

    @Parameter
    @Property
    private WebNode webNode;


    @Inject
    private ITagService tagService;

    @Inject
    private IWebSiteService webSiteService;


    public Tag getTag() {
        return tagService.getBrowseTag();
    }

    public College getCollege() {
        return webSiteService.getCurrentCollege();
    }
}
