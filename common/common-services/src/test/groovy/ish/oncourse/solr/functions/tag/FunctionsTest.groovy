package ish.oncourse.solr.functions.tag

import ish.oncourse.model.College
import ish.oncourse.model.Tag
import ish.oncourse.solr.model.SolrTag
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class FunctionsTest {

    @Test
    void test_getSolrTag() {
        College college = mock(College)
        when(college.id).thenReturn(51L)
        
        Tag tag = mock(Tag)
        when(tag.id).thenReturn(11L)
        when(tag.college).thenReturn(college)
        when(tag.name).thenReturn('subject')

        SolrTag solrTag = Functions.getSolrTag(tag)
        assertEquals("${tag.id}".toString(), solrTag.id)
        assertEquals(tag.college.id, solrTag.collegeId)
        assertEquals(tag.name, solrTag.name)

    }
}
