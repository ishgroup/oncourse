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
import com.nulabinc.zxcvbn.Strength
import com.nulabinc.zxcvbn.Zxcvbn
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.messaging.MailDeliveryService
import ish.oncourse.server.scripting.api.MailDeliveryParamBuilder
import ish.oncourse.server.scripting.api.SmtpParameters
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.UserFunctions.toDbSystemUser
import static ish.oncourse.server.api.v1.function.UserFunctions.toRestUser
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
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.RandomStringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate

import static ish.util.SecurityUtil.generateUserInvitationToken

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

    @Override
    List<UserDTO> get() {
        ObjectSelect.query(SystemUser)
                .prefetch(SystemUser.ACL_ROLES.joint())
                .prefetch(SystemUser.DEFAULT_ADMINISTRATION_CENTRE.joint())
                .orderBy(SystemUser.IS_ACTIVE.desc())
                .orderBy(SystemUser.EMAIL.asc())
                .select(cayenneService.newContext)
                .collect { toRestUser(it) }
    }

    UserDTO getUserByInvitation(String invitationToken) {
        SystemUser dbUser = ObjectSelect.query(SystemUser)
                .where(SystemUser.INVITATION_TOKEN.eq(invitationToken))
                .selectOne(cayenneService.newContext)
        if (!dbUser) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('User not found').build())
        }
        if (LocalDate.now() > dbUser.invitationTokenExpiryDate) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('Sorry, but invitation was expired').build())
        }

        toRestUser(dbUser)
    }

    @Override
    Boolean requireComplexPass() {
        return preferenceController.getPasswordComplexity()
    }

    @Override
    String resetPassword(Long id) {
        ObjectContext context = cayenneService.newContext
        SystemUser user = getRecordById(context, SystemUser, id)
        if (!user) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('User not found').build())
        }

        String newPassword = RandomStringUtils.random(10, true, true)

        while (validateUserPassword(user.login, newPassword, preferenceController.passwordComplexity)) {
            newPassword = RandomStringUtils.random(10, true, true)
        }

        user.password = AuthenticationUtil.generatePasswordHash(newPassword)
        user.passwordLastChanged = LocalDate.now()
        context.commitChanges()

        newPassword
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
            String invitationToken = sendInvitationToNewUser(dbUser)
            dbUser.invitationToken = invitationToken
            dbUser.invitationTokenExpiryDate = new Date() + 1
        }

        context.commitChanges()
    }

    @Override
    void updatePassword(String password) {
        // This method doesn't have permission checking, see ServletFunctions.allowGetSecurityPrefencesAndUpdatePass(...)
        ObjectContext context = cayenneService.newContext

        SystemUser user = context.localObject(systemUserService.currentUser)

        String errorMessage = validateUserPassword(user.login, password, preferenceController.passwordComplexity)

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

    private String sendInvitationToNewUser(SystemUser user) {
        String collegeKey = licenseService.getSecurity_key()
        if (!collegeKey) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('College key is not set').build())
        }
        String invitationToken = generateUserInvitationToken()
        String subject = "Welcome to onCourse!"
        String messageText =
                """
                ${user.fullName} has given you access to the ish onCourse application for ${preferenceController.collegeName}. Please click here to accept this invitation.

                https://${collegeKey}.cloud.oncourse.cc/invite/${invitationToken}

                This invitation will expire in 24 hours.
                """

        SmtpParameters parameters = new SmtpParameters(preferenceController.emailFromAddress, preferenceController.emailFromName, user.email, subject, messageText)
        mailDeliveryService.sendEmail(MailDeliveryParamBuilder.valueOf(parameters).build())

        return invitationToken
    }
}
