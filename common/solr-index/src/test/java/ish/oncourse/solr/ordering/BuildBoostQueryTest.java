/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.ordering;

import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.solr.query.BuildBoostQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: akoiro
 * Date: 13/6/18
 */
public class BuildBoostQueryTest {

	@Test
	public void test() {
		SolrQuery q = new BuildBoostQuery()
				.hideAge(ClassAge.valueOf(1, ClassAgeType.afterClassStarts))
				.build();

		Assert.assertEquals("ms(NOW-1DAYS)", q.getParams(BuildBoostQuery.Param.hideAge.name())[0]);

		q = new BuildBoostQuery()
				.hideAge(ClassAge.valueOf(1, ClassAgeType.beforeClassStarts))
				.build();
		Assert.assertEquals("ms(NOW+1DAYS)", q.getParams(BuildBoostQuery.Param.hideAge.name())[0]);
	}

}
