package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class PageOptions {

	@Parameter
	@Property
	private WebNode node;

	@Parameter
	@Property
	private Zone updateZone;

	@Property
	@Persist
	private WebNode editNode;

	@Property
	@Component(id = "optionsForm")
	private Form optionsForm;

	@Property
	@Component(id = "urlForm")
	private Form urlForm;

	@Component
	private Zone urlZone;

	@Inject
	private PropertyAccess access;

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
	private ListSelectModel<WebNodeType> pageTypeModel;

	@Property
	@Persist
	private ListValueEncoder<WebNodeType> pageTypeEncoder;

	@Property
	private WebUrlAlias webUrlAlias;

	@Property
	private String urlPath;

	@SetupRender
	public void beforeRender() {
		ObjectContext ctx = cayenneService.newContext();
		this.editNode = (WebNode) ctx.localObject(node.getObjectId(), node);

		List<WebNodeType>  webNodeTypes = new ArrayList<WebNodeType>(15);
		
		for (WebNodeType t : webNodeTypeService.getWebNodeTypes()) {
			webNodeTypes.add((WebNodeType) ctx.localObject(t.getObjectId(), null));
		}

		this.pageTypeModel = new ListSelectModel<WebNodeType>(webNodeTypes, WebNodeType.LAYOUT_KEY_PROPERTY, access);
		this.pageTypeEncoder = new ListValueEncoder<WebNodeType>(webNodeTypes, "id", access);
	}

	public boolean isNotDefault() {
		if (editNode.getDefaultWebURLAlias() != null) {
			return !editNode.getDefaultWebURLAlias().equals(webUrlAlias.getId());
		}
		return true;
	}

	public boolean isHasDefault() {
		return editNode.getDefaultWebURLAlias() != null;
	}

	Object onActionFromRemoveUrl(String id) {
		WebUrlAlias alias = (WebUrlAlias) editNode.getObjectContext().localObject(aliasService.findById(Long.parseLong(id)).getObjectId(), null);
		editNode.removeFromWebUrlAliases(alias);
		editNode.getObjectContext().commitChanges();
		return urlZone.getBody();
	}

	Object onActionFromMakeDefault(String id) {
		WebUrlAlias alias = (WebUrlAlias) editNode.getObjectContext().localObject(aliasService.findById(Long.parseLong(id)).getObjectId(), null);
		editNode.setDefaultWebURLAlias(alias);
		editNode.getObjectContext().commitChanges();
		return urlZone.getBody();
	}

	Object onSuccessFromUrlForm() {
		ObjectContext ctx = editNode.getObjectContext();

		WebUrlAlias alias = ctx.newObject(WebUrlAlias.class);
		alias.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		alias.setUrlPath(urlPath);

		editNode.addToWebUrlAliases(alias);
		return urlZone.getBody();
	}

	Object onSuccessFromOptionsForm() {
		editNode.getObjectContext().commitChanges();
		return updateZone.getBody();
	}

	public String getSiteUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName();
	}
}
