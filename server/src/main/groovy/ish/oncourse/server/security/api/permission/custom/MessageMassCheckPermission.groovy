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

package ish.oncourse.server.security.api.permission.custom

import io.bootique.jetty.servlet.DefaultServletEnvironment
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.server.api.v1.model.MessageTypeDTO
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.security.api.permission.PermissionCheckingResult
import ish.oncourse.server.security.api.permission.ResourcePermission
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.trimToEmpty

class MessageMassCheckPermission extends ResourcePermission {

    private static final String RECIPIENTS_COUNT = 'recipientsCount'
    private static final String MESSAGE_TYPE = 'messageType'
    IPermissionService permissionService
    String queryString
    String permissionKey
    String method

    MessageMassCheckPermission(String permissionKey, String method) {
        this.permissionKey = permissionKey
        this.method = method
    }

    @Override
    String getPermissionKey() {
        return super.getPermissionKey()
    }

    @Override
    PermissionCheckingResult check() {
        permissionService = injector.getInstance(IPermissionService)
        queryString = injector.getInstance(DefaultServletEnvironment)?.request()?.get()?.queryString
        String messageType = getMessageType()
        if (!StringUtils.isEmpty(messageType)) {
            if (messageType.equalsIgnoreCase(MessageTypeDTO.EMAIL.toString())) {
                return emailCheck(queryString)
            } else {
                return smsCheck(queryString)
            }
        }
        return new PermissionCheckingResult(false, "MessageType should be specified.")
    }

    PermissionCheckingResult emailCheck(String queryString) {
        if (!permissionService.currentUserCan(KeyCode.SPECIAL_EMAIL_50, Mask.VIEW)) {
            return new PermissionCheckingResult(false, "Sorry, you have no permissions to send emails. Please contact your administrator")
        }
        if (!permissionService.currentUserCan(KeyCode.SPECIAL_EMAIL_MASS, Mask.VIEW)) {
            if (recipientsCount() > 50) {
                return new PermissionCheckingResult(false, "Sorry, you have no permissions to send more than 50 emails. Please contact your administrator")
            }
        }
        PermissionCheckingResult.successful()
    }

    PermissionCheckingResult smsCheck(String queryString) {
        if (!permissionService.currentUserCan(KeyCode.SPECIAL_SMS_50, Mask.VIEW)) {
            return new PermissionCheckingResult(false, "Sorry, you have no permissions to send sms. Please contact your administrator")
        }
        if (!permissionService.currentUserCan(KeyCode.SPECIAL_SMS_MASS, Mask.VIEW)) {
            if (recipientsCount() <=> 50 == 1) {
                return new PermissionCheckingResult(false, "Sorry, you have no permissions to send more than 50 sms. Please contact your administrator")
            }
        }

        PermissionCheckingResult.successful()
    }

    private int recipientsCount() {
        if (!trimToEmpty(queryString).contains(RECIPIENTS_COUNT)) {
            return 0
        }
        int value = Integer.valueOf(queryString.split('&').find{ it.contains(RECIPIENTS_COUNT) }.split('=')[1])
        return value
    }

    private String getMessageType() {
        if (!trimToEmpty(queryString).contains(MESSAGE_TYPE)) {
            return ""
        }
        return queryString.split('&').find{ it.contains(MESSAGE_TYPE) }.split('=')[1].toString()
    }
}
