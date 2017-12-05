package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.*;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Title extends ISHCommon {

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

    public Product getProduct() {
        return (Product) request.getAttribute(Product.class.getSimpleName());
    }

    public Course getCourse() {
        return (Course) request.getAttribute(Course.class.getSimpleName());
    }

    public CourseClass getCourseClass() {
        return (CourseClass) request.getAttribute(CourseClass.class.getSimpleName());
    }

    public Site getSite() {
        return (Site) request.getAttribute(Site.class.getSimpleName());
    }

    public Room getRoom() {
        return (Room) request.getAttribute(Room.class.getSimpleName());
    }
}
