package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext

@CompileStatic
abstract class Validate<T> {


    ObjectContext context
    College college

    Validate(ObjectContext context, College college) {
        this.context = context
        this.college = college
    }

    abstract Validate validate(T o)

    List<String> errors = []
    List<String> warnings = []
}
