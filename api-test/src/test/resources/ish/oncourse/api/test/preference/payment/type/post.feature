@parallel=false
Feature: Main feature for all POST requests with path 'preference/payment/type'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/payment/type'
        


    Scenario: (+) Create new valid paymentType
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]

        Given path ishPath
        And request somePaymentTypeArray
        When method POST
        Then status 204

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200
        And def paymentType = get[0] response[?(@.name == 'someName')]

        Given path ishPath + '/' + paymentType.id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Create new invalid (empty) paymentType
        * def emptyPaymentTypeArray = [{}]

        Given path ishPath
        And request emptyPaymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment type name can not be empty"


    Scenario: (-) Create new invalid (not 'asset' accountId) paymentType
        * def paymentTypeArray = [{name: 'SomeName', active: true, reconcilable: true, bankedAuto: true, accountId: "4", undepositAccountId: "4", type: "Cheque"}]

        Given path ishPath
        And request paymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment account is wrong"


    Scenario: (-) Create paymentType with not unique name
        * def nonuniquePaymentTypeArray = [{name: 'Cash', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Cheque"}]

        Given path ishPath
        And request nonuniquePaymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment type with name Cash already exist"


    Scenario: (-) Create additional 'Credit card' paymentType
        * def creditcardPaymentTypeArray = [{name: 'somename', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Credit card"}]

        Given path ishPath
        And request creditcardPaymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "You already have a real time credit card method. Only one such payment method allowed in onCourse"


    Scenario: (+) Create a bunch of valid paymentTypes
        * def paymentTypeArray = 
        """
        [
            {name: 'someName1', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"},
            {name: 'someName2', active: false, reconcilable: false, bankedAuto: false, accountId: "1", undepositAccountId: "1", type: "Cheque"}
        ]
        """

        Given path ishPath
        And request paymentTypeArray
        When method POST
        Then status 204

#       Scenario have been finished. Now find and remove created objects from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200
        And def paymentType1 = get[0] response[?(@.name == 'someName1')]
        And def paymentType2 = get[0] response[?(@.name == 'someName2')]

        Given path ishPath + '/' + paymentType1.id
        When method DELETE
        Then status 204

        Given path ishPath + '/' + paymentType2.id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Create a bunch of paymentTypes, some of them are INVALID because without name
        * def paymentTypeArray =
        """
        [
            {name: 'someName1', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"},
            {name: '', active: false, reconcilable: false, bankedAuto: false, accountId: "1", undepositAccountId: "1", type: "Cheque"}
        ]
        """

        Given path ishPath
        And request paymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment type name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName1'
        And match response[*].name !contains ''


    Scenario: (-) Create a bunch of paymentTypes with duplicate names
        * def paymentTypeArray =
        """
        [
            {name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"},
            {name: 'someName', active: false, reconcilable: false, bankedAuto: false, accountId: "1", undepositAccountId: "1", type: "Cheque"}
        ]
        """

        Given path ishPath
        And request paymentTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment type name should be unique: someName"


    Scenario: (+) Update existing paymentType
#       Prepare new paymentType to update it
#       <--->
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]

        Given path ishPath
        And request somePaymentTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'updatedName'
        * def id = get[0] response[?(@.name == 'someName')].id

        * def paymentTypeToUpdateArray = [{id: '#(id)', name: 'updatedName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Cheque"}]

        Given path ishPath
        And request paymentTypeToUpdateArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'updatedName'        

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Update paymentType's name to existing one
#       Prepare new paymentType to update it
#       <--->
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]

        Given path ishPath
        And request somePaymentTypeArray
        When method POST
        Then status 204

        * def existedName = 'Cash'
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains '#(existedName)'
        And match response[*].name contains 'someName'

        * def id = get[0] response[?(@.name == 'someName')].id

        * def paymentTypeToUpdateArray = [{id: '#(id)', name: '#(existedName)', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Cheque"}]
        Given path ishPath
        And request paymentTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Payment type with name Cash already exist'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now remove created object from DB"
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Update paymentType's accountId to not 'asset' account
#       Prepare new paymentType to update it
#       <--->
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]

        Given path ishPath
        And request somePaymentTypeArray
        When method POST
        Then status 204        
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someName'
        * def id = get[0] response[?(@.name == 'someName')].id

        * def paymentTypeToUpdateArray = [{id: '#(id)', name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "4", undepositAccountId: "4", type: "Cheque"}]
        Given path ishPath
        And request paymentTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment account is wrong"  

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now remove created object from DB"
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Update not existing paymentType
        * def nonExistingPaymentTypeToUpdateArray = [{id: 11111, name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]
        Given path ishPath
        And request nonExistingPaymentTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Payment type 11111 is not exist"
        
        
    Scenario: (-) Change names of 2 existing paymentTypes
#       Prepare new paymentType to update it
#       <--->
        * def paymentTypeArray = 
        """
        [
            {name: 'someName1', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"},
            {name: 'someName2', active: false, reconcilable: false, bankedAuto: false, accountId: "1", undepositAccountId: "1", type: "Cheque"}
        ]
        """

        Given path ishPath
        And request paymentTypeArray
        When method POST
        Then status 204     
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def firstId = get[0] response[?(@.name == 'someName1')].id
        * def firstName = get[0] response[?(@.name == 'someName1')].name
        
        * def secondId = get[0] response[?(@.name == 'someName2')].id
        * def secondName = get[0] response[?(@.name == 'someName2')].name
        
        * def paymentTypeArrayToUpdate = 
        """
        [
            {id: '#(firstId)', name: '#(secondName)', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"},
            {id: '#(secondId)', name: '#(firstName)', active: false, reconcilable: false, bankedAuto: false, accountId: "1", undepositAccountId: "1", type: "Cheque"}
        ]
        """
        
        Given path ishPath
        And request paymentTypeArrayToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "To replace payment types' names you should remove that paymentTypes"
  
#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now remove created objects from DB"
        Given path ishPath + '/' + firstId
        When method DELETE
        Then status 204
        
        Given path ishPath + '/' + secondId
        When method DELETE
        Then status 204
#       <--->
