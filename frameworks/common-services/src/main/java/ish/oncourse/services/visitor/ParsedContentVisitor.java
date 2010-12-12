package ish.oncourse.services.visitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.visitor.BaseVisitor;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;
import org.apache.log4j.Logger;


public class ParsedContentVisitor extends BaseVisitor<String> {

	private ITextileConverter textileConverter;
	private static final Logger LOGGER = Logger.getLogger(ParsedContentVisitor.class);


	public ParsedContentVisitor(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
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
	public String visitWebContent(WebContent block) {
		String text = "";

		if ((block != null) && (block.getContent() != null)) {
			text = block.getContent();
			Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);
			Matcher matcher = pattern.matcher(text);

			if (matcher.find()) {
				ValidationErrors errors = new ValidationErrors();
				text = textileConverter.convertCustomTextile(text, errors);

				if (errors.hasFailures()) {
					LOGGER.error("Validation errors on Textile cnversion", new ValidationException(errors));
				}
			}
		}
		return text;
	}
	
}
