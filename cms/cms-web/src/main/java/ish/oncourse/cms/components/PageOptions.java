package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

public class PageOptions {

	@Parameter
	@Property
	private WebNode node;

	@Property
	@Persist
	private WebNode editNode;

	@Parameter
	@Property
	private Map<String, Zone> updateZones;

	@Property
	@Component
	private Zone optionsZone;

	@Property
	@InjectComponent
	private Form optionsForm;

	@Component
	@Property
	private Zone urlZone;

	@Property
	@InjectComponent
	private Form urlForm;

	@Component
	private Zone buttonsZone;

	@Inject
	private PropertyAccess access;

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebUrlAliasService aliasService;

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

	@Property
	private boolean cancelEditing;

	@Property
	private boolean savedNewPage;

	@InjectComponent(value = "urlPath")
	private Field urlAlias;

	@InjectComponent
	private Field pageName;

	@InjectComponent
	@Property
	private Hidden cancelEditingHiddenField;

	@SetupRender
	public void beforeRender() {

		ObjectContext ctx = null;
		if (node.getPersistenceState() == PersistenceState.NEW) {
			ctx = node.getObjectContext();
			this.editNode = node;
		} else {
			ctx = node.getObjectContext().createChildContext();
			this.editNode = (WebNode) ctx.localObject(node.getObjectId(), null);
		}
		List<WebNodeType> webNodeTypes = new ArrayList<WebNodeType>();

		for (WebNodeType t : webNodeTypeService.getWebNodeTypes()) {
			webNodeTypes.add((WebNodeType) ctx.localObject(t.getObjectId(), null));
		}

		this.pageTypeModel = new ListSelectModel<WebNodeType>(webNodeTypes,
				WebNodeType.LAYOUT_KEY_PROPERTY, access);
		this.pageTypeEncoder = new ListValueEncoder<WebNodeType>(webNodeTypes, "id", access);
		optionsForm.clearErrors();
		urlForm.clearErrors();
		urlPath = null;
	}

	public boolean isNotDefault() {

		if (editNode.getDefaultWebURLAlias() != null) {
			return !editNode.getDefaultWebURLAlias().equals(webUrlAlias);
		}
		return true;
	}

	public boolean isHasDefault() {
		return editNode.getDefaultWebURLAlias() != null;
	}

	Object onActionFromRemoveUrl(String urlPath) {
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
		editNode.removeFromWebUrlAliases(alias);
		return urlZone.getBody();
	}

	Object onActionFromMakeDefault(String urlPath) {
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
		editNode.setDefaultWebURLAlias(alias);
		return urlZone.getBody();
	}

	void onValidateFromOptionsForm() {
		if (cancelEditing) {
			return;
		}
		if (editNode.getName() == null || "".equals(editNode.getName())) {
			optionsForm.recordError(pageName, "The page name cannot be empty");
			return;
		}
		if (editNode.getName().length() < 3) {
			optionsForm.recordError(pageName, "There should be at least 3 characters in page name");
		}
	}

	void onValidateFromUrlForm() {
		if (urlPath == null || "".equals(urlPath)) {

			urlForm.recordError(urlAlias, "URL path is required");
			return;

		}
		if (!urlPath.startsWith("/")) {
			urlPath = "/" + urlPath;
		}
		WebUrlAlias alias = aliasService.getAliasByPath(urlPath);
		if (alias == null) {
			for (Object o : editNode.getObjectContext().uncommittedObjects()) {
				if (o instanceof WebUrlAlias && ((WebUrlAlias) o).getUrlPath().equals(urlPath)) {
					alias = (WebUrlAlias) o;
				}
			}
		}
		if (alias != null) {
			urlForm.recordError(urlAlias, "Already in use");
		}
	}

	Object onFailureFromUrlForm() {
		return urlZone;
	}

	Object onSuccessFromUrlForm() {
		ObjectContext ctx = editNode.getObjectContext();

		WebUrlAlias alias = ctx.newObject(WebUrlAlias.class);
		alias.setWebSite((WebSite) ctx.localObject(
				webSiteService.getCurrentWebSite().getObjectId(), null));
		alias.setUrlPath(urlPath);

		editNode.addToWebUrlAliases(alias);
		urlPath = null;
		return urlZone;
	}

	Object onFailureFromOptionsForm() {
		return new MultiZoneUpdate("optionsZone", optionsZone.getBody()).add("buttonsZone",
				buttonsZone);
	}

	Object onSuccessFromOptionsForm() {
		urlForm.clearErrors();
		optionsForm.clearErrors();
		urlPath = null;
		ObjectContext editingContext = editNode.getObjectContext();
		MultiZoneUpdate multiZoneUpdate = new MultiZoneUpdate("optionsZone", optionsZone.getBody());
		boolean isNewPage = editNode.getPersistenceState() == PersistenceState.NEW;
		if (cancelEditing) {
			if (isNewPage) {
				editingContext.rollbackChanges();
			} else {
				editingContext.rollbackChangesLocally();
			}
			cancelEditing = false;
		} else {
			editingContext.commitChanges();
			if (isNewPage) {
				savedNewPage = true;
			}

			if (updateZones != null) {
				for (String key : updateZones.keySet()) {
					multiZoneUpdate = multiZoneUpdate.add(key, updateZones.get(key));
				}
			}
		}
		return multiZoneUpdate.add("urlZone", urlZone).add("buttonsZone", buttonsZone);
	}

	public String getSiteUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName();
	}
}
