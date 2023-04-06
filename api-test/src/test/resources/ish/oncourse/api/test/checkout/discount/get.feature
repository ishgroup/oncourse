@parallel=false
Feature: Main feature for all GET requests with path 'checkout/discount'

  Background: Authorize first
    * configure headers = { Authorization: 'admin' }
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'checkout/discount'
    

  Scenario: Get discounts for contact and class

    Given path ishPath
    And param contactId = 10
    And param classId = 3
    And param membershipIds = 1003
    And param classIds = 3
    And param purchaseTotal = 0
    And param payerId = 7
    When method GET
    Then status 200
    And match karate.sizeOf(response) == 1
    And match parseFloat(response[0].discount.discountPercent) == 0.20

  Scenario: Get not coporate discounts for contact and class

    Given path ishPath
    And param contactId = 10
    And param classId = 3
    And param membershipIds = 1003
    And param classIds = 3
    And param purchaseTotal = 0
    And param payerId = 10
    When method GET
    Then status 200
    Then status 200
    And match $ == []

  Scenario: No discounts for contact and class

    Given path ishPath
    And param contactId = 2
    And param classId = 2
    And param promoIds = null
    And param membershipIds = null
    And param enrolmentsCount = 1
    And param purchaseTotal = 0
    And param payerId = 2
    When method GET
    Then status 200
    And match $ == []

  Scenario: No corporate discounts for contact and class

    Given path ishPath
    And param contactId = 7
    And param classId = 2
    And param promoIds = null
    And param membershipIds = null
    And param enrolmentsCount = 1
    And param purchaseTotal = 0
    And param payerId = 7
    When method GET
    Then status 200
    And match $ == []