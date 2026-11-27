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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * What the AI model is asked: the system prompt and the conversation so far, which ends with the user's message.
 */
public final class ModelRequest {

    private final String systemPrompt;
    private final List<ModelMessage> messages;

    public ModelRequest(String systemPrompt, List<ModelMessage> messages) {
        this.systemPrompt = Objects.requireNonNull(systemPrompt);
        this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public List<ModelMessage> getMessages() {
        return messages;
    }

    /**
     * @return text of the last user message, or null if there is none
     */
    public String getLastUserText() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getRole() == ModelMessage.Role.USER) {
                return messages.get(i).getText();
            }
        }
        return null;
    }
}
