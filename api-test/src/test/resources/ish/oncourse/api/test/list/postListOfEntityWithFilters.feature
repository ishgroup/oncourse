@ignore
@parallel=false
Feature: Re-usable feature to get list for each entity using POST request


  Scenario:

    * url 'https://127.0.0.1:8182/a/v1'

    Given path ishPath
    And param entity = entity
    And request request
    When method POST
    Then status 200
    And assert response.rows.length > 0