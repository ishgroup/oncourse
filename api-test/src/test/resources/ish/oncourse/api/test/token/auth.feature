@ignore
@parallel=false
Feature: Sign in as Admin

  Background: Authorize first
    * url 'https://127.0.0.1:8182'
    * configure headers = null
    * configure ssl = true
    


  Scenario: logout any current user
    Given path 'a/v1/logout'
    And request {}
    When method PUT


  Scenario: Wrong auth token
    Given path 'a/v1/list/entity/contact/1'
    And header Authorization = 'any!invalid!token!here'
    When method GET
    Then status 403

  Scenario: Inactive auth token
    Given path 'a/v1/list/entity/contact/1'
    And header Authorization = 'pSxGg3Jd1CJm4c1dK6v'
    When method GET
    Then status 403

  Scenario: Valid auth token
    Given path 'a/v1/list/entity/contact/1'
    And header Authorization = 'Y89AI7J3BkjO0QB6r2xtjw'
    When method GET
    Then status 200   