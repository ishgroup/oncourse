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
import ish.oncourse.ui.pages.internal.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
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

	@InjectPage
	private Page page;

	@Inject
	private Request request;

	@Parameter
	@Property
	private WebNode node;

	@Property
	@Persist
	private WebNode editNode;

	@Parameter
	@Property
	private Map<String, Zone> updateZones;

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

	@Persist
	@Property
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

	@Property
	private boolean savedPage;

	@InjectComponent(value = "urlPath")
	private Field urlAlias;

	@InjectComponent
	private Field pageName;

	@InjectComponent
	@Property
	private Hidden cancelEditingHiddenField;

	@SetupRender
	public boolean beforeRender() {
		if (node == null) {
			return false;
		}
		ObjectContext ctx = null;
		if (node.getPersistenceState() == PersistenceState.NEW) {
			ctx = node.getObjectContext();
			this.editNode = node;
		} else {
			ctx = node.getObjectContext().createChildContext();
			this.editNode = (WebNode) ctx.localObject(node.getObjectId(), null);
		}
		refreshThemes();
		optionsForm.clearErrors();
		urlForm.clearErrors();
		urlPath = null;
		return true;
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
	
	Object onActionFromRemoveDefault() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		editNode.setDefaultWebURLAlias(null);
		return urlZone;
	}

	Object onActionFromRemoveUrl(String urlPath) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
		editNode.removeFromWebUrlAliases(alias);
		return urlZone;
	}

	Object onActionFromMakeDefault(String urlPath) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
		editNode.setDefaultWebURLAlias(alias);
		return urlZone;
	}

	void onValidateFromOptionsForm() {
		if (request.getSession(false) == null) {
			return;
		}
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
		if (request.getSession(false) == null) {
			return;
		}
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
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		return urlZone;
	}

	Object onSuccessFromUrlForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = editNode.getObjectContext();

		WebUrlAlias alias = ctx.newObject(WebUrlAlias.class);
		alias.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		alias.setUrlPath(urlPath);

		editNode.addToWebUrlAliases(alias);
		urlPath = null;
		return urlZone;
	}

	Object onFailureFromOptionsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		return getOptionsAndButtonsZone();
	}

	Object onSuccessFromOptionsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
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
			} else {
				savedPage = true;
			}

			if (updateZones != null) {
				for (String key : updateZones.keySet()) {
					multiZoneUpdate = multiZoneUpdate.add(key, updateZones.get(key).getBody());
				}
			}
		}
		return multiZoneUpdate.add("urlZone", urlZone).add("buttonsZone", buttonsZone);
	}

	public String getSiteUrl() {
		return "http://" + request.getServerName();
	}

	public Zone getOptionsZone() {
		return optionsZone;
	}

	public void refreshThemes() {
		List<WebNodeType> webNodeTypes = new ArrayList<WebNodeType>();

		for (WebNodeType t : webNodeTypeService.getWebNodeTypes()) {
			webNodeTypes.add((WebNodeType) editNode.getObjectContext().localObject(t.getObjectId(), null));
		}

		this.pageTypeModel = new ListSelectModel<WebNodeType>(webNodeTypes, WebNodeType.NAME_PROPERTY, access);
		this.pageTypeEncoder = new ListValueEncoder<WebNodeType>(webNodeTypes, "id", access);

	}

	public Object getOptionsAndButtonsZone() {
		savedPage = false;
		return new MultiZoneUpdate("optionsZone", optionsZone).add("buttonsZone", buttonsZone);
	}
}
