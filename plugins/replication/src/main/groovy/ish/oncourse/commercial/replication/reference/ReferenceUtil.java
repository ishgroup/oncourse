/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
