@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/waitingList'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/waitingList'
        * def ishPathList = 'list'
        * def ishPathPlain = 'list/plain'
        * def ishPathCustomFieldsType = 'preference/field/type'
        



    Scenario: (+) Create WaitingList by admin

#      <---> Create new entity ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 10",
        "studentNotes":"should be null after creation",
        "studentCount":10,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["10"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":"#number",
        "privateNotes":"Some notes 10",
        "studentNotes":null,
        "studentCount":10,
        "contactId":3,
        "studentName":"stud2",
        "courseId":2,
        "courseName":"Course2 course2",
        "tags":[{"id":221,"name":"waitingList 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "sites":[{"id":200,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create WaitingList with Custom fields which have both mandatory and optional fields

#      <---> Pretest: Create mandatory and optional Custom fields for WaitingList in Preferences:

        * def someFieldType =
        """
        [
        {"dataType":"Text","name":"Age","defaultValue":"18;19;20;*","fieldKey":"age","mandatory":false,"sortOrder":0,"entityType":"WaitingList"},
        {"dataType":"Text","name":"Passport number","defaultValue":"value","fieldKey":"passportNumber","mandatory":true,"sortOrder":0,"entityType":"WaitingList"}
        ]
        """

        Given path ishPathCustomFieldsType
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPathCustomFieldsType
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Age')].id
        * def id2 = get[0] response[?(@.name == 'Passport number')].id
        * print "id1 = " + id1
        * print "id2 = " + id2

#      <---> Create new entity ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 10",
        "studentNotes":null,
        "studentCount":80,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{"age":"18","passportNumber":"1234567890"}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["80"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":"#number",
        "privateNotes":"Some notes 10",
        "studentNotes":null,
        "studentCount":80,
        "contactId":3,
        "studentName":"stud2",
        "courseId":2,
        "courseName":"Course2 course2",
        "tags":[{"id":221,"name":"waitingList 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "sites":[{"id":200,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
        "customFields":{"age":"18","passportNumber":"1234567890"},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Remove all Custom fields for WaitingList from Preferences:
        Given path ishPathCustomFieldsType + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCustomFieldsType + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (+) Create WaitingList with mandatory Custom field and without optional Custom field

#      <---> Pretest: Create mandatory and optional Custom fields for WaitingList in Preferences:

        * def someFieldType =
        """
        [
        {"dataType":"Text","name":"Age","defaultValue":"18;19;20;*","fieldKey":"age","mandatory":false,"sortOrder":0,"entityType":"WaitingList"},
        {"dataType":"Text","name":"Passport number","defaultValue":"value","fieldKey":"passportNumber","mandatory":true,"sortOrder":0,"entityType":"WaitingList"}
        ]
        """

        Given path ishPathCustomFieldsType
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPathCustomFieldsType
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Age')].id
        * def id2 = get[0] response[?(@.name == 'Passport number')].id
        * print "id1 = " + id1
        * print "id2 = " + id2

#      <---> Create new entity ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 10",
        "studentNotes":"should be null after creation",
        "studentCount":81,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{"passportNumber":"1234567890"}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["81"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":"#number",
        "privateNotes":"Some notes 10",
        "studentNotes":null,
        "studentCount":81,
        "contactId":3,
        "studentName":"stud2",
        "courseId":2,
        "courseName":"Course2 course2",
        "tags":[{"id":221,"name":"waitingList 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "sites":[{"id":200,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
        "customFields":{"passportNumber":"1234567890"},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Remove all Custom fields for WaitingList from Preferences:
        Given path ishPathCustomFieldsType + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCustomFieldsType + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Create WaitingList without mandatory Custom field

#      <---> Pretest: Create mandatory and optional Custom fields for WaitingList in Preferences:

        * def someFieldType =
        """
        [
        {"dataType":"Text","name":"Age","defaultValue":"18;19;20;*","fieldKey":"age","mandatory":false,"sortOrder":0,"entityType":"WaitingList"},
        {"dataType":"Text","name":"Passport number","defaultValue":"value","fieldKey":"passportNumber","mandatory":true,"sortOrder":0,"entityType":"WaitingList"}
        ]
        """

        Given path ishPathCustomFieldsType
        And request someFieldType
        When method POST
        Then status 204

        Given path ishPathCustomFieldsType
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Age')].id
        * def id2 = get[0] response[?(@.name == 'Passport number')].id
        * print "id1 = " + id1
        * print "id2 = " + id2


#      <---> Create new entity ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 10",
        "studentNotes":"should be null after creation",
        "studentCount":82,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Passport number is required."

#       <---> Remove all Custom fields for WaitingList from Preferences:
        Given path ishPathCustomFieldsType + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCustomFieldsType + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Create WaitingList when contact is not a student

#       <---> Contact is tutor:
        * def newWaitingList = {"studentCount":4,"courseId":2,"contactId":1}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Contact is not a student."

#       <---> Contact is company:
        * def newWaitingList = {"studentCount":4,"courseId":2,"contactId":7}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Contact is not a student."



    Scenario: (-) Create WaitingList for not existing contact

        * def newWaitingList = {"studentCount":4,"courseId":2,"contactId":99999}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Contact with id:99999 doesn't exist"



    Scenario: (-) Create WaitingList without contact

        * def newWaitingList = {"studentCount":4,"courseId":2,"contactId":null}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Student is required."



    Scenario: (-) Create WaitingList for not existing course

        * def newWaitingList = {"studentCount":4,"courseId":99999,"contactId":3}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Course with id:99999 doesn't exist"



    Scenario: (-) Create WaitingList without course

        * def newWaitingList = {"studentCount":4,"courseId":null,"contactId":3}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 400
        And match $.errorMessage == "Course is required."



    Scenario: (+) Create WaitingList by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#      <---> Create new entity ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 11",
        "studentNotes":null,
        "studentCount":11,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["11"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":"#number",
        "privateNotes":"Some notes 11",
        "studentNotes":null,
        "studentCount":11,
        "contactId":3,
        "studentName":"stud2",
        "courseId":2,
        "courseName":"Course2 course2",
        "tags":[{"id":221,"name":"waitingList 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "sites":[{"id":200,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create WaitingList by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def newWaitingList = {"studentCount":4,"courseId":2,"contactId":{"id":1}}

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create waitingList. Please contact your administrator"



