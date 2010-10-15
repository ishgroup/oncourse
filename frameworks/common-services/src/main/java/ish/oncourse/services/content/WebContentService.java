package ish.oncourse.services.content;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebContentService implements IWebContentService {
	
	@Inject
	private ITextileConverter textileConverter;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ICayenneService cayenneService;

	public String getParsedContent(WebContent webContent) {
		String text = webContent.getContent();

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

	public WebContent getWebContent(String searchProperty, Object value) {
		WebSite currentSite = webSiteService.getCurrentWebSite();
		
		ObjectContext sharedContext = cayenneService.sharedContext();
		
		Expression qualifier = ExpressionFactory.matchExp(
				WebContent.WEB_SITE_PROPERTY, currentSite);
		
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		
		SelectQuery query = new SelectQuery(WebContent.class, qualifier);
		
		@SuppressWarnings("unchecked")
		List<WebContent> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}
}
