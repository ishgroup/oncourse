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
import ish.messaging.ICourse;
import ish.messaging.ITag;
import ish.messaging.ITaggableObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TagService {

	/**
	 * @return List of Tags in subject category, alphabetically sorted.
	 */
	public List<? extends ITag> getSubjectTagsForCourse(ICourse course) {
		List<ITag> result = new ArrayList<>();

		for (ITag n : course.getTags()) {
			if (NodeSpecialType.SUBJECTS.equals(n.getRoot().getSpecialType())) {
				result.add(n);
			}
		}
		Ordering o = new Ordering(ITag.NAME_KEY, SortOrder.ASCENDING);
		o.orderList(result);
		return result;
	}

    /**
     * Check to see whether this object has this tag.
     *
     * @param taggable the object that can have related tags
     * @param path the tag name or the full path to the tag
     * @param isSearchWithChildren define if method will be searching for tag in parents of taggable's tags
     * @return
     */
	public boolean hasTag(ITaggableObject taggable, String path, boolean isSearchWithChildren) {

		path = trimPath(path);

		if (path == null) {
			return false;
		}

		if (path.contains("/")) {
			// we can say that if a user specifies a string which contains the "/" char,
			// then it must be a full path (eg. starts with 'Subjects')
			for (ITag tag : taggable.getTags()) {
			    String tagPathName = getPathName(tag);

			    if (isSearchWithChildren) {
                    if (tagPathName.startsWith(path)) {
                        return true;
                    }
                }
			    else if (tagPathName.equals(path)) {
                    return true;
                }
			}
		} else {
			for (ITag tag : taggable.getTags()) {
                String tagPathName = getPathName(tag);

			    if (isSearchWithChildren){
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

	public ITag getTagBy(ITag rootTag, String[] fullPath) {

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

	private ITag getChildWithName(ITag parent, String childName) {

		for (ITag child : parent.getChildTags()) {
			if (childName.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}

	private String getPathName(ITag tag) {
		String result = tag.getName();
		if (tag.getParentTag() != null) {
			result = getPathName(tag.getParentTag()) + "/" + result;
		}
		return result;
	}
}
