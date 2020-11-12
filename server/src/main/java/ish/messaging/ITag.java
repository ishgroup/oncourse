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
package ish.messaging;

import ish.common.types.NodeSpecialType;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.cayenne.TagInterface;
import ish.oncourse.cayenne.Taggable;

import java.util.List;

public interface ITag extends TagInterface, PersistentObjectI {

	String NAME_KEY = "name";
	String SHORT_NAME_KEY = "shortName";
	String PARENT_TAG_KEY = "parentTag";
	String TAG_REQUIREMENTS_KEY = "tagRequirements";

	@Override
	ITag getParentTag();

	ITag getRoot();

	String getName();

	String getShortName();

	String getPathName();

	NodeSpecialType getSpecialType();

	List<? extends ITag> getChildTags();

	boolean isRoot();

	boolean isRequiredFor(Class<? extends Taggable> entity);

	boolean isMultipleFor(Class<? extends Taggable> entity);

	Boolean getIsVocabulary();

	List<? extends ITagRequirement> getTagRequirements();

	boolean isTagAncestor(ITag anotherNode);

	List<? extends ITag> getSiblings();
}
