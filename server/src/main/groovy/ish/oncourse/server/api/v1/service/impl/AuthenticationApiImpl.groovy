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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.api.v1.login.Sso
import ish.oncourse.server.api.v1.model.LoginRequestDTO
import ish.oncourse.server.api.v1.model.LoginResponseDTO
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.UserDTO
import ish.oncourse.server.api.v1.service.AuthenticationApi
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.preference.UserPreferenceService
import ish.oncourse.server.services.TOTPService
import ish.oncourse.server.users.SystemUserService
import ish.security.AuthenticationUtil
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.time.LocalDateTime

import static ish.common.types.TwoFactorAuthorizationStatus.*
import static ish.oncourse.server.api.v1.function.AuthenticationFunctions.*
import static ish.oncourse.server.api.v1.function.UserFunctions.updateLoginAttemptNumber
import static ish.oncourse.server.api.v1.function.UserFunctions.validateUserPassword
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.*

@CompileStatic
class AuthenticationApiImpl implements AuthenticationApi {

    @Inject
    private ISessionManager sessionManager

    @Inject
    private LicenseService licenseService

    @Inject
    private ICayenneService cayenneService

    @Inject
    private PreferenceController prefController

    @Inject
    private SystemUserService systemUserService

    @Inject
    private UserPreferenceService userPreferenseService;

    @Inject
    private TOTPService totpService

    @Context
    private HttpServletRequest request

    @Context
    private HttpServletResponse response

    @Override
    UserDTO getUser() {
        
        new UserDTO().with {
            it.firstName = systemUserService.currentUser?.firstName
            it.lastName = systemUserService.currentUser?.lastName
            it
        }
        
    }

    @Override
    LoginResponseDTO login(LoginRequestDTO details) {
        //login and password always required
        if (!details.login || !details.password) {
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, 'Email / password data must be specified')
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(content).build())
        }

        //associate with system user
        ObjectContext context = cayenneService.newContext

        SystemUser user = UserDao.getByLogin(context, details.login)

        if (!user) {
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, 'Invalid email / password')
            throwUnauthorizedException(content)
        }

        checkUser(user)

        //check credentials (ldap/internal verification)
        String errorMessage = !user.isAdmin && prefController.servicesLdapAuthentication ?
                checkLdapAuth(user, details.password, prefController) :
                checkInternalAuth(user, details.password)
        if (errorMessage) {
            updateLoginAttemptNumber(user, prefController.numberOfLoginAttempts)
            user.context.commitChanges()
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, errorMessage)
            throwUnauthorizedException(content)
        }

        // check eula agreements
        checkEula(user, details.eulaAccess)

        //password to check
        String passToCheck = details.newPassword ?: details.password
        checkPasswordComplexityAndOutDate(user, details.newPassword, passToCheck)

        checkAnotherSession(user, details.kickOut)

        //check 2FA security options
        checkTfa(user, details.secretCode, details.token, details.skipTfa)

        //if user has totp token we need to verify it
        boolean cookieTokenValid = checkToken(user, passToCheck, details.token, details.secretCode)

        sessionManager.createUserSession(user, prefController.timeoutSec, request)

        LocalDateTime lastLoginOn = LocalDateUtils.dateToTimeValue(user.lastLoginOn != null ? user.lastLoginOn : user.createdOn)

        user.lastLoginOn = new Date()
        user.passwordUpdateRequired = false
        user.loginAttemptNumber = 0

        if (details.newPassword) {
            user.password = AuthenticationUtil.generatePasswordHash(details.newPassword)
            user.passwordLastChanged = LocalDate.now()
        }

        context.commitChanges()

        Integer periodOfHours = prefController.twoFactorAuthExpiryPeriod
        if (user.token && !cookieTokenValid && periodOfHours) {
            String value = generateCookieValue(passToCheck, user.token, periodOfHours)
            addTotpCookie(response, value, periodOfHours)
        }

        return createAuthenticationContent(LOGIN_SUCCESSFUL, null, null, lastLoginOn)
    }

    @Override
    LoginResponseDTO loginSso(String ssoType, String authorizationCode, Boolean kickOut) {
        def sso = getSsoByType(ssoType)

        def configuration = ObjectSelect.query(IntegrationConfiguration)
                .where(IntegrationConfiguration.TYPE.eq(sso.integrationType.intValue()))
                .selectFirst(cayenneService.newReadonlyContext)

        if(!configuration){
            throw new ClientErrorException("Integration of this sso provider is not configured. Connect your administrator", Response.Status.BAD_REQUEST)
        }

        def ssoProvider = sso.getSsoProvider(configuration: configuration, cayenneService: cayenneService)

        String userEmail = null
        try {
            userEmail = ssoProvider.getUserEmailByCode(authorizationCode)
        } catch(ClientErrorException e){
            throwUnauthorizedException(createAuthenticationContent(INVALID_CREDENTIALS, e.getMessage()))
        }

        def context = cayenneService.newContext
        SystemUser user = UserDao.getByEmail(context, userEmail)

        if(!user) {
            def content = createAuthenticationContent(INVALID_CREDENTIALS, "User with related ${userEmail} OKTA email not found in onCourse")
            throwUnauthorizedException(content)
        }

        checkUser(user)
        checkAnotherSession(user, kickOut)
        sessionManager.createUserSession(user, prefController.timeoutSec, request)

        LocalDateTime lastLoginOn = LocalDateUtils.dateToTimeValue(user.lastLoginOn != null ? user.lastLoginOn : user.createdOn)

        user.lastLoginOn = new Date()
        user.loginAttemptNumber = 0
        context.commitChanges()
        return createAuthenticationContent(LOGIN_SUCCESSFUL, null, null, lastLoginOn)
    }

    private void checkUser(SystemUser user){
        if (user.loginAttemptNumber >= prefController.numberOfLoginAttempts) {
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, 'Login access was disabled after too many incorrect login attempts. Please contact onCourse Administrator.')
            throwUnauthorizedException(content)
        }

        if (prefController.autoDisableInactiveAccounts && !user.isActive) {
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, 'User is disabled. Please contact onCourse Administrator.')
            throwUnauthorizedException(content)
        }

        //check max users limit
        if (sessionManager.checkConcurrentUsersLimit(user, licenseService.max_concurrent_users, prefController.timeoutThreshold)) {
            LoginResponseDTO content = createAuthenticationContent(TOO_MANY_USERS, 'Reached limit of logged in users.')
            throwUnauthorizedException(content)
        }
    }


    private void checkEula(SystemUser user, Boolean eulaAccess = null){
        if (licenseService.modified != null) {

            Preference lastAccessDatePreferense = user.preferences.find { preference ->
                preference.name == PreferenceEnumDTO.EULA_LAST_ACCESS_DATE.toString()
            }

            if (lastAccessDatePreferense) {
                LocalDateTime eulaModifiedDate = licenseService.modified
                LocalDateTime lastAccessDate = LocalDateUtils.stringToTimeValue(lastAccessDatePreferense.valueString)

                if (eulaModifiedDate.isAfter(lastAccessDate)) {

                    if (eulaAccess) {
                        lastAccessDatePreferense.valueString = LocalDateUtils.timeValueToString(LocalDateTime.now())
                        lastAccessDatePreferense.context.commitChanges()
                    } else {
                        LoginResponseDTO content = createAuthenticationContent(EULA_REQUIRED, 'Eula required')
                        content.eulaUrl = licenseService.url
                        throwUnauthorizedException(content)
                    }
                }

            } else {

                if (eulaAccess) {
                    userPreferenseService.createEula(user, LocalDateUtils.timeValueToString(LocalDateTime.now()))
                } else {
                    LoginResponseDTO content = createAuthenticationContent(EULA_REQUIRED, 'Eula required')
                    content.eulaUrl = licenseService.url
                    throwUnauthorizedException(content)
                }
            }

        }
    }

    private void checkPasswordComplexityAndOutDate(SystemUser user, String newPassword = null, String passToCheck = null){
        boolean complexityRequired = prefController.passwordComplexity
        String errorMessage = null

        //checked forced password update
        if (user.passwordUpdateRequired && !newPassword) {
            LoginResponseDTO content = createAuthenticationContent(FORCED_PASSWORD_UPDATE, errorMessage)
            content.passwordComlexity = complexityRequired
            throwUnauthorizedException(content)
        }

        //check password complexity
        if(passToCheck)
            errorMessage = validateUserPassword(user.email, user.login, passToCheck, complexityRequired)

        if (errorMessage) {
            LoginResponseDTO content = createAuthenticationContent(WEAK_PASSWORD, errorMessage)
            content.passwordComlexity = complexityRequired
            throwUnauthorizedException(content)
        }

        //check requirements for outdate password
        if (!newPassword) {
            Integer periodOfDays = prefController.passwordExpiryPeriod
            if (periodOfDays) {
                if (!user.passwordLastChanged || !user.passwordLastChanged.plusDays(periodOfDays).isAfter(LocalDate.now())) {
                    errorMessage = 'Password outdated. Update required.'
                    LoginResponseDTO content = createAuthenticationContent(PASSWORD_OUTDATED, errorMessage)
                    content.passwordComlexity = complexityRequired
                    throwUnauthorizedException(content)
                }
            }
        }
    }

    private void checkAnotherSession(SystemUser user, Boolean kickOut = null) {
        if (user.sessionId != null && user.lastAccess.after(prefController.timeoutThreshold) && !kickOut) {
            def errorMessage = 'You are currently logged in from another session.'
            LoginResponseDTO content = createAuthenticationContent(CONCURRENT_SESSIONS_FOUND, errorMessage)
            throwUnauthorizedException(content)
        }

        if (kickOut) {
            sessionManager.doKickOut(user)
        }
    }

    private void checkTfa(SystemUser user, String secretCode = null, Integer token = null, Boolean skipTfa = null){
        boolean noDataForTFA = !(user.token || (secretCode && token))
        String errorMessage = null

        switch (prefController.twoFactorAuthStatus) {
            case DISABLED:
                if (!skipTfa && noDataForTFA) {
                    LoginResponseDTO content = createAuthenticationContent(TFA_OPTIONAL, '', totpService.generateKey(user.email).url)
                    throwUnauthorizedException(content)
                }
                break
            case ENABLED_FOR_ADMIN:
                if (user.isAdmin && noDataForTFA) {
                    errorMessage = 'Two factor authentication required for this user.'
                }
                break
            case ENABLED_FOR_ALL:
                if (!user.token && noDataForTFA) {
                    errorMessage = 'Two factor authentication required for this user.'
                }
                break
        }

        if (errorMessage) {
            LoginResponseDTO content = createAuthenticationContent(TFA_REQUIRED, errorMessage, totpService.generateKey(user.email).url)
            throwUnauthorizedException(content)
        }
    }

    private boolean checkToken(SystemUser user, String passToCheck = null, Integer token = null, String secretCode = null){
        boolean cookieTokenValid = false
        String errorMessage = null
        //if user has totp token we need to verify it
        if (user.token) {
            String skipTotpCookie = getTotpCookie(request)

            if (!token && !skipTotpCookie) {
                LoginResponseDTO content = createAuthenticationContent(TOKEN_REQUIRED, 'Auth Token required')
                throwUnauthorizedException(content)
            }

            cookieTokenValid = validateCookieToken(skipTotpCookie, passToCheck, user.token)

            if (!cookieTokenValid) {
                if (!token) {
                    LoginResponseDTO content = createAuthenticationContent(TOKEN_REQUIRED, 'Auth Token required')
                    throwUnauthorizedException(content)
                }

                if ((errorMessage = checkTokenAuth(user, token))) {
                    LoginResponseDTO content = createAuthenticationContent(INVALID_OR_EXPIRED_TOKEN, errorMessage)
                    throwUnauthorizedException(content)
                }
            }
        } else if (secretCode && token) {
            if (totpService.checkToken(secretCode, token)) {
                user.token = secretCode
            } else {
                LoginResponseDTO content = createAuthenticationContent(INVALID_OR_EXPIRED_TOKEN, errorMessage)
                throwUnauthorizedException(content)
            }
        }
        cookieTokenValid
    }

    @Override
    String getSsoLink(String ssoType, Boolean kickOut) {
        def sso = getSsoByType(ssoType)

        def configuration = ObjectSelect.query(IntegrationConfiguration)
                .where(IntegrationConfiguration.TYPE.eq(sso.integrationType.intValue()))
                .selectFirst(cayenneService.newReadonlyContext)

        def ssoProvider = sso.getSsoProvider(configuration: configuration, cayenneService: cayenneService)
        return ssoProvider.getAuthorizationPageLink(kickOut)
    }

    @Override
    String logout() {
        sessionManager.logout(request)
        return ''
    }

    private static void throwUnauthorizedException(LoginResponseDTO content) {
        throw new ClientErrorException(Response.status(Response.Status.UNAUTHORIZED).entity(content).build())
    }

    private static Sso getSsoByType(String ssoType){
        def sso = Sso.ofType(ssoType)
        if(!sso){
            throw new ClientErrorException("Incorrect sso type: ${sso}", Response.Status.BAD_REQUEST)
        }
        sso
    }
}
