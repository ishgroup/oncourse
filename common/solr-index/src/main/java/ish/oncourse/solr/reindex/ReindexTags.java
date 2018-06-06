package ish.oncourse.solr.reindex;

import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.tag.Functions;
import ish.oncourse.solr.model.STag;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;

public class ReindexTags extends ReindexCollection<STag> {
	public ReindexTags(ObjectContext objectContext, SolrClient solrClient) {
		super(solrClient,
				SolrCollection.tags,
				Functions.getGetSolrTags().call(objectContext));
	}
}