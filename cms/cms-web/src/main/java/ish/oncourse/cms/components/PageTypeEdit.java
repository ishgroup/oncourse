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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
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

	@Inject
	private Request request;

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

	@Property
	@Persist
	private SelectModel layoutSelectModel;

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICayenneService cayenneService;

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
		return webContentService.getBlocksForRegionKey(pageType, null);
	}

	/**
	 * Handles ajax call to sort menu items. Done when user sorts items with
	 * drag&drop.
	 * 
	 * @return
	 */
	StreamResponse onActionFromSort() {

		String id = request.getParameter("id");
		String region = request.getParameter("region");
		RegionKey regionKey = RegionKey.valueOf(region);
		int weight = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = editPageType.getObjectContext();

		WebContent block = webContentService.findById(Long.valueOf(id));

		block = (WebContent) ctx.localObject(block.getObjectId(), null);

		WebContentVisibility webContentVisibility = block.getWebContentVisibility(editPageType);

		if (regionKey == RegionKey.unassigned) {
			if (webContentVisibility != null) {
				// remove assignment to this type
				ctx.deleteObject(webContentVisibility);
			}
		} else {

			if (webContentVisibility == null) {
				// create new visibility if it is moved from unassigned blocks
				webContentVisibility = ctx.newObject(WebContentVisibility.class);
				webContentVisibility.setWebContent(block);
				webContentVisibility.setWebNodeType(editPageType);
			}
			webContentVisibility.setRegionKey(regionKey);

			webContentVisibility.setWeight(weight);
			SortedSet<WebContentVisibility> vSet = webContentService.getBlockVisibilityForRegionKey(editPageType, regionKey);

			// change weight of the items in region
			int w = 0;
			Iterator<WebContentVisibility> it = vSet.iterator();

			while (it.hasNext()) {
				WebContentVisibility v = it.next();
				if (w >= weight && v.getWeight() <= w) {
					v.setWeight(w + 1);
				}
				w++;
			}

		}
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

	private String[] readAvailableLayouts() {
		PrivateResource res = resourceService.getTemplateResource("", "");

		File dir = res.getFile();

		return dir.list(new FilenameFilter() {
			public boolean accept(File arg, String arg1) {
				File f = new File(arg.getAbsolutePath() + "/" + arg1);
				return f.isDirectory() && !(arg1.charAt(0) == '.');
			}
		});
	}

	@SetupRender
	public void beforeRender() {
		editPageType = pageType.getPersistenceState() == PersistenceState.NEW ? pageType : (WebNodeType) cayenneService.newContext()
				.localObject(pageType.getObjectId(), pageType);
		layoutSelectModel = new StringSelectModel(readAvailableLayouts());
	}

	Object onSubmitFromPageTypeEditForm() {
		ObjectContext ctx = editPageType.getObjectContext();
		switch (action) {
		case cancel:
			ctx.rollbackChanges();
			break;
		case delete:
			ctx.deleteObject(editPageType);
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
