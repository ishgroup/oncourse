package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.ui.utils.EmptyRenderable;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ContentStructure {

	private static final Logger LOGGER = Logger.getLogger(ContentStructure.class);

	@Parameter
	@Property
	private WebNode node;

	@Property
	@Persist
	private WebContentVisibility visibility;

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
	private Block editorBlock;

	@Inject
	@Property
	private Block regionContentBlock;

	public String getRegionContent() {
		return visibility.getWebContent().accept(new ParsedContentVisitor(textileConverter));
	}

	Object onActionFromEditRegion(String id) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Edit region with id: %s", id));
		}

		ObjectContext ctx = node.getObjectContext().createChildContext();
		WebContent region = (WebContent) ctx.localObject(
				webContentService.findById(Long.parseLong(id)).getObjectId(), null);

		WebNodeType webNodeType = node.getWebNodeType();
		this.visibility = region.getWebContentVisibility(webNodeType);

		return editorBlock;
	}

	Object onSuccessFromRegionForm() {
		this.visibility.getObjectContext().commitChanges();
		return new MultiZoneUpdate("editorZone", new EmptyRenderable()).add(getCurrentZoneKey(),
				regionContentBlock).add("updatedZone", updateZone);
	}

	public String getCurrentZoneKey() {
		return "z_" + this.visibility.getRegionKey();
	}
}
