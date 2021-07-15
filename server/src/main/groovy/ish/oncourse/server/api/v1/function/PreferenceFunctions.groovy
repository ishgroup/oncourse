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

import ish.common.types.ClassFundingSource
import ish.common.types.DeliveryMode
import ish.common.types.TwoFactorAuthorizationStatus
import ish.common.types.TypesUtil
import ish.common.util.DisplayableExtendedEnumeration
import ish.math.Country
import static ish.oncourse.DefaultAccount.defaultAccountPreferences
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.SystemPreferenceDTO
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import static ish.persistence.Preferences.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import java.time.ZoneOffset

class PreferenceFunctions {

    public static final List<String> PRIVATE_PREFERENCES = [LDAP_BIND_USER_PASS, EMAIL_POP3PASSWORD]

    public static final List<String> READONLY_PREFERENCES = [  LICENSE_ACCESS_CONTROL,
                                                               LICENSE_SMS,
                                                               LICENSE_CC_PROCESSING,
                                                               LICENSE_PAYROLL,
                                                               LICENSE_VOUCHER,
                                                               LICENSE_MEMBERSHIP,
                                                               LICENSE_ATTENDANCE,
                                                               LICENSE_SCRIPTING,
                                                               LICENSE_FEE_HELP_EXPORT,
                                                               LICENSE_FUNDING_CONTRACT
    ]

    static String toString(String key, PreferenceController controller) {

        if (key in PRIVATE_PREFERENCES) {
            return null
        }
        Object value = controller.getValueForKey(key)

        if (value != null) {


            if (value instanceof DisplayableExtendedEnumeration) {
                return (value as DisplayableExtendedEnumeration).databaseValue.toString()
            }

            if (value instanceof Country) {
                return (value as Country).currencySymbol()
            }

            return value.toString()
        }
        return null

    }
    static void addTimeStamp(SystemPreferenceDTO preference, PreferenceController controller) {

        if (preference.valueString) {
            Preference dbPreference  = controller.getPreference(preference.uniqueKey, false)
            if (dbPreference) {
                preference.created = dbPreference.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                preference.modified = dbPreference.modifiedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            }
        }
    }

    static Object toValue(String key, String value) {
        if (value == null) {
            return null
        }
        if (defaultAccountPreferences.contains(key)) {
            return Long.valueOf(value)
        }

        switch (key) {
            case USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS:
            case SERVICES_LDAP_AUTHENTICATION:
            case SERVICES_LDAP_AUTHORISATION:
            case SERVICES_CC_ENABLED:
            case EMAIL_BOUNCE_ENABLED:
            case LDAP_SSL:
            case LDAP_GROUP_POSIX_STYLE:
            case LOGOUT_ENABLED:
            case AVETMISS_SHOW_GUI:
            case AUTO_DISABLE_INACTIVE_ACCOUNT:
            case PASSWORD_COMPLEXITY:
                return Boolean.valueOf(value)
            case SERVICES_INFO_REPLICATION_VERSION:
            case LDAP_SERVERPORT:
            case PAY_PERIOD_DAYS:
            case CLASS_DEFAULTS_MAXIMUM_PLACES:
            case CLASS_DEFAULTS_MINIMUM_PLACES:
            case ACCOUNT_INVOICE_TERMS:
            case PASSWORD_EXPIRY_PERIOD:
            case TFA_EXPIRY_PERIOD:
            case NUMBER_OF_LOGIN_ATTEMPTS:
                return Integer.valueOf(value)
            case CLASS_DEFAULTS_DELIVERY_MODE:
                return TypesUtil.getEnumForDatabaseValue(Integer.valueOf(value), DeliveryMode)
            case CLASS_DEFAULTS_FUNDING_SOURCE:
                return TypesUtil.getEnumForDatabaseValue(Integer.valueOf(value), ClassFundingSource)
            case AVETMISS_JURISDICTION:
                return TypesUtil.getEnumForDatabaseValue(Integer.valueOf(value), ExportJurisdiction)
            case TWO_FACTOR_AUTHENTICATION:
                return TypesUtil.getEnumForDatabaseValue(value, TwoFactorAuthorizationStatus)
            case ACCOUNT_CURRENCY:
                return Country.forCurrencySymbol(value)
            default:
                return value
        }
    }

    static Preference createPreference(ObjectContext context, String key, String value, SystemUser applyToUser) {
        Preference pref = context.newObject(Preference)
        pref.name = key
        pref.valueString = value
        pref.user = context.localObject(applyToUser)
        pref
    }

    static Preference getOrCreateUserPreference(ObjectContext context, SystemUser user, String key, String defaultValue) {
        Preference pref = ObjectSelect.query(Preference).where(Preference.NAME.eq(key).andExp(Preference.USER.eq(user))).selectFirst(context)
        if (!pref) {
            pref = createPreference(context, key, defaultValue, user)
            context.commitChanges()
        }
        pref
    }
}

