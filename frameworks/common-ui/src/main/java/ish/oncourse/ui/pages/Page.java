package ish.oncourse.ui.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeContent;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;

public class Page {

	private static final String WELCOME_TEMPLATE_ID = "welcome";

	private static final Logger LOGGER = Logger.getLogger(Page.class);

	private static final String MAIN_PAGE_NAME = "Index";
	
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private Request request;

	@Inject
	private ITextileConverter textileConverter;

	@Component
	private Zone regionZone;

	@Property
	private WebNodeContent activeRegion;

	@Property
	@Persist
	private WebNode activeNode;

	@Inject
	private ComponentResources componentResources;
	
	@SetupRender
	void beforeRender() {
		this.activeNode = (this.activeNode != null) ? this.activeNode
				: getCurrentNode();
	}

	public Zone regionZone() {
		return regionZone;
	}

	public IWebNodeService getWebNodeService() {
		return webNodeService;
	}

	public void selectActiveRegion(WebNodeContent activeRegion) {
		this.activeRegion = activeRegion;
	}

	public void selectActiveNode(WebNode node) {
		this.activeNode = node;
	}

	protected WebNode getCurrentNode() {

		WebNode node = null;

		if (request.getAttribute(IWebNodeService.NODE) != null) {
			return (WebNode) request.getAttribute(IWebNodeService.NODE);
		} else if (request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			try {
				Integer nodeNumber = Integer.parseInt(request
						.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
				node = webNodeService.getNodeForNodeNumber(nodeNumber);
			} catch (Exception e) {
				LOGGER.debug("Unable to convert node number to integer: "
						+ request
								.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
			}
		} else if (request.getAttribute(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			try {
				Integer nodeNumber = Integer.parseInt(request.getAttribute(
						IWebNodeService.NODE_NUMBER_PARAMETER).toString());
				node = webNodeService.getNodeForNodeNumber(nodeNumber);
			} catch (Exception e) {
				LOGGER.debug("Unable to convert node number to integer: "
						+ request
								.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
			}
		} else if (request.getParameter(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			String pagePath = request
					.getParameter(IWebNodeService.PAGE_PATH_PARAMETER);
			node = webNodeService.getNodeForNodeName(pagePath);
		} else if (request.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			String pagePath = (String) request
					.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER);
			node = webNodeService.getNodeForNodeName(pagePath);
		}

		return node;
	}

	public String getActiveRegionContent() {
		String text = activeRegion.getContent();

		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);

		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			ValidationErrors errors = new ValidationErrors();
			text = textileConverter.convert(text, errors);
			if (errors.hasFailures()) {
				try {
					throw new ValidationException(errors);
				} catch (ValidationException e) {
					e.printStackTrace();
				}
			}
		}

		return text;
	}

	public DynamicDelegate getBlockSource() {
		return new DynamicDelegate() {
			public Block getBlock(final String regionKey) {
				final Expression expr = ExpressionFactory.matchExp(
						WebNodeContent.REGION_KEY_PROPERTY, regionKey);

				final WebNodeContent nodeContent = expr.filterObjects(
						activeNode.getWebNodeContents()).get(0);

				selectActiveRegion(nodeContent);

				return regionZone.getBody();
			}

			public ComponentResources getComponentResources() {
				return null;
			}
		};
	}

	public boolean isRegionSelected() {
		return this.activeRegion != null;
	}
	
	public String getTemplateId(){
		String pageName = componentResources.getPageName();

		if (MAIN_PAGE_NAME.equals(pageName)) {
			return WELCOME_TEMPLATE_ID;
		}
		return "";
	}
}
