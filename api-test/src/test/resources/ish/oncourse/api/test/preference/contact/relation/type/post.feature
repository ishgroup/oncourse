@parallel=false
Feature: Main feature for all POST requests with path 'preference/contact/relation/type'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/contact/relation/type'
        


    Scenario: (+) Create new valid ContactRelationType

        * def someContactRelationType = [{"relationName":"relationName#1","reverseRelationName":"reverseRelationName#1","portalAccess":true}]

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#1'
        And match response[*].reverseRelationName contains 'reverseRelationName#1'
        * def list = get response[?(@.relationName == 'relationName#1')]
        * match each list[*].portalAccess == true

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.relationName == 'relationName#1')].id
        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].description !contains 'relationName#1'


    Scenario: (+) Create not unique ContactRelationType

#       Prepare new ContactRelationType to create a duplicate
#       <--->
        * def someContactRelationType = [{"relationName":"relationName#2","reverseRelationName":"reverseRelationName#2","portalAccess":false}]

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#2'
#       <--->

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 7
        * def list = get response[?(@.relationName == 'relationName#2')]
        * match karate.sizeOf(list) == 2

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * def id1 = get[0] response[?(@.relationName == 'relationName#2')].id
        * def id2 = get[1] response[?(@.relationName == 'relationName#2')].id

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#2'


    Scenario: (+) Create a bunch of valid ContactRelationTypes

        * def someContactRelationTypeArray =
        """
        [
            {"relationName":"relationName#3","reverseRelationName":"reverseRelationName#3","portalAccess":false},
            {"relationName":"relationName#4","reverseRelationName":"reverseRelationName#4","portalAccess":true}
        ]
        """

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 7
        And match response[*].relationName contains ['relationName#3', 'relationName#4']

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * def id1 = get[0] response[?(@.relationName == 'relationName#3')].id
        * def id2 = get[0] response[?(@.relationName == 'relationName#4')].id

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains ['relationName#3', 'relationName#4']


    Scenario: (-) Create new invalid (empty) ContactRelationTypes

        * def someContactRelationType = [{}]

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 400
        And match response.errorMessage == "Relation name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5


    Scenario: (-) Create new invalid ('Relationship Name' field is empty) ContactRelationType

         * def someContactRelationType = [{"relationName":"","reverseRelationName":"someReverseRelationName#3","portalAccess":false}]

         Given path ishPath
         And request someContactRelationType
         When method POST
         Then status 400
         And match response.errorMessage == "Relation name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 5


    Scenario: (-) Create new invalid ('Relationship Name' is null) ContactRelationType

         * def someContactRelationType = [{"relationName":null,"reverseRelationName":"someReverseRelationName#3","portalAccess":false}]

         Given path ishPath
         And request someContactRelationType
         When method POST
         Then status 400
         And match response.errorMessage == "Relation name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 5


    Scenario: (-) Create new invalid ('Reverse relationship Name' field is empty) ContactRelationType

         * def someContactRelationType = [{"relationName":"someRelationName","reverseRelationName":"","portalAccess":true}]

         Given path ishPath
         And request someContactRelationType
         When method POST
         Then status 400
         And match response.errorMessage == "Reverse relationName name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 5


    Scenario: (-) Create new invalid ('Reverse relationship Name' is null) ContactRelationType

         * def someContactRelationType = [{"relationName":"someRelationName","reverseRelationName":null,"portalAccess":true}]

         Given path ishPath
         And request someContactRelationType
         When method POST
         Then status 400
         And match response.errorMessage == "Reverse relationName name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 5


    Scenario: (+) Update existing ContactRelationType

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationType = [{"relationName":"relationName#5","reverseRelationName":"reverseRelationName#5","portalAccess":false}]

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#5'
        And match response[*].reverseRelationName contains 'reverseRelationName#5'
        And match each response[*].portalAccess == false
#       <--->
        * def id = get[0] response[?(@.relationName == 'relationName#5')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id)',"relationName":"relationName#6","reverseRelationName":"reverseRelationName#6","portalAccess":true}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = get response[?(@.relationName == 'relationName#6')]
        * match each list[*].relationName contains 'relationName#6'
        * match each list[*].reverseRelationName contains 'reverseRelationName#6'
        * match each list[*].portalAccess == true

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains ['relationName#5', 'relationName#6']


    Scenario: (+) Update 'Relationship Name' and 'Reverse relationship Name' to existing ones

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray =
        """
        [
            {"relationName":"relationName#7","reverseRelationName":"reverseRelationName#7","portalAccess":false},
            {"relationName":"relationName#8","reverseRelationName":"reverseRelationName#8","portalAccess":false}
        ]
        """

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 7
        And match response[*].relationName contains ['relationName#7', 'relationName#8']
#       <--->

        * def id1 = get[0] response[?(@.relationName == 'relationName#7')].id
        * def id2 = get[0] response[?(@.relationName == 'relationName#8')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id2)',"relationName":"relationName#7","reverseRelationName":"reverseRelationName#7","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 7
        And match response[*].relationName !contains 'relationName#8'
        * def list = get response[?(@.relationName == 'relationName#7')]
        * match karate.sizeOf(list) == 2
        * match each list[*].relationName contains 'relationName#7'
        * match each list[*].reverseRelationName contains 'reverseRelationName#7'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains ['relationName#7', 'relationName#8']


    Scenario: (-) Update ('Relationship Name' and 'Reverse relationship Name') to system ContactRelationType

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray = [{"relationName":"relationName#9","reverseRelationName":"reverseRelationName#9","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#9'
#       <--->

        * def id1 = get[0] response[?(@.relationName == 'Parent or Guardian')].id
        * def id2 = get[0] response[?(@.relationName == 'relationName#9')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id2)',"relationName":"Parent or Guardian","reverseRelationName":"Child","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName !contains 'relationName#9'
        * def list = get response[?(@.relationName == 'Parent or Guardian')]
        * match karate.sizeOf(list) == 2
        * match each list[*].relationName contains 'Parent or Guardian'
        * match each list[*].reverseRelationName contains 'Child'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#9'


    Scenario: (-) Update 'Relationship Name' to empty

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray = [{"relationName":"relationName#10","reverseRelationName":"reverseRelationName#10","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#10'
#       <--->

        * def id = get[0] response[?(@.relationName == 'relationName#10')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id)',"relationName":"","reverseRelationName":"reverseRelationName#10","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Relation name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#10'


    Scenario: (-) Update 'Relationship Name' to null

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray = [{"relationName":"relationName#10","reverseRelationName":"reverseRelationName#10","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#10'
#       <--->

        * def id = get[0] response[?(@.relationName == 'relationName#10')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id)',"relationName":null,"reverseRelationName":"reverseRelationName#10","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Relation name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#10'


    Scenario: (-) Update 'Reverse relationship Name' to empty

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray = [{"relationName":"relationName#11","reverseRelationName":"reverseRelationName#11","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#11'
#       <--->

        * def id = get[0] response[?(@.relationName == 'relationName#11')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id)',"relationName":"relationName#11","reverseRelationName":"","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Reverse relationName name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#11'


    Scenario: (-) Update 'Reverse relationship Name' to null

#       Prepare new ContactRelationType to update it
#       <--->
        * def someContactRelationTypeArray = [{"relationName":"relationName#11","reverseRelationName":"reverseRelationName#11","portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'relationName#11'
#       <--->

        * def id = get[0] response[?(@.relationName == 'relationName#11')].id
        * def someContactRelationTypeToUpdate = [{id: '#(id)',"relationName":"relationName#11","reverseRelationName":null,"portalAccess":false}]

        Given path ishPath
        And request someContactRelationTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Reverse relationName name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].relationName !contains 'relationName#11'


    Scenario: (-) Update not existing ContactRelationType

        * def nonExistingContactRelationTypeToUpdateArray = [{id: '99999',"relationName":"relationName#12","reverseRelationName":"reverseRelationName#12","portalAccess":false}]

        Given path ishPath
        And request nonExistingContactRelationTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Contact relation type 99999 is not exist"
