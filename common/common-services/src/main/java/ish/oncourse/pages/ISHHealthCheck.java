package ish.oncourse.pages;

import ish.oncourse.model.Country;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ISHHealthCheck {

    private static final Logger LOGGER = Logger.getLogger(ISHHealthCheck.class);
    @Inject
    private ICayenneService cayenneService;

    @Inject
    private RequestGlobals requestGlobals;

    private static final String SQL_TEST_CONNECTION = "select id from Country where id = #bind($id)";

    @SetupRender
    public boolean beforeRender() throws IOException {
        HttpServletResponse httpResponse = requestGlobals.getHTTPServletResponse();

        try {
            ObjectContext context = cayenneService.newNonReplicatingContext();

            Map<String, Object> params = new HashMap<>();
            params.put("id", 1);

            SQLTemplate query = new SQLTemplate(Country.class, SQL_TEST_CONNECTION);
            query.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
            query.setParameters(params);
            List<DataRow> result = (List<DataRow>) context.performQuery(query);
            if (result.size() != 1)
            {
                httpResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "DB TEST FAILED");
            }
        } catch (Exception e) {
            LOGGER.warn(e);
            httpResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "DB TEST FAILED");
        }
        return true;
    }
}
