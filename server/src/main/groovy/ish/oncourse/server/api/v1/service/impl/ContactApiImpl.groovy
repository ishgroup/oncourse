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

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.ContactApiService
import static ish.oncourse.server.api.v1.function.ContactRelationFunctions.toRestContactRelationType
import static ish.oncourse.server.api.v1.function.ContactRelationFunctions.updateContactRelation
import static ish.oncourse.server.api.v1.function.ContactRelationFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.ContactRelationFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.ContactDTO
import ish.oncourse.server.api.v1.model.ContactRelationTypeDTO
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.UsiRequestDTO
import ish.oncourse.server.api.v1.model.UsiVerificationResultDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.ContactApi
import ish.oncourse.server.cayenne.ContactRelationType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class ContactApiImpl implements ContactApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ContactApiService contactApiService

    @Override
    List<Long> createContact(ContactDTO contact) {
        List<Long> id = []
        id << contactApiService.create(contact).id
        return id
    }

    @Override
    ContactDTO getContact(Long id) {
        contactApiService.get(id)
    }

    @Override
    void bulkChange(DiffDTO diff) {
        contactApiService.bulkChange(diff)
    }

    @Override
    void removeContact(Long id) {
        contactApiService.remove(id)
    }

    @Override
    void updateContact(Long id, ContactDTO contact) {
        contactApiService.update(id, contact)
    }

    @Override
    List<ContactRelationTypeDTO> get() {
        ObjectSelect.query(ContactRelationType)
                .select(cayenneService.newContext)
                .collect { toRestContactRelationType(it) }
    }

    @Override
    void remove(String id) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateForDelete(context, id)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        } else {
            ContactRelationType dbType = SelectById.query(ContactRelationType, id).selectOne(context)
            context.deleteObject(dbType)
            context.commitChanges()
        }
    }

    @Override
    void update(List<ContactRelationTypeDTO> contactRelationTypes) {
        ObjectContext context = cayenneService.newContext

        contactRelationTypes.each { type ->
            ValidationErrorDTO error = validateForUpdate(context, type)
            if (error) {
                context.rollbackChanges()
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
            updateContactRelation(context, type)
        }
        context.commitChanges()
    }

    @Override
    UsiVerificationResultDTO verifyUsi(UsiRequestDTO dto) {
        contactApiService.verifyUsi(dto.firstName, dto.lastName, dto.birthDate, dto.usiCode)
    }
}
