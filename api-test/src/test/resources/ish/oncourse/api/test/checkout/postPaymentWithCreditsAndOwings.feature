@parallel=false
Feature: performance of payment with previous credits and previous owings

  Background: Authorize first, create checkoutModel
    * callonce read('../signIn.feature')
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'checkout'
    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment                                                             | membershipIds                  | voucherData              | product |
      | 0 | 4         | 4         | null     | [{"classId": 6,"appliedDiscountId": null,"studyReason":"Not stated"}] | {"first": null,"second": null} | {"id": null, "value": 0} | []      |
    * call read('getCheckoutModel.feature') checkoutModelTable
    
  Scenario: right payment with credits and owings

    * set checkoutModel.paymentMethodId = 1
    * set checkoutModel.payNow = 110
    * set checkoutModel.payForThisInvoice = 220
    * set checkoutModel.previousInvoices = { 38: -220, 39: 110 }

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And match response.invoice.amountOwing == 0.0


  Scenario: right payment with credits, without owings. (pay by credits)

    And set checkoutModel.payNow = 0
    And set checkoutModel.payForThisInvoice = 220
    And set checkoutModel.previousInvoices = { 38: -220}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And match response.invoice.amountOwing == 0.0


  Scenario: right payment with owings, without credits

    And set checkoutModel.payNow = 330
    And set checkoutModel.payForThisInvoice = 220
    And set checkoutModel.previousInvoices = { 39: 110}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And match response.invoice.amountOwing == 0.0


  Scenario: right payment with credits and owings, pay by credits for owing

    And set checkoutModel.payNow = 0
    And set checkoutModel.payForThisInvoice = 0
    And set checkoutModel.previousInvoices = { 38: -110, 39: 110 }

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And match response.invoice.amountOwing == 220.0


  Scenario: right payment without credits and owings, pay later

    And set checkoutModel.payNow = 0
    And set checkoutModel.payForThisInvoice = 0
    And set checkoutModel.previousInvoices = { 39: 0 }

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And match response.invoice.amountOwing == 220.0


  Scenario: not right payment with credits and owings: trying to pay more than total

    And set checkoutModel.payNow = 220
    And set checkoutModel.payForThisInvoice = 0
    And set checkoutModel.previousInvoices = { 38: -220, 39: 110}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 400
    And match response[0].error == 'Payment amount doesn\'t match invoice allocated total amount'


  Scenario: not right payment with credits and owings, amount of current invoice is more than summary of checkout

    And set checkoutModel.payNow = 110
    And set checkoutModel.payForThisInvoice = 330
    And set checkoutModel.previousInvoices = { 38: -220, 39: 110}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 400
    And match response[0].error == 'Payment amount allocated to current invoice is bigger than invoice total'
    And match response[1].error == 'Payment amount doesn\'t match invoice allocated total amount'


  Scenario: not right payment with credits and owings: amount of previous invoices is more than real one

    And set checkoutModel.payNow = 220
    And set checkoutModel.payForThisInvoice = 220
    And set checkoutModel.previousInvoices = { 38: -220, 39: 220}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 400
    And match response[0].error == 'Payment amount allocated to invoice #46 bigger than invoice outstanding amount'
    And match response[1].error == 'Payment amount doesn\'t match invoice allocated total amount'


  Scenario: not right payment with credits and owings: amount of previous credits is more than real one

    And set checkoutModel.payNow = 0
    And set checkoutModel.payForThisInvoice = 220
    And set checkoutModel.previousInvoices = { 38: -330, 39: 110}

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 400
    And match response[0].error == 'Payment amount allocated to invoice #43 bigger than invoice outstanding amount'
    And match response[1].error == 'Payment amount doesn\'t match invoice allocated total amount'