package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.Field
import ish.oncourse.model.WebSite
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.functions.CheckParent
import ish.oncourse.willow.functions.CreateOrGetContact
import ish.oncourse.willow.functions.CreateParentChildrenRelation
import ish.oncourse.willow.functions.field.ContactFieldsBuilder
import ish.oncourse.willow.functions.field.GetCompanyFields
import ish.oncourse.willow.functions.field.GetContactFields
import ish.oncourse.willow.functions.SubmitContactFields
import ish.oncourse.willow.functions.concession.AddConcession
import ish.oncourse.willow.functions.concession.GetConcessionTypes
import ish.oncourse.willow.functions.concession.GetConcessionsAndMemberships
import ish.oncourse.willow.functions.field.GetCoursesByClassIds
import ish.oncourse.willow.functions.field.MergeFields
import ish.oncourse.willow.model.checkout.ConcessionsAndMemberships
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
            logger.info("Can not create parent children relation, college id: ${college.id}, request: ${request}")
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(createRelation.error).build())
        } else {
            context.commitChanges()
        }
    }
    
    @Override
    ContactFields getContactFields(ContactFieldsRequest request) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        ish.oncourse.model.Contact contact = new GetContact(context, college, request.contactId).get(false)

        List<Course> coursesByClassIds = new GetCoursesByClassIds(request.classIds, college, context).courses

        List<Field> fieldsToMerge = []

        if (request.isPayer) {
            fieldsToMerge.addAll(coursesByClassIds*.fieldConfigurationScheme*.payerFieldConfiguration*.fields.flatten().findAll() as List<Field>)
        } else if (request.isParent) {
            fieldsToMerge.addAll(coursesByClassIds*.fieldConfigurationScheme*.parentFieldConfiguration*.fields.flatten().findAll() as List<Field>)
        }

        if (fieldsToMerge.empty) {
            if (contact.isCompany) {
                fieldsToMerge.addAll(new GetCompanyFields(contact, college, context).fields)
            } else {
                if (request.classIds.empty
                        && request.products.empty
                        && request.waitingCourseIds.empty) {
                    logger.info("classesIds required, request param: $request")
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(new CommonError(message: 'classesIds required')).build())
                }
                if (!request.fieldSet) {
                    logger.info("fieldSet required, request param: $request")
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(new CommonError(message: 'fieldSet required')).build())
                }
                List<String> productIds = request.products.collect{p -> p.productId}
                fieldsToMerge.addAll(new GetContactFields(contact, coursesByClassIds, request.waitingCourseIds, productIds, request.mandatoryOnly).fields)
            }
        }

        Set<Field> fields = new MergeFields(fieldsToMerge).fields
        new ContactFieldsBuilder(request.mandatoryOnly, contact, collegeService.webSite, fields).build()
    }
    
    @Override
    ContactId submitContactDetails(ContactFields contactFields) {
        
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        WebSite webSite = collegeService.webSite
        ish.oncourse.model.Contact contact = new GetContact(context, college, contactFields.contactId).get(false)
        ValidationError errors = new ValidationError()
        
        SubmitContactFields submit = new SubmitContactFields(objectContext: context, errors: errors, college: college, webSite: webSite, contact: contact)
        contactFields.headings.each {it ->
            submit.submitContactFields(contact, it.fields)
        }
        ContactId response = new ContactId().id(contact.id.toString()).newContact(false).parentRequired(false)
        
        if (!contact.isCompany) {
            CheckParent checkParent = new CheckParent(college, context, contact).perform()
            response = response.parentRequired(checkParent.parentRequired)
        }
        
        if (errors.fieldsErrors.empty && errors.formErrors.empty) {
            context.commitChanges()
            return response
        } else {
            logger.info(" Vaidation error: $submit.errors")
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(submit.errors).build())
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
            logger.info("There is no contact with uuid: ${studentUniqueIdentifier}")
            throw new IllegalArgumentException("There is no contact with uuid: ${studentUniqueIdentifier}")
        }

    }

    @Override
    ConcessionsAndMemberships getContactConcessionsAndMemberships(List<String> contactIds) {
        new GetConcessionsAndMemberships(college: collegeService.college, context: cayenneService.newContext(), contactIds: contactIds).get()
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
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(addConcession.errors).build())
        }
    }

}
