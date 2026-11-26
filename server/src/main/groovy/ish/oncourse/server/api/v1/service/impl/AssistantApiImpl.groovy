/*
 * Copyright ish group pty ltd 2026.
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
import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.AssistantMessageRequestDTO
import ish.oncourse.server.api.v1.model.AssistantReplyDTO
import ish.oncourse.server.api.v1.service.AssistantApi
import ish.oncourse.server.assistant.AssistantConversationService

@CompileStatic
class AssistantApiImpl implements AssistantApi {

    @Inject
    private AssistantConversationService conversationService

    @Override
    AssistantReplyDTO sendMessage(AssistantMessageRequestDTO request) {
        AssistantConversationService.Reply reply = conversationService.send(request?.conversationId, request?.text)
        return new AssistantReplyDTO(conversationId: reply.conversationId, text: reply.text)
    }
}
