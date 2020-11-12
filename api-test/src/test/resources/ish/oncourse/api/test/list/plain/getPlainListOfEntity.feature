@ignore
@parallel=false
Feature: Re-usable feature to get list for each entity


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entity = entity
        And param columns = 'id'
        When method GET
        Then status 200
        And match $.rows == '##[_ > 0]'
        And match $.rows[0].values == '##[_ == 1]'