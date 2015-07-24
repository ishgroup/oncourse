package ish.oncourse.ui.components;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.*;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Title extends ISHCommon {

    @Inject
    @Property
    private Request request;

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
