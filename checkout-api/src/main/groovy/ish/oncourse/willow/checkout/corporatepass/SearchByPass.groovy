package ish.oncourse.willow.checkout.corporatepass

import ish.oncourse.model.College
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Product
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.checkout.corporatepass.GetCorporatePassRequest
import ish.oncourse.willow.model.common.ValidationError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils 

class SearchByPass {
    
    private ObjectContext context
    private College college
    private GetCorporatePassRequest request

    private ValidationError validationError = new ValidationError()
    private ish.oncourse.willow.model.checkout.corporatepass.CorporatePass result

    private CorporatePass pass


    SearchByPass(ObjectContext context, College college, GetCorporatePassRequest request) {
        this.context = context
        this.college = college
        this.request = request
    }


    SearchByPass get() {
        
        String code = StringUtils.trimToNull(request.code)

        if (!code) {
            validationError.formErrors << 'CorporatePass must be supplied.'
            return this
        }
        
        pass = ((ObjectSelect.query(CorporatePass).
                where(CorporatePass.PASSWORD.eq(request.code))
                & CorporatePass.COLLEGE.eq(college))
                & CorporatePass.EXPIRY_DATE.gte(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull())).
                selectFirst(context)


        List<CourseClass> classes = request.classIds.collect { id -> new GetCourseClass(context, college, id).get()}
        List<Product> products = request.productIds.collect { id -> new GetProduct(context, college, id).get()}

        ValidateCorporatePass validatePass = new ValidateCorporatePass(pass, classes, products)

        if (validatePass.validate()) {
            result = new ish.oncourse.willow.model.checkout.corporatepass.CorporatePass().with { corpPass ->
                corpPass.id = pass.id.toString()
                corpPass.code = pass.password
                corpPass.email = pass.contact.emailAddress
                corpPass.message = "Valid code entered. This transaction will be invoiced to ${pass.contact.fullName} when you press the Confirm Purchase button below. Your details will be forwarded to the relevant manager at ${pass.contact.fullName}."
                corpPass
            }
        } else {
            validationError.formErrors << validatePass.error.message
        }
        
        return this
    }

    ValidationError getValidationError() {
        return validationError
    }

    ish.oncourse.willow.model.checkout.corporatepass.CorporatePass getResult() {
        return result
    }
}
