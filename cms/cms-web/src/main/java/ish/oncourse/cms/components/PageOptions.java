package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.ui.ISelectModelService;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
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

	@Parameter
	@Property
	private Zone updateZone;

	@Property
	@Component(id = "optionsForm")
	private Form optionsForm;

	@Property
	@Component(id = "urlForm")
	private Form urlForm;

	@Component
	private Zone urlZone;

	@Inject
	private ISelectModelService selectModelService;

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebUrlAliasService aliasService;

	@Inject
	private ICayenneService cayenneService;

	@Property
	@Persist
	private SelectModel pageTypeModel;

	@Property
	private WebUrlAlias webUrlAlias;

	@Property
	private String urlPath;

	@SetupRender
	public void beforeRender() {
		this.pageTypeModel = selectModelService.newSelectModel(
				webNodeTypeService.getWebNodeTypes(),
				WebNodeType.LAYOUT_KEY_PROPERTY, "id");
	}

	public boolean isNotDefault() {
		if (this.node.getDefaultWebURLAlias() != null) {
			return !node.getDefaultWebURLAlias().equals(webUrlAlias.getId());
		}
		return true;
	}

	public boolean isHasDefault() {
		return this.node.getDefaultWebURLAlias() != null;
	}

	void onSelectedFromAddUrl() {

	}

	Object onActionFromRemoveUrl(String id) {
		WebUrlAlias alias = aliasService.loadByIds(Long.parseLong(id)).get(0);

		this.node.removeFromWebUrlAliases(alias);
		cayenneService.sharedContext().deleteObject(alias);

		cayenneService.sharedContext().commitChanges();

		return urlZone.getBody();
	}

	Object onActionFromMakeDefault(String id) {
		WebUrlAlias alias = aliasService.loadByIds(Long.parseLong(id)).get(0);

		this.node.setDefaultWebURLAlias(alias);
		cayenneService.sharedContext().commitChanges();

		return urlZone.getBody();
	}

	Object onSuccessFromUrlForm() {
		WebUrlAlias alias = cayenneService.sharedContext().newObject(
				WebUrlAlias.class);

		alias.setUrlPath(urlPath);
		node.addToWebUrlAliases(alias);
		alias.setWebSite(this.node.getWebSite());

		this.urlPath = "";

		cayenneService.sharedContext().commitChanges();

		return urlZone.getBody();
	}

	Object onSuccessFromOptionsForm() {
		cayenneService.sharedContext().commitChanges();
		return updateZone.getBody();
	}

	public String getSiteUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName() + "/";
	}
}
