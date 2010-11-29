package ish.oncourse.ui.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.ui.utils.EmptyRenderable;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;
import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

public class ContentStructureTemplate {

	private static final Logger LOGGER = Logger.getLogger(ContentStructureTemplate.class);

	@Parameter
	private WebNode node;
	
	@Parameter
	private Zone inspectorZone;

	@Property
	@Persist
	private WebContent currentRegion;

	@Property
	@Component(id = "regionForm")
	private Form regionForm;

	@Inject
	private IWebContentService webContentService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private ComponentResources resources;

	@Inject
	private IResourceService resourceService;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private Block editorBlock;

	@Inject
	@Property
	private Block regionBlock;
	
	@Inject
	@Property
	private Block regionContentBlock;

	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	@BeginRender
	RenderCommand beginRender() {
		return template.createRenderCommand(new DynamicDelegate() {
			public Block getBlock(final String regionKey) {
				RegionKey key = RegionKey.valueOf(regionKey.toLowerCase());
				
				List<WebContentVisibility> list = ExpressionFactory.matchExp(WebContentVisibility.REGION_KEY_PROPERTY, key).filterObjects(node.getWebContentVisibility());
				
				if (list.size() > 0) {
					currentRegion = list.get(0).getWebContent();
				}
				else {
					//new region. Assuming New Page was clicked.
					ObjectContext ctx = node.getObjectContext();
					
					WebContentVisibility contentVisibility = ctx.newObject(WebContentVisibility.class);
					contentVisibility.setRegionKey(key);
					
					WebContent webContent = ctx.newObject(WebContent.class);
					webContent.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
					webContent.setContent("Sample content text.");
					contentVisibility.setWebContent(webContent);
					
					node.addToWebContentVisibility(contentVisibility);
					
					ctx.commitChanges();
					
					currentRegion = webContent;
				}
					
				return regionBlock;
			}

			public ComponentResources getComponentResources() {
				return resources;
			}
		});
	}
	
	public String getRegionContent() {
		return currentRegion.accept(new ParsedContentVisitor(textileConverter));
	}
	
	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(node.getWebNodeType().getLayoutKey(), "ContentStructure.tml");
		return template;
	}
	
	public String getTemplateId() {
		return (webNodeService.getHomePage().getId() == this.node.getId()) ? IWebNodeService.WELCOME_TEMPLATE_ID
				: "";
	}

	Object onActionFromEditRegion(String id) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Edit region with id: %s", id));
		}

		ObjectContext ctx = node.getObjectContext().createChildContext();
		currentRegion = (WebContent) ctx.localObject(webContentService.findById(Long.parseLong(id)).getObjectId(), null);

		return editorBlock;
	}

	Object onSuccessFromRegionForm() {
		currentRegion.getObjectContext().commitChangesToParent();
		return new MultiZoneUpdate("editorZone", new EmptyRenderable()).add(getCurrentZoneKey(), regionContentBlock).add("inspectorZone", inspectorZone);
	}

	public String getCurrentZoneKey() {
		return "z_" + currentRegion.getWebContentVisibility().getRegionKey();
	}
}
