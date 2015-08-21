package ish.oncourse.cms.components;

import ish.oncourse.model.*;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PageTypeEdit {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private Request request;
	
	@InjectPage
	private Page page;

	@Parameter(required = true)
	@Property
	private WebNodeType pageType;

	@Property
	@Persist
	private WebNodeType editPageType;

	@Parameter
	@Property
	private Zone updateZone;

	@Property
	@Component
	private Form pageTypeEditForm;

	@SuppressWarnings("all")
	@Property
	@Persist
	private ListSelectModel<WebSiteLayout> layoutSelectModel;

	@SuppressWarnings("all")
	@Property
	@Persist
	private ListValueEncoder<WebSiteLayout> layoutEncoder;

	@Inject
	private PropertyAccess access;

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Messages messages;

	@SuppressWarnings("all")
	@Property
	private WebContent block;

	private Action action;

	public SortedSet<WebContent> getHeaderBlocks() {
		return getBlocksForRegion(RegionKey.header);
	}

	public SortedSet<WebContent> getLeftBlocks() {
		return getBlocksForRegion(RegionKey.left);
	}

	public SortedSet<WebContent> getCenterBlocks() {
		return getBlocksForRegion(RegionKey.content);
	}

	public SortedSet<WebContent> getRightBlocks() {
		return getBlocksForRegion(RegionKey.right);
	}

	public SortedSet<WebContent> getFooterBlocks() {
		return getBlocksForRegion(RegionKey.footer);
	}

	public SortedSet<WebContent> getUnassignedBlocks() {
		return getBlocksForRegion(RegionKey.unassigned);
	}
	
	private SortedSet<WebContent> getBlocksForRegion(RegionKey regionKey) {
		if (pageType != null && pageType.getObjectId().isTemporary() && !RegionKey.unassigned.equals(regionKey)) {
			//return no web content for temporary webNodeType because no web visibility exist for it,
			//we exclude unassigned block because only this block may already have elements for manipulation
			return new TreeSet<>(); 
		} else {
			return webContentService.getBlocksForRegionKey(editPageType, regionKey);
		}
	}
	
	public boolean getIsSpecialType() {
		return WebNodeType.PAGE.equals(editPageType.getName());
	}

	/**
	 * Handles ajax call to sort menu items. Done when user sorts items with
	 * drag&drop.
	 * 
	 * @return
	 */
	StreamResponse onActionFromSort() {
		if (!isSessionAndEntityValid()) {
			return new TextStreamResponse("text/json","{status: 'session timeout'}");
		}
		String id = request.getParameter("id");
		String region = request.getParameter("region");
		RegionKey regionKey = RegionKey.valueOf(region);
		int regionPosition = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = editPageType.getObjectContext();
		WebContent block = webContentService.refresh(Long.valueOf(id));
		block = ctx.localObject(block);
		sort(editPageType, block, regionKey, regionPosition);
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
	
	final void sort(WebNodeType webNodeType, WebContent block, RegionKey regionKey, int regionPosition) {
		ObjectContext context = webNodeType.getObjectContext();
		WebContentVisibility webContentVisibility = null;
		if (webNodeType != null && webNodeType.getObjectId().isTemporary()) {
			Expression expression = ExpressionFactory.matchExp(WebContentVisibility.WEB_NODE_TYPE_PROPERTY, webNodeType);
			List<WebContentVisibility> visibilities = expression.filterObjects(block.getWebContentVisibilities());
			webContentVisibility = visibilities.isEmpty() ? null : visibilities.get(0);
		} else {
			webContentVisibility = block.getWebContentVisibility(webNodeType);
		}
		if (regionKey == RegionKey.unassigned) {
			if (webContentVisibility != null) {
				// remove assignment to this type
				//mark as unasigned before delete to take the places
				webContentVisibility.setRegionKey(regionKey);
				context.deleteObject(webContentVisibility);
			}
		} else {
			if (webContentVisibility == null) {
				// create new visibility if it is moved from unassigned blocks
				webContentVisibility = context.newObject(WebContentVisibility.class);
				webContentVisibility.setWebContent(block);
				webContentVisibility.setWebNodeType(webNodeType);
			}
			webContentService.putWebContentVisibilityToPosition(webNodeType, regionKey, webContentVisibility, regionPosition);
		}
	}

	@SetupRender
	public void beforeRender() {
		editPageType = pageType.getPersistenceState() == PersistenceState.NEW ? pageType :
                Cayenne.objectForPK(cayenneService.newContext(), WebNodeType.class, pageType.getId());
		
		
		List<WebSiteLayout> layouts = ObjectSelect.query(WebSiteLayout.class).
				where(WebSiteLayout.WEB_SITE_VERSION.eq(editPageType.getWebSiteVersion())).
				select(editPageType.getObjectContext());
		
		if (layouts == null || layouts.size() == 0) {
			logger.error("The layout directory is empty!");
		}

		this.layoutSelectModel = new ListSelectModel<>(layouts, WebSiteLayout.LAYOUT_KEY.getName(), access);
		this.layoutEncoder = new ListValueEncoder<>(layouts, "id", access);
	}
	
	private boolean isSessionAndEntityValid() {
		return (request.getSession(false) != null && editPageType != null && editPageType.getObjectContext() != null);
	}

	Object onSubmitFromPageTypeEditForm() {
		if(!isSessionAndEntityValid()){
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = editPageType.getObjectContext();
		switch (action) {
		case cancel:
			ctx.rollbackChanges();
			break;
		case delete:
			if (editPageType.getWebNodes().size() > 0) {
				request.setAttribute("problemMessage", "This theme " + editPageType.getName()
						+ " cannot be deleted since it is has been used by a page.");
			} else {
				ctx.deleteObjects(editPageType);
			}
		case save:
            editPageType.setModified(new Date());
			ctx.commitChanges();
		}
		pageTypeEditForm.clearErrors();
		return updateZone.getBody();
	}

	void onSelectedFromCancel() {
		action = Action.cancel;
	}

	void onSelectedFromDelete() {
		action = Action.delete;
	}

	void onSelectedFromSave() {
		action = Action.save;
	}

	enum Action {
		save, cancel, delete
	}
	
	void onValidateFromPageTypeEditForm() {
		if (action!=Action.save){
			return;
		}
		
		String name= StringUtils.trimToEmpty(editPageType.getName());
        //we need the code to set trimmed name to for the page
        editPageType.setName(name);

		if (name.length() < 3) {
			pageTypeEditForm.recordError(messages.get("message-shortPageTypeName"));
		}
		WebNodeType webNodeType = webContentService.getWebNodeTypeByName(name);
		if (webNodeType != null && !webNodeType.getObjectId().equals(editPageType.getObjectId())){
			pageTypeEditForm.recordError(messages.get("message-duplicatePageTypeName"));
		}

        if (editPageType.getWebSiteLayout() == null)
        {
            pageTypeEditForm.recordError(messages.get("message-layoutKeyNotDefined"));
        }
	}

	Object onFailureFromPageTypeEditForm() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		return this;
	}
}
