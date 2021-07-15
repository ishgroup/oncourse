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
import ish.oncourse.server.cayenne.glue._Room
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull


/**
 * A room in a location in which training is delivered. It may be a virtual room (linked to a virtual site)
 * or a physical location.
 *
 * Every site must have at least one room.
 *
 */
@API
@QueueableEntity
class Room extends _Room implements Queueable, NotableTrait, AttachableTrait {

	public static final String DEFAULT_ROOM_NAME = "Default room"
	public static final Integer DEFAULT_ROOM_CAPACITY = 10


	private static final Logger logger = LogManager.getLogger()

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#onEntityCreation()
	 */
	@Override
	void onEntityCreation() {
		super.onEntityCreation()

		if (getSeatedCapacity() == null) {
			setSeatedCapacity(DEFAULT_ROOM_CAPACITY)
		}
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((RoomAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((RoomAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return RoomAttachmentRelation.class
	}
	/**
	 * method used to fetch/create default site, used in data upgrades.
	 *
	 * @return default room
	 */
	static Room getDefaultRoom(@Nonnull DataContext context) {
		Room defaultRoom = (ObjectSelect.query(Room.class)
				.where(SITE.dot(Site.NAME).eq(Site.DEFAULT_SITE_NAME)) & NAME.eq(DEFAULT_ROOM_NAME))
				.selectFirst(context)

		return defaultRoom == null ? createDefaultRoom(context) : defaultRoom
	}

	static Room createDefaultRoom(@Nonnull DataContext context) {
		Room defaultRoom = context.newObject(Room.class)
		defaultRoom.setName(DEFAULT_ROOM_NAME)
		defaultRoom.setSeatedCapacity(DEFAULT_ROOM_CAPACITY)
		defaultRoom.setSite(Site.getDefaultSite(context))
		if (context.hasChanges()) {
			context.commitChanges()
		}
		return defaultRoom
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
	 * Directions should be relative to the site. That is, a person arrives at the site by public transport
	 * or driving and then these directions will get them from the site to the room itself.
	 *
	 * @return directions (rich text)
	 */
	@API
	@Override
	String getDirections() {
		return super.getDirections()
	}

	/**
	 * @return facilities available in this room (not usually made public)
	 */
	@API
	@Override
	String getFacilities() {
		return super.getFacilities()
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
	 * @return The public name for this room. For example "The great hall" or "Room 143"
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return The number of people this room holds.
	 */
	@Nonnull
	@API
	@Override
	Integer getSeatedCapacity() {
		return super.getSeatedCapacity()
	}

	/**
	 * Use of this method is discouraged since it can return a very large number of objects.
	 * Instead consider a query to find only the records you require.
	 * @return all courseclasses held or to be held in this room
	 */
	@Nonnull
	@API
	@Override
	List<CourseClass> getCourseClasses() {
		return super.getCourseClasses()
	}

	/**
	 * Use of this method is discouraged since it can return a very large number of objects.
	 * Instead consider a query to find only the records you require.
	 * @return all sessions held or to be held in this room
	 */
	@Nonnull
	@API
	@Override
	List<Session> getSessions() {
		return super.getSessions()
	}

	/**
	 * @return The site in which this room is located
	 */
	@Nonnull
	@API
	@Override
	Site getSite() {
		return super.getSite()
	}

	/**
	 * @return The list of tags assigned to room
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (RoomTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

}
