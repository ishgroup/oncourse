package ish.oncourse.ui.pages;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.node.IWebNodeService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TextilePage {
	private static final String DISPLAYED_REGION_KEY = "content";

	@Inject
	private IWebContentService webContentService;

	@Inject
	private IWebNodeService webNodeService;

	private WebNode node;

	private WebContent region;

	@SetupRender
	void beforeRender() {
		this.node = webNodeService.getCurrentNode();
		List<WebContentVisibility> list = ExpressionFactory.matchExp(
				WebContentVisibility.REGION_KEY_PROPERTY, DISPLAYED_REGION_KEY)
				.filterObjects(node.getWebContentVisibility());

		if (list.size() > 0) {
			region = list.get(0).getWebContent();
		}
	}

	public String getRegionContent() {
		return webContentService.getParsedContent(region);
	}

}
