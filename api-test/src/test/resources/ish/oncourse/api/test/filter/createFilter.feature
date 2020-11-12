@ignore
@parallel=false
Feature: Re-usable feature to create/check/delete filter by admin or non-admin with access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entity = entity
        And request dataToCreate
        When method POST
        Then status 204

        Given path ishPath
        And param entity = entity
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'filter1')].id

        And match $ == dataToAssert

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        And param entity = entity
        When method DELETE
        Then status 204