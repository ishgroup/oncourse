package ish.oncourse.services.location;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
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
	public List<PostcodeDb> findBySuburb(String state, String... suburbs) {

		ObjectSelect objectSelect = ObjectSelect.query(PostcodeDb.class)
				.where(PostcodeDb.SUBURB.in(Arrays.asList(suburbs)));
		if (StringUtils.trimToNull(state) != null) {
			objectSelect = objectSelect.and(PostcodeDb.STATE.eq(state));
		}
		return objectSelect.cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
				.cacheGroup(PostcodeDb.class.getSimpleName())
				.select(cayenneService.newContext());
	}
	
	@SuppressWarnings("unchecked")
	public List<PostcodeDb> getAllPostcodes() {
		final SelectQuery query = new SelectQuery(PostcodeDb.class);
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		query.setCacheGroup(PostcodeDb.class.getSimpleName());
		query.andQualifier(ExpressionFactory.noMatchExp(PostcodeDb.SUBURB_PROPERTY, StringUtils.EMPTY));
		//query.setFetchLimit(20);
		return cayenneService.newContext().performQuery(query);
	}
}
