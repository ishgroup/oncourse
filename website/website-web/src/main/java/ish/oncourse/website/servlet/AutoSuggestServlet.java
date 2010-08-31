package ish.oncourse.website.servlet;

import ish.oncourse.services.search.ISearchService;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class AutoSuggestServlet extends ServiceAwareServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		ISearchService searchService = getService(ISearchService.class);

		String term = req.getParameter("term");
		QueryResponse suggestions = searchService.autoSuggest(term);

		JSONArray jsonArray = new JSONArray();

		int i = 0, j = suggestions.getResults().size() - 1;

		for (SolrDocument doc : suggestions.getResults()) {
			String doctype = (String) doc.get("doctype");
			if ("course".equalsIgnoreCase(doctype)) {
				jsonArray.put(j--, buildCourse(doc));
			} else if ("location".equalsIgnoreCase(doctype)) {
				jsonArray.put(i++, buildLocation(doc));
			}
		}

		resp.setContentType("application/json");
		resp.getWriter().println(jsonArray.toString());
	}

	private JSONObject buildCourse(SolrDocument doc) {
		JSONObject obj = new JSONObject();
		obj.put("label", doc.get("course_name"));
		obj.put("category", "Courses");
		obj.put("href", "/courses?id=" + doc.get("id"));
		return obj;
	}

	private JSONObject buildLocation(SolrDocument doc) {
		JSONObject obj = new JSONObject();
		obj.put("label", doc.get("location_suburb") + " "
				+ doc.get("location_postcode"));
		obj.put("category", "Show courses near...");
		obj.put("href", "/courses?near=" + doc.get("postcode"));
		return obj;
	}
}
