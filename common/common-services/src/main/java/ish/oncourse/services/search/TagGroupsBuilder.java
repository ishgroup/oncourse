package ish.oncourse.services.search;

import ish.oncourse.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TagGroupsBuilder {
    private Tag subject;
    private List<Tag> tags;

    private List<Tag> subjectTagGroup;
    private HashMap<Tag, List<Tag>> mapping = new HashMap<>();

    public void build() {
        if (subject != null) {
            subjectTagGroup = updateTagGroup(subject);
        }

        for (Tag tag : tags) {
            updateTagGroup(tag);
        }
    }

    private List<Tag> updateTagGroup(Tag tag) {
        Tag key;
        if (tag.getParent() != null && tag.getParent().getIsTagGroup()) {
           key = tag.getParent();
        } else {
            key = tag;
        }
        List<Tag> tagGroup = mapping.get(key);
        if (tagGroup == null) {
            tagGroup = new ArrayList<>();
            mapping.put(key, tagGroup);
        }
        tagGroup.add(tag);
        return tagGroup;
    }

    public List<List<Tag>> getTagGroups() {
        return new ArrayList<>(mapping.values());
    }

    public List<Tag> getSubjectTagGroup() {
        return subjectTagGroup;
    }

    public static TagGroupsBuilder valueOf(Tag subject, List<Tag> tags) {
        TagGroupsBuilder result = new TagGroupsBuilder();
        result.subject = subject;
        result.tags = new ArrayList<>(tags);
        return result;
    }
}
