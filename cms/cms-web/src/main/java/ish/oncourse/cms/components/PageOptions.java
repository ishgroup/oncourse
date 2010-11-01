package ish.oncourse.cms.components;

import ish.oncourse.cms.alias.IWebAliasWriteService;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.alias.IWebUrlAliasReadService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.ui.ISelectModelService;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageOptions {
	
	@Parameter
	@Property
	private WebNode node;

	@Inject
	private ISelectModelService selectModelService;

	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private IWebAliasWriteService aliasWriteService; 
	
	@Inject
	private IWebUrlAliasReadService aliasReadService;
	
	@Property
	private WebUrlAlias webUrlAlias;

	@Property
	@Persist
	private SelectModel pageTypeModel;

	@Property
	@Persist
	private String urlPath;

	@Component
	private Zone aliasZone;
	
	@Property
	@Component
	private Form optionsForm;

	@SetupRender
	public void beforeRender() {
		this.pageTypeModel = selectModelService.newSelectModel(
				webNodeService.getWebNodeTypes(), WebNodeType.NAME_PROPERTY,
				WebNodeType.ID_PROPERTY);
	}
	
	@OnEvent(component="optionsForm", value="add")
	public void onSelectedFromAdd() {
		WebUrlAlias alias = aliasWriteService.create(node.getId(), urlPath);
	}

	Object onActionFromDeleteAlias(String id) {
		WebUrlAlias alias = aliasReadService.getAliasById(id);
		aliasWriteService.removeAlias(alias);
		return aliasZone.getBody();
	}
	
	public Object onSuccess() {
		return aliasZone.getBody();
	}
}
