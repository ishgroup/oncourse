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
import com.google.inject.Provider
import com.nulabinc.zxcvbn.Strength
import com.nulabinc.zxcvbn.Zxcvbn
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.http.HttpFactory
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.messaging.MailDeliveryService

import javax.mail.MessagingException

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.UserFunctions.sendInvitationEmailToNewSystemUser
import static ish.oncourse.server.api.v1.function.UserFunctions.toDbSystemUser
import static ish.oncourse.server.api.v1.function.UserFunctions.toRestUser
import static ish.oncourse.server.api.v1.function.UserFunctions.toRestUserMinimized
import static ish.oncourse.server.api.v1.function.UserFunctions.validateForUpdate
import static ish.oncourse.server.api.v1.function.UserFunctions.validateUserPassword
import ish.oncourse.server.api.v1.model.PasswordComplexityDTO
import ish.oncourse.server.api.v1.model.UserDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.UserApi
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.users.SystemUserService
import ish.security.AuthenticationUtil
import org.apache.cayenne.ObjectContext

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate


class UserApiImpl implements UserApi {

    @Inject
    private ICayenneService cayenneService
    @Inject
    private LicenseService licenseService
    @Inject
    private SystemUserService systemUserService
    @Inject
    private MailDeliveryService mailDeliveryService
    @Inject
    private PreferenceController preferenceController
    @Inject
    private Provider<HttpFactory> httpFactoryProvider
    
    @Override
    List<UserDTO> get() {
        UserDao.getList(cayenneService.newContext)
                .collect { toRestUser(it) }
    }

    @Override
    List<UserDTO> getActive() {
        UserDao.getActive(cayenneService.newContext)
                .collect { toRestUserMinimized(it) }
    }

    String getUserEmailByInvitation(String invitationToken) {
        SystemUser dbUser = getUserByInvitation(invitationToken)
        dbUser.email
    }

    void createPassword(String invitationToken, String password) {
        SystemUser dbUser = getUserByInvitation(invitationToken)
        String errorMessage = validateUserPassword(dbUser.email, dbUser.login, password, preferenceController.passwordComplexity)
        if (errorMessage) {
            ValidationErrorDTO error = new ValidationErrorDTO(dbUser.id?.toString(), SystemUser.PASSWORD.name, errorMessage)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        dbUser.password = AuthenticationUtil.generatePasswordHash(password)
        dbUser.passwordLastChanged = LocalDate.now()
        dbUser.invitationToken = null
        dbUser.invitationTokenExpiryDate = null

        dbUser.context.commitChanges()
    }

    @Override
    Boolean requireComplexPass() {
        return preferenceController.getPasswordComplexity()
    }

    @Override
    void resetPassword(Long id) {
        ObjectContext context = cayenneService.newContext
        SystemUser user = getRecordById(context, SystemUser, id)
        if (!user) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('User not found').build())
        }
        sendInvitationToUser(user)
        user.password = null
        user.passwordLastChanged = null
        user.loginAttemptNumber = 0

        context.commitChanges()
    }

    @Override
    void update(UserDTO user) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateForUpdate(context, user, preferenceController.passwordComplexity)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        SystemUser dbUser = toDbSystemUser(context, user)
        if (!user.id || user.inviteAgain) {
            sendInvitationToUser(dbUser)
        }

        context.commitChanges()
    }

    @Override
    void updatePassword(String password) {
        // This method doesn't have permission checking, see ServletFunctions.allowGetSecurityPrefencesAndUpdatePass(...)
        ObjectContext context = cayenneService.newContext

        SystemUser user = context.localObject(systemUserService.currentUser)

        String errorMessage = validateUserPassword(user.email, user.login, password, preferenceController.passwordComplexity)

        if (errorMessage) {
            ValidationErrorDTO error = new ValidationErrorDTO(
                    preferenceController.passwordComplexity ? 'complexPassword' : 'weakPassword',
                    user.PASSWORD.name,
                    errorMessage)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        user.password = AuthenticationUtil.generatePasswordHash(password)
        user.passwordLastChanged = LocalDate.now()
        context.commitChanges()
    }

    @Override
    PasswordComplexityDTO checkPassword(String password) {
        Strength strength = new Zxcvbn().measure(password)

        new PasswordComplexityDTO().with { pc ->
            pc.score = strength.score
            if (score <= 2) {
                pc.feedback = strength.feedback.warning ?: strength.feedback.suggestions[0]
            }
            pc
        }
    }

    @Override
    void disableTFA(Long id) {
        ObjectContext context = cayenneService.newContext
        SystemUser user = getRecordById(context, SystemUser, id)
        if (!user) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('User not found').build())
        }

        user.token = null
        user.tokenScratchCodes

        context.commitChanges()
    }

    private SystemUser getUserByInvitation(String invitationToken) {
        SystemUser dbUser = UserDao.getByInvitation(cayenneService.newContext, invitationToken)
        if (!dbUser) {
            ValidationErrorDTO error = new ValidationErrorDTO(invitationToken, SystemUser.INVITATION_TOKEN.name, 'User not found')
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        if (new Date() > dbUser.invitationTokenExpiryDate) {
            ValidationErrorDTO error = new ValidationErrorDTO(dbUser.id?.toString(), SystemUser.INVITATION_TOKEN_EXPIRY_DATE.name, 'Sorry, but invitation was expired')
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        dbUser
    }

    private void sendInvitationToUser(SystemUser whoBeChanged) {
        SystemUser whoChange = systemUserService.currentUser
        String hostName = licenseService.currentHostName
        HttpFactory httpFactory = httpFactoryProvider.get() 
        try {
            whoBeChanged.invitationToken = sendInvitationEmailToNewSystemUser(whoChange, whoBeChanged, preferenceController, mailDeliveryService, hostName, httpFactory.ip, httpFactory.port)
        } catch (MessagingException | IllegalArgumentException ex) {
            ValidationErrorDTO error = new ValidationErrorDTO()
            error.setErrorMessage(ex.message)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        whoBeChanged.invitationTokenExpiryDate = new Date() + 1
    }
}
