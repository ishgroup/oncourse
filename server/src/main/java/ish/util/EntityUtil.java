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
package ish.util;

import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of general utilities to deal with CayenneEntity.
 *
 * // TODO: turn into an injected service, move to the common package. Remove duplicate code from client.
 *
 */
public class EntityUtil {
	private static final Logger logger = LogManager.getLogger(EntityUtil.class);

	private static final Package COURSE_PACKAGE = Course.class.getPackage();
	// batch size for queries due to jtds limitation in 2000 elements in IN clause
	private static final int BATCH_SIZE = 2000;

	/**
	 * resolves the entity name based on string.
	 *
	 * @param entityName to find
	 * @param <T>
	 * @return required Class
	 */
	public static <T extends PersistentObjectI> Class<T> entityClassForName(String entityName) {
		try {
			Class<?> clazz;

			// TODO: generalize this to avoid explicit specification of PaymentInterface since it can be in the reports along with Cayenne entities
			if (PaymentInterface.class.getSimpleName().equals(entityName)) {
				clazz = PaymentInterface.class;
			} else {
				clazz = Class.forName(COURSE_PACKAGE.getName() + "." + entityName.substring(entityName.lastIndexOf('.') + 1));
			}

			if (PersistentObjectI.class.isAssignableFrom(clazz)) {
				return (Class<T>) clazz;
			}
			throw new RuntimeException(String.format("Found class is not Cayenne entity class: %s.", clazz.getSimpleName()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("Entity class not found: %s.", entityName), e);
		}
	}

	/**
	 * returns a list of objects based on the given list of ids.
	 *
	 * @param context to use for query
	 * @param entityClass to query
	 * @param ids of the objects
	 * @param <T>
	 * @return list of the objects
	 */
	public static <T extends PersistentObjectI> List<T> getObjectsByIds(ObjectContext context, Class<T> entityClass, List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		// we need to return list of records in the same order as their ids go in received list
		// since db will return records in arbitrary order not depending on order of ids in IN clause
		// we need to reorder records manually
		//
		// to do this all records are accumulated into HashMap having id as a key
		// and then final list of records is formed to return items in necessary order

		Map<Long, T> records = new HashMap<>();

		for (var offset = 0; offset < ids.size(); offset += BATCH_SIZE) {
			var qual = ExpressionFactory.inExp("id", ids.subList(offset, Math.min(offset + BATCH_SIZE, ids.size())));
			var query = SelectQuery.query(entityClass, qual);

			var recordsBatch = context.select(query);

			for (var record : recordsBatch) {
				records.put((Long) record.getObjectId().getIdSnapshot().get(CayenneDataObject.ID), record);
			}
		}

		List<T> result = new ArrayList<>(records.size());

		for (var id : ids) {
			result.add(records.get(id));
		}

		return result;
	}

	/**
	 * Returns a list of objects based on the given qualifier.
	 *
	 * @param context Cayenne context
	 * @param entityClass Cayenne entity class
	 * @param qualifier Cayenne expression qualifier
	 * @return list of objects based on the given qualifier
	 */
	public static <T extends PersistentObjectI> PaginatedResultIterable<T> getObjectsByQualifier(ObjectContext context, Class<T> entityClass, Expression qualifier) {
		var query = SelectQuery.query(entityClass);
		if (qualifier != null) {
			query.setQualifier(qualifier);
		}
		return PaginatedResultIterable.valueOf(context, query, 500);
	}

	/**
	 * Translates a list of persistent objects into a list of related persistent objects using cayenne path.
	 *
	 * @param records - list of records
	 * @param pathToEntity  - translation path
	 * @return list of the translated records
	 */
	public static <T extends PersistentObjectI> List<T> applyPathTranform(List<T> records, String pathToEntity) {
		if (!StringUtils.isEmpty(pathToEntity) && pathToEntity.contains(".")) {
			return applyPathTranform(records, pathToEntity, 1);
		}
		return records;

	}

	private static <T extends PersistentObjectI> List<T> applyPathTranform(List<T> records, String pathToEntity, int depth) {
		if (pathToEntity.contains(".")) {
			var pathToEntityArray = pathToEntity.split("\\.");

			if (records.size() > 0 && pathToEntityArray.length > depth) {
				List<T> result = new ArrayList<>();
				for (var record : records) {
					try {
						var object = record.getValueForKey(pathToEntityArray[depth]);
						if (object instanceof PersistentObjectI) {
							result.add((T) object);
						} else {
							result.addAll((List)object);
						}
					} catch (Exception e) {
						throw new RuntimeException("failed to materialise "+pathToEntityArray[depth]+" for "+record);
					}
				}
				return applyPathTranform(result, pathToEntity, depth + 1);
			}
		}
		return records;
	}
}
