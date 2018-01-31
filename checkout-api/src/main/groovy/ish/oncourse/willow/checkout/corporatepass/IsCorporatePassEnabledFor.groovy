package ish.oncourse.willow.checkout.corporatepass

import ish.oncourse.model.College
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Product
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

class IsCorporatePassEnabledFor {

    private ObjectContext context
    private College college
    private CheckoutModelRequest checkoutModelRequest

    private List<CourseClass> classes
    private List<Product> products

    IsCorporatePassEnabledFor(ObjectContext context, College college, CheckoutModelRequest checkoutModelRequest) {
        this.context = context
        this.college = college
        this.checkoutModelRequest = checkoutModelRequest
        List<ContactNode> nodes = checkoutModelRequest.contactNodes
        this.classes = (nodes*.enrolments.findAll {it.selected}.classId.flatten().unique() as List<String>).collect { new GetCourseClass(context, college, it).get()}
        this.products = (nodes*.memberships.findAll {it.selected}.productId.flatten().unique() as List<String>).collect { new GetProduct(context, college, it).get()}
        this.products += (nodes*.vouchers.findAll {it.selected}.productId.flatten().unique() as List<String>).collect { new GetProduct(context, college, it).get()}
        this.products += (nodes*.articles.findAll {it.selected}.productId.flatten().unique() as List<String>).collect { new GetProduct(context, college, it).get()}
    }
    
    boolean get() {
        if (classes.empty && products.empty) {
            return false
        } else if (products.empty) {
            if (hasUnlimitedPassesFor(CorporatePass.VALID_CLASSES.outer().dot(CourseClass.CODE))) {
                return true
            }
            List<CorporatePass> selectedClassesPass = getPassesFor(CorporatePass.VALID_CLASSES, classes)
            for (CorporatePass pass : selectedClassesPass) {
                if (pass.validClasses*.id.containsAll(classes*.id)) {
                    return true
                }
            }
            return false
        } else if (classes.empty) {
            if (hasUnlimitedPassesFor(CorporatePass.VALID_PRODUCTS.outer().dot(Product.SKU))) {
                return true
            }
            List<CorporatePass> selectedProductsPass = getPassesFor(CorporatePass.VALID_PRODUCTS, products)
            
            for (CorporatePass pass : selectedProductsPass) {
                if (pass.validProducts*.id.containsAll(products*.id)) {
                    return true
                }
            }
            return false
        } else {
            List<CorporatePass> allProductsClassesPass = (ObjectSelect.query(CorporatePass)
                    .where(CorporatePass.VALID_PRODUCTS.outer().dot(Product.SKU).isNull())
                    .and(CorporatePass.VALID_CLASSES.outer().dot(CourseClass.CODE).isNull()))
                    .and(CorporatePass.EXPIRY_DATE.gt(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull()))
                    .and(CorporatePass.COLLEGE.eq(college))
                    .select(context)

            if (!allProductsClassesPass.empty) {
                return true
            }
            
            List<CorporatePass> selectedProductsClassesPass = getPassesFor(CorporatePass.VALID_PRODUCTS, products)
            selectedProductsClassesPass += getPassesFor(CorporatePass.VALID_CLASSES, classes)
            
            for (CorporatePass pass : selectedProductsClassesPass) {
                if ((pass.validProducts*.id.containsAll(products*.id) && pass.validClasses*.id.containsAll(classes*.id))
                        || (pass.validProducts*.id.containsAll(products*.id) &&  pass.validClasses.empty)
                        || (pass.validProducts.empty &&  pass.validClasses*.id.containsAll(classes*.id))) {
                    return true
                }
            }

            return false
        }
    }
    
    private List<CorporatePass> getPassesFor(Property property, List qualifier) {
        return ObjectSelect.query(CorporatePass)
                .where(property.in(qualifier))
                .and(CorporatePass.EXPIRY_DATE.gt(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull()))
                .and(CorporatePass.COLLEGE.eq(college))
                .prefetch(property.joint())
                .select(context)
    }

    private boolean hasUnlimitedPassesFor(Property property) {
        return !ObjectSelect.query(CorporatePass)
                .where(property.isNull())
                .and(CorporatePass.EXPIRY_DATE.gt(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull()))
                .and(CorporatePass.COLLEGE.eq(college))
                .select(context).empty
    }
    
    
}
