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

package ish.oncourse.server.api.v1.function

import ish.common.types.KeyCode
import static ish.common.types.KeyCode.*
import ish.common.types.Mask
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.v1.model.AccessStatusDTO
import ish.oncourse.server.api.v1.model.UserRoleDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ACLAccessKey
import ish.oncourse.server.cayenne.ACLRole
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

class UserRoleFunctions {

    private static final List<KeyCode> CHECKBOXES = [
            PRINT_CERTIFICATE_WITHOUT_USI,
            PRINT_CERTIFICATE_WITHOUT_VERIFIED_USI,
            ENROLMENT_DISCOUNT,
            OUTCOMES,
            OVERRIDE_TUTOR_SESSION_PAYABLE_TIME,
            BULK_CONFIRM_TUTOR_WAGES,
            INVOICE_CREDIT,
            BANKING,
            RECONCILIATION,
            SUMMARY_EXTRACTS,
            SPECIAL_DUPLICATE,
            SPECIAL_CLASS_CANCEL,
            SPECIAL_EXPORT_XML,
            SPECIAL_CERTIFICATE,
            SPECIAL_DE_DUPE,
            SPECIAL_CANCEL_TRANSFER_ENROLMENTS,
            SPECIAL_AVETMISS_EXPORT,
            SPECIAL_IMPORT,
            SPECIAL_OVERRIDE_TUTOR_PAYRATE,
            SPECIAL_EDIT_NOTES,
            SPECIAL_EMAIL_50,
            SPECIAL_EMAIL_MASS,
            SPECIAL_SMS_50,
            SPECIAL_SMS_MASS,
            SPECIAL_CHANGE_ADMINISTRATION_CENTRE,
            SPECIAL_TWO_FACTOR_AUTHENTICATION,
            VET_COURSE,
            SPECIAL_EDIT_EXTENDED_TAGS
    ]

    static UserRoleDTO toRestUserRole(ACLRole aclRole) {
        new UserRoleDTO().with { userRole ->
            userRole.id = aclRole.id
            userRole.name = aclRole.name
            userRole.modified = aclRole.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            userRole.created = aclRole.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            userRole.rights = aclRole.accessKeys.collectEntries { ak ->
                if (CHECKBOXES.contains(ak.keycode)) {
                    return [ (ak.keycode.displayName) : Boolean.toString(can(ak.mask, Mask.ALL))]
                } else {
                    return [ (ak.keycode.displayName) : toStatus(ak.rawMask).toString()]
                }
            } as Map<String, String>
            values().findAll { it.defaultValueMask != Mask.NONE && !aclRole.accessKeys*.keycode.contains(it)}
                    .each { userRole.rights.put(it.displayName, toStatus(it.defaultValueMask).toString()) }
            userRole
        }

    }

    private static AccessStatusDTO toStatus(Integer currentMask) {
        if (can(currentMask, Mask.DELETE)) {
            return AccessStatusDTO.DELETE
        } else if (can(currentMask, Mask.CREATE)) {
            return AccessStatusDTO.CREATE
        } else if (can(currentMask, Mask.EDIT)) {
            return AccessStatusDTO.EDIT
        } else if (can(currentMask, Mask.PRINT)) {
            return AccessStatusDTO.PRINT
        } else if (can(currentMask, Mask.VIEW)) {
            return AccessStatusDTO.VIEW
        } else {
            return AccessStatusDTO.HIDE
        }
    }


    private static boolean can(Integer currentMask, Integer action) {
        (currentMask & action) == action
    }

    static ValidationErrorDTO validateForDelete(ACLRole aclRole, Long id) {
        if (!aclRole) {
            return new ValidationErrorDTO(id?.toString(), ACLRole.ID.name, "User role is not exist")
        }

        if (!aclRole.systemUsers.empty) {
            return new ValidationErrorDTO(id?.toString(), ACLRole.SYSTEM_USERS.name, "User role is assigned for some users.")
        }
        null
    }

    static ValidationErrorDTO validateForUpdate(ObjectContext context, UserRoleDTO userRole) {
        if (!userRole.name?.trim()) {
            return new ValidationErrorDTO(userRole.id?.toString(), ACLRole.NAME.name, "User role name should be specified")
        } else if (userRole.name.length() > 45) {
            return new ValidationErrorDTO(userRole.id?.toString(), ACLRole.NAME.name, "The maximum length is 45.")
        }

        ACLRole aclRole = ObjectSelect.query(ACLRole)
                .where(ACLRole.NAME.eq(userRole.name))
                .selectOne(context)

        if (aclRole != null && aclRole.id != userRole.id) {
            return new ValidationErrorDTO(userRole.id?.toString(), ACLRole.NAME.name, "User role name should be unique")
        }

        boolean isRoleExists = ObjectSelect.query(ACLRole)
        .where(ACLRole.ID.eq(userRole.id))
        .selectCount(context) > 0

        if (!isRoleExists && userRole.id) {
            return new ValidationErrorDTO(userRole.id?.toString(), null, "User Role is not exist")
        }

        null
    }

    static ACLRole toDbACLRole(ObjectContext context, UserRoleDTO userRole) {
        ACLRole aclRole = userRole.id ? CayenneFunctions.getRecordById(context, ACLRole, userRole.id, ACLRole.ACCESS_KEYS.joint()) : context.newObject(ACLRole)
        aclRole.name = userRole.name

        userRole.rights.each { k, v ->
            ACLAccessKey ak = aclRole.accessKeys.find { it.keycode.displayName.equalsIgnoreCase(k) } ?: newACLAccessKey(aclRole, k)
            ak.mask = CHECKBOXES.contains(ak.keycode) ? Boolean.parseBoolean(v) ? Mask.ALL : Mask.NONE : toMask(AccessStatusDTO.fromValue(v))
        }
        aclRole
    }

    static ACLAccessKey newACLAccessKey(ACLRole aclRole, String key) {
        aclRole.context.newObject(ACLAccessKey).with { ak ->
            ak.role = aclRole
            ak.keycode = values().find { key.equalsIgnoreCase(it.displayName) }
            ak
        }
    }

    private static Integer toMask(AccessStatusDTO status) {
        Integer mask = Mask.NONE
        switch (status) {
            case AccessStatusDTO.DELETE:
                mask += Mask.DELETE
            case AccessStatusDTO.CREATE:
                mask += Mask.CREATE
            case AccessStatusDTO.EDIT:
                mask += Mask.EDIT
            case AccessStatusDTO.PRINT:
                mask += Mask.PRINT
            case AccessStatusDTO.VIEW:
                mask += Mask.VIEW
            case AccessStatusDTO.HIDE:
                break
        }

        mask
    }
}
