@ignore
@parallel=false
Feature: Re-usable feature to get list for each entity without access rights using POST request


  Scenario:

    * url 'https://127.0.0.1:8182/a/v1'

    Given path ishPath
    And param entity = entity
    And request {"search":"","pageSize":50,"offset":0}
    When method POST
    Then status 403
    And match $.errorMessage contains "Sorry, you have no permissions"

