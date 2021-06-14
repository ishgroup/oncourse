@parallel=false
Feature: Main feature for all POST requests with path 'checkout'

  Background: Authorize first
    * callonce read('../signIn.feature')
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPathLogin = 'login'
    * def ishPath = 'checkout'
    * def ishPathList = 'list'
    * def ishPathPlain = 'list/plain'
    * def ishPathEntity = 'list/entity/'
    * def fun =
      """
        function(list, sum, i) {
          if (list[i]) {
            return fun(list, sum + parseFloat(list[i]), ++i)
          } else {
            return sum
          }
        }
      """
    

#  Scenario: Receive pre Invoice with all Invoice Line which will created after payment. Without payment method. X-validate-only
#
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment                                   | membershipIds                    | voucherData               | product                          | expectedResult |
#      | 0 | 22      | 22      | 21       | [{"classId": 14,"appliedDiscountId": null}] | {"first": 1003, "second": 1003 } | {"id": 1002, "value": 70} | []                               | 600.0          |
#      | 1 | 22      | 22      | 21       | [{"classId": 3, "appliedDiscountId": 1002}] | {"first": 1006, "second": 1006 } | {"id": 1005, "value": 70} | []                               | 1322.0         |
#      | 2 | 22      | 22      | 21       | [{"classId": 3, "appliedDiscountId": 1002}] | {"first": 1003, "second": 1006 } | {"id": 1002, "value": 70} | [{productId: 1001, quantity: 1}] | 1401.0         |
#
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#      Given path ishPath
#      And request checkoutModel
#      And header xValidateOnly = true
#      When method POST
#      Then status 200
#      And match response.invoice.amountOwing == expectedResult
#
#
#  Scenario: Validation on payer and contact who buy voucher there is same person. Without payment method. X-validate-only
#
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment                                    | membershipIds                   | voucherData               | product |
#      | 0 | 21      | 22      | 21       | [{"classId": 14, "appliedDiscountId": null}] | {"first": null, "second": null} | {"id": 1002, "value": 70} | []      |
#
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'Voucher purchase available for payer only'
#
#
#  Scenario: Validation on contact isn't company. Without payment method. X-validate-only
#
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment                                    | membershipIds                   | voucherData               | product |
#      | 0 | 21      | 21      | 7        | [{"classId": 14, "appliedDiscountId": null}] | {"first": 1003, "second": 1003} | {"id": 1002, "value": 70} | []      |
#
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'Company can not be enrolled'
#
#
#  Scenario: Validation on expiry date of membership. Without payment method. X-validate-only
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment | membershipIds                   | voucherData              | product |
#      | 0 | 21      | 21      | null     | []        | {"first": 1003, "second": null} | {"id": null, "value": 0} | []      |
#
#    * call read('getCheckoutModel.feature') checkoutModelTable
#    * remove checkoutModel.contactNodes[0].memberships[0].validTo
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'Expiry date cannot be null.'
#
#
#  Scenario: Try to enrol to class without places
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment                                                           | membershipIds                   | voucherData              | product |
#      | 0 | 30      | 30      | null     | [{classId: 16, appliedDiscountId: null, studyReason: "Not stated"}] | {"first": null, "second": null} | {"id": null, "value": 0} | []      |
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'No places available for class course4-3'
#
#    * set checkoutModel.payNow = 110
#    * set checkoutModel.payForThisInvoice = 0
#    * set checkoutModel.previousInvoices = {40: 110}
#    * set checkoutModel.paymentMethodId = 1
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'No places available for class course4-3'
#
#
#  Scenario: Try to buy something, but will pay after. Without previous credits. Payment In shouldn't be created
#
#    * table checkoutModelTable
#      | i | payerId   | firstId   | secondId | enrolment | membershipIds                  | voucherData              | product                          |
#      | 0 | 30        | 30        | null     | []        | {"first": 1003,"second": null} | {"id": null, "value": 0} | [{productId: 1004, quantity: 2}] |
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    * set checkoutModel.payNow = 0.0
#    * set checkoutModel.payForThisInvoice = 0.0
#    * set checkoutModel.previousInvoices = {40: 0}
#    * set checkoutModel.paymentMethodId = 1
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 200
#    And match response.paymentId == null
#
#
#  Scenario: Getting pre invoice without any products and sum it with previous owing. Result equals previous owing
#
#    * def contactId = 4
#
#    Given path ishPathPlain
#    And param entity = 'Invoice'
#    And param search = 'contact.id is ' + contactId + ' and amountOwing > 0'
#    And param columns = 'amountOwing'
#    When method GET
#    Then status 200
#    And def listOwing = karate.jsonPath(response, '$.rows[*].values[0]')
#    And def previousOwing = fun(listOwing, 0, 0)
#    * print previousOwing
#
#    * table checkoutModelTable
#      | i | payerId   | firstId   | secondId | enrolment | membershipIds                    | voucherData               | product |
#      | 0 | contactId | contactId | null     | []        | {"first": null, "second": null } | {"id": null, "value": 0 } | []      |
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    * set checkoutModel.payNow = 110
#    * set checkoutModel.payForThisInvoice = 0
#    * set checkoutModel.previousInvoices = { "39": 110 }
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 200


  Scenario: Performance of the full payment cycle without previous invoices. Try to enrolling again after
    * def contactData = { "id": 6, "classId": 14 }
    * call read('fullPaymentCycle.feature') contactData

#
#  Scenario: Performance of the full payment cycle with previous invoices. Try to enrolling again after
#    * def contactData = { "id": 18, "classId": 14 }
#    * call read('fullPaymentCycle.feature') contactData
#
#
#  Scenario: Performance of the full payment with discount (membership is mandatory)
#    * def contactData = { "id": 10, "classId": 3 }
#    * call read('fullPaymentCycle.feature') contactData
#
#    Given path ishPathEntity + 'invoice/' + invoiceId
#    When method GET
#    Then status 200
#    And match karate.jsonPath(response.invoiceLines, '[?(@.discountEachExTax!=0.0)]')[0].discountEachExTax == 127.27
#
#
#  Scenario: Specify negative amount of current payment (payNow), bigger amount than current
#
#    * table checkoutModelTable
#      | i | payerId | firstId | secondId | enrolment | membershipIds                   | voucherData               | product |
#      | 0 | 21      | 21      | null     | []        | {"first": 1003, "second": null} | {"id": 1002, "value": 70} | []      |
#    * call read('getCheckoutModel.feature') checkoutModelTable
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 200
#    And def currentOwing = parseFloat(response.invoice.amountOwing)
#
#    And set checkoutModel.payNow = -currentOwing
#    And set checkoutModel.payForThisInvoice = 0
#    And set checkoutModel.paymentMethodId = 1
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'Payment amount doesn\'t match invoice allocated total amount'
#
#    And set checkoutModel.payNow = currentOwing*2
#    And set checkoutModel.payForThisInvoice = currentOwing*2
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = true
#    When method POST
#    Then status 400
#    And match response[0].error == 'Payment amount allocated to current invoice is bigger than invoice total'
#    And match response[1].error == 'Payment amount doesn\'t match invoice allocated total amount'
#
#
#  Scenario: Zero payment, buying membership and voucher. ContactId - 21
#
#    * def contactId = 21
#
#    * table checkoutModelTable
#      | i | payerId   | firstId   | secondId | enrolment | membershipIds                   | voucherData               | product |
#      | 0 | contactId | contactId | null     | []        | {"first": 1003, "second": null} | {"id": 1002, "value": 70} | []      |
#    * call read('getCheckoutModel.feature') checkoutModelTable
#    
#
#    And set checkoutModel.payNow = 0
#    And set checkoutModel.payForThisInvoice = 0
#    And set checkoutModel.paymentMethodId = 7
#
#    Given path ishPath
#    And request checkoutModel
#    And header xValidateOnly = false
#    When method POST
#    Then status 200
#    And def paymentId = response.paymentId
#    And def invoiceId = response.invoice.id
#
#    Given path ishPathEntity + 'paymentIn/' + paymentId
#    When method GET
#    Then status 200
#    And match response.payerId == contactId
#    And match response.amount == checkoutModel.payNow
#    And match response.paymentInType == 'Contra'
#
#    Given path ishPathEntity + 'invoice/' + invoiceId
#    When method GET
#    Then status 200
#    And match response.contactId == contactId
#    And match response.total == response.amountOwing
#    And response.dateDue >= response.invoiceDate

