@parallel=false
Feature: Main feature for all POST requests with path 'integration'
    
    
    Background: Authorize first
        * callonce read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'integration'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Create MYOB integration
        * def integration = {name: 'someName', type: {type: 'myob'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'myob'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}


    Scenario: (+) Create Moodle integration
        * def integration = {name: 'someName', type: {type: 'moodle'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'moodle'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}


    Scenario: (+) Create Mailchimp integration
        * def integration = {name: 'someName', type: {type: 'mailchimp'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'mailchimp'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}


    Scenario: (+) Create SurveyMonkey integration
        * def integration = {name: 'someName', type: {type: 'surveymonkey'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'surveymonkey'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match response[0].props[*].value contains any [null, false]

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}


    Scenario: (+) Create SurveyGizmo integration
        * def integration = {name: 'someName', type: {type: 'surveygizmo'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'surveygizmo'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match response[0].props[*].value contains any [null, false]

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}

    
    Scenario: (+) Create Xero integration
        * def integration = {name: 'someName', type: {type: 'xero'}}
        
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'xero'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}
    
    
    Scenario: (+) Create Cloud Assess integration
        * def integration = {name: 'someName', type: {type: 'cloudassess'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 'cloudassess'
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}


    Scenario: (-) Create integration with nonexisting type
        * def integration = {name: 'someName', type: {type: 'nonexistType'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration type isn't specified or incorrect"


    Scenario: (-) Create integration with type set in camelCase
#       integration types must be in LOWER case
        * def integration = {name: 'someName', type: {type: 'CloudAssess'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration type isn't specified or incorrect"


    Scenario: (-) Create integration without type
        * def integration = {name: 'someName'}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration type isn't specified or incorrect"


    Scenario: (-) Create integration without name
        * def integration = {type: {type: 'cloudassess'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be specified"
        
        
    Scenario: (-) Create integration with empty name
        * def integration = {type: {name: '', type: 'cloudassess'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be specified"
        
        
    Scenario: (-) Create integration with 'null' name
        * def integration = {type: {name: null, type: 'cloudassess'}}
        Given path ishPath
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be specified"
        
        
    Scenario: (-) Create valid integration with data set in wrong order: 'type' before 'name'
#       In case, when 'type' is going before 'name'. 'name' value is missing
        * def integration = {type: {type: 'cloudassess'}, name: 'someName'}
        Given path 'integration'
        And request integration
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be specified"
        
        
    Scenario: (-) Create 2 integration with the same names and types
        * def integration1 = {name: 'someName', type: {type: 'myob'}}
        * def integration2 = {name: 'someName', type: {type: 'myob'}}
        
        Given path ishPath
        And request integration1
        When method POST
        Then status 204
        
        Given path ishPath
        And request integration2
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be unique"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}
        
    
    
    Scenario: (-) Create 2 integration with different types and the same names
        * def integration1 = {name: 'someName', type: {type: 'xero'}}
        * def integration2 = {name: 'someName', type: {type: 'myob'}}
        
        Given path ishPath
        And request integration1
        When method POST
        Then status 204
        
        Given path ishPath
        And request integration2
        When method POST
        Then status 400
        And match response.errorMessage == "Integration name should be unique"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}
        

    Scenario: (-) Create 2 integrations
#    We CAN'T post several integration entities in one POST query
        * def integrationArray = 
        """
        [
            {name: 'someName1', type: 'myob'},
            {name: 'someName2', type: 'myob'}
        ]
        """    
        Given path ishPath
        And request integrationArray
        When method POST
        Then status 500