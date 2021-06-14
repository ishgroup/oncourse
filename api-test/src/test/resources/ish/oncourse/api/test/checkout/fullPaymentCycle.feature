@ignore
@parallel=false
Feature: re-usable feature to performance of full payment cycle and delete all object after

  Background: Authorize first
    * call read('../signIn.feature')
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'checkout'
    * def ishPathList = 'list'
    * def ishPathPlain = 'list/plain'
    * def ishPathEntity = 'list/entity/'
    * def fun =
      """
        function(list, sum, i) {
          if (list.length && list[i]) {
            return fun(list, sum + parseFloat(list[i]), ++i)
          } else {
            return sum
          }
        }
      """

  Scenario:

    * def contactId = contactData.id
    * def classId = contactData.classId
    * def discountId = 1002

    Given path ishPathPlain
    And param entity = 'Invoice'
    And param search = 'contact.id is ' + contactId + ' and amountOwing > 0'
    And param columns = 'amountOwing'
    When method GET
    Then status 200
    And def listOwing = karate.jsonPath(response, '$.rows[*].values[0]')
    And def previousOwing = fun(listOwing, 0, 0)

    Given path ishPathPlain
    And param entity = 'Invoice'
    And param search = 'contact.id is ' + contactId + ' and amountOwing < 0'
    And param columns = 'amountOwing'
    When method GET
    Then status 200
    And def listCredits = karate.jsonPath(response, '$.rows[*].values[0]')
    And def previousCredits = fun(listCredits, 0, 0)

    * table checkoutModelTable
      | i | payerId   | firstId   | secondId | enrolment                                                                      | membershipIds                  | voucherData                | product                          |
      | 0 | contactId | contactId | null     | [{"classId": #(classId),"appliedDiscountId": null,"studyReason":"Not stated"}] | {"first": 1003,"second": null} | {"id": 1002, "value": 50 } | [{productId: 1001, quantity: 2}] |
    * call read('getCheckoutModel.feature') checkoutModelTable

    * if (contactId == 10) checkoutModel.contactNodes[0].enrolments[0].appliedDiscountId = discountId

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 200
    And def currentOwing = parseFloat(response.invoice.amountOwing)
    And def payNow = currentOwing + previousOwing + previousCredits
    

    And set checkoutModel.payNow = payNow
    And set checkoutModel.payForThisInvoice = currentOwing
    And set checkoutModel.paymentMethodId = 1
    * if (contactId == 18) checkoutModel.previousInvoices = { 37: 750 }

    * print checkoutModel

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = false
    When method POST
    Then status 200
    And def paymentId = response.paymentId
    And def invoiceId = response.invoice.id
    And def invoiceLines = karate.jsonPath(response, '$.invoice.invoiceLines[*]')
    And def invoiceLinesSize = karate.sizeOf(invoiceLines)
    And def classId = karate.jsonPath(invoiceLines, '[?(@.enrolment != null)].enrolment.classId')[0]
    And def enrolmentId = karate.jsonPath(invoiceLines, '[?(@.enrolment != null)].enrolment.id')[0]
    * print paymentId
    * print invoiceId
    * print invoiceLinesSize
    * print classId
    * print enrolmentId

    Given path ishPathEntity + 'courseClass/budget/' + classId
    When method GET
    Then status 200
    And def paymentPlanSize = karate.sizeOf(karate.jsonPath(response[0], '$.paymentPlan[?(@.dayOffset != null)]'))

    Given path ishPathEntity + 'paymentIn/' + paymentId
    When method GET
    Then status 200
    And match response.payerId == contactId

    Given path ishPathEntity + 'invoice/' + invoiceId
    When method GET
    Then status 200
    And match response.contactId == contactId
    And match karate.sizeOf(karate.jsonPath(response, '$.invoiceLines[*]')) == invoiceLinesSize
    And match karate.sizeOf(karate.jsonPath(response, '$.paymentPlans[?(@.type=="Payment due")]')) == paymentPlanSize

    Given path ishPathEntity + 'enrolment/' + enrolmentId
    When method GET
    Then status 200
    And match response.studentContactId == contactId
    And def outcomesCount = response.outcomesCount
    And def studentName = response.studentName

    Given path ishPathList
    And param entity = 'Outcome'
    And param search = 'enrolment.id == ' + enrolmentId
    When method GET
    Then status 200
    And match karate.sizeOf(response.rows) == outcomesCount

    Given path ishPathList
    And param entity = 'Course'
    And param search = 'courseClasses.id == ' + classId
    When method GET
    Then status 200
    And def courseId = response.rows[0].id

    Given path ishPathEntity + 'course/' + courseId
    When method GET
    Then status 200
    And assert karate.sizeOf(response.modules) <= outcomesCount

    Given path ishPathEntity + 'courseClass/timetable/' + classId
    When method GET
    Then status 200
    And def classCessions = karate.sizeOf(response)

    Given path ishPathEntity + 'courseClass/attendance/student/' + classId
    When method GET
    Then status 200
    And match karate.sizeOf(karate.jsonPath(response, '[?(@.contactId=='+contactId+')]')) == classCessions

    Given path ishPath
    And request checkoutModel
    And header xValidateOnly = true
    When method POST
    Then status 400
    And match response[0].error == studentName + ' is already enrolled'
