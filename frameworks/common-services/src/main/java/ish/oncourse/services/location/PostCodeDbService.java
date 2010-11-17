package ish.oncourse.services.location;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.model.services.persistence.ICayenneService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PostCodeDbService implements IPostCodeDbService {
	
	@Inject
	private ICayenneService cayenneService;

	public List<PostcodeDb> loadByIds(Object... ids) {
		
		if (ids.length == 0) {
			return Collections.emptyList();
		}

		List<Object> params = Arrays.asList(ids);

		EJBQLQuery q = new EJBQLQuery(
				"select c from PostcodeDb c where c.id IN (:ids)");

		q.setParameter("ids", params);

		return cayenneService.sharedContext().performQuery(q);
	}
	
}
