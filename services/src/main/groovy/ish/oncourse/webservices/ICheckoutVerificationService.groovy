package ish.oncourse.webservices

interface ICheckoutVerificationService {

    CheckoutValidationResult verify(Long studentId, Long courseClassId)
}