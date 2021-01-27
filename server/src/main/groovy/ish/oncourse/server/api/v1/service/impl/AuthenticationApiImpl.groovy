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
import ish.oncourse.server.api.dao.UserDao

import static ish.common.types.TwoFactorAuthorizationStatus.DISABLED
import static ish.common.types.TwoFactorAuthorizationStatus.ENABLED_FOR_ADMIN
import static ish.common.types.TwoFactorAuthorizationStatus.ENABLED_FOR_ALL
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.servlet.ISessionManager
import static ish.oncourse.server.api.v1.function.AuthenticationFunctions.*
import ish.oncourse.server.api.v1.model.LoginRequestDTO
import ish.oncourse.server.api.v1.model.LoginResponseDTO

import static ish.oncourse.server.api.v1.function.UserFunctions.validateUserPassword
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.CONCURRENT_SESSIONS_FOUND
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.FORCED_PASSWORD_UPDATE
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.INVALID_CREDENTIALS
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.INVALID_OR_EXPIRED_TOKEN
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.LOGIN_SUCCESSFUL
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.PASSWORD_OUTDATED
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.TFA_OPTIONAL
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.TFA_REQUIRED
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.TOKEN_REQUIRED
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.TOO_MANY_USERS
import static ish.oncourse.server.api.v1.model.LoginStatusDTO.WEAK_PASSWORD
import ish.oncourse.server.api.v1.service.AuthenticationApi
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.TOTPService
import ish.oncourse.server.users.SystemUserService
import ish.security.AuthenticationUtil
import org.apache.cayenne.ObjectContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import java.time.LocalDate

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
    private TOTPService totpService

    @Context
    private HttpServletRequest request

    @Context
    private HttpServletResponse response

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

        //check credentials (ldap/internal verification)
        String errorMessage = !user.isAdmin && prefController.servicesLdapAuthentication ?
                checkLdapAuth(user, details.password, prefController) :
                checkInternalAuth(user, details.password)
        if (errorMessage) {
            updateLoginAttemptNumber(user, prefController.numberOfLoginAttempts)
            LoginResponseDTO content = createAuthenticationContent(INVALID_CREDENTIALS, errorMessage)
            throwUnauthorizedException(content)
        }

        //password to check
        String passToCheck = details.newPassword ?: details.password


        boolean complexityRequired = prefController.passwordComplexity

        //checked forced password update
        if (user.passwordUpdateRequired && !details.newPassword) {
            LoginResponseDTO content = createAuthenticationContent(FORCED_PASSWORD_UPDATE, errorMessage)
            content.passwordComlexity = complexityRequired
            throwUnauthorizedException(content)
        }

        //check password complexity
        errorMessage = validateUserPassword(user.email, user.login, passToCheck, complexityRequired)
        if (errorMessage) {
            LoginResponseDTO content = createAuthenticationContent(WEAK_PASSWORD, errorMessage)
            content.passwordComlexity = complexityRequired
            throwUnauthorizedException(content)
        }

        //check requirements for outdate password
        if (!details.newPassword) {
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


        if (user.sessionId != null && user.lastAccess.after(prefController.timeoutThreshold) && !details.kickOut) {
            errorMessage = 'You are currently logged in from another session.'
            LoginResponseDTO content = createAuthenticationContent(CONCURRENT_SESSIONS_FOUND, errorMessage)
            throwUnauthorizedException(content)
        }

        if (details.kickOut) {
            sessionManager.doKickOut(user)
        }

        //check 2FA security options

        boolean noDataForTFA = !(user.token || (details.secretCode && details.token))

        switch (prefController.twoFactorAuthStatus) {
            case DISABLED:
                if (!details.skipTfa && noDataForTFA) {
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

        boolean cookieTokenValid = false
        //if user has totp token we need to verify it
        if (user.token) {
            String skipTotpCookie = getTotpCookie(request)

            if (!details.token && !skipTotpCookie) {
                LoginResponseDTO content = createAuthenticationContent(TOKEN_REQUIRED, 'Auth Token required')
                throwUnauthorizedException(content)
            }

            cookieTokenValid = validateCookieToken(skipTotpCookie, passToCheck, user.token)

            if (!cookieTokenValid) {
                if (!details.token) {
                    LoginResponseDTO content = createAuthenticationContent(TOKEN_REQUIRED, 'Auth Token required')
                    throwUnauthorizedException(content)
                }

                if ((errorMessage = checkTokenAuth(user, details.token))) {
                    LoginResponseDTO content = createAuthenticationContent(INVALID_OR_EXPIRED_TOKEN, errorMessage)
                    throwUnauthorizedException(content)
                }
            }
        } else if (details.secretCode && details.token) {
            if (totpService.checkToken(details.secretCode, details.token)) {
                user.token = details.secretCode
            } else {
                LoginResponseDTO content = createAuthenticationContent(INVALID_OR_EXPIRED_TOKEN, errorMessage)
                throwUnauthorizedException(content)
            }
        }

        sessionManager.createUserSession(user, prefController.timeoutSec, request)

        prefController.updateReleaseNotesMap(user)

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

        return createAuthenticationContent(LOGIN_SUCCESSFUL)
    }

    @Override
    String logout() {
        sessionManager.logout(request)
        return ''
    }

    private static void throwUnauthorizedException(LoginResponseDTO content) {
        throw new ClientErrorException(Response.status(Response.Status.UNAUTHORIZED).entity(content).build())
    }
}
