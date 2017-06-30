package ish.oncourse.services.location;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Arrays;
import java.util.List;

public class PostCodeDbService implements IPostCodeDbService {
	
	@Inject
	private ICayenneService cayenneService;

	@SuppressWarnings("unchecked")
	public List<PostcodeDb> findBySuburb(String... suburbs) {
		SelectQuery q = new SelectQuery(PostcodeDb.class);
		q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		q.setCacheGroup(PostcodeDb.class.getSimpleName());
		q.andQualifier(ExpressionFactory.inExp(PostcodeDb.SUBURB_PROPERTY, Arrays.asList(suburbs)));
		return cayenneService.sharedContext().performQuery(q);
	}
	
	@SuppressWarnings("unchecked")
	public List<PostcodeDb> getAllPostcodes() {
		final SelectQuery query = new SelectQuery(PostcodeDb.class);
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		query.setCacheGroup(PostcodeDb.class.getSimpleName());
		query.andQualifier(ExpressionFactory.noMatchExp(PostcodeDb.SUBURB_PROPERTY, StringUtils.EMPTY));
		//query.setFetchLimit(20);
		return cayenneService.newContext().performQuery(query);
	}
}
