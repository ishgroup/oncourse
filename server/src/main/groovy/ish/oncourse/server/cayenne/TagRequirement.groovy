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

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.glue._TagRequirement
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.Collections
import java.util.Date
import java.util.LinkedHashMap
import java.util.Map

/**
 * Object representing requirements and limitations put on applicability of a tag to certain entity type,
 * e.g. make tag mandatory or allow entity to have only one of certain type.
 */
@API
@QueueableEntity
class TagRequirement extends _TagRequirement implements Queueable {

	private static final Logger logger = LogManager.getLogger(TagRequirement.class)



	@Nonnull
	public final static Map<Class<? extends Taggable>, TaggableClasses> TAGGABLE_CLASSES

	static {
		Map<Class<? extends Taggable>, TaggableClasses> taggableClasses = new LinkedHashMap<>()
		taggableClasses.put(Course.class, TaggableClasses.COURSE)
		taggableClasses.put(CourseClass.class, TaggableClasses.COURSE_CLASS)
		taggableClasses.put(Student.class, TaggableClasses.STUDENT)
		taggableClasses.put(Tutor.class, TaggableClasses.TUTOR)
		taggableClasses.put(Report.class, TaggableClasses.REPORT)
		taggableClasses.put(Document.class, TaggableClasses.DOCUMENT)
		taggableClasses.put(Contact.class, TaggableClasses.CONTACT)
		taggableClasses.put(Site.class, TaggableClasses.SITE)
		taggableClasses.put(Room.class, TaggableClasses.ROOM)
		taggableClasses.put(Application.class, TaggableClasses.APPLICATION)
		taggableClasses.put(Enrolment.class, TaggableClasses.ENROLMENT)
		taggableClasses.put(Payslip.class, TaggableClasses.PAYSLIP)
		taggableClasses.put(WaitingList.class, TaggableClasses.WAITING_LIST)
		taggableClasses.put(Assessment.class, TaggableClasses.ASSESSMENT)
		TAGGABLE_CLASSES = Collections.unmodifiableMap(taggableClasses)
	}

	@Nonnull
	static Map<Class<? extends Taggable>, TaggableClasses> getTaggableClasses() {
		return TAGGABLE_CLASSES
	}


	/**
	 * @return class of entity this tag requirement applies to
	 */
	@Nullable
	@API
	Class<? extends Taggable> getEntityClass() {
		for (final Class<? extends Taggable> taggable : getTaggableClasses().keySet()) {
			if (getEntityIdentifier() == getTaggableClasses().get(taggable)) {
				return taggable
			}
		}
		return null
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getIsRequired() == null) {
			setIsRequired(false)
		}
		if (getManyTermsAllowed() == null) {
			setManyTermsAllowed(true)
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
	 * @return entity type identifier of entity this tag requirement applies to
	 */
	@Nonnull
	@API
	@Override
	TaggableClasses getEntityIdentifier() {
		return super.getEntityIdentifier()
	}


	/**
	 * @return true if entity records are required to be tagged with at least one child tag of this tag
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsRequired() {
		return super.getIsRequired()
	}

	/**
	 * @return true if entity records are allowed to have more than one child of this tag simultaneously
	 */
	@Nonnull
	@API
	@Override
	Boolean getManyTermsAllowed() {
		return super.getManyTermsAllowed()
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
