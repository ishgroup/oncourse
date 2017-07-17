package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.FieldConfigurationScheme
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.functions.CheckParent
import ish.oncourse.willow.functions.CreateOrGetContact
import ish.oncourse.willow.functions.CreateParentChildrenRelation
import ish.oncourse.willow.functions.GetContactFields
import ish.oncourse.willow.functions.SubmitContactFields
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.functions.concession.AddConcession
import ish.oncourse.willow.functions.concession.GetConcessionTypes
import ish.oncourse.willow.functions.concession.GetContactConcessions
import ish.oncourse.willow.model.checkout.CreateParentChildrenRequest
import ish.oncourse.willow.model.checkout.concession.Concession
import ish.oncourse.willow.model.checkout.concession.ConcessionType
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.ValidationError
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
    void createParentChildrenRelation(CreateParentChildrenRequest request) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        CreateParentChildrenRelation createRelation = new CreateParentChildrenRelation(college, context, request).create()

        if (createRelation.error) {
            context.rollbackChanges()
            logger.error("Can not create parent children relation, college id: ${college.id}, request: ${request}")
            throw new BadRequestException(Response.status(400).entity(createRelation.error).build())
        } else {
            context.commitChanges()
        }
    }

    @Override
    ContactFields getContactFields(ContactFieldsRequest contactFieldsRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        
        if (contactFieldsRequest.classIds.empty &&  contactFieldsRequest.productIds.empty) {
            logger.error("classesIds required, request param: $contactFieldsRequest")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'classesIds required')).build())
        }
        if (!contactFieldsRequest.fieldSet) {
            logger.error("fieldSet required, request param: $contactFieldsRequest")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'fieldSet required')).build())
        }
        
        ish.oncourse.model.Contact contact = new GetContact(context, college, contactFieldsRequest.contactId).get(false)
        
        if (contact.isCompany) {
            return new ContactFields()
        } else {
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
                        .cacheGroup(CourseClass.class.simpleName)
                        .select(context)
            }
            new GetContactFields(contact, classes, !contactFieldsRequest.productIds.empty, contactFieldsRequest.fieldSet, contactFieldsRequest.mandatoryOnly).contactFields
        }
    }
    
    @Override
    ContactId submitContactDetails(SubmitFieldsRequest contactFields) {
        
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        ish.oncourse.model.Contact contact = new GetContact(context, college, contactFields.contactId).get()
        ValidationError errors = new ValidationError()
        
        SubmitContactFields submit = new SubmitContactFields(objectContext: context, errors: errors).submitContactFields(contact, contactFields.fields)
        if (contactFields.concession) {
            new AddConcession(objectContext: context, errors: errors, college: college).add(contactFields.concession)
        }
        
        CheckParent checkParent = new CheckParent(college, context, contact).perform()
        
        if (errors.fieldsErrors.empty && errors.formErrors.empty) {
            context.commitChanges()
            return new ContactId().with { c ->
                c.id = contact.id.toString()
                c.newContact = false
                c.parentRequired = checkParent.parentRequired
                c.parent = checkParent.parent
                c
            }
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
                .cacheGroup(ish.oncourse.model.Contact.class.simpleName)
                .selectOne(cayenneService.newContext())
        if (contact) {
            new Contact(id: contact.id.toString(),
                    firstName: contact.givenName,
                    lastName: contact.familyName,
                    email: contact.emailAddress,
                    uniqueIdentifier: studentUniqueIdentifier,
                    company: contact.isCompany)
        } else {
            logger.error("There is no contact with uuid: ${studentUniqueIdentifier}")
            throw new IllegalArgumentException("There is no contact with uuid: ${studentUniqueIdentifier}")
        }

    }

    @Override
    List<Concession> getContactConcessions(List<String> contactIds) {
        new GetContactConcessions(college: collegeService.college, context: cayenneService.newContext(), contactIds: contactIds).get()
    }

    @Override
    List<ConcessionType> getConcessionTypes() {
        new GetConcessionTypes(context: cayenneService.newContext(), college: collegeService.college).get()
    }

    @Override
    void submitConcession(Concession concession) {
        
        ObjectContext context = cayenneService.newContext()
        AddConcession addConcession = new AddConcession(college: collegeService.college, objectContext: context, errors: new ValidationError()).add concession
        if (addConcession.errors.fieldsErrors.empty && addConcession.errors.formErrors.empty) {
            context.commitChanges()   
        } else {
            context.rollbackChanges()
            logger.warn(" Vaidation error: $addConcession.errors")
            throw new BadRequestException(Response.status(400).entity(addConcession.errors).build())        
        }
    }

}
