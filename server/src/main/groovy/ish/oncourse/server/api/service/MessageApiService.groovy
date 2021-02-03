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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import groovy.text.Template
import ish.common.types.EnrolmentStatus
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.MessageDao
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.messaging.SMTPService
import ish.oncourse.server.api.model.RecipientGroupModel
import ish.oncourse.server.api.model.RecipientsModel
import static ish.oncourse.server.api.v1.function.MessageFunctions.getEntityTransformationProperty
import static ish.oncourse.server.api.v1.function.MessageFunctions.getFindContactProperty
import ish.oncourse.server.api.v1.model.RecipientTypeDTO
import ish.oncourse.server.api.v1.model.RecipientsDTO
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.WaitingList
import static ish.oncourse.server.messaging.MessageService.createMessagePerson
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.EMPTY
import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery
import static ish.oncourse.server.api.v1.function.MessageFunctions.getRecipientsListFromEntity
import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly
import ish.oncourse.server.api.v1.model.SearchQueryDTO
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.api.v1.model.MessageDTO
import ish.oncourse.server.api.v1.model.MessageTypeDTO
import ish.oncourse.server.api.v1.model.SendMessageRequestDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.entity.mixins.MessageMixin
import ish.oncourse.server.scripting.api.TemplateService
import ish.oncourse.server.users.SystemUserService
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.ZoneOffset

class MessageApiService extends TaggableApiService<MessageDTO, Message, MessageDao> {
    private static final Logger logger = LogManager.logger
    private static final String CREATED_SUCCESS = "Messages created successfully"
    private static final int BATCH_SIZE = 50


    @Inject private AqlService aql

    @Inject private ICayenneService cayenneService

    @Inject private EmailTemplateApiService templateApiService

    @Inject private TemplateService templateService

    @Inject private SystemUserService systemUserService

    @Inject private SMTPService smtpService


    @Override
    Class<Message> getPersistentClass() {
        return Message
    }

    @Override
    MessageDTO toRestModel(Message message) {
        return new MessageDTO().with { messageDTO ->
            messageDTO.id = message.id
            messageDTO.createdOn = message.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            messageDTO.modifiedOn = message.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            messageDTO.creatorKey = message.creatorKey
            messageDTO.sentToContactFullname = MessageMixin.getRecipientsString(message)
            messageDTO.subject = message.emailSubject
            messageDTO.message = message.emailBody
            messageDTO.sms = message.smsText
            messageDTO.postDescription = message.postDescription
            messageDTO.htmlMessage = message.emailHtmlBody
            messageDTO
        }
    }

    @Override
    Message toCayenneModel(MessageDTO messageDTO, Message message) {
        // Is not applicable for this entity
        return null
    }

    @Override
    void validateModelBeforeSave(MessageDTO messageDTO, ObjectContext context, Long id) {
        // Is not applicable for this entity
    }

    @Override
    void validateModelBeforeRemove(Message message) {
        // Is not applicable for this entity
    }

    void validateBeforeSend(String messageType, BigDecimal recipientsCount, SendMessageRequestDTO request) {
        logger.debug("Validate SendMessageRequest before send")
        if (StringUtils.isEmpty(messageType)) {
            validator.throwClientErrorException("messageType", "The message type shoud be specifed")
        }
        MessageTypeDTO messageTypeDTO = MessageTypeDTO.fromValue(messageType)

        if (!messageTypeDTO) {
            validator.throwClientErrorException("messageType", "Unrecognized message type.")
        }

        if (recipientsCount == null || BigDecimal.ZERO == recipientsCount) {
            validator.throwClientErrorException("recipientsCount", "You didn't choose any records")
        }

        if (request.templateId == null) {
            validator.throwClientErrorException("templateId", "The email template shoud be specifed")
        }

        if (request.entity == null) {
            validator.throwClientErrorException("entity", "The entity should be specified")
        }

        if (request.fromAddress == null && MessageTypeDTO.EMAIL == messageTypeDTO) {
            validator.throwClientErrorException("fromAddress", "The fromAddress should be specified")
        }
    }


    RecipientsDTO getRecipients(String entityName, String messageType, SearchQueryDTO request) {
        ObjectContext context = cayenneService.newReadonlyContext
        List<Long> entitiesIds = getEntityIds(entityName, request, context)
        MessageTypeDTO messageTypeDTO = MessageTypeDTO.fromValue(messageType)
        RecipientsModel model = getRecipientsModel(entityName, messageTypeDTO, entitiesIds)

        new RecipientsDTO().with { dto ->
            dto.students = new RecipientTypeDTO().with { typeDto ->
                typeDto.sendSize = model.students.suitableForSend.size().toBigDecimal()
                typeDto.suppressToSendSize = model.students.suppressToSend.size().toBigDecimal()
                typeDto.withoutDestinationSize = model.students.withoutDestination.size().toBigDecimal()
                typeDto
            }
            dto.activeStudents = new RecipientTypeDTO().with { typeDto ->
                typeDto.sendSize = model.activeStudents.suitableForSend.size().toBigDecimal()
                typeDto.suppressToSendSize = model.activeStudents.suppressToSend.size().toBigDecimal()
                typeDto.withoutDestinationSize = model.activeStudents.withoutDestination.size().toBigDecimal()
                typeDto
            }
            dto.withdrawnStudents = new RecipientTypeDTO().with { typeDto ->
                typeDto.sendSize = model.withdrawStudents.suitableForSend.size().toBigDecimal()
                typeDto.suppressToSendSize = model.withdrawStudents.suppressToSend.size().toBigDecimal()
                typeDto.withoutDestinationSize = model.withdrawStudents.withoutDestination.size().toBigDecimal()
                typeDto
            }
            dto.tutors = new RecipientTypeDTO().with { typeDto ->
                typeDto.sendSize = model.tutors.suitableForSend.size().toBigDecimal()
                typeDto.suppressToSendSize = model.tutors.suppressToSend.size().toBigDecimal()
                typeDto.withoutDestinationSize = model.tutors.withoutDestination.size().toBigDecimal()
                typeDto
            }
            dto.other = new RecipientTypeDTO().with { typeDto ->
                typeDto.sendSize = model.other.suitableForSend.size().toBigDecimal()
                typeDto.suppressToSendSize = model.other.suppressToSend.size().toBigDecimal()
                typeDto.withoutDestinationSize = model.other.withoutDestination.size().toBigDecimal()
                typeDto
            }
            dto
        }
    }


    RecipientsModel getRecipientsModel(String entityName, MessageTypeDTO messageType, List<Long> entitiesIds) {
        RecipientsModel recipientsModel = new RecipientsModel()

        Expression exp = null
        Property<Long> contactFindProperty = getFindContactProperty(entityName)
        if ( contactFindProperty != null ) {
            exp = contactFindProperty.in(entitiesIds)
        } else {
            validator.throwClientErrorException("entityName", "Impossible to create expression. Unrecognized entity name.")
        }

        switch (entityName) {
            case Voucher.ENTITY_NAME:
                exp.andExp(Contact.PRODUCT_ITEMS.outer().isNotNull())
            case Application.ENTITY_NAME:
            case Contact.ENTITY_NAME:
            case Invoice.ENTITY_NAME:
            case PaymentIn.ENTITY_NAME:
            case PaymentOut.ENTITY_NAME:
            case ProductItem.ENTITY_NAME:
            case Article.ENTITY_NAME:
            case Membership.ENTITY_NAME:
                addRecipientsToGroup(recipientsModel.students, exp.andExp(Contact.STUDENT.isNotNull()), messageType)
                addRecipientsToGroup(recipientsModel.tutors, exp.andExp(Contact.TUTOR.isNotNull()), messageType)
                addRecipientsToGroup(recipientsModel.other, exp.andExp(Contact.STUDENT.outer().isNull()).andExp(Contact.TUTOR.outer().isNull()), messageType)
                break
            case Enrolment.ENTITY_NAME:
            case WaitingList.ENTITY_NAME:
                addRecipientsToGroup(recipientsModel.students, exp, messageType)
                break
            case CourseClass.ENTITY_NAME:
                Expression extraExp = exp.andExp(Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.STATUS).in(EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS))
                addRecipientsToGroup(recipientsModel.activeStudents, extraExp, messageType)

                extraExp = exp.andExp(Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.STATUS).in(EnrolmentStatus.STATUSES_CANCELLATIONS))
                addRecipientsToGroup(recipientsModel.withdrawStudents, extraExp, messageType)

                exp = Contact.TUTOR.dot(Tutor.COURSE_CLASS_ROLES).dot(CourseClassTutor.COURSE_CLASS).dot(CourseClass.ID).in(entitiesIds)
                addRecipientsToGroup(recipientsModel.tutors, exp, messageType)
                break
            case Payslip.ENTITY_NAME:
                addRecipientsToGroup(recipientsModel.tutors, exp.andExp(Contact.TUTOR.isNotNull()), messageType)
                addRecipientsToGroup(recipientsModel.other, exp.andExp(Contact.STUDENT.outer().isNull()), messageType)
                break
            default:
                validator.throwClientErrorException("entityName", "Impossible to create response. Unrecognized entity name.")
        }
        recipientsModel
    }


    RecipientGroupModel addRecipientsToGroup(RecipientGroupModel type, Expression expression, MessageTypeDTO messageType) {
        List<Long> suitableForSendIds = new ArrayList<>()
        List<Long> suppresstoSendIds = new ArrayList<>()
        List<Long> noDestinationIds = new ArrayList<>()
        Property<Long> ID = Property.create("id", Long)
        ObjectSelect<Contact> query = ObjectSelect.query(Contact.class)

        ObjectContext context = cayenneService.newContext

        if (MessageTypeDTO.EMAIL == messageType) {

            noDestinationIds = query.column(ID)
                    .where(expression.andExp(Contact.EMAIL.isNull().orExp(Contact.EMAIL.eq(EMPTY).orExp(Contact.DELIVERY_STATUS_EMAIL.eq(6)))))
                    .select(context)
            suppresstoSendIds = query.column(ID)
                    .where(expression.andExp(Contact.ALLOW_EMAIL.isFalse(), Contact.EMAIL.isNotNull(), Contact.EMAIL.ne(EMPTY), Contact.DELIVERY_STATUS_EMAIL.lt(6)))
                    .select(context)
            suitableForSendIds = query.column(ID)
                    .where(expression.andExp(Contact.EMAIL.isNotNull(), Contact.EMAIL.ne(EMPTY), Contact.DELIVERY_STATUS_EMAIL.lt(6), Contact.ALLOW_EMAIL.isTrue()))
                    .select(context)

        } else if (MessageTypeDTO.SMS == messageType) {

            noDestinationIds = query.column(ID)
                    .where(expression.andExp(Contact.MOBILE_PHONE.isNull().orExp(Contact.MOBILE_PHONE.eq(EMPTY).orExp(Contact.DELIVERY_STATUS_SMS.eq(6)))))
                    .select(context)
            suppresstoSendIds = query.column(ID)
                    .where(expression.andExp(Contact.ALLOW_SMS.isFalse(), Contact.MOBILE_PHONE.isNotNull(), Contact.MOBILE_PHONE.ne(EMPTY), Contact.DELIVERY_STATUS_SMS.lt(6)))
                    .select(context)
            suitableForSendIds = query.column(ID)
                    .where(expression.andExp(Contact.MOBILE_PHONE.isNotNull(), Contact.MOBILE_PHONE.ne(EMPTY), Contact.DELIVERY_STATUS_SMS.lt(6), Contact.ALLOW_SMS.isTrue()))
                    .select(context)
        }

        type.withoutDestination = noDestinationIds
        type.suppressToSend = suppresstoSendIds
        type.suitableForSend = suitableForSendIds
        type
    }


    String sendMessage(String messageType, BigDecimal recipientsCount, SendMessageRequestDTO request) {
        logger.debug("Messaging starts")
        ObjectContext context = cayenneService.newContext
        validateBeforeSend(messageType, recipientsCount, request)
        MessageTypeDTO messageTypeDTO = MessageTypeDTO.fromValue(messageType)

        String entityName = request.entity
        List<Long> entitiesIds = getEntityIds(entityName, request.searchQuery, context)
        RecipientsModel recipientsModel = getRecipientsModel(entityName, messageTypeDTO, entitiesIds)
        List<Long> recipientsToSend = new ArrayList<>()

        addRecipientsToSend(recipientsToSend, recipientsModel.students, request.sendToStudents, request.sendToSuppressStudents)
        addRecipientsToSend(recipientsToSend, recipientsModel.activeStudents, request.sendToActiveStudents, request.sendToSuppressActiveStudents)
        addRecipientsToSend(recipientsToSend, recipientsModel.withdrawStudents, request.sendToWithdrawnStudents, request.sendToSuppressWithdrawnStudents)
        addRecipientsToSend(recipientsToSend, recipientsModel.tutors, request.sendToTutors, request.sendToSuppressTutors)
        addRecipientsToSend(recipientsToSend, recipientsModel.other, request.sendToOtherContacts, request.sendToSuppressOtherContacts)

        if (recipientsCount != recipientsToSend.size().toBigDecimal()){
            logger.error("A real recipients number doesn't equal specified. Specified: {}, Real: {}, MessageType: {}",
                    recipientsCount.toString(), recipientsToSend.size().toString(), messageType)
            validator.throwClientErrorException("recipientsCount", "A real recipients number doesn't equal specified. Specified: ${recipientsCount}, Real: ${recipientsToSend.size()}")
        }
        if (smtpService.email_batch != null && recipientsToSend.size() > smtpService.email_batch && MessageTypeDTO.EMAIL == messageTypeDTO) {
            logger.error("A recipients number higher than allowed by license. License: {}, Real: {}",
                    smtpService.email_batch, recipientsToSend.size().toString())
            if (smtpService.email_batch == 0) {
                validator.throwClientErrorException("recipientsCount", "Your license does not allow sending emails. " +
                        "Please, contact onCourse administrator to upgrade your plan.")
            }
            validator.throwClientErrorException("recipientsCount", "Your license does not allow sending more than ${smtpService.email_batch} emails in one batch. " +
                    "Please send in smaller batches or upgrade to a plan with a higher limit.")
        }

        SystemUser user = context.localObject(systemUserService.currentUser)

        EmailTemplate template = ObjectSelect.query(EmailTemplate.class)
                .where(EmailTemplate.ID.eq(request.templateId).andExp(EmailTemplate.TYPE.eq(messageTypeDTO.dbType)))
                .selectOne(context)

        if (template == null) {
            logger.error("The message template didn't find out. MessageType: {}, Id: {}", messageTypeDTO.toString(), request.templateId)
            validator.throwClientErrorException("templateId", "The message template didn't find out.")
        }

        Map<String, Object> plainBindings = templateApiService.getVariablesMap(request.variables, template)
        Map<String, Object> htmlBindings = templateApiService.getVariablesMap(request.variables, template, true)

        template.options.each { opt ->
            plainBindings.put(opt.name, opt.objectValue)
            htmlBindings.put(opt.name, opt.objectValue)
        }

        Class<? extends CayenneDataObject> clazz = EntityUtil.entityClassForName(template.entity.capitalize())
        Property<Long> property = getEntityTransformationProperty(entityName, template.entity)
        if (!property) {
            validator.throwClientErrorException("templateId", "$template.name is not valid for $entityName records")
        }

        String entityVarName = StringUtils.uncapitalize(template.entity)

        htmlBindings = templateService.putBaseBindings(htmlBindings)
        plainBindings = templateService.putBaseBindings(plainBindings)

        if (validateOnly.get()) {
            CayenneDataObject entity = null
            Contact recipient = SelectById.query(Contact, recipientsToSend[0]).selectOne(context)
            if (template.entity.equalsIgnoreCase("Contact")) {
                entity = recipient
            } else {
                entity = ObjectSelect.query(clazz).where(property.in(entitiesIds[0]))
                        .select(context)[0]
            }

            if (!entity) {
                validator.throwClientErrorException("templateId", "$template.name is not valid for these records. No message won't be created.")
            }

            switch(messageTypeDTO) {
                case MessageTypeDTO.EMAIL:
                    htmlBindings.put(templateService.RECORD, entity)
                    htmlBindings.put(entityVarName, entity)
                    htmlBindings.put(templateService.TO, recipient)
                    plainBindings.put(templateService.RECORD, entity)
                    plainBindings.put(entityVarName, entity)
                    plainBindings.put(templateService.TO, recipient)
                    templateService.addSubject(template, plainBindings, htmlBindings)
                    return templateService.renderHtml(template, htmlBindings)
                case MessageTypeDTO.SMS:
                    plainBindings.put(templateService.RECORD, entity)
                    plainBindings.put(templateService.TO, recipient)
                    return templateService.renderPlain(template, plainBindings)
            }
        }

        Template plainTemplate = templateService.createPlainTemplate(template)
        Template htmlTemplate = templateService.createHtmlTemplate(template)

        Closure<Message> fillMessage
        switch(messageTypeDTO) {
            case MessageTypeDTO.EMAIL:
                fillMessage = { Message message ->
                    message.emailSubject  = templateService.addSubject(template, plainBindings, htmlBindings)
                    message.emailBody     = plainTemplate ? plainTemplate.make(plainBindings).toString() : null
                    message.emailHtmlBody = htmlTemplate ? htmlTemplate.make(htmlBindings).toString() : null
                    message.emailFrom = request.fromAddress
                    message
                }
                break
            case MessageTypeDTO.SMS:
                fillMessage = { Message message ->
                    message.smsText = plainTemplate ? plainTemplate.make(plainBindings).toString() : null
                    message
                }
                break
        }

        boolean templateForContact = false
        def iterator = ObjectSelect.query(clazz).where(property.in(entitiesIds)).batchIterator(context, BATCH_SIZE)
        if (template.entity.equalsIgnoreCase("Contact")) {
            templateForContact = true
            iterator = ObjectSelect.query(Contact).where(Contact.ID.in(recipientsToSend)).batchIterator(context, BATCH_SIZE)
        }
        iterator.forEach() { batch ->
            ObjectContext batchContext = cayenneService.newContext
            batch.each { CayenneDataObject entity ->
                List<Contact> recipients = getRecipientsListFromEntity(entity)

                recipients.each { recipient ->
                    if (templateForContact || recipientsToSend.contains(recipient.id)) {
                        plainBindings.put(templateService.RECORD, entity)
                        plainBindings.put(entityVarName, entity)
                        htmlBindings.put(templateService.RECORD, entity)
                        htmlBindings.put(entityVarName, entity)
                        plainBindings.put(templateService.TO, recipient)
                        htmlBindings.put(templateService.TO, recipient)

                        Message message = batchContext.newObject(Message.class)
                        message.createdBy = batchContext.localObject(user)
                        fillMessage(message)

                        createMessagePerson(message, batchContext.localObject(recipient), template.type)
                    }
                }
            }
            batchContext.commitChanges()
        }

        iterator.close()

        return CREATED_SUCCESS
    }


    List<Long> getEntityIds(String entityName, SearchQueryDTO request, ObjectContext context) {
        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(entityName)
        ObjectSelect objectSelect = ObjectSelect.query(clzz)

        parseSearchQuery(objectSelect, context, aql, entityName, request.search, request.filter, request.tagGroups)
                .column(Property.create("id", Long))
                .select(context)
    }


    static List<Long> addRecipientsToSend(List<Long> recipients, RecipientGroupModel group, boolean suitableForSend, boolean suppressToSend) {
        if (suitableForSend) {
            recipients.addAll(group.suitableForSend)
            if (suppressToSend) {
                recipients.addAll(group.suppressToSend)
            }
        }
        recipients
    }


    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
