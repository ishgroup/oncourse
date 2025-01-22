/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import ish.common.checkout.gateway.PaymentGatewayError
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.cayenne.CheckoutSession
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.query.ObjectSelect

class CheckoutSessionService {

    @Inject
    CayenneService cayenneService

    @Inject
    SystemUserService systemUserService


    void saveCheckoutSession(CheckoutModelDTO modelDTO, String sessionId) {
        ObjectMapper mapper = new ObjectMapper()
        String value = mapper.writeValueAsString(modelDTO)
        def context = cayenneService.newContext
        def checkoutSession = context.newObject(CheckoutSession)
        checkoutSession.sessionId = sessionId
        checkoutSession.value = value
        checkoutSession.createdOn = new Date()
        checkoutSession.createdByUser = systemUserService.currentUser
        context.commitChanges()
    }

    void removeCheckoutSession(String sessionId) {
        def context = cayenneService.newReadonlyContext
        def session = ObjectSelect.query(CheckoutSession)
                .where(CheckoutSession.SESSION_ID.eq(sessionId))
                .selectOne(context)

        if(session) {
            context.deleteObject(session)
            context.commitChanges()
        }
    }

    CheckoutModelDTO getCheckoutSession(String sessionId, PaymentServiceInterface paymentService) {
        def context = cayenneService.newReadonlyContext
        def session = ObjectSelect.query(CheckoutSession)
                .where(CheckoutSession.SESSION_ID.eq(sessionId))
                .selectOne(context)

        if(session == null) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Checkout session for this id not found in db: $sessionId")])
        }

        ObjectMapper mapper = new ObjectMapper()
        return mapper.readValue(session.value, CheckoutModelDTO.class)
    }
}
