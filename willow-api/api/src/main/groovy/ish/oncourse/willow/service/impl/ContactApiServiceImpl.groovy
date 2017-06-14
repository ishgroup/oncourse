package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.FieldConfigurationScheme
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.functions.CreateOrGetContact
import ish.oncourse.willow.functions.GetContactFields
import ish.oncourse.willow.functions.SubmitContactFields
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.ContactFieldsRequest
import ish.oncourse.willow.model.field.SubmitFieldsRequest
import ish.oncourse.willow.model.web.Contact
import ish.oncourse.willow.model.web.ContactId
import ish.oncourse.willow.model.web.CreateContactParams
import ish.oncourse.willow.service.ContactApi
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SelectById
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@CompileStatic
class ContactApiServiceImpl implements ContactApi{

    final static  Logger logger = LoggerFactory.getLogger(ContactApiServiceImpl.class)

    private CayenneService cayenneService
    private CollegeService collegeService
    
    @Inject
    ContactApiServiceImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    @Override
    ContactId createOrGetContact(CreateContactParams createContactParams) {
        CreateOrGetContact createOrGet = new CreateOrGetContact(params:createContactParams, context: cayenneService.newContext(), college: collegeService.college).perform()
        if (!createOrGet.validationError.formErrors.empty || !createOrGet.validationError.fieldsErrors.empty) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(createOrGet.validationError).build())
        } else {
            createOrGet.contactId
        }
    }

    @Override
    ContactFields getContactFields(ContactFieldsRequest contactFieldsRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        
        if (!contactFieldsRequest.contactId) {
            logger.error("contactId required, request param: $contactFieldsRequest")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contactId required')).build())
        }
        if (contactFieldsRequest.classIds.empty &&  contactFieldsRequest.productIds.empty) {
            logger.error("classesIds required, request param: $contactFieldsRequest")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'classesIds required')).build())
        }
        if (!contactFieldsRequest.fieldSet) {
            logger.error("fieldSet required, request param: $contactFieldsRequest")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'fieldSet required')).build())
        }
        ish.oncourse.model.Contact contact = new GetContact(context, college, contactFieldsRequest.contactId).get()
        
        List<CourseClass> classes = []
        
        if (!contactFieldsRequest.classIds.empty) {
            classes = (ObjectSelect.query(CourseClass)
                    .where(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, contactFieldsRequest.classIds))
                    & CourseClass.COLLEGE.eq(college))
                    .prefetch(CourseClass.COURSE.joint())
                    .prefetch(CourseClass.COURSE.dot(Course.FIELD_CONFIGURATION_SCHEME).joint())
                    .prefetch(CourseClass.COURSE.dot(Course.FIELD_CONFIGURATION_SCHEME).dot(FieldConfigurationScheme.ENROL_FIELD_CONFIGURATION).joint())
                    .prefetch(CourseClass.COURSE.dot(Course.FIELD_CONFIGURATION_SCHEME).dot(FieldConfigurationScheme.ENROL_FIELD_CONFIGURATION).dot(FieldConfiguration.FIELDS).joint())
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                    .cacheGroups(CourseClass.class.simpleName)
                    .select(context)
        }
        
        new GetContactFields(contact, classes, !contactFieldsRequest.productIds.empty, contactFieldsRequest.fieldSet, contactFieldsRequest.mandatoryOnly).contactFields
    }

    @Override
    void submitContactDetails(SubmitFieldsRequest contactFields) {
        ObjectContext context = cayenneService.newContext()
        if (!contactFields.contactId) {
            logger.error("contactId required, request param: $contactFields")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contactId required')).build())
        }

        ish.oncourse.model.Contact contact = SelectById.query(ish.oncourse.model.Contact, contactFields.contactId).selectOne(context)
        if (!contact) {
            logger.error("contact is not exist, request param: $contactFields")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contact is not exist')).build())
        }
        SubmitContactFields submit = new SubmitContactFields(objectContext: context).submitContactFields(contact, contactFields.fields)

        if (submit.errors.fieldsErrors.empty && submit.errors.formErrors.empty) {
            context.commitChanges()
        } else {
            logger.warn(" Vaidation error: $submit.errors")
            throw new BadRequestException(Response.status(400).entity(submit.errors).build())
        }
    }

    @Override
    Contact getContact(String studentUniqueIdentifier) {
        ish.oncourse.model.Contact contact = (ObjectSelect.query(ish.oncourse.model.Contact)
                .where(ish.oncourse.model.Contact.UNIQUE_CODE.eq(studentUniqueIdentifier)) & ish.oncourse.model.Contact.COLLEGE.eq(collegeService.college))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(ish.oncourse.model.Contact.class.simpleName)
                .selectOne(cayenneService.newContext())
        if (contact) {
            new Contact(id: contact.id.toString(),
                    firstName: contact.givenName,
                    lastName: contact.familyName,
                    email: contact.emailAddress,
                    uniqueIdentifier: studentUniqueIdentifier)
        } else {
            logger.error("There is no contact with uuid: ${studentUniqueIdentifier}")
            throw new IllegalArgumentException("There is no contact with uuid: ${studentUniqueIdentifier}")
        }

    }
}
