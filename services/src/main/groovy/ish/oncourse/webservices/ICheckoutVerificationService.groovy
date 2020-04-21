package ish.oncourse.webservices

interface ICheckoutVerificationService {

    List<CheckoutValidationResult> verify(Map<Long, List<Long>> enrolmentsMap)
}