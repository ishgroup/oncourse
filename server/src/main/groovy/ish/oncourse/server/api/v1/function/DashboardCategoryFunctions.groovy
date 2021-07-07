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
import ish.common.types.Mask
import ish.oncourse.server.api.v1.model.CategoryDTO
import static ish.oncourse.server.api.v1.model.CategoryDTO.*
import ish.oncourse.server.api.v1.model.CategoryItemDTO
import ish.oncourse.server.security.api.IPermissionService

class DashboardCategoryFunctions {

    private static final Closure<Integer> categoryComparator = { CategoryItemDTO a, CategoryItemDTO b ->
        a.category ==  CHECKOUT_QUICK_ENROL_ ? -1 : b.category == CHECKOUT_QUICK_ENROL_ ? 1 : (a.category.toString() <=> b.category.toString())
    }

    private static final Map<CategoryDTO, Closure<Boolean>> PERMISSION_MAP =
            [
                    (CHECKOUT_QUICK_ENROL_) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.ENROLMENT, Mask.CREATE)},
                    (CERTIFICATES) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.CERTIFICATE, Mask.VIEW)},
                    (WAITING_LISTS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.WAITING_LIST, Mask.VIEW)},
                    (LEADS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.WAITING_LIST, Mask.VIEW)},
                    (ENROLMENTS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.ENROLMENT, Mask.VIEW)},
                    (INVOICES) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.INVOICE, Mask.VIEW)},
                    (PAYMENTS_IN) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.PAYMENT_IN, Mask.VIEW)},
                    (PAYMENTS_OUT) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.PAYMENT_OUT, Mask.VIEW)},
                    (ACCOUNTS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.ACCOUNT, Mask.VIEW)},
                    (TRANSACTIONS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.TRANSACTION, Mask.VIEW)},
                    (TUTOR_PAY) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.PAYSLIP, Mask.VIEW)},
                    (DEPOSIT_BANKING) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.BANKING, Mask.VIEW)},
                    (DISCOUNTS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.DISCOUNT, Mask.VIEW)},
                    (CORPORATE_PASS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.CORPORATE_PASS, Mask.VIEW)},
                    (STUDENT_FEEDBACK) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SURVEYS, Mask.VIEW)},
                    (EXPORT_AVETMISS_8_) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SPECIAL_AVETMISS_EXPORT, Mask.VIEW)},
                    (APPLICATIONS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.APPLICATION, Mask.VIEW)},
                    (BANKING_DEPOSITS) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.BANKING, Mask.VIEW)},
                    (MESSAGES) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.EMAIL_TEMPLATE, Mask.VIEW)},
                    (FINALISE_PERIOD) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SUMMARY_EXTRACTS, Mask.VIEW)},
                    (OUTCOMES) : { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.OUTCOMES, Mask.VIEW)},
                    (TIMETABLE): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SESSION, Mask.VIEW)},
                    (PRIOR_LEARNINGS): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.CONTACT, Mask.VIEW)},
                    (BATCH_PAYMENT_IN): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.INVOICE, Mask.CREATE)},
                    (IMPORT_TEMPLATES): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SPECIAL_IMPORT, Mask.VIEW)},
                    (EXPORT_TEMPLATES): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.EXPORT_TEMPLATE, Mask.VIEW)},
                    (MESSAGE_TEMPLATES): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.EMAIL_TEMPLATE, Mask.VIEW)},
                    (SCRIPTS): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.SCRIPT_TEMPLATE, Mask.VIEW)},
                    (PDF_BACKGROUNDS): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.REPORT, Mask.VIEW)},
                    (PDF_REPORTS): { IPermissionService permissionService ->  permissionService.currentUserCan(KeyCode.REPORT, Mask.VIEW)}
            ]

    static boolean isPermitted(CategoryDTO category, IPermissionService permissionService) {
        Closure<Boolean> permission  = PERMISSION_MAP[category]

        if (permission) {
            return permission.call(permissionService)
        }
        return false
    }


    static List<CategoryItemDTO> toCategoryItems(List<CategoryDTO> all, List<CategoryDTO> favorite) {
       return all.collect {
           new CategoryItemDTO(category: it, favorite: (it in favorite))
       }.sort(categoryComparator)
    }
}
