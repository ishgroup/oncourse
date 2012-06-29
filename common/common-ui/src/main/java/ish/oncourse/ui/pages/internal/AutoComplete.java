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

public class AutoComplete {

	@Inject
	private Request request;

	@Inject
	private ISearchService searchService;

	StreamResponse onActionFromSub() {
		String term = request.getParameter("term");
		final JSONArray array = new JSONArray();
		if (StringUtils.trimToNull(term) == null || StringUtils.trimToNull(term).length() < 3) {
			//this is incorrect state which mean that next js code not able to evaluate input item value in some browser or pass less then 3 characters 
			// so we should return empty result.Used to avoid search fail in #14742 task.
			/*
			 * $j(".suburb-autocomplete").autocomplete({source: '/ui/internal/autocomplete.sub', minLength: 3, 
			 *	select: function(event, ui) {
			 *	setPostcodeAndStateFromSuburb(this.form, ui.item.value);
			 *	} 
			 *	});
			 */
			return new TextStreamResponse("text/json", array.toString());
		}
		SolrDocumentList responseResults = searchService.searchSuburbs(term).getResults();

		for (SolrDocument doc : responseResults) {
			String val = (String) doc.getFieldValue("suburb") + " "
					+ (String) doc.getFieldValue("postcode");
			
			JSONObject obj = new JSONObject();
			
			obj.put("id", val);
			obj.put("label", val);
			obj.put("value", val);
			
			array.put(obj);
		}

		return new TextStreamResponse("text/json", array.toString());
	}
}
