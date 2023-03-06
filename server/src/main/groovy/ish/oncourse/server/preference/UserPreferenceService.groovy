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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.TableModelDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISystemUserService
import ish.persistence.Preferences
import ish.util.AccountUtil
import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.ObjectContext
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.server.api.v1.model.PreferenceEnumDTO.ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID
import static ish.oncourse.server.api.v1.model.PreferenceEnumDTO.ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID

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
    private static final String NEWS = "news.read"
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

    private Preference getUserPref(String name) {
        return userService.currentUser.preferences.find {it.name == name}
    }

    private Preference getUserPrefByUniqueKey(String uniqueKey) {
        return userService.currentUser.preferences.find {it.uniqueKey == uniqueKey}
    }

    private String getReadNews() {
        List<Preference> preferenceList = userService.currentUser.preferences.findAll { it.name == NEWS}
        return preferenceList.stream().map({ preference -> preference.valueString })
                .toArray()
                .join(JOIN_DELIMETER)
    }

    private Preference createUserPref(String name) {
        ObjectContext context = cayenneService.newContext
        Preference preference = context.newObject(Preference)
        preference.user =  context.localObject(userService.currentUser)
        preference.name = name

        return preference
    }

    private Preference createEulaUserPref(SystemUser user, String name) {
        ObjectContext context = cayenneService.newContext
        Preference preference = context.newObject(Preference)
        preference.user =  context.localObject(user)
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
            case PreferenceEnumDTO.ACCOUNT_INVOICE_TERMS:
                return preferenceController.accountInvoiceTerms.toString()
            case PreferenceEnumDTO.LICENSE_ACCESS_CONTROL:
                return licenseService.getLisense(PreferenceEnumDTO.LICENSE_ACCESS_CONTROL.toString());
            case PreferenceEnumDTO.LICENSE_SCRIPTING:
                return licenseService.getLisense(PreferenceEnumDTO.LICENSE_SCRIPTING.toString())
            case PreferenceEnumDTO.EULA_LAST_ACCESS_DATE:
                 return preferenceController.getPreference(PreferenceEnumDTO.EULA_LAST_ACCESS_DATE.toString(),false)
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID:
                return preferenceController.getPreference(ACCOUNT_DEFAULT_STUDENTENROLMENTS_ID.toString(), false).getValueString()
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID:
                return preferenceController.getPreference(ACCOUNT_DEFAULT_VOUCHERLIABILITY_ID.toString(), false).getValueString()
            case PreferenceEnumDTO.REPLICATION_ENABLED:
                return !licenseService.isReplicationDisabled()
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
            case PreferenceEnumDTO.ONCOURSE_SERVER_TIMEZONE_DEFAULT:
                return preferenceController.getOncourseServerDefaultTimezone()
            case PreferenceEnumDTO.TUTORIAL_SKIP_SYSTEMUSER:
                return preferenceController.getTutorialSkipSystemUser()
            case PreferenceEnumDTO.BACKGROUND_QUALITY_SCALE:
                return preferenceController.getBackgroundQualityScale()
            case PreferenceEnumDTO.ACCOUNT_DEFAULT_INVOICELINE_ID:
                return preferenceController.getDefaultInvoiceLineAccount()
            case PreferenceEnumDTO.PAYMENT_GATEWAY_TYPE:
                return preferenceController.getPaymentGatewayType()
            case PreferenceEnumDTO.NEWS_READ:
                return getReadNews()
            default:
                String name = key.toString()
                Preference preference = getUserPref(name)
                return preference ? preference.valueString: StringUtils.EMPTY
        }
    }

    void set(PreferenceEnumDTO key, String value) {
        String name = key.toString()
        Preference preference

        if (key == PreferenceEnumDTO.NEWS_READ) {
            preference = getUserPrefByUniqueKey(name)
            if (preference == null) {
                preference = createUserPref(name)
                // we save preference in db to be able to change unique key
                preference.context.commitChanges()
            }
            preference.uniqueKey = userService.currentUser.id + key.toString() + value
        } else if (key == PreferenceEnumDTO.ACCOUNT_DEFAULT_INVOICELINE_ID) {
            preference = preferenceController.getPreference(name, false)
        } else {
            preference = getUserPref(name) ?: createUserPref(name)
        }
        preference.valueString = StringUtils.trimToNull(value)
        preference.context.commitChanges()

    }

    void createEula(SystemUser user, String value) {
        String name =  PreferenceEnumDTO.EULA_LAST_ACCESS_DATE.toString()
        Preference preference = createEulaUserPref(user,name)
        preference.valueString = StringUtils.trimToNull(value)
        preference.context.commitChanges()
    }
}
