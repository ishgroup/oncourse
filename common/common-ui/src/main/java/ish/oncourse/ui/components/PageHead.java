package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;

public class PageHead {

	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private ITagService tagService;
	
	@Parameter
    @Deprecated //we should use pageName instead of title
	private String title;

    @Parameter
    @Property
    private String pageName;

    @Parameter
    @Property
    private WebNode webNode;

	@Inject
	private Request request;
	
	@Property
	@Parameter
	private String canonicalLinkPath;

	@Property
	@Parameter
	private String metaDescription;

	/**
	 * Google tag mananger event name.
	 */
	@Property
	@Parameter
    @Deprecated //TODO: we don't use GoogleTagManager component in PageHead. We use the componnent in PageStructure.
    // This property can be deleted after all custom template will be updated
	private String eventName;

	@Property
	@Parameter
    @Deprecated //TODO: we don't use GoogleTagManager component in PageHead. We use the componnent in PageStructure.
    // This property can be deleted after all custom template will be updated
	private Cart cart;

    @SetupRender
    public void  setupRender()
    {
        //TODO: the code can be removed after all custom templates will be adjusted to use Title component
        if (pageName != null && title == null)
            title = pageName;
        if (title != null && pageName == null)
            pageName = title;
    }


    @Deprecated //all custome template should be adjusted to use Title component
	public String getTitle() {
		String collegeName = siteService.getCurrentCollege().getName();
		
		if (title != null) {
			return title + " " + collegeName;
		}
		return collegeName;
	}

	public String getMetaGeneratorContent() {
		return HTMLUtils.getMetaGeneratorContent(environmentService);
	}

	/**
	 * @return current tag, can be null
	 */
	public Tag getTag()
	{
		return tagService.getBrowseTag();
	}
}
