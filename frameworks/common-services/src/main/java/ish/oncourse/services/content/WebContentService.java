package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
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
			text = textileConverter.convertCustomTextile(text, errors);

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
		return !listResult.isEmpty() ? listResult.get(new Random()
				.nextInt(listResult.size())) : null;
	}

	public List<WebContent> loadByIds(Object... ids) {
		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.inDbExp("id", ids));

		List<WebContent> list = cayenneService.sharedContext().performQuery(q);

		return list;
	}

	public List<WebContent> getBlocks() {

		SelectQuery q = new SelectQuery(WebContent.class);

		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		Expression expr = ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITY_PROPERTY + "."
						+ WebContentVisibility.WEB_NODE_PROPERTY, null);

		expr = expr.orExp(ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITY_PROPERTY, null));
		
		q.andQualifier(expr);

		return cayenneService.sharedContext().performQuery(q);
	}

	public WebContent newWebContent() {
		WebContent webContent = cayenneService.sharedContext().newObject(
				WebContent.class);

		webContent.setWebSite(webSiteService.getCurrentWebSite());
		webContent.setContent("Sample content text.");

		cayenneService.sharedContext().commitChanges();

		return webContent;
	}

	public SortedSet<WebContent> getBlocksForRegionKey(RegionKey regionKey) {

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		if (regionKey == RegionKey.unassigned) {

			q.andQualifier(ExpressionFactory.matchExp(
					WebContent.WEB_CONTENT_VISIBILITY_PROPERTY, null));

		} else {
			q.andQualifier(ExpressionFactory.matchExp(
					WebContent.WEB_CONTENT_VISIBILITY_PROPERTY + "."
							+ WebContentVisibility.REGION_KEY_PROPERTY,
					regionKey));
		}

		return new TreeSet<WebContent>(cayenneService.sharedContext()
				.performQuery(q));
	}
}
