package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.content.cache.IContentCacheService;
import ish.oncourse.services.content.cache.IContentKeyFactory;
import ish.oncourse.services.content.cache.WillowContentKey;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;
import org.apache.cayenne.PersistentObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedContentVisitor implements IParsedContentVisitor {

	private static final Logger logger = LogManager.getLogger();
	public static final String WEB_CONTENT_CACHE = "willow.smc.web.content.cache";

	@Inject
	private IRichtextConverter textileConverter;

	@Inject
	private IContentCacheService<WillowContentKey, String> contentCacheService;

	@Inject
	private IContentKeyFactory<PersistentObject, WillowContentKey> contentKeyFactory;

	@Inject
	@Symbol(WEB_CONTENT_CACHE)
	private Boolean applyCache;
	
	@Override
	public String visitWebContent(WebContent block) {
		return visitWebContent(block, new ValidationErrors());
	}
	
	@Override
	/**
	 * Checks if web content contains any textile and converts any textile
	 * blocks to HTML content.
	 *
	 * @param block web content to check
	 *
	 * @return parsed web content
	 */
	public String visitWebContent(WebContent block, ValidationErrors errors) {

		if (block == null || block.getContent() == null) {
			return StringUtils.EMPTY;
		} else if (Boolean.FALSE.equals(applyCache)) {
			return  visit(block, errors);
		} else {
			WillowContentKey cacheKey = contentKeyFactory.createKey(this.getClass().getSimpleName(), block);
			String cachedContent = contentCacheService.get(cacheKey);

			if (cachedContent != null) {
				return cachedContent;
			} else {
				String text = visit(block, errors);
				contentCacheService.put(cacheKey,text);
				return text;
			}
		}
	}

	private String visit(WebContent block, ValidationErrors errors) {

		String text = block.getContent();
		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			text = textileConverter.convertCustomText(text, errors);
			if (errors.hasFailures()) {
				logger.debug("Validation errors on Textile cnversion", new ValidationException(errors));
			}
			text = StringEscapeUtils.unescapeHtml(text).replaceAll("(&amp;nbsp;)", " ");
		}
		
		return text;
	}

	public String visitWebNode(WebNode node) {
		throw new UnsupportedOperationException();
	}

	public String visitWebNodeType(WebNodeType type) {
		throw new UnsupportedOperationException();
	}

}
