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

package ish.oncourse.server.assistant;

import ish.common.types.SystemEventType;

/**
 * Who the onCourse Assistant is available to after its settings changed, the value of
 * {@link SystemEventType#ASSISTANT_SETTINGS_CHANGED}.
 */
public final class AssistantAvailability {

    private final boolean enabled;
    private final boolean adminOnly;

    public AssistantAvailability(boolean enabled, boolean adminOnly) {
        this.enabled = enabled;
        this.adminOnly = adminOnly;
    }

    /**
     * @param userIsAdmin whether the user has the Admin toggle on
     * @return true if the assistant is available to the user
     */
    public boolean isAvailableTo(boolean userIsAdmin) {
        return enabled && (!adminOnly || userIsAdmin);
    }

    @Override
    public String toString() {
        return "AssistantAvailability{enabled=" + enabled + ", adminOnly=" + adminOnly + "}";
    }
}
