@parallel=false
Feature: Main feature for GET checkout status

  Background: Authorize first
    * callonce read('../../signIn.feature')
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPath = 'checkout/status'
    

  Scenario: Get status with wrong sessionId

    Given path ishPath + '/12345'
    When method GET
    Then status 500