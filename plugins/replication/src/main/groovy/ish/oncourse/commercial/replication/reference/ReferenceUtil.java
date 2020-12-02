/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference;

import ish.oncourse.server.cayenne.Country;
import ish.oncourse.server.cayenne.Language;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Qualification;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

/**
 */
public class ReferenceUtil {

	public static Language findLanguageByWillowId(ObjectContext objectContext, Long willowId) {
		var q = SelectQuery.query(Language.class, Language.WILLOW_ID.eq(willowId));
		var list = objectContext.select(q);
		return (list.isEmpty()) ? null : list.get(0);
	}

	public static Country findCountryByWillowId(ObjectContext objectContext, Long willowId) {
		var q = SelectQuery.query(Country.class, Language.WILLOW_ID.eq(willowId));
		var list = objectContext.select(q);
		return (list.isEmpty()) ? null : list.get(0);
	}

	public static Qualification findQualificationByWillowId(ObjectContext objectContext, Long willowId) {
		var q = SelectQuery.query(Qualification.class, Qualification.WILLOW_ID.eq(willowId));
		var list = objectContext.select(q);
		return (list.isEmpty()) ? null : list.get(0);
	}

	public static Module findModuleByWillowId(ObjectContext objectContext, Long willowId) {
		var q = SelectQuery.query(Module.class, Module.WILLOW_ID.eq(willowId));
		var list = objectContext.select(q);
		return (list.isEmpty()) ? null : list.get(0);
	}
}
