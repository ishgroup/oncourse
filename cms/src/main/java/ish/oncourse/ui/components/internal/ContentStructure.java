package ish.oncourse.ui.components.internal;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.cache.RequestCached;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import ish.oncourse.ui.pages.internal.Page;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ContentStructure {
	private static final String UPDATED_ZONE_NAME = "updatedZone";
	private static final String EDITOR_ZONE_NAME = "editorZone";
	private static final String ZONE_PREFIX = "z_";
	private static final Logger logger = LogManager.getLogger();

	@Parameter
	@Property
	private WebNode node;

	@Property
	private String contentTextile;

	@Property
	private Long visibilityId;

	@Property
	private WebContentVisibility nodeVisibility;

	@Parameter
	private Zone updateZone;

	@Property
	@Component(id = "regionForm")
	private Form regionForm;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private ITextileConverter textileConverter;
	@Inject
	private IParsedContentVisitor visitor;

	@Inject
	private ICayenneService cayenneService;

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
				logger.error("The visibility is null in node {} in site {} in college {}", node.getName(),
						node.getWebSiteVersion().getWebSite().getName(),
						node.getWebSiteVersion().getWebSite().getCollege().getName());
			}
		}
	}

	public String getRegionContent() {
		ValidationErrors errors = new ValidationErrors();
		String accepted = visitor.visitWebContent(nodeVisibility.getWebContent(), errors);
		if (errors.hasFailures()) {
			syntaxError = errors.toString();
		} else {
			syntaxError = StringUtils.EMPTY;
		}
		return accepted;
	}

	public String getNodeZoneKey() {
		return ZONE_PREFIX + nodeVisibility.getRegionKey();
	}

	@OnEvent(component = "editRegion", value = "action")
	public Object onActionFromEditRegion(String visibilityId) throws Exception {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		if(!request.isXHR()){
            return page.getReloadPageBlock();
		}
		
		logger.debug("Edit region with id: {}", visibilityId);

		this.visibilityId = Long.parseLong(visibilityId);
		WebContentVisibility visibility = getWebContentVisibility();

		if (visibility == null) {
			return page.getReloadPageBlock();
		}
		this.visibilityId = visibility.getId();
		this.contentTextile = visibility.getWebContent().getContentTextile();
		return editorBlock;
	}
	
	Object onSuccessFromRegionForm() {
		if (request.getSession(false) == null || visibilityId == null) {
			return page.getReloadPageBlock();
		}
		WebContentVisibility visibility = getWebContentVisibility();
		WebContent webContent = visibility.getWebContent();
		webContent.setContentTextile(contentTextile);
		webContent.setContent(textileConverter.convertCoreTextile(contentTextile));
		visibility.getObjectContext().commitChanges();
		return page.getReloadPageBlock();
	}

	@RequestCached
	public WebContentVisibility getWebContentVisibility() {
		return SelectById.query(WebContentVisibility.class, visibilityId).selectOne(cayenneService.newContext());
	}
	
	public String getEditZoneKey() {
		return ZONE_PREFIX + getWebContentVisibility().getRegionKey();
	}
}
