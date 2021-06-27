@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/waitingList'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/waitingList'
        * def ishPathPlain = 'list/plain'
        * def ishPathCustomFieldsType = 'preference/field/type'
        



    Scenario: (+) Update WaitingList by admin

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 30",
        "studentNotes":null,
        "studentCount":30,
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

        * def id = get[0] response.rows[?(@.values == ["30"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 30 UPD",
            "studentNotes":"should be null after creation",
            "studentCount":31,
            "contactId":2,
            "courseId":1,
            "tags":[],
            "sites":[],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
            """
            {
            "id":"#number",
            "privateNotes":"Some private notes 30 UPD",
            "studentNotes":null,
            "studentCount":31,
            "contactId":2,
            "studentName":"stud1",
            "courseId":1,
            "courseName":"Course1 course1",
            "tags":[],
            "sites":[],
            "customFields":{},
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update WaitingList by notadmin with rights

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 32",
        "studentNotes":null,
        "studentCount":32,
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

        * def id = get[0] response.rows[?(@.values == ["32"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 32 UPD",
            "studentNotes":null,
            "studentCount":33,
            "contactId":3,
            "courseId":2,
            "tags":[{"id":222}],
            "sites":[{"id":201}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
            """
            {
            "id":"#number",
            "privateNotes":"Some private notes 32 UPD",
            "studentNotes":null,
            "studentCount":33,
            "contactId":3,
            "studentName":"stud2",
            "courseId":2,
            "courseName":"Course2 course2",
            "tags":[{"id":222,"name":"waitingList 2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
            "sites":[{"id":201,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"site1","street":null,"suburb":"Adelaide","state":null,"postcode":"5000","country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
            "customFields":{},
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList by notadmin without rights

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 34",
        "studentNotes":null,
        "studentCount":34,
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

        * def id = get[0] response.rows[?(@.values == ["34"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 34 UPD",
            "studentNotes":null,
            "studentCount":35,
            "contactId":3,
            "courseId":2,
            "tags":[{"id":222}],
            "sites":[{"id":201}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit waitingList. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList contact field to empty

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 36",
        "studentNotes":null,
        "studentCount":36,
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

        * def id = get[0] response.rows[?(@.values == ["36"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 36",
            "studentNotes":null,
            "studentCount":36,
            "contactId":null,
            "courseId":2,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Student is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList contact field to not existing

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 37",
        "studentNotes":null,
        "studentCount":37,
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

        * def id = get[0] response.rows[?(@.values == ["37"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 37",
            "studentNotes":null,
            "studentCount":37,
            "contactId":99999,
            "courseId":2,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Contact with id:99999 doesn't exist"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList contact to not a student

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 38",
        "studentNotes":null,
        "studentCount":38,
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

        * def id = get[0] response.rows[?(@.values == ["38"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 38",
            "studentNotes":null,
            "studentCount":38,
            "contactId":1,
            "courseId":2,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Contact is not a student."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList course field to empty

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 39",
        "studentNotes":null,
        "studentCount":39,
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

        * def id = get[0] response.rows[?(@.values == ["39"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 39",
            "studentNotes":null,
            "studentCount":39,
            "contactId":3,
            "courseId":null,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Course is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList course field to not existing

#       <----->  Add a new entity to update and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some private notes 40",
        "studentNotes":null,
        "studentCount":40,
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

        * def id = get[0] response.rows[?(@.values == ["40"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some private notes 40",
            "studentNotes":null,
            "studentCount":40,
            "contactId":3,
            "courseId":99999,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Course with id:99999 doesn't exist"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update WaitingList mandatory Custom field to empty

#      <---> Pretest: Create mandatory and optional Custom fields for WaitingList in Preferences:

        * def someFieldType =
        """
        [
        {"dataType":"Text", "name":"Age","defaultValue":"18;19;20;*","fieldKey":"age","mandatory":false,"sortOrder":0,"entityType":"WaitingList"},
        {"dataType":"Text", "name":"Passport number","defaultValue":"value","fieldKey":"passportNumber","mandatory":true,"sortOrder":0,"entityType":"WaitingList"}
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
        "studentCount":41,
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

        * def id = get[0] response.rows[?(@.values == ["41"])].id
        * print "id = " + id
#       <--->

        * def waitingListToUpdate =
            """
            {
            "id":"#(id)",
            "privateNotes":"Some notes 10",
            "studentNotes":null,
            "studentCount":41,
            "contactId":3,
            "courseId":2,
            "tags":[{"id":221}],
            "sites":[{"id":200}],
            "customFields":{"age":"18","passportNumber":""}
            }
            """

        Given path ishPath + '/' + id
        And request waitingListToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Passport number is required."

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