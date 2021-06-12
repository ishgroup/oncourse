@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/payment/type'
    
    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/payment/type'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Delete existing paymentType
#       Prepare new paymentType to delete it
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
        And def paymentType = get[0] response[?(@.name == 'someName')]
        
        Given path ishPath + '/' + paymentType.id
        When method DELETE
        Then status 204
        
        
    Scenario: (-) Delete system paymentType /Reverse, Zero, Contra, Voucher paymentTypes/
#       Try to delete 'Reverse' (id:10) system paymentType
        Given path ishPath + '/11'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System payment type can not be deleted"
        
#       Try to delete 'Zero' (id:5) system paymentType
        Given path ishPath + '/6'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System payment type can not be deleted"

#       Try to delete 'Contra' (id:7) system paymentType
        Given path ishPath + '/8'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System payment type can not be deleted"
        
#       Try to delete 'Voucher' (id:8) system paymentType
        Given path ishPath + '/9'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System payment type can not be deleted"
        
        
    Scenario: (-) Delete not existing paymentType
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Payment type is not exist"
        

    Scenario: (-) Delete paymentType with null ID
        Given path ishPath + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Payment type is not exist"