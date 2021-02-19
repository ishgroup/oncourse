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
import groovy.transform.CompileStatic
import ish.oncourse.server.PreferenceController
import static ish.oncourse.server.api.v1.function.DashboardCategoryFunctions.isPermitted
import static ish.oncourse.server.api.v1.function.DashboardCategoryFunctions.toCategoryItems
import ish.oncourse.server.api.v1.model.CategoryDTO
import ish.oncourse.server.api.v1.model.CategoryItemDTO
import ish.oncourse.server.api.v1.model.DashboardLinksDTO
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.UserPreferenceDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.UserPreferenceApi
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.preference.UserPreferenceService
import static ish.oncourse.server.preference.UserPreferenceService.JOIN_DELIMETER
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.services.ISystemUserService

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class UserPreferenceApiImpl implements UserPreferenceApi {


    private static final List<CategoryDTO> ALWAYS_AVAILABLE = [CategoryDTO.CONTACTS, CategoryDTO.STUDENTS, CategoryDTO.TUTORS, CategoryDTO.COMPANIES, CategoryDTO.COURSES, CategoryDTO.CLASSES,
                                                               CategoryDTO.UNITS_OF_COMPETENCY, CategoryDTO.QUALIFICATIONS, CategoryDTO.TRAINEESHIP_COURSES, CategoryDTO.TRAINEESHIPS,
                                                               CategoryDTO.SITES, CategoryDTO.ROOMS, CategoryDTO.DOCUMENTS, CategoryDTO.TAGS, CategoryDTO.SALES, CategoryDTO.MEMBERSHIPS,
                                                               CategoryDTO.PRODUCTS, CategoryDTO.VOUCHER_TYPES, CategoryDTO.ASSESSMENT_TASKS, CategoryDTO.ASSESSMENT_SUBMISSIONS, CategoryDTO.CHANGE_MY_PASSWORD,
                                                               CategoryDTO.ONCOURSE_NEWS, CategoryDTO.SEND_SUPPORT_REQUEST_, CategoryDTO.RELEASE_NOTES, CategoryDTO.COMMUNITY_SUPPORT, CategoryDTO.DOCUMENTATION,
                                                               CategoryDTO.AUTOMATION]

    private static final List<CategoryDTO> PERMISSIBLE_CATEGORIES = [CategoryDTO.CHECKOUT_QUICK_ENROL_, CategoryDTO.CERTIFICATES, CategoryDTO.WAITING_LISTS, CategoryDTO.ENROLMENTS, CategoryDTO.INVOICES,
                                                                     CategoryDTO.PAYMENTS_OUT, CategoryDTO.ACCOUNTS, CategoryDTO.TRANSACTIONS, CategoryDTO.TUTOR_PAY, CategoryDTO.DEPOSIT_BANKING,
                                                                     CategoryDTO.DISCOUNTS, CategoryDTO.CORPORATE_PASS, CategoryDTO.STUDENT_FEEDBACK, CategoryDTO.AUDIT_LOGGING,
                                                                     CategoryDTO.EXPORT_AVETMISS_8_, CategoryDTO.PAYMENTS_IN, CategoryDTO.FUNDING_CONTRACT,
                                                                     CategoryDTO.APPLICATIONS, CategoryDTO.BANKING_DEPOSITS, CategoryDTO.MESSAGES, CategoryDTO.FINALISE_PERIOD, CategoryDTO.OUTCOMES,
                                                                     CategoryDTO.TIMETABLE, CategoryDTO.PRIOR_LEARNINGS, CategoryDTO.BATCH_PAYMENT_IN, CategoryDTO.IMPORT_TEMPLATES, CategoryDTO.EXPORT_TEMPLATES, CategoryDTO.MESSAGE_TEMPLATES, CategoryDTO.SCRIPTS,
                                                                     CategoryDTO.PDF_BACKGROUNDS, CategoryDTO.PDF_REPORTS ]


    private static final List<CategoryDTO> ADMIN_ONLY = [CategoryDTO.PREFERENCES, CategoryDTO.SECURITY, CategoryDTO.INTEGRATIONS, CategoryDTO.HOLIDAYS,
                                                         CategoryDTO.DATA_COLLECTION_FORMS, CategoryDTO.DATA_COLLECTION_RULES, CategoryDTO.TUTOR_PAY_RATES,
                                                         CategoryDTO.PAYMENT_TYPES, CategoryDTO.TAX_TYPES, CategoryDTO.CONCESSION_TYPES,
                                                         CategoryDTO.CONTACT_RELATION_TYPES, CategoryDTO.SELLABLE_ITEMS_RELATION_TYPES, CategoryDTO.CUSTOM_FIELDS]

    @Inject
    private UserPreferenceService usePrefService

    @Inject
    private PreferenceController systemPrefService

    @Inject
    private LicenseService licenseService

    @Inject
    private ISystemUserService userService

    @Inject
    private IPermissionService permissionService

    @Override
    Map<String, String> get(String keys) {
        def throwException = { key ->
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "key: $key is not in user preferences list")).build())
        }

        keys.split(JOIN_DELIMETER)
                .collect { PreferenceEnumDTO.fromValue(it) ?: throwException.call(it) }
                .collectEntries { [ (it.toString()) : usePrefService.get(it as PreferenceEnumDTO) ] }
    }

    @Override
    DashboardLinksDTO getDashboardLinks() {
        List<CategoryDTO> userCategory = []
        if (userService.currentUser.isAdmin) {
            userCategory.addAll(CategoryDTO.values().toList())
        } else {
            userCategory.addAll(ALWAYS_AVAILABLE)
            userCategory.addAll(PERMISSIBLE_CATEGORIES.findAll { isPermitted(it, permissionService) })
        }
        List<CategoryItemDTO> items = toCategoryItems(userCategory, usePrefService.favoriteCategories)
        return new DashboardLinksDTO(categories: items, upgradePlanLink: getUpgradePlanLink())
    }

    @Override
    void set(UserPreferenceDTO preference) {
        usePrefService.set(preference.key, preference.value)
    }

    @Override
    void setFavorites(List<CategoryDTO> categories) {
        usePrefService.favoriteCategories = categories
    }


    private String getUpgradePlanLink() {
        if (!systemPrefService.replicationEnabled && licenseService.replicationDisabled) {
            return "https://www.ish.com.au/signup"
        }
        return null
    }
}
