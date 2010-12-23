/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import ish.oncourse.services.BaseService;
import java.util.Map;
import org.apache.cayenne.query.SQLTemplate;


/**
 * Implementation of the Reference Service.
 *
 * @author Marek Wawrzyczny
 */
public abstract class ReferenceService<T> extends BaseService<T> implements IReferenceService<T> {

	private static final Logger LOGGER = Logger.getLogger(ReferenceService.class);


	public ReferenceService() {
		super();
	}

	
	@SuppressWarnings({"unchecked"})
	public List<T> getForReplication(Long ishVersion) {

		List<T> records = null;

		if ((ishVersion != null) && (ishVersion >= 0)) {

			Expression qualifier = ExpressionFactory.matchExp(ISH_VERSION_PROPERTY, ishVersion);

			SelectQuery query = new SelectQuery(getEntityClass());
			query.setQualifier(qualifier);

			try {
				records = (List<T>) getCayenneService().sharedContext().performQuery(query);
			} catch (Exception e) {
				LOGGER.error("Query resulted in Exception thrown", e);
			}
		}

		if (records == null) {
			records = new ArrayList<T>();
		}

		return records;
	}

	public Long findMaxIshVersion() {

		Long max = null;

		String sql = "select max(ishVersion) from " + getEntityClass().getSimpleName();
		SQLTemplate query = new SQLTemplate(getEntityClass(), sql);
		query.setFetchingDataRows(true);
		List<?> results = getCayenneService().sharedContext().performQuery(query);

		if ((results != null) && ! results.isEmpty()) {
			try {
				Map row = (Map)results.get(0);
				max = (Long) row.get("max(ishVersion)");
			} catch(Exception e) {
				LOGGER.error("Error while attempting to convert ish_version", e);
			}
		}

		return max;
	}
}
