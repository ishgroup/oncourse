/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.function;

import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.function.GetTagGroupsInterface;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRequirement;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetTagGroups implements GetTagGroupsInterface {

    private List<TaggableClasses> taggableClasses;
    private ObjectContext context;

    private GetTagGroups() {
    }

    public static GetTagGroups valueOf(ObjectContext context, TaggableClasses... taggableClass) {
        return valueOf(context, Arrays.asList(taggableClass));
    }

    public static GetTagGroups valueOf(ObjectContext context, List<TaggableClasses> taggableClasses) {
        var getTagGroups = new GetTagGroups();
        getTagGroups.taggableClasses = taggableClasses;
        getTagGroups.context = context;
        return getTagGroups;
    }

    /**
     * list of parent tags. It doesn't contain parent tags without children (Parent tag can't be applied).
     */
    public List<Tag> get() {
        return get(taggableClasses);
    }

    public List<Tag> get(TaggableClasses taggableClass) {
        return get(Collections.singletonList(taggableClass));
    }

    private List<Tag> get(List<TaggableClasses> taggableClasses) {
        return ObjectSelect.query(Tag.class)
                .where(Tag.IS_VOCABULARY.isTrue())
                .and(Tag.TAG_REQUIREMENTS.dot(TagRequirement.ENTITY_IDENTIFIER).in(taggableClasses))
                .prefetch(Tag.CHILD_TAGS.joint())
                .prefetch(Tag.CHILD_TAGS.dot(Tag.CHILD_TAGS).joint())
                .prefetch(Tag.CHILD_TAGS.dot(Tag.CHILD_TAGS).dot(Tag.CHILD_TAGS).joint())
                .select(context)
                .stream()
                .filter(tag -> tag.getChildTags().size() > 0)
                .collect(Collectors.toList());
    }
}
