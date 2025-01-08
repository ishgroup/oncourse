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

package ish.oncourse.server.cayenne

import javax.inject.Inject
import ish.common.types.NodeType
import ish.common.types.SystemEventType
import ish.common.types.TypesUtil
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.cayenne.glue._TagRelation
import ish.oncourse.server.integration.EventService
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Object representing relation between tag and entity record tagged with it.
 */
@API
@QueueableEntity
class TagRelation extends _TagRelation implements Queueable {
	@Inject
	private EventService eventService;


	@Override
	boolean isAsyncReplicationAllowed() {
		return tag?.nodeType != NodeType.CHECKLIST
	}

	@Override
	protected void postPersist() {
		if(tag.nodeType.equals(NodeType.CHECKLIST)) {
			if (tag.parentTag != null) {
				if (checklistCompleted() && !taggedRelation.tagIds.contains(tag.parentTag.id)) {
					def relation = context.newObject(TagRelation.class)
					relation.tag = tag.parentTag
					relation.taggedRelation = taggedRelation
					relation.entityIdentifier = entityIdentifier
					relation.entityAngelId = taggedRelation.id
					context.commitChanges()
				}
			}
			def eventType = tag.parentTag ? SystemEventType.CHECKLIST_TASK_CHECKED : SystemEventType.CHECKLIST_COMPLETED
			eventService.postEvent(SystemEvent.valueOf(eventType, this))
		} else {
			eventService.postEvent(SystemEvent.valueOf(SystemEventType.TAG_ADDED, this))
		}
	}

	private boolean checklistCompleted(){
		def allTagChilds = tag.parentTag.allChildren.values().collect {it.id}
		def recordTagIds = taggedRelation.tagIds
		return recordTagIds.containsAll(allTagChilds)
	}

	@Override
	protected void preRemove() {
		if(tag.nodeType == NodeType.TAG)
			eventService.postEvent(SystemEvent.valueOf(SystemEventType.TAG_REMOVED, this))
	}

	/**
	 * To be overridden returning a constant from the TaggableClasses enum.
	 * @return
	 */
	@Nullable
	@API
	TaggableClasses getTaggableClassesIdentifier() {
		// Empty implementation, use one from subclass.
		return null
	}

	/**
	 * @return the tagged relation (determined by child entity relation)
	 */
	@Nullable
	Taggable getTaggedRelation() {
		// Empty implementation, use one from subclass.
		return null
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getEntityIdentifier() == null) {
			if (getTaggableClassesIdentifier() != null) {
				setEntityIdentifier(getTaggableClassesIdentifier().getDatabaseValue())
			}
		}
	}

	/**
	 * @param object - the taggable to set
	 */
	void setTaggedRelation(Taggable object) {
		// Empty implementation, use one from subclass.
	}

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#validateForSave(ValidationResult)
	 */
	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		if (getTag() != null && getTag().getParentTag() == null && getTag().nodeType == NodeType.TAG) {
			result.addFailure(ValidationFailure.validationFailure(this, TAG_PROPERTY, "Tag relations cannot be directly related to a tag group."))
		}

	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return angel id of the related record
	 */
	@Nonnull
	@API
	@Override
	Long getEntityAngelId() {
		return super.getEntityAngelId()
	}

	/**
	 * Use getEntity() instead
	 * @return
	 */
	@Nonnull
	@Deprecated
	@Override
	Integer getEntityIdentifier() {
		return super.getEntityIdentifier()
	}

	/**
	 * @return entity type of the related record
	 * TODO: change the model to use the enum directly so we don't need this hack
	 */
	@API
	TaggableClasses getEntity() {
		return TypesUtil.getEnumForDatabaseValue(super.getEntityIdentifier(), TaggableClasses.class)
	}


	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return linked tag
	 */
	@Nonnull
	@API
	@Override
	Tag getTag() {
		return super.getTag()
	}
}
