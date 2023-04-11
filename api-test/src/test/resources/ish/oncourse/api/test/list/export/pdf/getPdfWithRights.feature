@ignore
@parallel=false
Feature: Re-usable feature to get PDF with access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entityName = entity
        And request dataToExport
        When method POST
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}

        Given path ishPath + '/' + proxyId
        And param entityName = entity
        When method GET
        Then status 200
        And match $ contains 'PDF'
