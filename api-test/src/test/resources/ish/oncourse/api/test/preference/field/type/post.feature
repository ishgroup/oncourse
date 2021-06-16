@parallel=false
Feature: Main feature for all POST requests with path 'preference/field/type'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/field/type'
        


    Scenario: (+) Create new valid fieldType

        * def someFieldType = [{"dataType":"Pattern text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment", "pattern":"^\d{4}\*{8}\d{4}&"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * print "id = " + id

        * def fieldType = get[0] response[?(@.name == 'fieldType#1')]
        * print "fieldType = " + fieldType

        And match fieldType == {"dataType":"Pattern text","id":"#(id)","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment","created":"#ignore","modified":"#ignore","pattern":"^\d{4}\*{8}\d{4}&"}

#       <---> Scenario have been finished. Now find and remove created object from DB:

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'fieldType#1'


    Scenario: (+) Create a bunch of valid fieldTypes

        * def someFieldTypeArray =
        """
        [
            {"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"},
            {"dataType":"Text","name":"fieldType#2","defaultValue":"someValue","fieldKey":"fieldKey2","mandatory":false,"sortOrder":0,"entityType":"Application"},
            {"dataType":"Text","name":"fieldType#3","defaultValue":"someValue","fieldKey":"fieldKey3","mandatory":true,"sortOrder":0,"entityType":"Contact"},
            {"dataType":"Text","name":"fieldType#4","defaultValue":"someValue","fieldKey":"fieldKey4","mandatory":true,"sortOrder":0,"entityType":"Course"},
            {"dataType":"Text","name":"fieldType#5","defaultValue":"someValue","fieldKey":"fieldKey5","mandatory":true,"sortOrder":0,"entityType":"WaitingList"}
        ]
        """

        Given path ishPath
        And request someFieldTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains ['fieldType#1', 'fieldType#2', 'fieldType#3', 'fieldType#4', 'fieldType#5']
        And match response[*].entityType contains ['Enrolment', 'Application', 'Contact', 'Course', 'WaitingList']

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'fieldType#1')].id
        * def id2 = get[0] response[?(@.name == 'fieldType#2')].id
        * def id3 = get[0] response[?(@.name == 'fieldType#3')].id
        * def id4 = get[0] response[?(@.name == 'fieldType#4')].id
        * def id5 = get[0] response[?(@.name == 'fieldType#5')].id

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id3)'}
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id4)'}
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id5)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Create a bunch of fieldTypes some of them are invalid

        * def someFieldTypeArray =
        """
        [
            {"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"},
            {"dataType":"Text","name":"","defaultValue":"someValue","fieldKey":"","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}

        ]
        """

        Given path ishPath
        And request someFieldTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"


    Scenario: (-) Create new invalid (empty) fieldType

        * def someFieldType = [{}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"


    Scenario: (-) Create new invalid ('Name' field is empty) fieldType

        * def someFieldType = [{"name":"","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"


    Scenario: (-) Create new invalid ('Name' field is null) fieldType

        * def someFieldType = [{"dataType":"Text","name":null,"defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"


    Scenario: (-) Create new invalid ('Custom field key' field is empty) fieldType

        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key should be specified"


    Scenario: (-) Create new invalid ('Custom field key' field is null) fieldType

        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":null,"mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key should be specified"


    Scenario: (-) Create new invalid ('Record Type' field is empty) fieldType

        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":""}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field entity type should be specified"


    Scenario: (-) Create new invalid ('Record Type' field is null) fieldType

        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":null}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field entity type should be specified"


    Scenario: (-) Create new invalid (not unique 'Name') fieldType

#       Prepare new fieldType
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey2","mandatory":false,"sortOrder":0,"entityType":"Contact"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be unique"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].description !contains 'fieldType#1'


    Scenario: (-) Create new invalid (not unique 'Custom field key') fieldType

#       Prepare new fieldType
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def someFieldType = [{"dataType":"Text","name":"fieldType#2","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Contact"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key should be unique"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].description !contains 'fieldType#1'


    Scenario: (-) Create new invalid (not alphanumeric symbols for 'Custom field key') fieldType

         * table notAllowedSymbols
             | symbol      |
             | '123$key'   |
             | '123!key'   |
             | '123@key'   |
             | '123#key'   |
             | '123%key'   |
             | '123^key'   |
             | '123&key'   |
             | '123*key'   |
             | '123()key'  |
             | '123[]key'  |
             | '123{}key'  |
             | '123_key'   |
             | '123=key'   |
             | '123+key'   |
             | '123-key'   |
             | '123±key'   |
             | '123§key'   |
             | '123/key'   |
             | '123:key'   |
             | '123;key'   |
             | '123.key'   |
             | '123 key'   |
             | '123<key'   |
             | '123?key'   |
             | '123`key'   |
             | '123~key'   |

          * call read('getSymbols.feature') notAllowedSymbols



    Scenario: (+) Update existing fieldType to valid

#       Prepare new fieldType to update it:
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList", "pattern":null}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * print "id = " + id

        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#upd","defaultValue":"someValueUpd","fieldKey":"fieldKey1","mandatory":true,"sortOrder":1,"entityType":"WaitingList","pattern":null}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#upd')].id
        * print "id = " + id

        * def fieldType = get[0] response[?(@.name == 'fieldType#upd')]
        * print "fieldType = " + fieldType

        And match fieldType == {"dataType":"Text","id":"#(id)","name":"fieldType#upd","defaultValue":"someValueUpd","fieldKey":"fieldKey1","mandatory":true,"sortOrder":1,"entityType":"WaitingList","created":"#ignore","modified":"#ignore","pattern":null}

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].description !contains 'fieldType#upd'


    Scenario: (-) Update 'Name' to empty

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Name' to null

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":null,"defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Name' to not unique

#       Prepare two new fieldTypes to update one of it
#       <--->
        * def someFieldTypeArray =
        """
        [
            {"dataType":"Text","name":"fieldType#1","defaultValue":"someValue1","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"},
            {"dataType":"Text","name":"fieldType#2","defaultValue":"someValue2","fieldKey":"fieldKey2","mandatory":true,"sortOrder":1,"entityType":"Contact"}
        ]
        """

        Given path ishPath
        And request someFieldTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains ['fieldType#1', 'fieldType#2']
#       <--->

        * def id1 = get[0] response[?(@.name == 'fieldType#1')].id
        * def id2 = get[0] response[?(@.name == 'fieldType#2')].id

        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id2)',"name":"fieldType#1","defaultValue":"someValue2","fieldKey":"fieldKey2","mandatory":true,"sortOrder":1,"entityType":"Contact"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field name should be unique"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Custom field key' to empty

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Custom field key' to null

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":null,"mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Custom field key' to other value

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKeyUPD","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field key can not be changed"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Record Type' to empty

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":""}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field entity type should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Record Type' to null

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":null}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field entity type should be specified"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update 'Record Type' to other value

#       Prepare new fieldType to update it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'fieldType#1'
#       <--->

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * def someFieldTypeToUpdate = [{"dataType":"Text",id: '#(id)',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Contact"}]

        Given path ishPath
        And request someFieldTypeToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field entity type can not be changed"

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200


    Scenario: (-) Update not existing fieldType

        * def someFieldType = [{id: '99999',"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"WaitingList"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "Custom field type 99999 is not exist"