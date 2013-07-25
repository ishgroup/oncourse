package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageHead {

	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private ITagService tagService;
	
	@Parameter
	private String title;
	
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
	private String eventName;
	
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
