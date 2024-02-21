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
package ish.oncourse.server.cayenne.glue;


import com.google.inject.Inject;
import ish.common.types.NodeSpecialType;
import ish.common.types.NodeType;
import ish.common.types.SystemEventType;
import ish.oncourse.API;
import ish.oncourse.aql.AqlService;
import ish.oncourse.cayenne.Taggable;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.entity.services.TagService;
import ish.oncourse.server.api.v1.function.TagFunctions;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.TagRelation;
import ish.oncourse.server.integration.EventService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectQuery;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Entities which can be tagged. Some helper methods here are useful for manipulating tags.
 */
public abstract class TaggableCayenneDataObject extends CayenneDataObject implements Taggable {

	public static final List<NodeSpecialType> HIDDEN_SPECIAL_TYPES = List.of(NodeSpecialType.CLASS_EXTENDED_TYPES, NodeSpecialType.COURSE_EXTENDED_TYPES);

	public static final String BULK_TAG_PROPERTY = "bulkTag";
	public static final String BULK_UNTAG_PROPERTY = "bulkUntag";

	@Inject
	private transient TagService tagService;

	@Inject
	private AqlService aqlService;


	/**
	 * @see Taggable#getId()
	 */
	public Long getId() {
		if (isNewRecord()) {
			return null;
		}
		return Cayenne.longPKForObject(this);
	}

	/**
	 * Get firts 3   related tags colors.
	 *
	 * @return List of tag ids
	 */
	public List<Long> getTagIds() {
		TaggableClasses taggable = TagFunctions.taggableClassesBidiMap.get(this.getClass().getSimpleName());
		if (taggable != null) {
			return ObjectSelect.columnQuery(Tag.class, Tag.ID)
					.where(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_IDENTIFIER)
							.eq(taggable.getDatabaseValue()))
					.and(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_ANGEL_ID).eq(getId()))
					.select(this.getContext());
		} else {
			return Collections.emptyList();
		}

	}

	/**
	 * Get related tag colors
	 *
	 * @return List of colors
	 */
	public List<String> getTagColors() {
		TaggableClasses taggable = TagFunctions.taggableClassesBidiMap.get(this.getClass().getSimpleName());
		if (taggable != null) {
			return ObjectSelect.columnQuery(Tag.class, Tag.COLOUR)
					.where(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_IDENTIFIER)
							.eq(taggable.getDatabaseValue()))
					.and(Tag.NODE_TYPE.eq(NodeType.TAG))
					.and(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_ANGEL_ID).eq(getId()))
					.limit(3)
					.select(this.getContext());
		} else {
			return Collections.emptyList();
		}

	}

	/**
	 * Get related checklists colors
	 *
	 * @return List of colors
	 */
	public String getChecklistsColor() {
		var checklists = getChecklists();
		if(checklists.isEmpty())
			return null;
		var firstChecklist = checklists.get(0);
		var firstParent = firstChecklist.getParentTag();
		if(firstParent == null)
			firstParent = firstChecklist;
		String color = firstParent.getColour();
		var childChecklists = firstParent.getAllChildren().values();
		int allChildsNumber = childChecklists.size();
		long checkedChecklistsNumber = 0;
		for(var checklist:childChecklists){
			if(checklists.contains(checklist))
				checkedChecklistsNumber++;
		}
		return color+"|"+(double)checkedChecklistsNumber/allChildsNumber;
	}

	/**
	 * Get all tags related to this object.
	 *
	 * @return List of related tags
	 */
	@API
	public List<Tag> getTags() {
		List<Tag> result = new ArrayList<>();
		for (TagRelation relation : getTaggingRelations()) {
			if (relation.getTag() != null) {
				if(relation.getTag().getNodeType().equals(NodeType.TAG))
					result.add(relation.getTag());
			}
		}
		return result;
	}

	/**
	 * Get all tags and checked checklists related to this object.
	 *
	 * @return List of related tags
	 */
	@API
	public List<Tag> getAllTags() {
		List<Tag> result = new ArrayList<>();
		for (TagRelation relation : getTaggingRelations()) {
			if (relation.getTag() != null) {
				if(!HIDDEN_SPECIAL_TYPES.contains(relation.getTag().getSpecialType()))
					result.add(relation.getTag());
			}
		}
		return result;
	}

	/**
	 * Get all hidden tags related to this object.
	 *
	 * @return List of related tags
	 */
	@API
	public List<Tag> getHiddenTags() {
		List<Tag> result = new ArrayList<>();
		for (TagRelation relation : getTaggingRelations()) {
			if (relation.getTag() != null) {
				if(HIDDEN_SPECIAL_TYPES.contains(relation.getTag().getSpecialType()))
					result.add(relation.getTag());
			}
		}
		return result;
	}

	/**
	 * Get all checklists related to this object.
	 *
	 * @return List of related checklists
	 */
	@API
	public List<? extends Tag> getChecklists() {
		List<Tag> result = new ArrayList<>();
		for (TagRelation relation : getTaggingRelations()) {
			if (relation.getTag() != null) {
				if(relation.getTag().getNodeType().equals(NodeType.CHECKLIST))
					result.add(relation.getTag());
			}
		}
		result.sort(new Comparator<Tag>() {
			@Override
			public int compare(Tag o1, Tag o2) {
				return o1.getCreatedOn().compareTo(o2.getCreatedOn());
			}
		});
		return result;
	}

	/**
	 * Add a tag to this object.
	 *
	 * If the tag requirements don't allow this tag to be added, then this method does nothing and returns false.
	 *
	 * @param tag the tag you want to add
	 * @return true if the tag is found and added
	 */
	@API
	public boolean addTag(Tag tag) {
		if (tag != null && !getTags().contains(tag)) {
			if(tag.getRoot().getTagRequirement(this.getClass()) == null) {
				return false;
			}

			var taggedEntity = getContext().getEntityResolver().getObjEntity(getTagRelationClass());
			var joinEntity = getContext().getEntityResolver().getObjEntity(taggedEntity.getName());

			var relation = (TagRelation) getContext().newObject(joinEntity.getJavaClass());
			relation.setTag(tag);
			relation.setTaggedRelation(this);
			return true;
		}
		return false;
	}

	public abstract Class<? extends TagRelation> getTagRelationClass();

	/**
	 * Remove a tag from this object. If the tag isn't already attached, this method does nothing and returns false.
	 *
	 * @param tag the tag you want to remove
	 * @return true if the tag was found and removed
	 */
	@API
	public boolean removeTag(Tag tag) {
		if (tag != null ) {
			List<TagRelation> tagRelations = new ArrayList<>(tag.getTagRelations());
			for (var relation : tagRelations) {
				if (relation.getTaggedRelation() == this) {
					getObjectContext().deleteObjects(relation);
					return true;
				}
			}
		}
		return false;
	}

	public abstract List<? extends TagRelation> getTaggingRelations();

	/**
	 * Check to see whether this object has this tag. Either pass the name of a tag (for example "Painting")
	 * or the full path to the tag, for example "Subjects/Arts/Painting".
	 *
	 * @param path the tag name or the full path to the tag
	 * @return true if the tag is found and is attached to this object
	 */
	@API
	public boolean hasTag(String path) {
		return tagService.hasTag(this, path, false);
	}

	/**
	 * Check to see whether this object has this tag or it's child tag. Either pass the name of a tag (for example "Painting")
	 * or the full path to the tag, for example "Subjects/Arts/Painting".
	 *
	 * @param path the tag name or the full path to the tag
	 * @param isSearchWithChildren flag to define whether need to find in children tags or not
	 * @return true if the tag or it's children is found and is attached to this object
	 */
	@API
	public boolean hasTag(String path, boolean isSearchWithChildren) {
		return tagService.hasTag(this, path, isSearchWithChildren);
	}

	/**
	 * Add a tag to this object. You need to pass the full path to the tag, for example
	 * "Subjects/Arts/Painting".
	 *
	 * If the tag requirements don't allow this tag to be added, then this method does nothing and returns false.
	 *
	 * @param path the full path to the tag
	 * @return true if the tag is found and added
	 */
	@API
	public boolean addTag(String path) {
		return addTag(getTagByFullPath(path));
	}

	/**
	 * Remove a tag from this object. You need to pass the full path to the tag, for example
	 * "Subjects/Arts/Painting".  If the tag isn't already attached, this method does nothing and returns false.
	 *
	 * @param path the full path to the tag
	 * @return true if the tag was found and removed
	 */
	@API
	public boolean removeTag(String path) {
		return removeTag(getTagByFullPath(path));
	}

	private Tag getTagByFullPath(String path) {

		path = tagService.trimPath(path);

		if (path == null) {
			return null;
		}

		var tagNames = path.split("/");

		var rootExpression = Tag.NAME.eq(tagNames[0])
				.andExp(Tag.PARENT_TAG.isNull());
		var selectQuery = new SelectQuery<>(Tag.class, rootExpression);
		List<? extends Tag> rootTags = getContext().select(selectQuery);

		if (rootTags.isEmpty()) {
			return null;
		}
		//get first from rootTags because Tag.NAME for root tag is unique
		return (Tag) tagService.getTagBy(rootTags.get(0), tagNames);
	}

	public Set<Class<? extends Taggable>> getClassesForTags() {
		return Set.of(getClass());
	}
}
