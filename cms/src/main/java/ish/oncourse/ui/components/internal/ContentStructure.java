package ish.oncourse.ui.components.internal;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.ui.pages.internal.Page;
import ish.oncourse.ui.utils.EmptyRenderable;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.URL;

public class ContentStructure {
	private static final String UPDATED_ZONE_NAME = "updatedZone";
	private static final String EDITOR_ZONE_NAME = "editorZone";
	private static final String ZONE_PREFIX = "z_";
	private static final Logger LOGGER = Logger.getLogger(ContentStructure.class);

	@Parameter
	@Property
	private WebNode node;

	@Property
	@Persist
	private WebContentVisibility visibility;

	@Parameter
	private Zone updateZone;

	@SuppressWarnings("all")
	@Property
	@Component(id = "regionForm")
	private Form regionForm;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private Block editorBlock;

	@Inject
	@Property
	private Block regionContentBlock;

	@SuppressWarnings("all")
	@Property
	private String syntaxError;

	@InjectPage
	private Page page;

	@Inject
	private Request request;
	
	@SetupRender
	public void beforeRender() {
		for(WebContentVisibility visibility: node.getWebContentVisibility()){
			if(visibility == null){
				LOGGER.error(String.format("The visibility is null in node %s in site %s in college %s", node.getName(), 
						node.getWebSiteVersion().getWebSite().getName(), 
						node.getWebSiteVersion().getWebSite().getCollege().getName()));
			}
		}
	}

	public String getRegionContent() {
		ParsedContentVisitor visitor = new ParsedContentVisitor(textileConverter);
		String accepted = visibility.getWebContent().accept(visitor);
		ValidationErrors errors = visitor.getErrors();
		if (errors != null) {
			syntaxError = errors.toString();
		} else {
			syntaxError = StringUtils.EMPTY;
		}
		return accepted;
	}

	@OnEvent(component = "editRegion", value = "action")
	public Object onActionFromEditRegion(String id) throws Exception {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		if(!request.isXHR()){
			return new URL(request.getServerName());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Edit region with id: %s", id));
		}

		ObjectContext ctx = node.getObjectContext().createChildContext();
		WebContent regionForEdit = webContentService.findById(Long.parseLong(id));
		if (regionForEdit == null) {
			return page.getReloadPageBlock();
		}
		WebContent region = (WebContent) ctx.localObject(regionForEdit.getObjectId(), null);

		this.visibility = region.getWebContentVisibility(node);

		return editorBlock;
	}
	
	private boolean isSessionAndEntityValid() {
		return (request.getSession(false) != null && visibility != null && visibility.getObjectContext() != null);
	}

	Object onSuccessFromRegionForm() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		WebContent webContent = visibility.getWebContent();
		webContent.setContent(textileConverter.convertCoreTextile(webContent.getContentTextile()));
		this.visibility.getObjectContext().commitChanges();
		return new MultiZoneUpdate(EDITOR_ZONE_NAME, new EmptyRenderable()).add(getCurrentZoneKey(), regionContentBlock).add(UPDATED_ZONE_NAME, updateZone);
	}
	
	public boolean isNotEmptyVisibility() {
		return visibility != null;
	}

	public String getCurrentZoneKey() {
		return ZONE_PREFIX + this.visibility.getRegionKey();
	}
}
