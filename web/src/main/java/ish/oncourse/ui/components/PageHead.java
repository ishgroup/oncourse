package ish.oncourse.ui.components;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.services.WebProperty;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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

	@Property
	@Parameter
	private String canonicalLinkPath;

	@Property
	@Parameter
	private String canonicalRelativeLinkPath;

	@Parameter
	private String metaDescription;

    @SetupRender
    public void  setupRender() {
	    
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

	public String getCheckoutVersion() {
		return Configuration.getValue(WebProperty.CHECKOUT_VERSION);
	}
	
	public String getEditorVersion() {
		return Configuration.getValue(WebProperty.EDITOR_VERSION);
	}
	
	@Cached
	public String getMetaDescription() {
		if (StringUtils.isBlank(metaDescription)) {
			return siteService.getCurrentCollege().getName();
		} else {
			return metaDescription;
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
