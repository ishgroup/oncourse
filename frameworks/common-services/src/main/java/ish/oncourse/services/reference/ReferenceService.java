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
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SortOrder;


/**
 * Implementation of the Reference Service.
 *
 * @author Marek Wawrzyczny
 */
public abstract class ReferenceService<T> extends BaseService<T> implements IReferenceService<T> {

	private static final Logger LOGGER = Logger.getLogger(ReferenceService.class);
	private static int BATCH_SIZE = 200;


	public ReferenceService() {
		super();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getForReplication(Long ishVersion) {

		List<T> records = null;

		if ((ishVersion != null) && (ishVersion > 0)) {

			Expression qualifier = ExpressionFactory.matchExp(
					IReferenceService.ISH_VERSION_PROPERTY, ishVersion);

			SelectQuery query = new SelectQuery(getEntityClass());
			query.andQualifier(qualifier);
			query.addOrdering(ID_PK_COLUMN, SortOrder.ASCENDING);
			query.setPageSize(BATCH_SIZE);

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

	@Override
	public Long findMaxIshVersion() {

		Long max = null;

		String sql = "select max(ish_version) from " + getEntityClass().getName();
		SQLTemplate query = new SQLTemplate(getEntityClass(), sql);
		List<?> results = getCayenneService().sharedContext().performQuery(query);

		if ((results != null) && ! results.isEmpty()) {
			try {
				max = (Long) results.get(0);
			} catch(Exception e) {
				LOGGER.error("Error while attempting to convert ish_version", e);
			}
		}

		return max;
	}
}
