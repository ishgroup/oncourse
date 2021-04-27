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

    static SystemUser toDbSystemUser(ObjectContext context, UserDTO dtoModel) {
        SystemUser dbModel = dtoModel.id ?
                getRecordById(context, SystemUser, dtoModel.id, SystemUser.DEFAULT_ADMINISTRATION_CENTRE.joint(), SystemUser.ACL_ROLES.joint()) :
                context.newObject(SystemUser)

        dbModel.isActive = dtoModel.active
        dbModel.loginAttemptNumber = dtoModel.active ? 0 : dbModel.loginAttemptNumber
        dbModel.login = dtoModel.login
        dbModel.firstName = dtoModel.firstName
        dbModel.lastName = dtoModel.lastName
        dbModel.email = dtoModel.email
        dbModel.defaultAdministrationCentre = getRecordById(context, Site, dtoModel.administrationCentre)
        dbModel.canEditCMS = dtoModel.accessEditor
        if (dtoModel.password) {
            dbModel.password = AuthenticationUtil.generatePasswordHash(dtoModel.password)
            dbModel.passwordLastChanged = LocalDate.now()
        }
        dbModel.isAdmin = dtoModel.admin
        new ArrayList<ACLRole>(dbModel.getAclRoles()).each { dbModel.removeFromAclRoles(it) }
        if (!dtoModel.admin) {
            dbModel.addToAclRoles(getRecordById(context, ACLRole, dtoModel.role))
        }
        dbModel.passwordUpdateRequired = dtoModel.passwordUpdateRequired
        dbModel
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

    static ValidationErrorDTO validateForUpdate(ObjectContext context, UserDTO dtoModel, boolean complexity) {
        if (dtoModel.id && !SelectById.query(SystemUser, dtoModel.id).selectOne(context)) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.EMAIL.name, "User doesn't exist.")
        }

        if (!dtoModel.email?.trim()) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.EMAIL.name, "Email should be set.")
        } else if (dtoModel.email.length() > 128) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.EMAIL.name, "The maximum email length is 128.")
        }


        if (!dtoModel.firstName?.trim()) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.FIRST_NAME.name, "First name should be set.")
        } else if (dtoModel.firstName.length() > 100) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.FIRST_NAME.name, "The maximum first name length is 100.")
        }

        if (!dtoModel.lastName?.trim()) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.LAST_NAME.name, "Last name should be set.")
        } else if (dtoModel.lastName.length() > 100) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.LAST_NAME.name, "The maximum last name length is 100.")
        }

        if (dtoModel.id && !dtoModel.inviteAgain) {
            if (dtoModel.password) {
                String errorMessage = validateUserPassword(dtoModel.email, dtoModel.login, dtoModel.password, complexity)
                if (errorMessage) {
                    return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.PASSWORD.name, errorMessage)
                }
            }
        } else {
            if (dtoModel.password) {
                return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.PASSWORD.name, "Password cannot be created for new users.")
            }
        }

        SystemUser dbModel = UserDao.getByEmail(context, dtoModel.email)


        if (dbModel != null && dbModel.id != dtoModel.id) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), SystemUser.EMAIL.name, "Email should be unique.")
        }

        Site site = ObjectSelect.query(Site)
                .where(Site.ID.eq(dtoModel.administrationCentre))
                .selectOne(context)
        if (!site) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), 'administrationCentre', "Administration centre doesn't exist.")
        }

        if (!dtoModel.admin && !dtoModel.role) {
            return new ValidationErrorDTO(dtoModel.id?.toString(), 'role', "Not admin users can not be without role.")
        }

        if (dtoModel.role) {
            ACLRole aclRole = ObjectSelect.query(ACLRole)
                    .where(ACLRole.ID.eq(dtoModel.role))
                    .selectOne(context)
            if (!aclRole) {
                return new ValidationErrorDTO(dtoModel.id?.toString(), 'role', "User role doesn't exist.")
            }
        }

        null
    }

    static void updateLoginAttemptNumber(SystemUser user, Integer allowedNumberOfAttempts, Integer newAttemptNumber = null) {
        user.loginAttemptNumber = newAttemptNumber ?: ++(user.loginAttemptNumber)

        if (allowedNumberOfAttempts <= user.loginAttemptNumber) {
            user.isActive = Boolean.FALSE
        }
    }


    static String sendInvitationEmailToNewSystemUser(SystemUser currentUser, SystemUser newUser,
                                                     PreferenceController preferenceController,
                                                     MailDeliveryService mailDeliveryService,
                                                     String collegeKey, String host, Integer port) throws MessagingException {
        if (host == null) {
            host = '0.0.0.0'
        }
        
        if (port == null) {
            port = 8182
        }
        
        String serverAddress = collegeKey ? "https://${collegeKey}.cloud.oncourse.cc" : "https://${host}:${port}"
                
        String messageStart = currentUser ? "${currentUser.fullName} has given you access" : "You were provided access"
        String invitationToken = generateUserInvitationToken()
        String subject = "Welcome to onCourse!"
        String messageText =
                """
${messageStart} to the ish onCourse application for ${serverAddress} . Please click here to accept this invitation.

${serverAddress}/invite/${invitationToken}

This invitation will expire in 24 hours.
                """

        SmtpParameters parameters = new SmtpParameters(preferenceController.emailFromAddress, preferenceController.emailFromName, newUser.email, subject, messageText)
        mailDeliveryService.sendEmail(MailDeliveryParamBuilder.valueOf(parameters).build())

        return invitationToken
    }
}
