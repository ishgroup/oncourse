package ish.oncourse.services.host;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.WebHostName;
import ish.oncourse.services.persistence.ICayenneService;


@Scope("perthread")
public class WebHostNameService implements IWebHostNameService {

	private static final String WEB_HOST_CACHE_GROUP = "webhosts";

	@Inject private Request request;
	@Inject private ICayenneService cayenneService;

	private transient WebHostName webHostName;


	public WebHostName getCurrentWebHostName() {

		if (webHostName == null) {

			// for this to work in a proxied environment, Apache config must use
			// the following proxy directive:
			// ProxyPreserveHost On
			String serverName = request.getServerName().toLowerCase();

			SelectQuery query = new SelectQuery(WebHostName.class);
			query.andQualifier(ExpressionFactory.matchExp(
					WebHostName.DELETED_PROPERTY, null));
			query.orQualifier(ExpressionFactory.matchExp(
					WebHostName.DELETED_PROPERTY, false));
			query.andQualifier(ExpressionFactory.matchExp(
					WebHostName.NAME_PROPERTY, serverName));

			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(WEB_HOST_CACHE_GROUP);

			webHostName = (WebHostName) DataObjectUtils.objectForQuery(
					cayenneService.sharedContext(), query);

			if (webHostName == null) {
				throw new IllegalStateException(
						"Can't determine web host name for server name: '"
						+ serverName + "'");
			}
		}

		return webHostName;
	}
}
