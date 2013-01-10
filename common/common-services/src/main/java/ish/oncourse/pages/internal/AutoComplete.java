package ish.oncourse.pages.internal;

import ish.oncourse.model.Country;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

import static ish.oncourse.services.search.SolrQueryBuilder.FIELD_postcode;
import static ish.oncourse.services.search.SolrQueryBuilder.FIELD_suburb;

public class AutoComplete {

    private final static String REQUEST_PARAM_term = "term";
    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

	@Inject
	private ICayenneService cayenneService;

    StreamResponse onActionFromSub() {
        String term = StringUtils.trimToNull(request.getParameter(REQUEST_PARAM_term));
        final JSONArray array = new JSONArray();

        //this is incorrect state which mean that next js code not able to evaluate input item value in some browser or pass less then 3 characters
        // so we should return empty result.Used to avoid search fail in #14742 task.
        /*
           * $j(".suburb-autocomplete").autocomplete({source: '/ish/internal/autocomplete.sub', minLength: 3,
           *	select: function(event, ui) {
           *	setPostcodeAndStateFromSuburb(this.form, ui.item.value);
           *	}
           *	});
        */
        if (term != null && term.length() >= 3) {
            SolrDocumentList responseResults = searchService.searchSuburbs(term);

            for (SolrDocument doc : responseResults) {
                String val = doc.getFieldValue(FIELD_suburb) + " "
                        + doc.getFieldValue(FIELD_postcode);

                JSONObject obj = new JSONObject();

                obj.put("id", val);
                obj.put("label", val);
                obj.put("value", val);

                array.put(obj);
            }
        }
        return new TextStreamResponse("text/json", array.toString());
    }

	StreamResponse onActionFromCountry() {
		String term = StringUtils.trimToNull(request.getParameter(REQUEST_PARAM_term));
		final JSONArray array = new JSONArray();

		if (term != null)
		{
			ObjectContext context = cayenneService.sharedContext();
			SelectQuery query = new SelectQuery(Country.class);
			Expression exp = ExpressionFactory.likeExp(Country.NAME_PROPERTY,"%"+term+"%");
			query.setQualifier(exp);
			List<Country> countries = context.performQuery(query);
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
