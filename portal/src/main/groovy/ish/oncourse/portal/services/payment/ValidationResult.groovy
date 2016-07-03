package ish.oncourse.portal.services.payment

/**
 * User: akoiro
 * Date: 2/07/2016
 */
class ValidationResult {
    def WarningMessage warning
    def ErrorMessage error

    def boolean valid() {
        return warning == null && error == null
    }
}
