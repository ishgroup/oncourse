/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import ish.oncourse.services.BaseService;

/**
 * Implementation of the Reference Service.
 * 
 * @author Marek Wawrzyczny
 */
public abstract class AbstractReferenceService<T extends Persistent> extends BaseService<T> implements IReferenceService<T> {

	private static final Logger LOGGER = Logger.getLogger(AbstractReferenceService.class);

	/**
	 * Ish version property name.
	 */
	private static final String ISH_VERSION_PROPERTY = "ishVersion";

	public AbstractReferenceService() {
		super();
	}

	/**
	 * @see IReferenceService#getForReplication(Long)
	 */
	@SuppressWarnings({ "unchecked" })
	public List<T> getForReplication(Long ishVersion) {

		List<T> records = null;

		if ((ishVersion != null) && (ishVersion >= 0)) {

			SelectQuery query = new SelectQuery(getEntityClass());
			query.setQualifier(getQueryQualifier(ishVersion));

			try {
				records = (List<T>) getCayenneService().sharedContext().performQuery(query);
			} catch (Exception e) {
				LOGGER.error("Query resulted in Exception thrown", e);
			}
		}

		if (records == null) {
			records = new ArrayList<>();
		}

		return records;
	}

	protected Expression getQueryQualifier(Long ishVersion) {
		return ExpressionFactory.matchExp(ISH_VERSION_PROPERTY, ishVersion);
	}

	/**
	 * @see IReferenceService#findMaxIshVersion()
	 */
	@SuppressWarnings("rawtypes")
	public Long findMaxIshVersion() {

		Long max = null;

		String sql = "select max(ishVersion) as MAXV from " + getEntityClass().getSimpleName();
		SQLTemplate query = new SQLTemplate(getEntityClass(), sql);
		query.setFetchingDataRows(true);

		@SuppressWarnings("unchecked")
		List<Map> results = getCayenneService().sharedContext().performQuery(query);

		if (!results.isEmpty()) {
			try {
				max = (Long) results.get(0).get("MAXV");
			} catch (Exception e) {
				LOGGER.error("Error while attempting to convert ish_version", e);
				throw new RuntimeException("Error while attempting to convert ish_version", e);
			}
		}

		return max;
	}

	/**
	 * @see IReferenceService#getNumberOfRecordsForIshVersion(Long)
	 */
	@Override
	public Long getNumberOfRecordsForIshVersion(Long ishVersion) {
		
		String sql = String.format("select count(*) as number from %s where ishVersion=%s", getEntityClass().getSimpleName(), ishVersion);
		SQLTemplate query = new SQLTemplate(getEntityClass(), sql);
		query.setFetchingDataRows(true);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Map> results = getCayenneService().sharedContext().performQuery(query);
		long number = 0;

		if (!results.isEmpty()) {
			try {
				number = (Long) results.get(0).get("number");
			} catch (Exception e) {
				LOGGER.error(String.format("Unable to read the number of records for entity %s and ishVersion %s", getEntityClass()
						.getSimpleName(), ishVersion), e);
			}
		}

		return number;
	}
}
