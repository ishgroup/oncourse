package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.pages.internal.Page;

import java.io.File;
import java.io.FilenameFilter;
import java.util.SortedSet;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.log4j.Logger;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class PageTypeEdit {

	private static final Logger logger = Logger.getLogger(PageTypeEdit.class);

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
	private SelectModel layoutSelectModel;

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICayenneService cayenneService;

	@SuppressWarnings("all")
	@Property
	private WebContent block;

	private Action action;

	public SortedSet<WebContent> getHeaderBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.header);
	}

	public SortedSet<WebContent> getLeftBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.left);
	}

	public SortedSet<WebContent> getCenterBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.content);
	}

	public SortedSet<WebContent> getRightBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.right);
	}

	public SortedSet<WebContent> getFooterBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.footer);
	}

	public SortedSet<WebContent> getUnassignedBlocks() {
		return webContentService.getBlocksForRegionKey(pageType, RegionKey.unassigned);
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
		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json","{status: 'session timeout'}");
		}
		String id = request.getParameter("id");
		String region = request.getParameter("region");
		RegionKey regionKey = RegionKey.valueOf(region);
		int regionPosition = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = editPageType.getObjectContext();
		WebContent block = webContentService.findById(Long.valueOf(id));
		block = (WebContent) ctx.localObject(block.getObjectId(), null);
		sort(editPageType, block, regionKey, regionPosition);
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
	
	final void sort(WebNodeType webNodeType, WebContent block, RegionKey regionKey, int regionPosition) {
		ObjectContext context = webNodeType.getObjectContext();
		WebContentVisibility webContentVisibility = block.getWebContentVisibility(webNodeType);
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

	private String[] readAvailableLayouts() {
		PrivateResource res = resourceService.getTemplateResource("", "");

		File dir = res.getFile();

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Reading layouts from: %s", dir.getAbsolutePath()));
		}
		if (!dir.exists()) {
			logger.error("The layout directory \"" + dir.getAbsolutePath() + "\" doesn't exist.");
		}

		return dir.list(new FilenameFilter() {
			public boolean accept(File arg, String arg1) {
				File f = new File(arg.getAbsolutePath() + "/" + arg1);

				if (logger.isInfoEnabled()) {
					logger.info(String.format("Found layout: %s", f.getAbsolutePath()));
				}

				return f.isDirectory() && !(arg1.charAt(0) == '.');
			}
		});
	}

	@SetupRender
	public void beforeRender() {
		editPageType = pageType.getPersistenceState() == PersistenceState.NEW ? pageType : (WebNodeType) cayenneService
				.newContext().localObject(pageType.getObjectId(), pageType);
		String[] availableLayouts = readAvailableLayouts();
		if (availableLayouts == null || availableLayouts.length == 0) {
			logger.error("The layout directory is empty!");
		}
		layoutSelectModel = new StringSelectModel(availableLayouts);
	}

	Object onSubmitFromPageTypeEditForm() {
		if(request.getSession(false)==null){
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
				ctx.deleteObject(editPageType);
			}
		case save:
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

}
