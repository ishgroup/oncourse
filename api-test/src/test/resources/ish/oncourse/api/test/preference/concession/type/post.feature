@parallel=false
Feature: Main feature for all POST requests with path 'preference/concession/type'
    
    
    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/concession/type'
        
        
        
    Scenario: (+) Create valid concession type

        * def concessionTypeArray = [{name: 'SomeName1', requireExpary: false, requireNumber: false, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204

#       Scenario have been finished. Now find and remove created objects from DB:
        Given path ishPath
        When method GET
        Then status 200
        And def concessionType = get[0] response[?(@.name == 'SomeName1')]

        Given path ishPath + '/' + concessionType.id
        When method DELETE
        Then status 204



    Scenario: (-) Create invalid empty concession type

        * def concessionTypeArray = [{}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession requireNumber should be specified"



    Scenario: (-) Create invalid ('name' field is empty) concession type

        * def concessionTypeArray = [{name: "", requireExpary: true, requireNumber: true, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession name should be specified"



    Scenario: (-) Create invalid ('name' field is null) concession type

        * def concessionTypeArray = [{requireExpary: true, requireNumber: true, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession name should be specified"



    Scenario: (-) Create invalid concession type without any mandatory 'requireExpary' field

        * def concessionTypeArray = [{name: 'SomeName', requireNumber: false, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession requireExpary should be specified"



    Scenario: (-) Create invalid concession type without any mandatory 'requireNumber' field

        * def concessionTypeArray = [{name: 'SomeName', requireExpary: false, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession requireNumber should be specified"



    Scenario: (-) Create invalid concession type without any mandatory 'allowOnWeb' field

        * def concessionTypeArray = [{name: 'SomeName', requireExpary: false, requireNumber: false}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession allowOnWeb should be specified"



    Scenario: (-) Create invalid concession type with existing name

#       Prepare new concession type with name 'someName':
        * def concessionTypeArray = [{name: 'SomeName2', requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'SomeName2'
#       <--->

        * def duplicateNameConcessionTypeArray = [{name: 'SomeName2', requireExpary: false, requireNumber: false, allowOnWeb: true}]

        Given path ishPath
        And request duplicateNameConcessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession name should be unique"

#       Scenario have been finished. Now find and remove created objects from DB:
        Given path ishPath
        When method GET
        Then status 200
        And def concessionType = get[0] response[?(@.name == 'SomeName2')]

        Given path ishPath + '/' + concessionType.id
        When method DELETE
        Then status 204

    

    Scenario: (-) Create a bunch of concession types with duplicate names

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName'

        * def concessionTypeArray =
        """
        [
            {name: 'someName', requireExpary: true, requireNumber: true, allowOnWeb: false},
            {name: 'someName', requireExpary: false, requireNumber: true, allowOnWeb: false}
        ]
        """
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession name should be unique: someName"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName'



    Scenario: (-) Create a bunch of concession types, some of them are INVALID

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName1'
        And match response[*].name !contains 'someName2'

        * def concessionTypeArray =
        """
        [
            {name: 'someName1', requireExpary: true, requireNumber: true, allowOnWeb: false},
            {name: 'someName2', requireNumber: true, allowOnWeb: false}
        ]
        """
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession requireExpary should be specified"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName1'
        And match response[*].name !contains 'someName2'



    Scenario: (+) Update concession type

#       Prepare new concession type
        * def concessionTypeArray = [{name: 'SomeName', requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'OtherName'
        And match response[*].name contains 'SomeName'
        * def id = get[0] response[?(@.name == 'SomeName')].id

        * def concessionTypeToUpdateArray = [{id: '#(id)', name: 'OtherName', requireExpary: false, requireNumber: false, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeToUpdateArray
        When method POST
        Then status 204

        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'OtherName'
        And match response[*].name !contains 'SomeName'

#       Scenario have been finished. Now find and remove created objects from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Change names of 2 existing concession types

#       Prepare new concession types to update it
        * def concessionTypeArray =
        """
        [
            {name: 'someName10', requireExpary: true, requireNumber: true, allowOnWeb: false},
            {name: 'someName20', requireExpary: true, requireNumber: true, allowOnWeb: false}
        ]
        """

        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'someName10')].id
        * def id2 = get[0] response[?(@.name == 'someName20')].id

        * def concessionTypeToUpdateArray =
        """
        [
            {id: '#(id2)', name: 'someName10', requireExpary: true, requireNumber: true, allowOnWeb: false},
            {id: '#(id1)', name: 'someName20', requireExpary: true, requireNumber: true, allowOnWeb: false}
        ]
        """

        Given path ishPath
        And request concessionTypeToUpdateArray
        When method POST
        Then status 204

#       Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now remove created object from DB"
        Given path ishPath + '/' + id1
        When method DELETE
        Then status 204
        Given path ishPath + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Update concession type name to existing one

#       Prepare new concession types to update it
        * def concessionTypeArray =
        """
        [
            {name: 'someName11', requireExpary: true, requireNumber: true, allowOnWeb: false},
            {name: 'someName21', requireExpary: true, requireNumber: true, allowOnWeb: false}
        ]
        """

        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'someName11')].id
        * def id2 = get[0] response[?(@.name == 'someName21')].id

        * def concessionTypeToUpdateArray = [{id: '#(id1)', name: 'someName21', requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request concessionTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Concession name should be unique'

#       Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now remove created object from DB"
        Given path ishPath + '/' + id1
        When method DELETE
        Then status 204
        Given path ishPath + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Update concession type name to empty

#       Prepare new concession types to update it:
        * def concessionTypeArray = [{name: 'someName12', requireExpary: true, requireNumber: true, allowOnWeb: false}]

        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'someName12')].id

        * def concessionTypeToUpdateArray = [{id: '#(id)', name: "", requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request concessionTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Concession name should be specified'

#       Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update concession type name to null

#       Prepare new concession types to update it:
        * def concessionTypeArray = [{name: 'someName13', requireExpary: true, requireNumber: true, allowOnWeb: false}]

        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'someName13')].id

        * def concessionTypeToUpdateArray = [{id: '#(id)', name: null, requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request concessionTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Concession name should be specified'

#       Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing concession type

        * def nonExistingConcessionTypeArray = [{id: 11111, name: 'SomeName14', requireExpary: true, requireNumber: true, allowOnWeb: false}]
        Given path ishPath
        And request nonExistingConcessionTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Concession type 11111 is not exist"


