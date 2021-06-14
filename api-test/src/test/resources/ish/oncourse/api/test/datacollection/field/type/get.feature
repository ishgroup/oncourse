@parallel=false
Feature: Main feature for all GET requests with path '/datacollection/field/type'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/field/type'
        


    Scenario: (+) Get all fields for 'Enrolment' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'Enrolment'
        When method GET
        Then status 200
#        And match karate.sizeOf(response) == 33

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 1
        * match karate.sizeOf(contactFT) == 1
        * match karate.sizeOf(applicationFT) == 0
        * match karate.sizeOf(waitingListFT) == 0
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')
        
        
    Scenario: (+) Get all fields for 'Application' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'Application'
        When method GET
        Then status 200
#        And match karate.sizeOf(response) == 33

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 0
        * match karate.sizeOf(contactFT) == 1
        * match karate.sizeOf(applicationFT) == 1
        * match karate.sizeOf(waitingListFT) == 0
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')


    Scenario: (+) Get all fields for 'WaitingList' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'WaitingList'
        When method GET
        Then status 200
#        And match karate.sizeOf(response) == 33

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 0
        * match karate.sizeOf(contactFT) == 1
        * match karate.sizeOf(applicationFT) == 0
        * match karate.sizeOf(waitingListFT) == 1
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')


    Scenario: (+) Get all fields for 'Survey' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'Survey'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 0
        * match karate.sizeOf(contactFT) == 0
        * match karate.sizeOf(applicationFT) == 0
        * match karate.sizeOf(waitingListFT) == 0
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')
#

    Scenario: (+) Get all fields for 'Payer' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'Payer'
        When method GET
        Then status 200
#        And match karate.sizeOf(response) == 32

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 0
        * match karate.sizeOf(contactFT) == 1
        * match karate.sizeOf(applicationFT) == 0
        * match karate.sizeOf(waitingListFT) == 0
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')


    Scenario: (+) Get all fields for 'Parent' form type
        * call read('createFieldTypes.feature')

        Given path ishPath
        And param formType = 'Parent'
        When method GET
        Then status 200
#        And match karate.sizeOf(response) == 32

        * def enrolmentFT = get response[?(@.label == 'enrolmentFieldType')]
        * def contactFT = get response[?(@.label == 'contactFieldType')]
        * def applicationFT = get response[?(@.label == 'applicationFieldType')]
        * def waitingListFT = get response[?(@.label == 'waitingListFieldType')]
        * def courseFT = get response[?(@.label == 'courseFieldType')]

        * match karate.sizeOf(enrolmentFT) == 0
        * match karate.sizeOf(contactFT) == 1
        * match karate.sizeOf(applicationFT) == 0
        * match karate.sizeOf(waitingListFT) == 0
        * match karate.sizeOf(courseFT) == 0

        * call read('removeFieldTypes.feature')


    Scenario: (-) Get all fields without 'formType' param in query
        Given path ishPath
        When method GET
        Then status 400
        And match response.errorMessage == "Data collection type null is not exist"


    Scenario: (-) Get all fields with 'formType' param = null in query
        Given path ishPath
        And param formType = null
        When method GET
        Then status 400
        And match response.errorMessage == "Data collection type null is not exist"


    Scenario: (-) Get all fields with 'formType' param set in lower case
        Given path ishPath
        And param formType = 'enrolment'
        When method GET
        Then status 400
        And match response.errorMessage == "Data collection type enrolment is not exist"


    Scenario: (-) Get all fields with 'formType' param set in upper case
        Given path ishPath
        And param formType = 'ENROLMENT'
        When method GET
        Then status 400
        And match response.errorMessage == "Data collection type ENROLMENT is not exist"