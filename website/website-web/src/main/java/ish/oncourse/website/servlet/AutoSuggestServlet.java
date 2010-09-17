package ish.oncourse.website.servlet;

import ish.oncourse.services.search.ISearchService;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.spatial.geohash.GeoHashUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class AutoSuggestServlet extends ServiceAwareServlet {

	private static final Logger LOGGER = Logger.getLogger(AutoSuggestServlet.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		ISearchService searchService = getService(ISearchService.class);

		String term = req.getParameter("term");
		QueryResponse suggestions = searchService.autoSuggest(term);

		JSONArray jsonArray = new JSONArray();

		int i = 0, j = suggestions.getResults().size() - 1;

		for (SolrDocument doc : suggestions.getResults()) {
			String doctype = (String) doc.get("doctype");
			if ("course".equalsIgnoreCase(doctype)) {
				jsonArray.put(i++, buildCourse(doc));
			}
		}

		for (SolrDocument doc : suggestions.getResults()) {
			String doctype = (String) doc.get("doctype");
			if ("place".equalsIgnoreCase(doctype)) {
				jsonArray.put(buildLocation(doc));
			}
		}

		resp.setContentType("application/json");
		resp.getWriter().println(jsonArray.toString());
	}

	private JSONObject buildCourse(SolrDocument doc) {
		JSONObject obj = new JSONObject();
		obj.put("label", doc.get("name"));
		obj.put("category", "Courses");
		obj.put("href", "/coursedetails?id=" + doc.get("id"));
		return obj;
	}

	private JSONObject buildLocation(SolrDocument doc) {
		JSONObject obj = new JSONObject();

		String[] points = ((String) doc.get("loc")).split(",");
		String geohash = GeoHashUtils.encode(Double.parseDouble(points[0]), Double.parseDouble(points[1]));

		String suburb = (String) doc.get("suburb");
		String postcode = (String) doc.get("postcode");

		obj.put("label", suburb + " " + postcode);
		obj.put("category", "Show courses near...");
		obj.put("href", "/courses?near=" + geohash);

		return obj;
	}
}
