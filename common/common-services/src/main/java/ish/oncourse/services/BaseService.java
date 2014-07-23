package ish.oncourse.services;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


/**
 * Base class for majority of services containing the implementation of many 
 * generic and essential services.
 *
 * @author Marek
 */
public class BaseService<T extends Persistent> implements IBaseService<T> {

	@Inject
	private ICayenneService cayenneService;
	@Inject
	private IWebSiteService webSiteService;
	private Class<T> entityClass;

    @Inject
    private Request request;
	
	private static final Logger LOGGER = Logger.getLogger(BaseService.class);


	@SuppressWarnings("unchecked")
	public BaseService() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public BaseService(ICayenneService cayenneService, IWebSiteService webSiteService){
		this();
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
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
					//TODO: Should an exception be thrown to indicate the condition to the client?
				}
			} catch (Exception e) {
				LOGGER.error("Query resulted in Exception thrown", e);
				//TODO: Should the exception be rethrown to indicate error condition to the client code?
			}
		}

		return record;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByQualifier(Expression qualifier) {

		List<T> results = null;
		SelectQuery query = new SelectQuery(getEntityClass());
		query.andQualifier(qualifier);

		try {
			results = (List<T>) getCayenneService().sharedContext().performQuery(query);
		} catch (Exception e) {
			LOGGER.error("Query resulted in Exception thrown", e);
			//TODO: Should the exception be rethrown to indicate error condition to the client code?
		}

		if (results == null) {
			results = new ArrayList<>();
		}

		if (results.isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Query returned no results: " + query);
			}
		}

		return results;
	}

	/**
	 * Wrapper around the injected Cayenne service
	 *
	 * @return Cayenne service used internally
	 */
	protected ICayenneService getCayenneService() {
		return cayenneService;
	}

	/**
	 * Wrapper around the inject Web Site service
	 *
	 * @return Web Site service used internally
	 */
	protected IWebSiteService getWebSiteService() {
		return webSiteService;
	}

    public <V> V getFromRequest(Class<V> vClass, String key)
    {
        Object value = request.getAttribute(key);
        if (value != null && vClass.isAssignableFrom(value.getClass()))
            return (V) value;
        else
            return null;
    }

    public <V> void putToRequest(String key, V value)
    {
        request.setAttribute(key, value);
    }


}
