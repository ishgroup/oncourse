package ish.oncourse.services.location;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.model.services.persistence.ICayenneService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PostCodeDbService implements IPostCodeDbService {
	
	@Inject
	private ICayenneService cayenneService;

	public List<PostcodeDb> findBySuburb(String... suburbs) {
		SelectQuery q = new SelectQuery(PostcodeDb.class);
		q.andQualifier(ExpressionFactory.inExp(PostcodeDb.SUBURB_PROPERTY, suburbs));
		return cayenneService.sharedContext().performQuery(q);
	}	
}
