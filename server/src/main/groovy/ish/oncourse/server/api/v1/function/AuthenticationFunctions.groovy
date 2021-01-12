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

import com.nulabinc.zxcvbn.Zxcvbn
import com.warrenstrange.googleauth.GoogleAuthenticator
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.function.SecurityFunctions
import ish.oncourse.server.api.v1.model.LoginResponseDTO
import ish.oncourse.server.api.v1.model.LoginStatusDTO
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.SystemUser
import ish.security.AuthenticationUtil
import ish.security.LdapAuthConnection
import static ish.util.Constants.TOTP_COOKIE_NAME
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.naming.NamingException
import javax.naming.ldap.LdapName
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AuthenticationFunctions {

    private static final Logger logger = LogManager.getLogger(AuthenticationFunctions)

    private static final String DEMO_USERNAME ='demo'
    private static final String SEPARATOR_CHAR = ','


    static LoginResponseDTO createAuthenticationContent(LoginStatusDTO status, String errorMessage = null, String totpUrl = null) {
        new LoginResponseDTO().with { r ->
            r.loginStatus = status
            r.errorMessage = errorMessage
            r.passwordComlexity = false
            r.totpUrl = totpUrl
            r
        }
    }

    static SystemUser getSystemUserByLogin(String login, ObjectContext context, boolean disableInactiveAccounts) {
        ObjectSelect objectSelect = ObjectSelect.query(SystemUser)
                .where(SystemUser.LOGIN.eq(login).orExp(SystemUser.EMAIL.eq(login)))

        if (disableInactiveAccounts) {
            objectSelect.and(SystemUser.IS_ACTIVE.isTrue())
        }

        objectSelect.selectOne(context)
    }

    static void updateLoginAttemptNumber(SystemUser user, Integer allowedNumberOfAttempts, Integer newAttemptNumber = null) {
        user.loginAttemptNumber = newAttemptNumber ?: ++(user.loginAttemptNumber)

        if (allowedNumberOfAttempts <= user.loginAttemptNumber) {
            user.isActive = Boolean.FALSE
        }
        user.context.commitChanges()
    }

    static String checkLdapAuth(SystemUser user, String password, PreferenceController prefController) {
        try {
            LdapAuthConnection ldapConnection = LdapAuthConnection.valueOf(prefController)
            LdapName ldapUser = ldapConnection.findUser(user.email)

            if (!ldapUser) {
                return 'LDAP user not found.'
            }

            // check password
            if (!ldapConnection.authenticate(ldapUser, password)) {
                return 'LDAP password failed.'
            }

            // update internal password in onCourse DB if new LDAP password has changed
            // this is useful for willow CMS
            user.setPassword(AuthenticationUtil.generatePasswordHash(password))

            // if authorisation comes from LDAP then update the Roles in onCourse
            if (prefController.servicesLdapAuthorisation) {
                updateRoles(prefController, user, ldapConnection, ldapUser)
            }

            null
        } catch (NamingException e) {
            logger.warn(e)
            return "LDAP error occurred: $e.message"
        } catch (Exception e) {
            logger.warn(e)
            return "Cannot establish connection to LDAP: $e.message"
        }
    }

    private static void updateRoles(PreferenceController prefController, SystemUser user, LdapAuthConnection ldapConnection, LdapName ldapUser) {
        // remove all current roles for the given user
        user.aclRoles.each { user.removeFromAclRoles(it) }

        // then for each role see if the user is a member, adding it back if they are
        ObjectSelect.query(ACLRole)
                .select(user.context)
                .each { ACLRole role ->
            LdapName group = ldapConnection.findGroup(role.name)

            if (prefController.ldapGroupPosixStyle) {
                if (ldapConnection.authorisePosixUser(user.email, group)) {
                    user.addToAclRoles(role)
                }
            } else {
                if (ldapConnection.authorise(ldapUser, group)) {
                    user.addToAclRoles(role)
                }
            }
        }
    }

    static String checkInternalAuth(SystemUser user, String password) {

        if (AuthenticationUtil.checkPassword(password, user.password)) {
            if (AuthenticationUtil.upgradeEncoding(user.password)) {
                user.password = AuthenticationUtil.generatePasswordHash(password)
            }
            return null
        }

        return 'User or password incorrect.'
    }

    static String checkTokenAuth(SystemUser user, Integer token) {
        if (!user.token) {
            // we skip this authentication since the user isn't using a token
            return null
        }

        if (new GoogleAuthenticator().authorize(user.getToken(), token)) {
            return null
        }

        // if all else fails, let's check the scratch codes
        String[] scratchCodes = StringUtils.split(user.tokenScratchCodes, SEPARATOR_CHAR)
        String scratchCode = scratchCodes.find { it == token }
        if (scratchCode) {
            // success, this one matched so we'll throw it away now
            ArrayUtils.removeElement(scratchCodes, scratchCode)
            user.tokenScratchCodes = scratchCodes.join(SEPARATOR_CHAR)
            return null
        }

        'Invalid token'
    }

    static String validatePassword(String login, String newPassword) {
        if (login == newPassword) {
            return 'You must enter password which is different to login.'
        }

        if (newPassword?.length() < 5) {
            return 'You must enter a password that is at least 5 characters long.'
        }

        null
    }

    static String validateComplexPassword(String password) {
        new Zxcvbn().measure(password).score < 2 ? 'Password does not satisfy complexity restrictions.' : null
    }

    static boolean validateCookieToken(String cookieToken, String password, String secret) {
        if (cookieToken) {
            String expiry = cookieToken.replaceAll('expiryDateTime=(.+)&secure=(.+)', '$1')
            String secure = cookieToken.replaceAll('expiryDateTime=(.+)&secure=(.+)', '$2')
            if (SecurityFunctions.generateHashString(expiry, password + secret) == secure) {
                try {
                    LocalDateTime value = LocalDateTime.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    return value.isAfter(LocalDateTime.now())
                } catch (DateTimeParseException e) {
                    logger.warn(e)
                }
            }
        }
        false
    }

    static String generateCookieValue(String password, String secret, Integer hours) {
        String expiry = LocalDateTime.now().plusHours(hours).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        String secure = SecurityFunctions.generateHashString(expiry, password + secret)
        "expiryDateTime=$expiry&secure=$secure"
    }

    static void addTotpCookie(HttpServletResponse response, String value, Integer hours) {
        int ageInSeconds = hours * 60 * 60
        Cookie cookie = new Cookie(TOTP_COOKIE_NAME, value)
        cookie.path = '/'
        cookie.maxAge = ageInSeconds
        cookie.httpOnly = false
        cookie.secure = true
        response.addCookie(cookie)
    }

    static String getTotpCookie(HttpServletRequest request) {
        String totpCookie = request.getHeader(TOTP_COOKIE_NAME)
        if (!totpCookie) {
            totpCookie = request.getCookies()?.find { it.name == TOTP_COOKIE_NAME }?.value
        }
        totpCookie
    }
}
