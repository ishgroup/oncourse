package ish.oncourse.willow.functions.concession

import ish.oncourse.model.College
import ish.oncourse.model.ConcessionType
import ish.oncourse.model.Contact
import ish.oncourse.model.Student
import ish.oncourse.model.StudentConcession
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.concession.Concession
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.ParseException

class AddConcession {
    
    final static  Logger logger = LoggerFactory.getLogger(GetConcessionTypeById.class)

    ObjectContext objectContext
    College college
    ValidationError errors
    Contact contact
    
    StudentConcession deletingConcession = null

    AddConcession add(Concession concession) {
        
        if (!contact) {
            contact = new GetContact(objectContext, college, concession.contactId).get()  
        }
        ConcessionType concessionType = new GetConcessionTypeById(college: college, context: objectContext).get(concession.concessionTypeId)
        Date expiryOn = null
        String concessionNumber = null
        if (concessionType.hasExpiryDate) {
            expiryOn = parseExpiresOn(concession.expiryDate, concessionType)
        }
        if (concessionType.hasConcessionNumber) {
            concessionNumber = parseConcessionNumber(concession.number, concessionType)
        }
        
        validateStudent(contact.student, concessionType, expiryOn)
        
        if (errors.formErrors.empty && errors.fieldsErrors.empty) {
            if(deletingConcession) {
                objectContext.deleteObject(deletingConcession)
            }
            StudentConcession studentConcession = objectContext.newObject(StudentConcession)
            studentConcession.student = contact.student
            studentConcession.college = objectContext.localObject college
            studentConcession.concessionType = concessionType
            studentConcession.concessionNumber = concessionNumber
            studentConcession.expiresOn = expiryOn
        }
        this
    }

    Date parseExpiresOn(String expiresOn, ConcessionType concessionType) {

        if (expiresOn == null) {
            errors.fieldsErrors << new FieldError(name: 'expiryDate', error: "A ${concessionType.name} concession requires an expiry date")
            return null
        }
        
        try {
            Date expiry = Date.parse(FormatUtils.DATE_FIELD_PARSE_FORMAT, expiresOn)
            
            if (new Date().compareTo(expiry) > 0) {
                errors.fieldsErrors << new FieldError(name: 'expiryDate', error: "Expiry date shouldn't be at the past.")
            }
            return expiry
        } catch (ParseException e) {
            logger.error("Invalid date format for string: ${expiresOn}", e)
            errors.fieldsErrors << new FieldError(name: 'expiryDate', error: 'Expiry date should has dd/MM/yyyy format.')
            return null
        }
    }

    String parseConcessionNumber(String number, ConcessionType concessionType) {
        if (!StringUtils.trimToNull(number)) {
            errors.fieldsErrors << new FieldError(name: 'number', error: "A ${concessionType.name} concession requires a card number.")
            return null
        }
        return number
    }

    void validateStudent(Student student, ConcessionType concessionType, Date newExpiryOn) {

        StudentConcession concession = student.studentConcessions.find { c -> c.concessionType == concessionType }
        
        if (concession) {
            Date expiredDate = concession.expiresOn
            if (newExpiryOn != null && (expiredDate == null || expiredDate.before(new Date()))) {
                deletingConcession  = concession
                return
            }
            errors.formErrors << 'This concession is already on file for this student.'
        }
    }
    
}
