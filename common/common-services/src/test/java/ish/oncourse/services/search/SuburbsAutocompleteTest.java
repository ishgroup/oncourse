package ish.oncourse.services.search;

import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Test;

import static ish.oncourse.solr.query.SolrQueryBuilder.FIELD_postcode;
import static ish.oncourse.solr.query.SolrQueryBuilder.FIELD_suburb;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SuburbsAutocompleteTest {

    @Test
    public void getSuburb() {
        SolrDocument doc = mock(SolrDocument.class);
        when(doc.getFieldValue(FIELD_postcode)).thenReturn("7722");
        when(doc.getFieldValue(FIELD_suburb)).thenReturn("QUEENS");

        JSONObject obj = SuburbsAutocomplete.valueOf(null, null, null).buildElement(doc);

        assertEquals("QUEENS 7722", obj.getString("id"));
        assertEquals("QUEENS 7722", obj.getString("label"));
        assertEquals("QUEENS", obj.getString("value"));
        assertEquals("QUEENS", obj.getString("suburb"));
        assertEquals("7722", obj.getString("postcode"));
    }
}
