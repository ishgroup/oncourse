/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.event

import com.google.inject.Inject
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.service.impl.CheckoutApiImpl
import ish.oncourse.server.integration.OnCourseEventListener

class CheckoutEventListener implements OnCourseEventListener {

    @Inject
    WillowValidator willowValidator
    
    
    @Override
    void dispatchEvent(SystemEvent systemEvent) {

        List<CheckoutValidationErrorDTO> errors = willowValidator.validate(systemEvent.value as CheckoutModelDTO)
        if (!errors.empty) {
            CheckoutApiImpl.hanbleError(CheckoutApiImpl.VALIDATION_ERROR, errors)
        }
    
    }
}
