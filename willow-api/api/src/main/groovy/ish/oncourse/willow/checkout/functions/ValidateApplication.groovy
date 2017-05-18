package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.willow.model.checkout.Application
import org.apache.cayenne.ObjectContext

class ValidateApplication {

    ObjectContext context
    College college
    Application application
    
    List<String> errors = []
    List<String> warnings = []
    
    ValidateApplication validate() {
        application
        
        this
    }

}
