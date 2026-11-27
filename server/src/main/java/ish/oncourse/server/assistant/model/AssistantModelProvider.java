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

package ish.oncourse.server.assistant.model;

/**
 * The AI model behind the onCourse Assistant. The server's assistant configuration chooses the implementation:
 * Claude on Amazon Bedrock, a scripted fake for tests or none.
 */
public interface AssistantModelProvider {

    /**
     * Sends the conversation to the model and waits for its whole reply.
     *
     * @param request system prompt and conversation, the last message is the user's
     * @return the model's reply
     * @throws AssistantModelException if the model isn't configured, fails or times out
     */
    ModelReply reply(ModelRequest request) throws AssistantModelException;
}
