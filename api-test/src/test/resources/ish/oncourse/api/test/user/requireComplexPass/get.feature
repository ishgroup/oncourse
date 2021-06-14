@parallel=false
Feature: Main feature for all PUT requests with path 'user/requireComplexPass'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/requireComplexPass'
        * def ishPreferencePath = 'preference'
        * def ishPathUser = 'user'
        * def ishPathLogin = 'login'


    Scenario: (+) Check requireComplexPass

        Given path ishPath
        When method GET
        Then status 200
        And match $ == 'false'


    Scenario: (+) Check changed requireComplexPass

#       <---> Set new value for preference
        Given path ishPreferencePath
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match $ == 'true'

#       <---> Return old value
        Given path ishPreferencePath
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match $ == 'false'