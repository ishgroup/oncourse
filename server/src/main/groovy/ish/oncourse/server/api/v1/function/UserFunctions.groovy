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
import groovy.transform.CompileStatic
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.messaging.MailDeliveryService
import ish.oncourse.server.scripting.api.MailDeliveryParamBuilder
import ish.oncourse.server.scripting.api.SmtpParameters

import javax.mail.MessagingException

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.UserDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import ish.security.AuthenticationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.ZoneOffset

import static ish.util.SecurityUtil.generateUserInvitationToken

@CompileStatic
class UserFunctions {

    private static final Logger logger = LogManager.getLogger(UserFunctions)

    static UserDTO toRestUser(SystemUser systemUser) {
        new UserDTO().with { user ->
            user.id = systemUser.id
            user.active = systemUser.isActive
            user.login = systemUser.login
            user.firstName = systemUser.firstName
            user.lastName = systemUser.lastName
            user.email = systemUser.email
            Site centre = systemUser.defaultAdministrationCentre
            user.administrationCentre = centre.id
            user.lastLoggedIn = systemUser.lastLoginOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            user.accessEditor = systemUser.canEditCMS
            user.tfaEnabled = systemUser.token != null
            user.passwordLastChanged = systemUser.passwordLastChanged
            user.admin = systemUser.isAdmin
            if (!systemUser.aclRoles.empty) {
                user.role = systemUser.aclRoles[0].id
            }
            user.passwordUpdateRequired = systemUser.passwordUpdateRequired
            user.inviteAgain = systemUser.password == null
            user.created = systemUser.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            user.modified = systemUser.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            user
        }
    }

    static SystemUser toDbSystemUser(ObjectContext context, UserDTO user) {
        SystemUser systemUser = user.id ?
                getRecordById(context, SystemUser, user.id, SystemUser.DEFAULT_ADMINISTRATION_CENTRE.joint(), SystemUser.ACL_ROLES.joint()) :
                context.newObject(SystemUser)

        systemUser.isActive = user.active
        systemUser.login = user.login
        systemUser.firstName = user.firstName
        systemUser.lastName = user.lastName
        systemUser.email = user.email
        systemUser.defaultAdministrationCentre = getRecordById(context, Site, user.administrationCentre)
        systemUser.canEditCMS = user.accessEditor
        if (user.password) {
            systemUser.password = AuthenticationUtil.generatePasswordHash(user.password)
            systemUser.passwordLastChanged = LocalDate.now()
        }
        systemUser.isAdmin = user.admin
        new ArrayList<ACLRole>(systemUser.getAclRoles()).each { systemUser.removeFromAclRoles(it) }
        if (!user.admin) {
            systemUser.addToAclRoles(getRecordById(context, ACLRole, user.role))
        }
        systemUser.passwordUpdateRequired = user.passwordUpdateRequired
        systemUser
    }

    static String validateUserPassword(String email, String login, String password, boolean complexity) {
        complexity ? validateComplexPassword(password) : validatePassword(email, login, password)
    }

    static String validatePassword(String email, String login, String newPassword) {
        if (email == newPassword) {
            return 'You must enter password which is different to email.'
        }

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

    static ValidationErrorDTO validateForUpdate(ObjectContext context, UserDTO user, boolean complexity) {
        if (user.id && !SelectById.query(SystemUser, user.id).selectOne(context)) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.EMAIL.name, "User doesn't exist.")
        }

        if (!user.email?.trim()) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.EMAIL.name, "Email should be set.")
        } else if (user.email.length() > 128) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.EMAIL.name, "The maximum email length is 128.")
        }


        if (!user.firstName?.trim()) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.FIRST_NAME.name, "First name should be set.")
        } else if (user.firstName.length() > 100) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.FIRST_NAME.name, "The maximum first name length is 100.")
        }

        if (!user.lastName?.trim()) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.LAST_NAME.name, "Last name should be set.")
        } else if (user.lastName.length() > 100) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.LAST_NAME.name, "The maximum last name length is 100.")
        }

        if (user.id && !user.inviteAgain) {
            if (user.password) {
                String errorMessage = validateUserPassword(user.email, user.login, user.password, complexity)
                if (errorMessage) {
                    return new ValidationErrorDTO(user.id?.toString(), SystemUser.PASSWORD.name, errorMessage)
                }
            }
        } else {
            if (user.password) {
                return new ValidationErrorDTO(user.id?.toString(), SystemUser.PASSWORD.name, "Password cannot be created for new users.")
            }
        }

        SystemUser systemUser = UserDao.getByEmail(context, user.email)


        if (systemUser != null && systemUser.id != user.id) {
            return new ValidationErrorDTO(user.id?.toString(), SystemUser.EMAIL.name, "Email should be unique.")
        }

        Site site = ObjectSelect.query(Site)
                .where(Site.ID.eq(user.administrationCentre))
                .selectOne(context)
        if (!site) {
            return new ValidationErrorDTO(user.id?.toString(), 'administrationCentre', "Administration centre doesn't exist.")
        }

        if (!user.admin && !user.role) {
            return new ValidationErrorDTO(user.id?.toString(), 'role', "Not admin users can not be without role.")
        }

        if (user.role) {
            ACLRole aclRole = ObjectSelect.query(ACLRole)
                    .where(ACLRole.ID.eq(user.role))
                    .selectOne(context)
            if (!aclRole) {
                return new ValidationErrorDTO(user.id?.toString(), 'role', "User role doesn't exist.")
            }
        }

        null
    }

    static String sendInvitationEmailToNewSystemUser(SystemUser currentUser, SystemUser newUser,
                                                  PreferenceController preferenceController,
                                                  MailDeliveryService mailDeliveryService,
                                                  String collegeKey) throws MessagingException {
        String messageStart = currentUser ? "${currentUser.fullName} has given you access" : "You were provided access"
        String invitationToken = generateUserInvitationToken()
        String subject = "Welcome to onCourse!"
        String messageText =
                """
${messageStart} to the ish onCourse application for https://${collegeKey}.cloud.oncourse.cc. Please click here to accept this invitation.

https://${collegeKey}.cloud.oncourse.cc/invite/${invitationToken}

This invitation will expire in 24 hours.
                """

        SmtpParameters parameters = new SmtpParameters(preferenceController.emailFromAddress, preferenceController.emailFromName, newUser.email, subject, messageText)
        mailDeliveryService.sendEmail(MailDeliveryParamBuilder.valueOf(parameters).build())

        return invitationToken
    }
}
