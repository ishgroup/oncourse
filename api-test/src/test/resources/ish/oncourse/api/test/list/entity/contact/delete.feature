@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/contact'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Contact by admin

#       <----->  Add a new entity for deleting and get id:
        * def newContact =
        """
        {
            "id": 0,
            "student": {
                "labourForceStatus": "Not stated",
                "englishProficiency": "Not stated",
                "highestSchoolLevel": "Not stated",
                "citizenship": "No information",
                "feeHelpEligible": false,
                "indigenousStatus": "Not stated",
                "priorEducationCode": "Not stated",
                "isOverseasClient": false,
                "disabilityType": "Not stated",
                "clientIndustryEmployment": "Not Stated",
                "clientOccupationIdentifier": "Not Stated"
            },
            "tutor": null,
            "firstName": "client",
            "lastName": "toDelete",
            "middleName": null,
            "birthDate": null,
            "gender": null,
            "street": null,
            "suburb": null,
            "allowSms": false,
            "allowPost": false,
            "allowEmail": true,
            "isCompany": false,
            "deliveryStatusPost": 0,
            "deliveryStatusSms": 0,
            "deliveryStatusEmail": 0,
            "customFields": {
                "cf1": "wefwef"
            },
            "tags": []
        }
        """
        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["toDelete"])].id
        * print "id = " + id
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification deleting:
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Contact by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newContact =
        """
        {
            "id": 0,
            "student": {
                "labourForceStatus": "Not stated",
                "englishProficiency": "Not stated",
                "highestSchoolLevel": "Not stated",
                "citizenship": "No information",
                "feeHelpEligible": false,
                "indigenousStatus": "Not stated",
                "priorEducationCode": "Not stated",
                "isOverseasClient": false,
                "disabilityType": "Not stated",
                "clientIndustryEmployment": "Not Stated",
                "clientOccupationIdentifier": "Not Stated"
            },
            "tutor": null,
            "firstName": "client",
            "lastName": "toDelete",
            "middleName": null,
            "birthDate": null,
            "gender": null,
            "street": null,
            "suburb": null,
            "allowSms": false,
            "allowPost": false,
            "allowEmail": true,
            "isCompany": false,
            "deliveryStatusPost": 0,
            "deliveryStatusSms": 0,
            "deliveryStatusEmail": 0,
            "customFields": {
                "cf1": "wefwef"
            },
            "tags": []
        }
        """
        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["toDelete"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification deleting:
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing Contact by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/2'
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete contact. Please contact your administrator"



    Scenario: (-) Delete Contact with relations

        Given path ishPath + '/2'
        When method DELETE
        Then status 400
        And match $.errorMessage == "There are payments for this contact."



    Scenario: (-) Delete NOT existing Contact

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

