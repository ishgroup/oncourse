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
package ish.oncourse.entity.services;

import ish.common.types.NodeSpecialType;
import ish.oncourse.cayenne.Taggable;
import ish.oncourse.server.api.v1.function.TagRequirementFunctions;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class TagService {

    private static final List<Class<? extends CayenneDataObject>> SUBJECT_ENTITIES = List.of(Course.class,
            ArticleProduct.class, VoucherProduct.class, MembershipProduct.class);

    /**
     * @return List of Tags in subject category, alphabetically sorted.
     */
    public List<? extends Tag> getSubjectTagsForCourse(Course course) {
        List<Tag> result = new ArrayList<>();

        for (Tag n : course.getTags()) {
            if (NodeSpecialType.SUBJECTS.equals(n.getRoot().getSpecialType())) {
                result.add(n);
            }
        }
        Ordering o = new Ordering(Tag.NAME_KEY, SortOrder.ASCENDING);
        o.orderList(result);
        return result;
    }

    /**
     * Check to see whether this object has this tag.
     *
     * @param taggable             the object that can have related tags
     * @param path                 the tag name or the full path to the tag
     * @param isSearchWithChildren define if method will be searching for tag in parents of taggable's tags
     * @return
     */
    public boolean hasTag(Taggable taggable, String path, boolean isSearchWithChildren) {

        path = trimPath(path);

        if (path == null) {
            return false;
        }

        if (path.contains("/")) {
            // we can say that if a user specifies a string which contains the "/" char,
            // then it must be a full path (eg. starts with 'Subjects')
            for (Tag tag : taggable.getTags()) {
                String tagPathName = getPathName(tag);

                if (isSearchWithChildren) {
                    if (tagPathName.startsWith(path)) {
                        return true;
                    }
                } else if (tagPathName.equals(path)) {
                    return true;
                }
            }
        } else {
            for (Tag tag : taggable.getTags()) {
                String tagPathName = getPathName(tag);

                if (isSearchWithChildren) {
                    if (tagPathName.contains(path)) {
                        return true;
                    }
                }
                if (tagPathName.endsWith(path)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Tag getTagBy(Tag rootTag, String[] fullPath) {

        if (rootTag != null) {
            for (int i = 1; i < fullPath.length; i++) {
                rootTag = getChildWithName(rootTag, fullPath[i]);
                if (rootTag == null) {
                    return null;
                }
            }
        } else {
            return null;
        }

        return rootTag;

    }

    public String trimPath(String path) {
        path = StringUtils.strip(path, "/ ");
        path = StringUtils.trimToNull(path);
        return path;
    }

    private Tag getChildWithName(Tag parent, String childName) {

        for (Tag child : parent.getChildTags()) {
            if (childName.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    private String getPathName(Tag tag) {
        String result = tag.getName();
        if (tag.getParentTag() != null) {
            result = getPathName(tag.getParentTag()) + "/" + result;
        }
        return result;
    }

    public void updateSubjectsAsEntities(ObjectContext context) {
        var subjectTag = ObjectSelect.query(Tag.class)
                .where(Tag.SPECIAL_TYPE.eq(NodeSpecialType.SUBJECTS))
                .and(Tag.PARENT_TAG.isNull())
                .selectOne(context);

        Set<Class<? extends Taggable>> existedRequirements = new HashSet<>();

        for (var requirement : subjectTag.getTagRequirements()) {
            if (!SUBJECT_ENTITIES.contains(requirement.getEntityClass())) {
                context.deleteObject(requirement);
                continue;
            }

            existedRequirements.add(requirement.getEntityClass());

            if (requirement.getDisplayRule() != null)
                requirement.setDisplayRule(null);

            if (requirement.getIsRequired())
                requirement.setIsRequired(false);

            if (!requirement.getManyTermsAllowed())
                requirement.setManyTermsAllowed(true);
        }

        SUBJECT_ENTITIES.forEach(entityClass -> {
			if(!existedRequirements.contains(entityClass)){
				TagRequirement productRequirement = context.newObject(TagRequirement.class);
				productRequirement.setEntityIdentifier(TagRequirementFunctions.getTaggableClassForName(entityClass.getSimpleName()));
                productRequirement.setTag(subjectTag);
                productRequirement.setManyTermsAllowed(true);
                productRequirement.setIsRequired(false);
			}
        });

        context.commitChanges();
    }
}
