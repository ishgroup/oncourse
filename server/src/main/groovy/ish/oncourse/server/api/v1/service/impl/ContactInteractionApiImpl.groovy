/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.common.types.ApplicationStatus
import ish.common.types.LeadStatus
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.api.v1.model.ContactActivityDTO
import ish.oncourse.server.api.v1.model.ContactInsightDTO
import ish.oncourse.server.api.v1.model.ContactOverviewDTO
import ish.oncourse.server.api.v1.service.ContactInteractionApi

import java.time.Duration
import java.time.Instant

class ContactInteractionApiImpl implements ContactInteractionApi{
    @Inject
    private ContactDao contactDao

    @Inject
    private ICayenneService cayenneService

    @Override
    ContactInsightDTO getInteraction(Long id) {
        def context = cayenneService.newReadonlyContext
        def contact = contactDao.getById(context, id)
        def user = UserDao.getByEmail(context, contact.email)

        def contactActivityDto = new ContactActivityDTO()
        contactActivityDto.lastContacted(user.lastAccess.toLocalDate())
        contactActivityDto.inactiveDays(Duration.between(Instant.now(),user.lastAccess.toInstant()).toDays())
        //TODO: intercections initializing

        def contactOverview = new ContactOverviewDTO()
        contactOverview.enrolments(contact.student?.enrolments?.collect {it.id})
        contactOverview.openApplications(contact?.student?.applications?.findAll {it.status != ApplicationStatus.REJECTED && it.status != ApplicationStatus.WITHDRAWN}?.collect {it.id})
        contactOverview.closeApplications(contact?.student?.applications?.findAll {it.status == ApplicationStatus.REJECTED || it.status == ApplicationStatus.WITHDRAWN}?.collect {it.id})
        contactOverview.openLeads(contact.leads.findAll{it.status == LeadStatus.OPEN}.collect{it.id})
        contactOverview.closeLeads(contact.leads.findAll{it.status == LeadStatus.CLOSED}.collect{it.id})

        def contactInsight = new ContactInsightDTO()
        contactInsight.fullName = contact?.fullName
        contactInsight.email = contact?.email
        contactInsight.activity(contactActivityDto)
        contactInsight.overview(contactOverview)
        contactInsight
    }
}
