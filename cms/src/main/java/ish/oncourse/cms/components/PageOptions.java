package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.ui.pages.internal.Page;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.utils.ResourceNameValidator;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private IWebSiteVersionService webSiteVersionService;

	@Inject
	private IWebUrlAliasService aliasService;

    @Inject
    private IWebNodeService webNodeService;

	@SuppressWarnings("all")
	@Persist
	@Property
	private ListSelectModel<WebNodeType> pageTypeModel;

	@SuppressWarnings("all")
	@Property
	@Persist
	private ListValueEncoder<WebNodeType> pageTypeEncoder;

	@Property
	private WebUrlAlias webUrlAlias;

	@Property
	private String urlPath;

	@Property
	private boolean cancelEditing;

	@SuppressWarnings("all")
	@Property
	private boolean savedNewPage;

	@SuppressWarnings("all")
	@Property
	private boolean savedPage;

	@InjectComponent(value = "urlPath")
	private Field urlAlias;

	@InjectComponent
	private Field pageName;

	@SuppressWarnings("all")
	@InjectComponent
	@Property
	private Hidden cancelEditingHiddenField;
	
	@Inject
	private ComponentResources componentResources;

	@Inject
	private Messages messages;
	
	@Inject
	private IWebContentService webContentService;

	@SetupRender
	public boolean beforeRender() {
		return initPageWithNode(node);
	}
		
	public boolean initPageWithNode(WebNode nodeForInit) {
		if (nodeForInit == null) {
			return false;
		} else {
			node = nodeForInit;
		}
		if (node.getPersistenceState() == PersistenceState.NEW) {
			this.editNode = node;
		} else {
            ObjectContext ctx = node.getObjectContext().createChildContext();
			this.editNode = ctx.localObject(node);
		}
		refreshThemes();
		optionsForm.clearErrors();
		urlForm.clearErrors();
		urlPath = null;
		return true;
	}

    /**
     * for this component we use the method instead of WebNodeService.getDefaultWebURLAlias(node)
     * to be sure that we work with editing context, not with data which is in the database.
     */

    public WebUrlAlias getDefaultWebUrlAlias()
    {
        Expression expression =  ExpressionFactory.matchExp(WebUrlAlias.DEFAULT_PROPERTY, true);
        List<WebUrlAlias> result = expression.filterObjects(editNode.getWebUrlAliases());
        return result.isEmpty() ? null : result.get(0);
    }

	Object onActionFromRemoveDefault() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}

        WebUrlAlias defaultAlias = getDefaultWebUrlAlias();
        if (defaultAlias != null)
            defaultAlias.setDefault(false);
		return urlZone;
	}

	Object onActionFromRemoveUrl(String urlPath) {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
		editNode.removeFromWebUrlAliases(alias);
		return urlZone;
	}

	Object onActionFromMakeDefault(String urlPath) {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		WebUrlAlias alias = editNode.getWebUrlAliasByPath(urlPath);
        WebUrlAlias defaultAlias = getDefaultWebUrlAlias();
        if (defaultAlias != null)
               defaultAlias.setDefault(false);
        alias.setDefault(true);
		return urlZone;
	}

	void onValidateFromOptionsForm() {
		if (!isSessionAndEntityValid()) {
			return;
		}
		if (cancelEditing) {
			return;
		}

		String pageName = StringUtils.trimToEmpty(editNode.getName());
        //we need the code to set trimmed name to for the page
        editNode.setName(pageName);

        String error = new ResourceNameValidator().validate(pageName);

        if (error != null) {
			optionsForm.recordError(error);
			return;
		}
		
		//check blockName to unique exclude his blockName
		WebNode webNode = webContentService.getWebNodeByName(editNode.getName());
		if (webNode!=null && !webNode.getObjectId().equals(editNode.getObjectId()) ) {
			optionsForm.recordError(messages.get("message-duplicatePageName"));		
		}
	}

	void onValidateFromUrlForm() {
		if (!isSessionAndEntityValid()) {
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
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		return urlZone;
	}

	Object onSuccessFromUrlForm() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = editNode.getObjectContext();

		WebUrlAlias alias = ctx.newObject(WebUrlAlias.class);
		alias.setWebSiteVersion(
				webSiteVersionService.getCurrentVersion(ctx.localObject(webSiteService.getCurrentWebSite())));
		alias.setUrlPath(urlPath);

		//both sides relationship set required to prevent#14485 persist issue.
		editNode.addToWebUrlAliases(alias);
		alias.setWebNode(editNode);
		urlPath = null;
		return urlZone;
	}

	Object onFailureFromOptionsForm() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		return getOptionsAndButtonsZone();
	}
	
	private boolean isSessionAndEntityValid() {
		return (request.getSession(false) != null && editNode != null && editNode.getObjectContext() != null);
	}

	Object onSuccessFromOptionsForm() {
		if (!isSessionAndEntityValid()) {
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
			//TODO: remove this code after refactor the new page create action for #17107  
			//#17107. workaround to prevent the errors after cancel for new page.
			return page.getReloadPageBlock();
		} else {
			editingContext.commitChanges();
			if (isNewPage) {
				savedNewPage = true;
			} else {
				savedPage = true;
				return page.getReloadPageBlock();
			}

			if (updateZones != null) {
				for (String key : updateZones.keySet()) {
					multiZoneUpdate = multiZoneUpdate.add(key, updateZones.get(key).getBody());
				}
			}
		}
		return multiZoneUpdate.add("urlZone", urlZone).add("buttonsZone", buttonsZone);
	}

    /**
     * The method returns real side (no cms ) url to display it for an user. We should not use it in
     * anchor tags or for redirect
     *
     */
	public String getSiteUrl() {
		return HTMLUtils.HTTP_PROTOCOL + request.getServerName();
	}

	public Zone getOptionsZone() {
		return optionsZone;
	}

	public void refreshThemes() {
		List<WebNodeType> webNodeTypes = new ArrayList<>();
		for (WebNodeType t : webNodeTypeService.getWebNodeTypes()) {
			webNodeTypes.add((WebNodeType) editNode.getObjectContext().localObject(t.getObjectId(), null));
		}

		this.pageTypeModel = new ListSelectModel<>(webNodeTypes, WebNodeType.NAME_PROPERTY, access);
		this.pageTypeEncoder = new ListValueEncoder<>(webNodeTypes, "id", access);

	}

	public Object getOptionsAndButtonsZone() {
		savedPage = false;
		return new MultiZoneUpdate("optionsZone", optionsZone).add("buttonsZone", buttonsZone).add("urlZone", urlZone)
			.add("currentPageZone", ((PageInfo)componentResources.getContainer()).getCurrentPageZone());
	}
}
