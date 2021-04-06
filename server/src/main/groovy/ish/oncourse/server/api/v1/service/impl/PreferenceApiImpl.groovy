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
import ish.common.types.ClassFundingSource
import ish.common.types.DeliveryMode
import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.common.types.TwoFactorAuthorizationStatus
import ish.oncourse.common.AvetmissConstants
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import static ish.oncourse.server.api.v1.function.CountryFunctions.toRestCountry
import static ish.oncourse.server.api.v1.function.LanguageFunctions.toRestLanguage
import ish.oncourse.server.api.v1.function.PreferenceFunctions
import static ish.oncourse.server.api.v1.function.PreferenceFunctions.READONLY_PREFERENCES
import static ish.oncourse.server.api.v1.function.PreferenceFunctions.getOrCreateUserPreference
import static ish.oncourse.server.api.v1.function.PreferenceFunctions.toValue
import ish.oncourse.server.api.v1.model.ColumnWidthDTO
import ish.oncourse.server.api.v1.model.CountryDTO
import ish.oncourse.server.api.v1.model.CurrencyDTO
import ish.oncourse.server.api.v1.model.EnumItemDTO
import static ish.oncourse.server.api.v1.model.EnumNameDTO.ADDRESSSTATES
import static ish.oncourse.server.api.v1.model.EnumNameDTO.CLASSFUNDINGSOURCE
import static ish.oncourse.server.api.v1.model.EnumNameDTO.DELIVERYMODE
import static ish.oncourse.server.api.v1.model.EnumNameDTO.EXPORTJURISDICTION
import static ish.oncourse.server.api.v1.model.EnumNameDTO.MAINTENANCETIMES
import static ish.oncourse.server.api.v1.model.EnumNameDTO.TRAININGORG_TYPES
import static ish.oncourse.server.api.v1.model.EnumNameDTO.TWOFACTORAUTHSTATUS
import static ish.oncourse.server.api.v1.model.EnumNameDTO.fromValue
import ish.oncourse.server.api.v1.model.LanguageDTO
import ish.oncourse.server.api.v1.model.SystemPreferenceDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.PreferenceApi
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.security.LdapAuthConnectionService
import ish.oncourse.server.services.ISystemUserService
import ish.oncourse.server.services.TransactionLockedService
import ish.persistence.CommonPreferenceController
import ish.security.LdapProperties
import ish.security.TestLdapAuthConnection
import ish.util.TimeFormatter
import ish.util.TimeZoneUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate

class PreferenceApiImpl implements PreferenceApi {

    private static final String KEYS_SPLITTER = ","
    private static final String SECURITY_KEY_PIECE = "security"
    private static final String CURRENCY_PREFERENCE_KEY = "default.currency"

    @Inject
    private LdapAuthConnectionService ldapAuthConnectionService
    @Inject
    private ICayenneService cayenneService
    @Inject
    private PreferenceController controller
    @Inject
    private ISystemUserService userService
    @Inject
    private TransactionLockedService transactionLockedService

    private static Logger logger = LogManager.logger

    @Override
    Boolean checkConnection(String host, String port, String isSsl, String baseDn, String user) {
        return TestLdapAuthConnection.valueOf(LdapProperties.valueOf(host, Integer.valueOf(port), Boolean.valueOf(isSsl), baseDn, user)).testLdapConnection()
    }

    @Override
    List<SystemPreferenceDTO> get(String search) {
        if (!search || search.empty) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, 'search parameter is required')).build())
        }

        String[] keys = search.split(',')
        try {
            return keys.collect { key ->
                new SystemPreferenceDTO().with { pref ->
                    pref.uniqueKey = key
                    pref.valueString = PreferenceFunctions.toString(key, controller)
                    PreferenceFunctions.addTimeStamp(pref, controller)
                    pref
                }
            }
        } catch (IllegalArgumentException e) {
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'uniqueKey', e.message)).build())
        }
    }


    @Override
    void update(List<SystemPreferenceDTO> preferences) {
        cayenneService.serverRuntime.performInTransaction {
            preferences.each {
                try {
                    if (it.uniqueKey in READONLY_PREFERENCES) {
                        throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(it.uniqueKey, 'valueString', 'preference read only')).build())
                    } else {
                        controller.getValueForKey(it.uniqueKey)
                        controller.setValueForKey(it.uniqueKey, toValue(it.uniqueKey, it.valueString))
                    }
                } catch (IllegalArgumentException e) {
                    controller.rollbackChanges()
                    logger.catching(e)
                    throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(it.uniqueKey, 'valueString', e.message)).build())
                }
            }
        }
    }

    @Override
    void updateColumnSettings(ColumnWidthDTO settings) {
        ObjectContext context = cayenneService.newContext

        if (settings.preferenceLeftColumnWidth) {
            Preference prefWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.preference.leftColumnWidth', null)
            prefWdth.valueString = settings.preferenceLeftColumnWidth
        }
        if (settings.tagLeftColumnWidth) {
            Preference tagWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.tag.leftColumnWidth', null)
            tagWdth.valueString = settings.tagLeftColumnWidth
        }
        if (settings.securityLeftColumnWidth) {
            Preference securityWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.security.leftColumnWidth', null)
            securityWdth.valueString = settings.securityLeftColumnWidth
        }
        if (settings.automationLeftColumnWidth) {
            Preference securityWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.automation.leftColumnWidth', null)
            securityWdth.valueString = settings.automationLeftColumnWidth
        }

        context.commitChanges()
    }

    @Override
    List<CountryDTO> getCountries() {
        ObjectSelect.query(Country)
                .select(cayenneService.newContext)
                .collect { toRestCountry(it)}
    }

    @Override
    List<LanguageDTO> getLanguages() {
        ObjectSelect.query(Language)
                .select(cayenneService.newContext)
                .collect { toRestLanguage(it)}
    }

    @Override
    CurrencyDTO getCurrency() {
        Preference currencyPreference = ObjectSelect.query(Preference)
                .where(Preference.UNIQUE_KEY.eq(CURRENCY_PREFERENCE_KEY))
                .selectOne(cayenneService.newContext)
        CurrencyDTO currency = new CurrencyDTO()
        if (currencyPreference == null) {
            currency.setName(ish.math.Country.AUSTRALIA.name())
            currency.setCurrencySymbol(ish.math.Country.AUSTRALIA.currencySymbol())
            currency.setShortCurrencySymbol(ish.math.Country.AUSTRALIA.currencyShortSymbol())
        } else {
            ish.math.Country country = ish.math.Country.forCurrencySymbol(currencyPreference.getValueString())
            currency.setName(country.name())
            currency.setCurrencySymbol(country.currencySymbol())
            currency.setShortCurrencySymbol(country.currencyShortSymbol())
        }
        return currency
    }

    @Override
    List<EnumItemDTO> getEnum(String name) {
        switch (fromValue(name)) {
            case DELIVERYMODE:
                return DeliveryMode.values().collect {
                    new EnumItemDTO(value: it.databaseValue.toString(), label: it.displayName)
                }
            case CLASSFUNDINGSOURCE:
                return ClassFundingSource.values().collect {
                    new EnumItemDTO(value: it.databaseValue.toString(), label: it.displayName)
                }
            case EXPORTJURISDICTION:
                return ExportJurisdiction.values().collect {
                    new EnumItemDTO(value: it.databaseValue.toString(), label: it.displayName)
                }
            case TRAININGORG_TYPES:
                return AvetmissConstants.TrainingOrg_Types.collect { new EnumItemDTO(value: it.value, label: it.key) }
            case ADDRESSSTATES:
                return CommonPreferenceController.AddressStates.collect { new EnumItemDTO(value: it.value, label: it.key) }
            case MAINTENANCETIMES:
                return TimeFormatter.MAINTENANCE_TIMES.collect {
                    new EnumItemDTO(value: it.key.toString(), label: it.value)
                }
            case TWOFACTORAUTHSTATUS:
                return TwoFactorAuthorizationStatus.values().collect {
                    new EnumItemDTO(value: it.databaseValue, label: it.displayName)
                }
            default:
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "Wrong enumeration type: ${name}")).build())
        }
    }

    @Override
    LocalDate getLockedDate() {
        return transactionLockedService.transactionLocked
    }

    @Override
    ColumnWidthDTO getColumnSettings() {
        ObjectContext context = cayenneService.newContext
        Preference prefWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.preference.leftColumnWidth', '200')
        Preference tagWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.tag.leftColumnWidth', '200')
        Preference securityWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.security.leftColumnWidth', '200')
        Preference automationWdth = getOrCreateUserPreference(context, userService.currentUser, 'html.ui.automation.leftColumnWidth', '250')

        new ColumnWidthDTO(preferenceLeftColumnWidth: new BigDecimal(prefWdth.valueString),
                tagLeftColumnWidth: new BigDecimal(tagWdth.valueString),
                securityLeftColumnWidth: new BigDecimal(securityWdth.valueString),
                automationLeftColumnWidth: new  BigDecimal(automationWdth.valueString))
    }

    @Override
    List<String> getTimezones() {
        return new ArrayList<String>(TimeZoneUtil.choices.values())
    }

    @Override
    Integer messageQueued(String type) {
        MessageType messageType
        switch (type) {
            case MessageType.EMAIL.displayName:
                messageType = MessageType.EMAIL
                break
            case MessageType.SMS.displayName:
                messageType = MessageType.SMS
                break
            default:
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'MessageType', "Incorrect message type: ${type}")).build())
        }

        ObjectSelect.query(MessagePerson)
                .where(MessagePerson.STATUS.eq(MessageStatus.QUEUED))
                .and(MessagePerson.TYPE.eq(messageType))
                .count()
                .selectOne(cayenneService.newContext)
    }

}
