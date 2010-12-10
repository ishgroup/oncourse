package ish.oncourse.ui.pages;

import ish.oncourse.services.search.ISearchService;
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

		SolrDocumentList responseResults = searchService.searchSuburbs(term)
				.getResults();

		JSONArray array = new JSONArray();

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
