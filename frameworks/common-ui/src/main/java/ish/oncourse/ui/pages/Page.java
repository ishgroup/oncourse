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
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;

public class Page {

	private static final String WELCOME_TEMPLATE_ID = "welcome";

	@Inject
	private Request request;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private Block regionBlock;

	@Property
	private WebNodeContent region;

	@Property
	@Persist
	private WebNode node;

	private Page _self = this;

	@SetupRender
	public void beforeRender() {
		this.node = webNodeService.getCurrentNode();
	}

	public WebNode getCurrentNode() {
		return this.node;
	}

	public void setCurrentNode(WebNode node) {
		this.node = node;
		request.setAttribute(webNodeService.NODE, this.node);
	}

	public String getRegionContent() {
		String text = region.getContent();

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

				_self.region = expr.filterObjects(node.getWebNodeContents())
						.get(0);

				return regionBlock;
			}

			public ComponentResources getComponentResources() {
				return null;
			}
		};
	}

	public String getTemplateId() {
		if (this.node.getId().equals(webNodeService.getHomePage().getId())) {
			return WELCOME_TEMPLATE_ID;
		}

		return "";
	}
}
