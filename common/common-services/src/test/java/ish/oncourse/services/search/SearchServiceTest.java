package ish.oncourse.services.search;

import ish.oncourse.solr.query.SearchParams;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {

	@Mock
	private ISearchService searchService;

	@Test
	public void testNulls() throws UnsupportedEncodingException, ParseException {
		SearchParams searchParams = new SearchParams();
		int start = 0;
		Integer rows = null;
		searchService.searchCourses(searchParams, start, rows);
	}

	//@Test
	public void testCloudSolrClient() throws IOException, SolrServerException {
		CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost("127.0.0.1:2181,127.0.0.1:2182").build();
		cloudSolrClient.connect();
		QueryResponse response = cloudSolrClient.query(SearchService.SolrCore.courses.name(), new SolrQuery("*:*"));
		System.out.println(response);
	}
}
