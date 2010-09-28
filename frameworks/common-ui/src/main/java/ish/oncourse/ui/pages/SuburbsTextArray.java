package ish.oncourse.ui.pages;

import ish.oncourse.services.search.ISearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SuburbsTextArray extends TextStringArray {

	@Inject
	private Request request;
	@Inject
	private ISearchService searchService;

	@SetupRender
	void beforeRender() {
		List<String> strings=null;
		String searchParam = request.getParameter("q");
		SolrDocumentList responseResults = searchService.searchSuburbs(
				searchParam).getResults();
		if (!responseResults.isEmpty()) {
			strings = new ArrayList<String>(responseResults.size());
			for (SolrDocument doc : responseResults) {
				strings.add((String) doc.getFieldValue("suburb") + " "
						+ (String) doc.getFieldValue("postcode"));
			}
		}
		if (strings == null) {
			strings = Collections.emptyList();
		}
		setStrings(strings);
	}

}
