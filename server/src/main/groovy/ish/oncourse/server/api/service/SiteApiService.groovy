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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.SiteDao
import ish.oncourse.server.api.v1.function.RoomFunctions
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.ClientErrorException
import java.time.ZoneOffset

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.GetKioskUrl.getKioskUrl
import static ish.oncourse.server.api.v1.function.CountryFunctions.toRestCountry
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static org.apache.commons.lang3.StringUtils.trimToNull
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields

class SiteApiService extends TaggableApiService<SiteDTO, Site, SiteDao> {

    @Inject
    private DocumentService documentService

    @Inject
    private RoomApiService roomApiService

    @Inject
    private PreferenceController preferenceController


    @Override
    Class<Site> getPersistentClass() {
        return Site
    }

    @Override
    SiteDTO toRestModel(Site dbSite) {
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
            site.tags = dbSite.allTags.collect { it.id }
            site.rooms = dbSite.rooms.collect { RoomFunctions.toRestRoomMinimized(it) }
            site.documents = dbSite.activeAttachments.collect { toRestDocument(it.document, documentService) }
            site.rules = dbSite.unavailableRuleRelations*.rule.collect{ toRestHoliday(it as UnavailableRule) }
            site.createdOn = dbSite.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            site.modifiedOn = dbSite.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            site.customFields = dbSite?.customFields?.collectEntries { [(it.customFieldType.key) : it.value] }
            site
        }
    }

    @Override
    Site toCayenneModel(SiteDTO dto, Site dbSite) {
        dbSite.isAdministrationCentre = dto.isAdministrationCentre
        dbSite.isVirtual = dto.isVirtual
        dbSite.isShownOnWeb = dto.isShownOnWeb
        dbSite.name = trimToNull(dto.name)
        dbSite.street = trimToNull(dto.street)
        dbSite.suburb = trimToNull(dto.suburb)
        dbSite.state = trimToNull(dto.state)
        dbSite.postcode = trimToNull(dto.postcode)
        dbSite.localTimezone = trimToNull(dto.timezone)

        if (dto.country) {
            dbSite.country = getRecordById(dbSite.context, Country, dto.country.id)
        }
        dbSite.longitude = dto.longitude
        dbSite.latitude = dto.latitude
        dbSite.drivingDirections = trimToNull(dto.drivingDirections)
        dbSite.publicTransportDirections = trimToNull(dto.publicTransportDirections)
        dbSite.specialInstructions = trimToNull(dto.specialInstructions)

        updateRooms(dbSite, dto.rooms)
        updateTags(dbSite, dbSite.taggingRelations, dto.tags, SiteTagRelation, dbSite.context)
        updateAvailabilityRules(dbSite, dbSite.unavailableRuleRelations*.rule, dto.rules, SiteUnavailableRuleRelation)
        updateDocuments(dbSite, dbSite.attachmentRelations, dto.documents, SiteAttachmentRelation, dbSite.context)
        updateCustomFields(dbSite.context, dbSite, dto.customFields, SiteCustomField)

        dbSite
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

    @Override
    void validateModelBeforeSave(SiteDTO siteDTO, ObjectContext context, Long id) {
        if ((siteDTO.longitude != null) && ((siteDTO.longitude > 180) || (siteDTO.longitude < -180))) {
            validator.throwClientErrorException(siteDTO?.id, 'longitude', 'Invalid longitude value. Longitude must be between 180 and -180')
        }

        if ((siteDTO.latitude != null) && ((siteDTO.latitude > 90) || (siteDTO.latitude < -90))) {
            validator.throwClientErrorException(siteDTO?.id, 'latitude', 'Invalid latitude value. Latitude must be between 90 and -90')
        }

        if (siteDTO.isAdministrationCentre == null) {
            validator.throwClientErrorException(siteDTO?.id, 'isAdministrationCentre', 'Administration centre is required.')
        }

        if (siteDTO.isVirtual == null) {
            validator.throwClientErrorException(siteDTO?.id, 'isVirtual', 'Virtual site is required.')
        }

        if (siteDTO.isShownOnWeb == null) {
            validator.throwClientErrorException(siteDTO?.id, 'isShownOnWeb', 'Visibility is required.')
        }

        if (siteDTO.timezone == null) {
            validator.throwClientErrorException(siteDTO?.id, 'timezone', 'Timezone is required.')
        }

        if (StringUtils.isBlank(siteDTO.name)) {
            validator.throwClientErrorException(siteDTO?.id, 'name', 'Name is required.')
        } else if (siteDTO.name.size() > 150) {
            validator.throwClientErrorException(siteDTO?.id, 'name', 'Name can\'t be more than 150 chars.')
        }

        def dbSite = ObjectSelect.query(Site)
                .where(Site.NAME.eq(trimToNull(siteDTO.name)))
                .selectFirst(context)

        if(dbSite && dbSite.id != siteDTO.id)
            validator.throwClientErrorException(siteDTO?.id, 'name', 'The name of the site must be unique.')

        List<RoomDTO> duplicates = siteDTO.rooms.groupBy {it.name}.values().find { it.size() > 1}
        if (duplicates && !duplicates.empty) {
            int index = siteDTO.rooms.indexOf(duplicates[0])
            validator.throwClientErrorException(siteDTO?.id, "rooms[$index]name", "Room name ${duplicates[0].name} should be unique")
        }

        siteDTO.rooms.eachWithIndex { RoomDTO room, int i ->
            RoomFunctions.validateForSave(room, context, room.id, validator, id != null)

            if(!siteDTO.isVirtual && trimToNull(room.virtualRoomUrl) != null)
                validator.throwClientErrorException(siteDTO?.id, "rooms[$i]name", "Room named ${duplicates[0].name} cannot have virtual room url if site is not virtual")
        }

        TagFunctions.validateTagForSave(Site, context, siteDTO.tags)
                ?.with { validator.throwClientErrorException(it) }

        TagFunctions.validateRelationsForSave(Site, context, siteDTO.tags, TaggableClasses.SITE)
                ?.with { validator.throwClientErrorException(it) }
    }

    @Override
    void validateModelBeforeRemove(Site site) {
        if (!site.users.empty) {
            validator.throwClientErrorException(site.id, 'id', "Cannot delete site assigned to users.")
        }

        if (!site.paymentsIn.empty || !site.paymentsOut.empty) {
            validator.throwClientErrorException(site.id, 'id', "Cannot delete site assigned to payments")
        }

        if (!site.waitingLists.empty) {
            validator.throwClientErrorException(site.id, 'id', "Cannot delete site assigned to waiting lists.")
        }
        if (!site.bankings.empty) {
            validator.throwClientErrorException(site.id, 'id', "Cannot delete site assigned to banking.")
        }

        try {
            site.rooms.each { roomApiService.validateModelBeforeRemove(it) }
        } catch(ClientErrorException ignored){
            validator.throwClientErrorException(site.id, 'id', "Cannot delete related rooms in site.")
        }
    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
