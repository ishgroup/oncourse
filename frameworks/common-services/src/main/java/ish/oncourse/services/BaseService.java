/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services;

import java.lang.reflect.ParameterizedType;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author marek
 */
public class BaseService<T> implements IBaseService<T> {

	@Inject
	private ICayenneService cayenneService;
	@Inject
	private IWebSiteService webSiteService;
	private Class<T> entityClass;
	
	private static final Logger LOGGER = Logger.getLogger(BaseService.class);


	@SuppressWarnings("unchecked")
	public BaseService() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	protected ICayenneService getCayenneService() {
		return cayenneService;
	}

	protected IWebSiteService getWebSiteService() {
		return webSiteService;
	}

	@Override
	public T findById(Long willowId) {

		T record = null;

		if ((willowId != null) && (willowId > 0)) {

			try {
				Expression qualifier = ExpressionFactory.matchDbExp(
						IBaseService.ID_PK_COLUMN, willowId);

				List<T> results = findByQualifier(qualifier);

				if (results.size() == 1) {
					record = results.get(0);
				} else {
					LOGGER.error("Query returned multiple results where only one expected");
				}
			} catch (Exception e) {
				LOGGER.error("Query resulted in Exception thrown", e);
			}
		}

		return record;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByQualifier(Expression qualifier) {

		List<T> results = new ArrayList<T>();
		SelectQuery query = new SelectQuery(getEntityClass());
		query.andQualifier(qualifier);

		try {
			results = (List<T>) getCayenneService().sharedContext().performQuery(query);
			
			if ((results == null) || (results.isEmpty())) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Query returned no results: " + query);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Query resulted in Exception thrown", e);
		}

		return results;
	}
}
