package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Course

@CompileStatic
class EnrolmentNode {
    Money finalPrice
    Course course
    Contact contact
    Money payNow
}
