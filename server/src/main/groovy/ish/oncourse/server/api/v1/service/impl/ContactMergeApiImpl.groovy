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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.v1.model.MergeDataDTO
import ish.oncourse.server.api.v1.model.MergeRequestDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.ContactMergeApi
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.deduplication.ContactMergeService
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ContactMergeApiImpl implements ContactMergeApi {

    @Inject
    CayenneService cayenneService
    @Inject
    ContactDao contactDao
    @Inject
    ContactMergeService mergeContactService
    @Inject
    SystemUserService systemUserService
    @Inject
    EntityValidator validator

    static final Logger logger = LogManager.getLogger()


    @Override
    MergeDataDTO getMergeData(Long contactAId, Long contactBId) {

        ObjectContext context = cayenneService.getNewContext()

        Contact contactA = contactDao.getById(context, contactAId)
        Contact contactB = contactDao.getById(context, contactBId)

        try {
            mergeContactService.checkMergeAvailability(contactA, contactB)
        } catch (Exception ex) {
            logger.catching(ex)
            validator.throwClientErrorException(new ValidationErrorDTO(null, 'contactMerge', ex.getMessage()))
        }

        MergeDataDTO result = new MergeDataDTO().with { data ->
            data.mergeLines = mergeContactService.getDifferenceAttributes(contactA, contactB)
            data.infoLines = mergeContactService.getInfoLineAttributes(contactA, contactB)
            data
        }
        result
    }

    @Override
    Long merge(MergeRequestDTO mergeRequest) {

        ObjectContext context = cayenneService.getNewContext()

        Contact contactA = contactDao.getById(context, mergeRequest.contactA)
        Contact contactB = contactDao.getById(context,  mergeRequest.contactB)

        try {
            mergeContactService.checkMergeAvailability(contactA, contactB)
            mergeContactService.merge(contactA, contactB, mergeRequest.data)
        } catch (Exception ex) {
            logger.catching(ex)
            validator.throwClientErrorException(new ValidationErrorDTO(null, 'contactMerge', ex.getMessage()))
        }
        return contactA.id
    }
}
