package ish.oncourse.willow.checkout.corporatepass

import ish.oncourse.model.CorporatePass
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Product
import ish.oncourse.willow.model.common.CommonError

class ValidateCorporatePass {


    private CorporatePass pass
    private List<CourseClass> classes
    private List<Product> products


    private CommonError error

    ValidateCorporatePass(CorporatePass pass, List<CourseClass> classes, List<Product> products) {
        this.pass = pass
        this.classes = classes
        this.products = products
    }

    boolean validate() {
        if (!pass) {
            error = new CommonError(message:'This code is not valid or has expired. Please contact the college.')
            return false
        } else {
            return  validateClasses() && validateProducts()
        }
    }

    boolean validateClasses() {
        List<CourseClass> validClasses = pass.validClasses

        if (validClasses.empty) {
            //if no classes specified, all the classes should pass as valid
            return true
        }
        
        for (CourseClass courseClass : classes) {
            if (!validClasses.contains(courseClass)) {
                error = new CommonError(message: "This CorporatePass is not valid for class ${courseClass.course.name} (${courseClass.course.code}-${courseClass.code})")
                return false
            }
        }
        return true
    }

    boolean validateProducts() {
        List<Product> validProducts = pass.validProducts

        if (validProducts.empty) {
            return true
        }

        for (Product product : products) {
            if (!validProducts.contains(product)) {
                error = new CommonError(message: "This CorporatePass is not valid for product ${product.name}")
                return false
            }
        }
        return true
    }

    CommonError getError() {
        return error
    }
}
