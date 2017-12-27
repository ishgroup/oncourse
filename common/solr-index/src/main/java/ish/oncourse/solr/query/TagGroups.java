package ish.oncourse.solr.query;

import ish.oncourse.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TagGroups {
    private SearchParams params;
    private Tag filterTag;

    private List<Tag> tags;

    private HashMap<Tag, List<Tag>> mapping = new HashMap<>();

    public List<List<Tag>> getTagGroups() {
        return new ArrayList<>(mapping.values());
    }

    private void init() {
        initTags();
        for (Tag tag : tags) {
            updateTagGroup(tag);
        }

       if (filterTag != null &&
               mapping.containsKey(getKeyTag(filterTag)) &&
               !tags.contains(filterTag)) {
           updateTagGroup(filterTag);
       }
    }

    private List<Tag> updateTagGroup(Tag tag) {
        Tag key = getKeyTag(tag);
        List<Tag> tagGroup = mapping.get(key);
        if (tagGroup == null) {
            tagGroup = new ArrayList<>();
            mapping.put(key, tagGroup);
        }
        tagGroup.add(tag);
        return tagGroup;
    }

    private Tag getKeyTag(Tag tag) {
        Tag key;
        if (tag.getParent() != null && tag.getParent().getIsTagGroup()) {
            key = getKeyTag(tag.getParent());
        } else {
            key = tag;
        }
        return key;
    }


    public static TagGroups valueOf(SearchParams params, Tag filterTag) {
        TagGroups result = new TagGroups();
        result.params = params;
        result.filterTag = filterTag;
        result.init();
        return result;
    }

    public static TagGroups valueOf(SearchParams params) {
        TagGroups result = new TagGroups();
        result.params = params;
        result.init();
        return result;
    }

    private void initTags() {
        tags = new ArrayList<>(params.getTags());
        if (params.getSubject() != null) {
            tags.add(params.getSubject());
        }
    }
}

