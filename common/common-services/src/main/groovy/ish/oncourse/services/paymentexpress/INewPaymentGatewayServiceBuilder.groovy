package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic

@CompileStatic
interface INewPaymentGatewayServiceBuilder {
	INewPaymentGatewayService buildService()
}