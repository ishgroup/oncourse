@parallel=false
Feature: Main feature for all POST requests with path 'preference/column'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/column'
        



    Scenario: (+) Change preferenceLeftColumnWidth

        Given path ishPath
        And request {"preferenceLeftColumnWidth":400}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":400,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}

#       <---> Scenario have been finished. Now change back settings:
        Given path ishPath
        And request {"preferenceLeftColumnWidth":200}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}



    Scenario: (+) Change tagLeftColumnWidth

        Given path ishPath
        And request {"tagLeftColumnWidth":360}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":360,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}

#       <---> Scenario have been finished. Now change back settings:
        Given path ishPath
        And request {"tagLeftColumnWidth":200}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}



    Scenario: (+) Change securityLeftColumnWidth

        Given path ishPath
        And request {"securityLeftColumnWidth":300}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":300,"automationLeftColumnWidth":250}

#       <---> Scenario have been finished. Now change back settings:
        Given path ishPath
        And request {"securityLeftColumnWidth":200}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}



    Scenario: (+) Change automationLeftColumnWidth

        Given path ishPath
        And request {"automationLeftColumnWidth":350}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":350}

#       <---> Scenario have been finished. Now change back settings:
        Given path ishPath
        And request {"automationLeftColumnWidth":250}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}

