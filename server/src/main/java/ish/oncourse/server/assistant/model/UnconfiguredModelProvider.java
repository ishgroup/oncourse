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
 * Used when the server has no AI model configured (assistant provider "none", the default): every message fails,
 * so the assistant answers that it can't answer right now.
 */
public class UnconfiguredModelProvider implements AssistantModelProvider {

    public static final String NOT_CONFIGURED = "assistant model provider is not configured";

    @Override
    public ModelReply reply(ModelRequest request) throws AssistantModelException {
        throw new AssistantModelException(NOT_CONFIGURED);
    }
}
