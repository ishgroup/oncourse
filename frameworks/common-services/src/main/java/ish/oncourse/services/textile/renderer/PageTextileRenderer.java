package ish.oncourse.services.textile.renderer;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.validator.PageTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

/**
 * Displays content from a single page, using the defined template or the title
 * of the page
 * 
 * <pre>
 * Example: 
 * 
 * {page template:&quot;Page with intro&quot; code:&quot;123&quot; tag:&quot;tag name&quot; } 
 * 
 * The parameters are as follows: 
 * 
 * template: the name of the Template used to display the page content.
 * 
 * code: if 123 is specified, only the page with that node number will be displayed.
 * Otherwise, a random page will be displayed. 
 * 
 * tag: if specified, the random choice is restricted to pages with this tag.
 * 
 * </pre>
 */
//TODO implement the template and tag attibutes
public class PageTextileRenderer extends AbstractRenderer {

	private IWebNodeService webNodeService;
	
	private IPageRenderer pageRenderer;
	
	public PageTextileRenderer(IWebNodeService webNodeService,
			IPageRenderer pageRenderer) {
		this.webNodeService = webNodeService;
		this.pageRenderer = pageRenderer;
		validator = new PageTextileValidator(webNodeService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			//TODO getReal number from attributes
			WebNode page = webNodeService.getNode(WebNode.NODE_NUMBER_PROPERTY, Integer.valueOf(492));
			if (page  != null) {
				Map<String, Object> parameters=new HashMap<String, Object>();
				parameters.put("node", page);
				tag = pageRenderer.renderPage("WebNodePage", parameters);
			}
		}
		return tag;
	}
}
