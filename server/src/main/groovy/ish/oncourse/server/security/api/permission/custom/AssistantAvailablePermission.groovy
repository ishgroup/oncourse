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

package ish.oncourse.server.security.api.permission.custom

import groovy.transform.CompileStatic
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.assistant.AssistantConversationService
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.security.api.permission.PermissionCheckingResult
import ish.oncourse.server.security.api.permission.ResourcePermission
import ish.oncourse.server.services.ISystemUserService

/**
 * Lets the request through only when the onCourse Assistant is available to the current user:
 * the assistant is enabled and either open to all users or the user has the Admin toggle on.
 */
@CompileStatic
class AssistantAvailablePermission extends ResourcePermission {

    AssistantAvailablePermission(String permissionKey, String method) {
        setPermissionKey(permissionKey)
        setMethod(method)
    }

    @Override
    PermissionCheckingResult check() {
        SystemUser user = injector.getInstance(ISystemUserService).currentUser
        PreferenceController preferenceController = injector.getInstance(PreferenceController)
        if (user != null && preferenceController.isAssistantAvailableTo(Boolean.TRUE == user.isAdmin)) {
            return PermissionCheckingResult.successful()
        }
        return new PermissionCheckingResult(false, AssistantConversationService.NOT_AVAILABLE_MSG)
    }
}
