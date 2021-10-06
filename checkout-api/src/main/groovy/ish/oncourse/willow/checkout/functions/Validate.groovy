package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext

@CompileStatic
abstract class Validate<T> {


    ObjectContext context
    College college
    WebSite site
    
    Validate(ObjectContext context, College college) {
        this(context, college, null)
    }
    
    Validate(ObjectContext context, College college, WebSite site) {
        this.context = context
        this.college = college
        this.site = site
    }
    
    abstract Validate validate(T o)

    List<String> errors = []
    List<String> warnings = []
}
