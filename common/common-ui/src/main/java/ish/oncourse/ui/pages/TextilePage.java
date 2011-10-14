package ish.oncourse.ui.pages;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class TextilePage {

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IWebNodeService webNodeService;

	private WebNode node;

	private WebContent region;

	@SetupRender
	void beforeRender() {
		this.node = webNodeService.getCurrentNode();
		List<WebContentVisibility> list = ExpressionFactory.matchExp(
				WebContentVisibility.REGION_KEY_PROPERTY, RegionKey.content)
				.filterObjects(node.getWebContentVisibility());

		if (list.size() > 0) {
			region = list.get(0).getWebContent();
		}
	}

	public String getRegionContent() {
		return region.accept(new ParsedContentVisitor(textileConverter));
	}

}
