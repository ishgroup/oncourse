package ish.oncourse.solr.model;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.solr.InitSolr;
import ish.oncourse.solr.functions.tag.Functions;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SolrTagIndexTest extends SolrTestCaseJ4 {
	static  {
		InitSolr.INIT_STATIC_BLOCK();
	}

	private static InitSolr initSolr;

	@BeforeClass
	public static void beforeClass() throws Exception {
		initSolr = InitSolr.tagsCore();
		initSolr.init();
	}

	@Test
	public void test() throws IOException, SolrServerException {
		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		College college = mock(College.class);
		when(college.getId()).thenReturn(51L);
		
		Tag tag = mock(Tag.class);
		when(tag.getId()).thenReturn(11L);
		when(tag.getCollege()).thenReturn(college);
		when(tag.getName()).thenReturn("Subject");

		STag solrTag = Functions.getGetSTag().apply(tag);

		solrClient.addBean(solrTag);
		solrClient.commit();

		STag actual = solrClient.query("tags", new SolrQuery("*:*")).getBeans(STag.class).get(0);
		assertEquals(solrTag, actual);

	}
}
