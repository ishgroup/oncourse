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

package ish.print.transformations;

import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.cayenne.TaggableClasses;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * PrintTransformation implementation which transforms Tag to Contact records tagged with it.
 */
public class TagContactTransformation extends PrintTransformation {

	public TagContactTransformation() {
		setInputEntityName("Tag");
		setOutputEntityName("Contact");
	}

	@Override
	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {

		if (sourceIds == null || sourceIds.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		List<PersistentObjectI> records = new ArrayList<>();

		for (int offset = 0; offset < sourceIds.size(); offset += getBatchSize()) {
			ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect.query(PersistentObjectI.class, "Contact")
					.where(ExpressionFactory.matchExp("taggingRelations.entityIdentifier", TaggableClasses.CONTACT.getDatabaseValue()))
					.and(ExpressionFactory.inExp("taggingRelations.tag.id", sourceIds.subList(offset, Math.min(offset + getBatchSize(), sourceIds.size()))))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

			records.addAll(objectSelect.select(context));
		}

		return records;
	}

}
