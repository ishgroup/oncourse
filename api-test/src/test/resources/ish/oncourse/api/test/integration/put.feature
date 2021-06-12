@parallel=false
Feature: Main feature for all PUT requests with path 'integration'


    Background: Authorize first
        * callonce read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'integration'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
    
    Scenario: (+) Update integration name setting 'id' in path and in body
#       Prepare new integration to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someName'
        And match response[*].name !contains 'updatedName'
        * def id = get[0] response[?(@.name == 'someName')].id

        * def integrationToUpdate = {id: '#(id)', name: 'updatedName', type: 6}
        Given path ishPath + '/' + id
        And request integrationToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName'
        And match response[*].name contains 'updatedName'

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'updatedName'}



    Scenario: (+) Update integration name setting 'id' only in path
#       Prepare new integration to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someName'
        And match response[*].name !contains 'updatedName'
        * def id = get[0] response[?(@.name == 'someName')].id

        * def integrationToUpdate = {name: 'updatedName', type: 6}
        Given path ishPath + '/' + id
        And request integrationToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someName'
        And match response[*].name contains 'updatedName'

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'updatedName'}


    Scenario: (+) Update integration name setting different 'ids' in body and in path
#       Prepare new integrations to update
#       <--->
        * def integration1 = {name: 'someName1', type: 6}
        * def integration2 = {name: 'someName2', type: 6}

        Given path ishPath
        And request integration1
        When method POST
        Then status 204

        Given path ishPath
        And request integration2
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'someName1')].id
        * def id2 = get[0] response[?(@.name == 'someName2')].id

        * def integrationToUpdate = {id: '#(id1)', name: 'updatedName', type: 6}
        Given path ishPath + '/' + id2
        And request integrationToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'updatedName'
        * def updateIntergationId = get[0] response[?(@.name == 'updatedName')].id
        * match id1 != updateIntergationId
        * match id2 == updateIntergationId

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName1'}
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'updatedName'}



        Scenario: (-) Update integration name to existing one
#       Prepare new integrations to update
#       <--->
        * def integration1 = {name: 'someName1', type: 6}
        * def integration2 = {name: 'someName2', type: 6}

        Given path ishPath
        And request integration1
        When method POST
        Then status 204

        Given path ishPath
        And request integration2
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'someName1')].id
        * def id2 = get[0] response[?(@.name == 'someName2')].id

        * def integrationToUpdate = {name: 'someName1', type: 6}
        Given path ishPath + '/' + id2
        And request integrationToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Integration name should be unique"


#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName1'}
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName2'}



    Scenario: (-) Update integration name to empty
#       Prepare new integration to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someName'
        And match response[*].name !contains ''
        * def id = get[0] response[?(@.name == 'someName')].id

        * def integrationToUpdate = {name: '', type: 6}
        Given path ishPath + '/' + id
        And request integrationToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Integration name should be specified"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}



    Scenario: (-) Update integration name to 'null' value
#       Prepare new integration to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someName'
        And match response[*].name !contains ''
        * def id = get[0] response[?(@.name == 'someName')].id

        * def integrationToUpdate = {name: null, type: 6}
        Given path ishPath + '/' + id
        And request integrationToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Integration name should be specified"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}



    Scenario: (-) Update not existing integration
#       Prepare new intergation to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->
        * def integrationToUpdate = {name: 'someNameUPD', type: 6}

        Given path ishPath + '/11111'
        And request integrationToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Integration 11111 is not exist"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}



    Scenario: (-) Update integration setting 'id' as not number
#       Prepare new intergation to update it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->
        * def integrationToUpdate = {name: 'someNameUPD', type: 6}

        Given path ishPath + '/null'
        And request integrationToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Integration id 'null' is incorrect. It must contain of only numbers"

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}