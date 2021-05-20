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

package ish.oncourse.server.api.v1.function

import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SiteAttachmentRelation
import ish.oncourse.server.cayenne.SiteTagRelation
import ish.oncourse.server.cayenne.SiteUnavailableRuleRelation
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.UnavailableRule
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import static org.apache.commons.lang3.StringUtils.trimToNull
import static ish.oncourse.server.api.function.GetKioskUrl.getKioskUrl
import static ish.oncourse.server.api.v1.function.CountryFunctions.toRestCountry
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags

import java.time.ZoneOffset

class SiteFunctions {

    static SiteDTO toRestSite(Site dbSite, PreferenceController preferenceController, DocumentService documentService) {
        new SiteDTO().with { site ->
            site.id = dbSite.id
            site.isAdministrationCentre = dbSite.isAdministrationCentre
            site.isVirtual = dbSite.isVirtual
            site.isShownOnWeb = dbSite.isShownOnWeb
            site.kioskUrl = getKioskUrl(preferenceController.collegeURL, 'site', dbSite.id)
            site.name = dbSite.name
            site.street = dbSite.street
            site.suburb = dbSite.suburb
            site.state = dbSite.state
            site.postcode = dbSite.postcode
            if (dbSite.country) {
                site.country = toRestCountry(dbSite.country)
            }
            site.timezone = dbSite.localTimezone
            site.longitude = dbSite.longitude
            site.latitude = dbSite.latitude
            site.drivingDirections = dbSite.drivingDirections
            site.publicTransportDirections = dbSite.publicTransportDirections
            site.specialInstructions = dbSite.specialInstructions
            site.tags = dbSite.tags.collect { toRestTagMinimized(it) }
            site.rooms = dbSite.rooms.collect { RoomFunctions.toRestRoomMinimized(it) }
            site.documents = dbSite.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            site.rules = dbSite.unavailableRuleRelations*.rule.collect{ toRestHoliday(it as UnavailableRule) }
            site.createdOn = dbSite.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            site.modifiedOn = dbSite.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            site
        }
    }

    static Site toDbSite(SiteDTO site, Site dbSite, ObjectContext context, SystemUser currentUser) {
        dbSite.isAdministrationCentre = site.isAdministrationCentre
        dbSite.isVirtual = site.isVirtual
        dbSite.isShownOnWeb = site.isShownOnWeb
        dbSite.name = trimToNull(site.name)
        dbSite.street = trimToNull(site.street)
        dbSite.suburb = trimToNull(site.suburb)
        dbSite.state = trimToNull(site.state)
        dbSite.postcode = trimToNull(site.postcode)
        dbSite.localTimezone = trimToNull(site.timezone)

        if (site.country) {
            dbSite.country = CayenneFunctions.getRecordById(context, Country, site.country.id)
        }
        dbSite.longitude = site.longitude
        dbSite.latitude = site.latitude
        dbSite.drivingDirections = trimToNull(site.drivingDirections)
        dbSite.publicTransportDirections = trimToNull(site.publicTransportDirections)
        dbSite.specialInstructions = trimToNull(site.specialInstructions)

        updateRooms(dbSite, site.rooms)
        updateTags(dbSite, dbSite.taggingRelations, site.tags*.id, SiteTagRelation, context)
        updateAvailabilityRules(dbSite, dbSite.unavailableRuleRelations*.rule, site.rules, SiteUnavailableRuleRelation)
        updateDocuments(dbSite, dbSite.attachmentRelations, site.documents, SiteAttachmentRelation, context)

        dbSite
    }

    static ValidationErrorDTO validateForDelete(Site entity) {
        if (!entity.users.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Cannot delete site assigned to users.")
        }

        if (!entity.paymentsIn.empty || !entity.paymentsOut.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Cannot delete site assigned to payments")
        }

        if (!entity.waitingLists.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Cannot delete site assigned to waiting lists.")
        }
        if (!entity.bankings.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Cannot delete site assigned to banking.")
        }

        if (entity.rooms.collect { RoomFunctions.validateForDelete(it) }.find()) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Cannot delete related rooms in site.")
        }

        null
    }

    static ValidationErrorDTO validateForSave(SiteDTO site, ObjectContext context, boolean isSiteExists, Long dbSiteId = null) {
        ValidationErrorDTO error = null

        if ((site.longitude != null) && ((site.longitude > 180) || (site.longitude < -180))) {
            return new ValidationErrorDTO(site?.id?.toString(), 'longitude', 'Invalid longitude value. Longitude must be between 180 and -180')
        }

        if ((site.latitude != null) && ((site.latitude > 90) || (site.latitude < -90))) {
            return new ValidationErrorDTO(site?.id?.toString(), 'latitude', 'Invalid latitude value. Latitude must be between 90 and -90')
        }

        if (site.isAdministrationCentre == null) {
            return new ValidationErrorDTO(site?.id?.toString(), 'isAdministrationCentre', 'Administration centre is required.')
        }

        if (site.isVirtual == null) {
            return new ValidationErrorDTO(site?.id?.toString(), 'isVirtual', 'Virtual site is required.')
        }

        if (site.isShownOnWeb == null) {
            return new ValidationErrorDTO(site?.id?.toString(), 'isShownOnWeb', 'Visibility is required.')
        }

        if (site.timezone == null) {
            return new ValidationErrorDTO(site?.id?.toString(), 'timezone', 'Timezone is required.')
        }

        if (StringUtils.isBlank(site.name)) {
            return new ValidationErrorDTO(site?.id?.toString(), 'name', 'Name is required.')
        } else if (site.name.size() > 150) {
            return new ValidationErrorDTO(site?.id?.toString(), 'name', 'Name can\'t be more than 150 chars.')
        }

        Long siteId = ObjectSelect.query(Site)
                .where(Site.NAME.eq(trimToNull(site.name)))
                .selectFirst(context)?.id

        if (siteId && siteId != dbSiteId) {
            return new ValidationErrorDTO(site?.id?.toString(), 'name', 'The name of the site must be unique.')
        }

        List<RoomDTO> duplicates = site.rooms.groupBy {it.name}.values().find { it.size() > 1}
        if (duplicates && !duplicates.empty) {
            int index = site.rooms.indexOf(duplicates[0])
            return new ValidationErrorDTO(site?.id?.toString(), "rooms[$index]name", "Room name ${duplicates[0].name} should be unique")
        }


        site.rooms.eachWithIndex { RoomDTO room, int i ->
            error = error ?: RoomFunctions.validateForSave(room, context, room.id, isSiteExists)?.with(true, { propertyName = "rooms[$i]$propertyName"}) as ValidationErrorDTO
        }


        return error ?: TagFunctions.validateRelationsForSave(Site, context, site.tags*.id,  TaggableClasses.SITE)
    }

    private static void updateRooms(Site site, List<RoomDTO> rooms) {
        ObjectContext context = site.context

        List<Long> savedRoomIds = rooms*.id.findAll()
        context.deleteObjects(site.rooms.findAll { !savedRoomIds.contains(it.id) })

        rooms.each { RoomDTO room ->
            if (room.id) {
                Room dbRoom = site.rooms.find { it.id == room.id }
                if (dbRoom.name != trimToNull(room.name)) {
                    dbRoom.name = trimToNull(room.name)
                }
                if (dbRoom.seatedCapacity != room.seatedCapacity) {
                    dbRoom.seatedCapacity = room.seatedCapacity
                }
            } else {
                context.newObject(Room).with { it ->
                    it.site = site
                    it.name = trimToNull(room.name)
                    it.seatedCapacity = room.seatedCapacity
                }
            }
        }

        if (site.isVirtual && site.rooms.empty) {
            context.newObject(Room).with { it ->
                it.site = site
                it.name = site.name
                it.seatedCapacity = 0
            }
        }
    }

    static SiteDTO toRestSiteMinimized(Site dbSite) {
        new SiteDTO().with { site ->
            site.id = dbSite.id
            site.name = dbSite.name
            site.suburb = dbSite.suburb
            site.postcode = dbSite.postcode
            site
        }
    }
}
