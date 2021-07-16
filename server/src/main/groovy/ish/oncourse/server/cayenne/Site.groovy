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
import ish.oncourse.server.cayenne.glue._Site
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A Site is a location where training occurs or payments are received (an administrative site).
 * A virtual site can be used for online training.
 *
 * A site has latitude and longitude and directions, and contains one or more rooms in which training
 * is delivered. Even if a site contains only a single room, it is important to create both the site and room.
 *
 */
@API
@QueueableEntity
class Site extends _Site implements Queueable, NotableTrait, AttachableTrait {

	public static final String DEFAULT_SITE_NAME = "Default site"


	private static Logger logger = LogManager.getLogger()

	@Override
	void onEntityCreation() {
		super.onEntityCreation()

		if (getIsAdministrationCentre() == null) {
			setIsAdministrationCentre(false)
		}
		if (getIsShownOnWeb() == null) {
			setIsShownOnWeb(false)
		}
		if (getLocalTimezone() == null) {
			setLocalTimezone(TimeZone.getDefault().getID())
		}
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getCountry() == null) {
			Country defaultCountry

			defaultCountry = Country.defaultCountry(getObjectContext())
			if (defaultCountry != null) {
				setCountry(defaultCountry)
			}
		}
	}

	@Override
	void prePersist() {
		super.prePersist()
		if (getLocalTimezone() == null) {
			setLocalTimezone(Calendar.getInstance().getTimeZone().getID())
		}
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((SiteAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((SiteAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return SiteAttachmentRelation.class
	}

	/**
	 * method used to fetch/create default site, used in data upgrades.
	 *
	 * @param context
	 * @return
	 */
	static Site getDefaultSite(@Nonnull DataContext context) {
		// check if we have the site with the name "Default site" and create it if not
		Site defaultSite = ObjectSelect.query(Site.class)
				.where(NAME.eq(DEFAULT_SITE_NAME))
				.selectFirst(context)

		return defaultSite == null ? createDefaultSite(context) : defaultSite
	}

	static Site createDefaultSite(@Nonnull DataContext context) {
		Site defaultSite = context.newObject(Site.class)
		defaultSite.setName(DEFAULT_SITE_NAME)
		defaultSite.setIsShownOnWeb(false)
		defaultSite.setIsAdministrationCentre(true)
		defaultSite.setLocalTimezone(Calendar.getInstance().getTimeZone().getID())
		if (context.hasChanges()) {
			context.commitChanges()
		}
		return defaultSite
	}

	/**
	 * @return the public name for this site.
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
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
	 * @return driving directions for the site (rich text)
	 */
	@API
	@Override
	String getDrivingDirections() {
		return super.getDrivingDirections()
	}


	/**
	 * An administration centre is a site which is used for the collection of money.
	 * When cash or cheques are receipted, they are linked to a particular administration centre
	 * so that banking can be performed separately for each centre.
	 *
	 * @return true if this is an admin centre
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsAdministrationCentre() {
		return super.getIsAdministrationCentre()
	}

	/**
	 * @return whether this site is visible on the website in the /sites list
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsShownOnWeb() {
		return super.getIsShownOnWeb()
	}

	/**
	 * Virtual sites are a special online type for delivery of online training. Typically you
	 * would have just one of these.
	 *
	 * @return true for virtual sites
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsVirtual() {
		return super.getIsVirtual()
	}

	/**
	 * Use for directions on your website and allowing students to find classes "near me"
	 *
	 * @return latitude of this site
	 */
	@API
	@Override
	BigDecimal getLatitude() {
		return super.getLatitude()
	}

	/**
	 * Timezones are vital for interpreting the session times for delivery at a particular site.
	 * If you deliver in multliple timezones, your website will automatically detect the student timezone
	 * and adjust delivery times for online training.
	 *
	 * @return the timezone of the site
	 */
	@Nonnull
	@API
	@Override
	String getLocalTimezone() {
		return super.getLocalTimezone()
	}

	/**
	 * Use for directions on your website and allowing students to find classes "near me"
	 *
	 * @return longitude of this site
	 */
	@API
	@Override
	BigDecimal getLongitude() {
		return super.getLongitude()
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
	 * @return the postcode of the site
	 */
	@API
	@Override
	String getPostcode() {
		return super.getPostcode()
	}

	/**
	 * @return public transport directions for the site (rich text)
	 */
	@API
	@Override
	String getPublicTransportDirections() {
		return super.getPublicTransportDirections()
	}

	/**
	 * @return special instructions are displayed on the website (rich text)
	 */
	@API
	@Override
	String getSpecialInstructions() {
		return super.getSpecialInstructions()
	}

	/**
	 * @return the state of the site (eg. NSW, VIC, QLD)
	 */
	@API
	@Override
	String getState() {
		return super.getState()
	}

	/**
	 * @return the street address of the site
	 */
	@API
	@Override
	String getStreet() {
		return super.getStreet()
	}

	/**
	 * @return the suburb of the site
	 */
	@API
	@Override
	String getSuburb() {
		return super.getSuburb()
	}

	/**
	 * @return the country of this site
	 */
	@Nullable
	@API
	@Override
	Country getCountry() {
		return super.getCountry()
	}

	/**
	 * A site must have at least one room. Even virtual sites have virtual rooms.
	 *
	 * @return all the rooms located in this site
	 */
	@Nonnull
	@API
	@Override
	List<Room> getRooms() {
		return super.getRooms()
	}

	/**
	 * @return all the waiting list records linked to this site
	 */
	@Nonnull
	@API
	@Override
	List<WaitingList> getWaitingLists() {
		return super.getWaitingLists()
	}


	/**
	 * @return The list of tags assigned to site
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (SiteTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

}
