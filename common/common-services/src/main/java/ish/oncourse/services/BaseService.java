package ish.oncourse.services;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

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
	
	
	private static final Logger logger = LogManager.getLogger();


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

	public T refresh(Long willowId) {
		return 	ObjectSelect.query(getEntityClass(), ExpressionFactory.matchDbExp(IBaseService.ID_PK_COLUMN, willowId))
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH, getEntityClass().getSimpleName())
				.selectOne(getCayenneService().sharedContext());
	}

	@Override
	public T findById(Long willowId) {

		if ((willowId != null) && (willowId > 0)) {

			try {
				return ObjectSelect.query(getEntityClass()).
						where(ExpressionFactory.matchDbExp(IBaseService.ID_PK_COLUMN, willowId)).
						cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
						cacheGroup(getCacheGroup()).
						selectOne(getCayenneService().sharedContext());

			} catch (Exception e) {
				logger.error("Query resulted in Exception thrown. willowId: {}", willowId, e);
			}
		}
		return null;
	}

	@Override
	public List<T> findByQualifier(Expression qualifier) {
		try {
			return new ArrayList<>(ObjectSelect.query(getEntityClass(), qualifier).
					cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).						
					cacheGroup(getCacheGroup()).
					select(getCayenneService().sharedContext()));
		} catch (Exception e) {
			logger.error("Query resulted in Exception thrown. Query: {}", qualifier, e);
			//TODO: Should the exception be rethrown to indicate error condition to the client code?
			return new ArrayList<>();
		}
	}
	
	public String getCacheGroup() {
		if (webSiteService.getCurrentWebSite() != null
				&& webSiteService.getCurrentWebSite().getSiteKey() != null) {
			return webSiteService.getCurrentWebSite().getSiteKey();
		} else {
			return getEntityClass().getSimpleName();
		}
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
}
