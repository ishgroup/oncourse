package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class TagRelationDeduperTest {

    @Test
    public void dedupeTaggableTagTest() {
        Tag tag1 = tagMock(1l, "tag1");
        Tag tag2 = tagMock(2l, "tag2");

        Taggable taggable1 = taggableMock(1l, 1l, 1l, Contact.class);
        Taggable taggable2 = taggableMock(2l, 2l, 2l, Contact.class);

        List<TaggableTag> taggableTags1 = new ArrayList();

        taggableTags1.add(taggableTagMock(1l, taggable1, tag1));
        taggableTags1.add(taggableTagMock(2l, taggable1, tag1));

        taggableTags1.add(taggableTagMock(3l, taggable1, tag2));
        taggableTags1.add(taggableTagMock(4l, taggable1, tag2));


        when(taggable1.getTaggableTags()).thenReturn(taggableTags1);

        List<TaggableTag> unused = TagRelationDeduper.valueOf(null, null).dedupeTaggableTags(taggable1);

        assertFalse(unused.isEmpty());
        assertEquals(2, unused.size());
        assertEquals(2, (long) unused.get(0).getId());
        assertEquals(4, (long) unused.get(1).getId());

        List<TaggableTag> unused2 = TagRelationDeduper.valueOf(null, null).dedupeTaggableTags(taggable2);
    }

    @Test
    public void relinkTaggableRelationsTest() {
        Tag tag1 = tagMock(1l, "tag1");

        Taggable taggable1 = taggableMock(1l, 1l, 1l, Contact.class);
        List<TaggableTag> taggableTags1 = new ArrayList<>();
        TaggableTag taggableTag1 = taggableTagMock(1l, taggable1, tag1);
        taggableTags1.add(taggableTag1);
        when(taggable1.getTaggableTags()).thenReturn(taggableTags1);

        Taggable taggable2 = taggableMock(2l, 2l, 2l, Contact.class);
        List<TaggableTag> taggableTags2 = new ArrayList<>();
        TaggableTag taggableTag2 = taggableTagMock(2l, taggable2, tag1);
        taggableTags2.add(taggableTag2);
        when(taggable2.getTaggableTags()).thenReturn(taggableTags2);

        List<Taggable> duplicationTaggables = new ArrayList<>();
        duplicationTaggables.add(taggable1);
        duplicationTaggables.add(taggable2);

        Taggable headTaggable = taggableMock(1l, 1l, 1l, Contact.class);
        List<TaggableTag> taggableTags3 = new ArrayList<>();
        taggableTags3.add(taggableTagMock(3l, headTaggable, tag1));
        when(headTaggable.getTaggableTags()).thenReturn(taggableTags3);

        TagRelationDeduper.valueOf(null, null).relinkTaggableRelations(duplicationTaggables, headTaggable);

        verify(headTaggable).addToTaggableTags(taggableTag1);
        verify(headTaggable).addToTaggableTags(taggableTag2);
    }

    @Test
    public void defineHeadTaggableTest() {
        List<Taggable> list = new ArrayList<>();
        list.add(taggableMock(1l, 1l, 1l, Contact.class ));
        list.add(taggableMock(2l, 2l, 2l, Contact.class ));
        list.add(taggableMock(3l, 3l, 3l, Contact.class ));
        Taggable taggable = TagRelationDeduper.valueOf(null, null).defineHeadTaggable(list);

        assertEquals(1l, (long) taggable.getId());
    }

    @Test
    public void defineDuplicatesTest() {
        List<Taggable> list = new ArrayList<>();
        list.add(taggableMock(1l, 1l, 1l, Contact.class ));
        list.add(taggableMock(2l, 2l, 2l, Contact.class ));
        list.add(taggableMock(3l, 3l, 3l, Contact.class ));
        List<Taggable> taggable = TagRelationDeduper.valueOf(null, null).defineDuplicates(list);

        assertEquals(2, taggable.size());
        assertEquals(2, (long) taggable.get(0).getId());
        assertEquals(3, (long) taggable.get(1).getId());
    }

    private Taggable taggableMock(long id, long entityWillowId, long entityAngelId, Class<? extends Queueable> taggableType) {
        Taggable taggable = mock(Taggable.class);
        when(taggable.getEntityWillowId()).thenReturn(entityWillowId);
        when(taggable.getEntityWillowId()).thenReturn(entityAngelId);
        String type = taggableType.getSimpleName();
        when(taggable.getEntityIdentifier()).thenReturn(type);
        when(taggable.getId()).thenReturn(id);
        when(taggable.getAngelId()).thenReturn(id);
        return taggable;
    }

    private TaggableTag taggableTagMock(long id, Taggable taggable, Tag tag) {
        TaggableTag taggableTag = mock(TaggableTag.class);
        when(taggableTag.getTag()).thenReturn(tag);
        when(taggableTag.getTaggable()).thenReturn(taggable);
        when(taggableTag.getId()).thenReturn(id);
        when(taggableTag.getAngelId()).thenReturn(id);
        return taggableTag;
    }

    private Tag tagMock(long id, String name) {
        Tag tag = mock(Tag.class);
        when(tag.getId()).thenReturn(id);
        when(tag.getAngelId()).thenReturn(id);
        when(tag.getName()).thenReturn(name);
        return tag;
    }
}
