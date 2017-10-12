package ish.oncourse.pages.internal;

import ish.oncourse.model.Country;
import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SuburbsAutocomplete;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;
import static ish.oncourse.services.search.SuburbsAutocomplete.REQUEST_PARAM_term;

public class AutoComplete {

    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

	@Inject
	private ICayenneService cayenneService;

    StreamResponse onActionFromSub() {
        return SuburbsAutocomplete.valueOf(request, searchService).getResult();
    }

	StreamResponse onActionFromCountry() {
		String term = StringUtils.trimToNull(request.getParameter(REQUEST_PARAM_term));
		final JSONArray array = new JSONArray();

		if (term != null) {
			ObjectContext context = cayenneService.sharedContext();
			List<Country> countries = ObjectSelect.query(Country.class).
            cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebContent.class.getSimpleName()).
            where(Country.NAME.likeIgnoreCase("%"+term+"%")).
					select(context);
					
			for (Country country : countries) {
				JSONObject obj = new JSONObject();
				obj.put("id", country.getName());
				obj.put("label", country.getName());
				obj.put("value", country.getName());
				array.put(obj);
			}
		}
		return new TextStreamResponse("text/json", array.toString());
	}
}
