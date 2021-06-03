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

package ish.oncourse.server.preference

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.DefaultAccount
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.CategoryDTO
import ish.oncourse.server.cayenne.Account
import ish.util.AccountUtil

import static ish.oncourse.server.api.v1.model.PreferenceEnumDTO.ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID
import static ish.oncourse.server.api.v1.model.PreferenceEnumDTO.ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.TableModelDTO
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISystemUserService
import ish.persistence.Preferences
import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Singleton
@CompileStatic
class UserPreferenceService {

    private static  Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ISystemUserService userService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private LicenseService licenseService

    private static final String USER_PREF_PREFIX = "html.table"
    private static final String DASHBOARD_FAVORITE_CATEGORY = "html.dashboard.favorite"
    public static final String JOIN_DELIMETER = ','


    private ObjectMapper mapper = new ObjectMapper()

    void setTableModel(String entity, TableModelDTO model) {
        String name = "$USER_PREF_PREFIX.$entity".toString()
        Preference preference = getUserPref(name)?:createUserPref(name)
        preference.valueString = mapper.writeValueAsString(model)
        preference.context.commitChanges()
    }

    TableModelDTO getTableModel(String entity) {
        TableModelDTO defaultModel = DefaultUserPreference.DEFAULT_MODEL_MAP[entity]
        String name = "$USER_PREF_PREFIX.$entity"
        try {
            Preference preference = getUserPref(name)

            if (preference && preference.valueString) {

                TableModelDTO tableModel = mapper.readValue(preference.valueString, TableModelDTO)
                if (!tableModel.equalsByColumns(defaultModel)) {
                    return defaultModel
                }
                return tableModel
            }
        } catch (IOException|CayenneRuntimeException e) {
            logger.catching(e)
        }

        return defaultModel
    }

    void setFavoriteCategories(List<CategoryDTO> categories) {

        Preference preference = getUserPref(DASHBOARD_FAVORITE_CATEGORY)?:createUserPref(DASHBOARD_FAVORITE_CATEGORY)
        preference.valueString = categories.empty ? null : categories*.name().join(JOIN_DELIMETER)
        preference.context.commitChanges()
    }

    List<CategoryDTO> getFavoriteCategories() {
        try {
            Preference preference = getUserPref(DASHBOARD_FAVORITE_CATEGORY)

            if (preference && preference.valueString) {
                return preference.valueString.split(JOIN_DELIMETER)
                        .findAll {str -> str in CategoryDTO.values()*.name()}
                        .collect { str ->  CategoryDTO.values().find {it.name() == str } }
            }
        } catch (IOException|CayenneRuntimeException e) {
            logger.catching(e)
        }

        return Collections.EMPTY_LIST
    }


    private Preference getUserPref(String name) {
        return userService.currentUser.preferences.find {it.name == name}
    }

    private Preference createUserPref(String name) {
        ObjectContext context = cayenneService.newContext
        Preference preference = context.newObject(Preference)
        preference.user =  context.localObject(userService.currentUser)
        preference.name = name

        return preference
    }

    String get(PreferenceEnumDTO key) {
        switch (key) {
            case PreferenceEnumDTO.EMAIL_FROM:
                return preferenceController.emailFromAddress
            case PreferenceEnumDTO.GOOGLE_ANALYTICS_CID:
                return DigestUtils.md5Hex(licenseService.security_key?:'' + userService.currentUser.id)
            case PreferenceEnumDTO.GOOGLE_ANALYTICS_CI:
                return preferenceController.collegeABN
            case PreferenceEnumDTO.GOOGLE_ANALYTICS_CN:
                return preferenceController.collegeName
            case PreferenceEnumDTO.GOOGLE_ANALYTICS_UID:
                return userService.currentUser.willowId
            case PreferenceEnumDTO.TIMEZONE_DEFAULT:
                return preferenceController.oncourseServerDefaultTimezone
            case PreferenceEnumDTO.SYSTEMUSER_DEFAULTADMINISTRATIONCENTRE_NAME:
                return userService.currentUser.defaultAdministrationCentre.name
            case PreferenceEnumDTO.LICENSE_ACCESSCONTROL:
                return preferenceController.licenseAccessControl.toString()
            case PreferenceEnumDTO.LICENSE_SCRIPTING:
                return preferenceController.licenseScripting.toString()
            case PreferenceEnumDTO.ACCOUNT_INVOICE_TERMS:
                return preferenceController.accountInvoiceTerms.toString()
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID:
                return preferenceController.getPreference(ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID.toString(), false).getValueString()
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID:
                return preferenceController.getPreference(ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID.toString(), false).getValueString()
            case PreferenceEnumDTO.REPLICATION_ENABLED:
                return preferenceController.replicationEnabled
            case PreferenceEnumDTO.AVETMISS_IDENTIFIER:
                return preferenceController.avetmissID
            case PreferenceEnumDTO.COURSECLASS_DEFAULT_DELIVERYMODE:
                return preferenceController.getValueForKey(Preferences.CLASS_DEFAULTS_DELIVERY_MODE)
            case PreferenceEnumDTO.COURSECLASS_DEFAULT_FUNDINGSOURCE:
                return preferenceController.getValueForKey(Preferences.CLASS_DEFAULTS_FUNDING_SOURCE)
            case PreferenceEnumDTO.COURSECLASS_DEFAULT_MAXIMUMPLACES:
                return preferenceController.getValueForKey(Preferences.CLASS_DEFAULTS_MAXIMUM_PLACES)
            case PreferenceEnumDTO.COURSECLASS_DEFAULT_MINIMUMPLACES:
                return preferenceController.getValueForKey(Preferences.CLASS_DEFAULTS_MINIMUM_PLACES)
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_VOUCHERUNDERPAYMENT_ID:
                return AccountUtil.getDefaultVoucherExpenseAccount(preferenceController.objectContext, Account.class)?.id?.toString()
            default:
                String name = key.toString()
                Preference preference = getUserPref(name)
                return preference ? preference.valueString: StringUtils.EMPTY
        }
    }

    void set(PreferenceEnumDTO key, String value) {
        String name = key.toString()
        Preference preference = getUserPref(name)?:createUserPref(name)
        preference.valueString = StringUtils.trimToNull(value)
        preference.context.commitChanges()

    }

}
