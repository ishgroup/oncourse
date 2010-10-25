package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.ui.ISelectModelService;

import org.apache.cayenne.DataObjectUtils;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageOptions {

	@Property
	private WebNode node;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ISelectModelService selectModelService;

	@Inject
	private IWebNodeService webNodeService;

	@Property
	private SelectModel pageTypeModel;
	
	@Property
	private WebUrlAlias webUrlAlias;
	
	@Property
	private WebUrlAlias newWebUrlAlias;

	void onActivate(String webNodeId) {
		this.node = DataObjectUtils.objectForPK(cayenneService.sharedContext(),
				WebNode.class, webNodeId);
	}

	@SetupRender
	public void beforeRender() {
		this.pageTypeModel = selectModelService.newSelectModel(
				webNodeService.getWebNodeTypes(), WebNodeType.NAME_PROPERTY,
				WebNodeType.ID_PROPERTY);
	}
}
