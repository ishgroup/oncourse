package ish.oncourse.ui.components;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;

public class PageHead extends ISHCommon {

	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private IWebSiteVersionService webSiteVersionService;

	@Inject
	private ITagService tagService;
	
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
	private String canonicalRelativeLinkPath;

	@Property
	@Parameter
	private String metaDescription;

    @SetupRender
    public void  setupRender() {
	    metaDescription = StringUtils.isNotBlank(metaDescription) ? metaDescription : siteService.getCurrentCollege().getName();
    }

	public String getCiVersion()
	{
		WebSiteVersion currentVersion = webSiteVersionService.getCurrentVersion();
		if (currentVersion != null) {
			return "r" + currentVersion.getId().toString();
		} else {
			return "development";
		}
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
