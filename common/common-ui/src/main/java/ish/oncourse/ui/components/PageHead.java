package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.commons.lang.StringUtils;
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
	
	public String getTitle() {
		String collegeName = siteService.getCurrentCollege().getName();
		
		if (title != null) {
			return title + " " + collegeName;
		}
		
		return collegeName;
	}

	public String getMetaGeneratorContent() {
		StringBuilder buff = new StringBuilder(
				environmentService.getApplicationName());

		String buildServerID = environmentService.getBuildServerID();
		if (!StringUtils.isEmpty(buildServerID)) {
			buff.append(' ').append(buildServerID);
		}

		String scmVersion = environmentService.getScmVersion();
		if (!StringUtils.isEmpty(scmVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(scmVersion);
		}

		String ciVersion = environmentService.getCiVersion();
		if (!StringUtils.isEmpty(ciVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(ciVersion);
		}
		return buff.toString();
	}

	/**
	 * @return current tag, can be null
	 */
	public Tag getTag()
	{
		return tagService.getBrowseTag();
	}
}
