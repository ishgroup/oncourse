package ish.oncourse.ui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * A page wrapper component.
 */
public class PageWrapper {

	private static final String WEB_NODE_COMPONENT_NAME = "webNodeTemplate";
	private static final String WEB_NODE_PAGE_NAME = "Page";
	private static final String MAIN_PAGE_NAME = "Main";
	private static final String REAL_MAIN_PAGE_NAME = "Index";
	
	@Inject
	private ComponentResources componentResources;

	public String getBodyId() {
		
		String pageName = componentResources.getPageName();
		
		if (REAL_MAIN_PAGE_NAME.equals(pageName)) {
			return MAIN_PAGE_NAME;
		} else if (WEB_NODE_PAGE_NAME.equals(pageName)) {
			Long nodeNumber = ((WebNodeTemplate) componentResources
					.getPage().getComponentResources().getEmbeddedComponent(
							WEB_NODE_COMPONENT_NAME)).getNode().getId();
			return pageName + nodeNumber;
		}

		return pageName;

	}
}
