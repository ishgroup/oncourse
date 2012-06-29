package ish.oncourse.ui.pages.internal;

import ish.oncourse.services.search.ISearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import static ish.oncourse.services.search.SolrQueryBuilder.FIELD_postcode;
import static ish.oncourse.services.search.SolrQueryBuilder.FIELD_suburb;

public class AutoComplete {

    private final static String REQUEST_PARAM = "term";
    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

    StreamResponse onActionFromSub() {
        String term = request.getParameter(REQUEST_PARAM);
        final JSONArray array = new JSONArray();

        //this is incorrect state which mean that next js code not able to evaluate input item value in some browser or pass less then 3 characters
        // so we should return empty result.Used to avoid search fail in #14742 task.
        /*
           * $j(".suburb-autocomplete").autocomplete({source: '/ui/internal/autocomplete.sub', minLength: 3,
           *	select: function(event, ui) {
           *	setPostcodeAndStateFromSuburb(this.form, ui.item.value);
           *	}
           *	});
        */
        if (StringUtils.trimToNull(term) == null || StringUtils.trimToNull(term).length() < 3) {
            SolrDocumentList responseResults = searchService.searchSuburbs(term).getResults();

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
}
