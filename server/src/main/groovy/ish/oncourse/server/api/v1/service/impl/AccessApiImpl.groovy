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
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.server.api.v1.model.KeyCodeDTO
import ish.oncourse.server.api.v1.model.PermissionRequestDTO
import ish.oncourse.server.api.v1.model.PermissionResponseDTO
import ish.oncourse.server.api.v1.service.AccessApi
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.security.api.permission.PermissionCheckingResult
import ish.oncourse.server.users.SystemUserService

class AccessApiImpl implements AccessApi {

    @Inject
    private IPermissionService permissionService

    @Inject
    private SystemUserService systemUserService

    @Override
    PermissionResponseDTO checkPermissions(PermissionRequestDTO permissionRequest) {
        PermissionResponseDTO response = new PermissionResponseDTO()

        if (permissionRequest.keyCode) {
            switch (permissionRequest.keyCode) {
                case KeyCodeDTO.VET_COURSE:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.VET_COURSE, Mask.VIEW)
                    response.cause = 'Sorry, you have no permissions to look at VET details. Please contact your administrator'
                    return response
                case KeyCodeDTO.ENROLMENT_CREATE:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.ENROLMENT, Mask.CREATE)
                    response.cause = 'Sorry, you have no permissions to make enrolment. Please contact your administrator'
                    return response
                case KeyCodeDTO.SPECIAL_EMAIL_50:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.SPECIAL_EMAIL_50, Mask.VIEW)
                    response.cause = 'Sorry, you have no permissions to send email. Please contact your administrator'
                    return response
                case KeyCodeDTO.SPECIAL_EMAIL_MASS:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.SPECIAL_EMAIL_MASS, Mask.VIEW)
                    response.cause = 'Sorry, you have no permissions to send email. Please contact your administrator'
                    return response
                case KeyCodeDTO.PAYMENT_IN_CREATE:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.PAYMENT_IN, Mask.CREATE)
                    response.cause = 'Sorry, you have no permissions to make paymentIn. Please contact your administrator'
                    return response
                case KeyCodeDTO.ADMIN:
                    response.hasAccess = systemUserService.currentUser.isAdmin
                    return response
                case KeyCodeDTO.SCRIPT_EXECUTE:
                    response.hasAccess = permissionService.currentUserCan(KeyCode.SCRIPT_EXECUTE, Mask.ALL)
                    return response
                default:
                    throw new IllegalArgumentException("Unsapported permission kode $permissionRequest.keyCode")
            }
        }

        PermissionCheckingResult access = permissionService.hasAccess(permissionRequest.getPath(), permissionRequest.getMethod())
        response.hasAccess = access.successful
        response.cause = access.errorMessage
        return response
    }
}
