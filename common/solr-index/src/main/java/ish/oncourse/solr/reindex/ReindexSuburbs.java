package ish.oncourse.solr.reindex;

import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.suburb.Functions;
import ish.oncourse.solr.model.SSuburb;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;

public class ReindexSuburbs extends ReindexCollection<SSuburb> {
	public ReindexSuburbs(ObjectContext objectContext, SolrClient solrClient) {
		super(solrClient,
				SolrCollection.suburbs,
				Functions.getGetSolrSuburbs().call(objectContext));
	}
}
