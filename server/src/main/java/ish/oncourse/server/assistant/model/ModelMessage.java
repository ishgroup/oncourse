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

import java.util.Objects;

/**
 * One turn of a conversation with the AI model.
 */
public final class ModelMessage {

    public enum Role {
        USER, ASSISTANT
    }

    private final Role role;
    private final String text;

    public ModelMessage(Role role, String text) {
        this.role = Objects.requireNonNull(role);
        this.text = Objects.requireNonNull(text);
    }

    public static ModelMessage user(String text) {
        return new ModelMessage(Role.USER, text);
    }

    public static ModelMessage assistant(String text) {
        return new ModelMessage(Role.ASSISTANT, text);
    }

    public Role getRole() {
        return role;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelMessage)) {
            return false;
        }
        ModelMessage that = (ModelMessage) o;
        return role == that.role && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, text);
    }

    @Override
    public String toString() {
        // the text may contain student data, keep it out of logs
        return "ModelMessage{" + role + ", " + text.length() + " chars}";
    }
}
