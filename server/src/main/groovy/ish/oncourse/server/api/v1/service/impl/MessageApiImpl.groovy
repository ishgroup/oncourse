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

import ish.oncourse.server.api.service.MessageApiService
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.MessageApi

import javax.inject.Inject

class MessageApiImpl implements MessageApi {

    @Inject private MessageApiService service

    @Override
    MessageDTO get(Long id) {
        return service.get(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    String sendMessage(BigDecimal recipientsCount, SendMessageRequestDTO request, String messageType) {
        return service.sendMessage(messageType, recipientsCount, request)
    }

    RecipientsDTO getRecipients(String entity, String messageType, SearchQueryDTO search, Long templateId) {
        return service.getRecipients(entity, messageType, search, templateId)
    }

    @Override
    void bulkChange(DiffDTO diff) {
        service.bulkChange(diff)
    }
}
