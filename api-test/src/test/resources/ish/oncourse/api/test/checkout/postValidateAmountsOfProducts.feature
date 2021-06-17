@parallel=false
Feature: Validations on invoices amount after payment

  Background: Authorize first, create checkoutModel
    * callonce read('../signIn.feature')
    * url 'https://127.0.0.1:8182/a/v1'
    
    * def ishPath = 'checkout'
    * def ishPathEntity = 'list/entity/'


  Scenario: Buy membership, check invoice line amount with membership cost

    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment | membershipIds                  | voucherData              | product |
      | 0 | 30        | 30        | null     | []        | {"first": 1006,"second": null} | {"id": null, "value": 0} | []      |
    * call read('getCheckoutModel.feature') checkoutModelTable

    * set checkoutModel.paymentMethodId = 1
    * set checkoutModel.payNow = 66.0
    * set checkoutModel.payForThisInvoice = 66.0

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = false
    When method POST
    Then status 200
    And def invoiceId = response.invoice.id
    And def paymentId = response.paymentId

    Given path ishPathEntity + 'invoice/' + invoiceId
    When method GET
    Then status 200
    And match response.total == 66.0
    And match response.invoiceLines[0].priceEachExTax == 60.0
    And match response.invoiceLines[0].taxEach == 6.0

    Given path ishPathEntity + 'paymentIn/' + paymentId
    When method GET
    Then status 200
    And match response.amount == 66.0
    And match response.invoices[0].amount == 66.0


  Scenario: Buy vouchers, check invoice lines amount with voucher cost

    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment | membershipIds                  | voucherData              | product |
      | 0 | 30        | 30        | null     | []        | {"first": null,"second": null} | {"id": null, "value": 0} | []      |
    * call read('getCheckoutModel.feature') checkoutModelTable

    * set checkoutModel.paymentMethodId = 1
    * set checkoutModel.payNow = 50.0
    * set checkoutModel.payForThisInvoice = 50.0
    * set checkoutModel.contactNodes[0].vouchers[0] = { "productId":1009, "validTo":"2021-05-07", "value":0, "restrictToPayer":false }
    * set checkoutModel.contactNodes[0].vouchers[1] = { "productId":1002, "validTo":"2021-05-07", "value":50, "restrictToPayer":false }

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = false
    When method POST
    Then status 200
    And def invoiceId = response.invoice.id
    And def paymentId = response.paymentId

    Given path ishPathEntity + 'invoice/' + invoiceId
    When method GET
    Then status 200
    And match response.total == 50.0
    And assert karate.jsonPath(response.invoiceLines, '[?(@.priceEachExTax==0.0)]')[0].id != null
    And assert karate.jsonPath(response.invoiceLines, '[?(@.priceEachExTax==50.0)]')[0].id != null
    And match karate.sizeOf(response.invoiceLines) == 2

    Given path ishPathEntity + 'paymentIn/' + paymentId
    When method GET
    Then status 200
    And match response.amount == 50.0
    And match response.invoices[0].amount == 50.0


  Scenario: Buy product, check invoice line amount with product cost

    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment | membershipIds                  | voucherData              | product                          |
      | 0 | 30        | 30        | null     | []        | {"first": null,"second": null} | {"id": null, "value": 0} | [{productId: 1004, quantity: 2}] |
    * call read('getCheckoutModel.feature') checkoutModelTable

    * set checkoutModel.paymentMethodId = 1
    * set checkoutModel.payNow = 264.0
    * set checkoutModel.payForThisInvoice = 264.0

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = false
    When method POST
    Then status 200
    And def invoiceId = response.invoice.id
    And def paymentId = response.paymentId

    Given path ishPathEntity + 'invoice/' + invoiceId
    When method GET
    Then status 200
    And match response.total == 264.0
    And match response.invoiceLines[0].priceEachExTax == 120.0
    And match response.invoiceLines[0].taxEach == 12.0
    And match response.invoiceLines[0].quantity == 2

    Given path ishPathEntity + 'paymentIn/' + paymentId
    When method GET
    Then status 200
    And match response.amount == 264.0
    And match response.invoices[0].amount == 264.0


  Scenario: Buy product, check invoice line amount with product cost

    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment                                                              | membershipIds                  | voucherData              | product |
      | 0 | 30        | 30        | null     | [{"classId":15,"appliedDiscountId":null,"studyReason":"To get a job"}] | {"first": null,"second": null} | {"id": null, "value": 0} | []      |
    * call read('getCheckoutModel.feature') checkoutModelTable

    * set checkoutModel.paymentMethodId = 1
    * set checkoutModel.payNow = 250.0
    * set checkoutModel.payForThisInvoice = 250.0

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = false
    When method POST
    Then status 200
    And def invoiceId = response.invoice.id
    And def paymentId = response.paymentId

    Given path ishPathEntity + 'invoice/' + invoiceId
    When method GET
    Then status 200
    And match (response.total - response.amountOwing) == 250.0
    And match response.invoiceLines[0].priceEachExTax == 1000.0
    And match response.invoiceLines[0].taxEach == 0.0

    Given path ishPathEntity + 'paymentIn/' + paymentId
    When method GET
    Then status 200
    And match response.amount == 250.0
    And match response.invoices[0].amount == 250.0
    And match response.invoices[0].amountOwing == 750.0