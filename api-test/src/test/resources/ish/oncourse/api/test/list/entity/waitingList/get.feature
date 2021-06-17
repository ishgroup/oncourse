@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/waitingList'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/waitingList'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Get list of all waitingLists by admin

        Given path ishPathList
        And param entity = 'WaitingList'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001","1000"]



    Scenario: (+) Get waitingList by admin

        Given path ishPath + "/1000"
        When method GET
        Then status 200

        And match $ ==
            """
            {
            "id":1000,
            "privateNotes":"Some notes 1",
            "studentNotes":"not editable student notes",
            "studentCount":1,
            "contactId":4,
            "studentName":"stud3",
            "courseId":2,
            "courseName":"Course2 course2",
            "tags":
                [
                {"id":221,"name":"waitingList 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]},
                {"id":222,"name":"waitingList 2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}
                ],
            "sites":
                [
                {"id":200,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null},
                {"id":201,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"site1","street":null,"suburb":"Adelaide","state":null,"postcode":"5000","country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}
                ],
            "customFields":{},
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (-) Get not existing waitingList

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "WaitingList with id:9999 doesn't exist"


    Scenario: (+) Get list of all waitingLists by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'WaitingList'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001","1000"]



    Scenario: (+) Get list of all waitingLists by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'WaitingList'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (+) Get waitingList by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + "/1001"
        When method GET
        Then status 200

        And match $ ==
            """
            {
            "id":1001,
            "privateNotes":"some notes 2",
            "studentNotes":null,
            "studentCount":3,
            "contactId":3,
            "studentName":"stud2",
            "courseId":2,
            "courseName":"Course2 course2",
            "tags":[],
            "sites":[{"id":202,"isAdministrationCentre":null,"isVirtual":null,"isShownOnWeb":null,"kioskUrl":null,"name":"Default site 2","street":null,"suburb":null,"state":null,"postcode":null,"country":null,"timezone":null,"longitude":null,"latitude":null,"drivingDirections":null,"publicTransportDirections":null,"specialInstructions":null,"tags":[],"rooms":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
            "customFields":{},
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (-) Get waitingList by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1001"
        When method GET
        Then status 403

        And match $.errorMessage == "Sorry, you have no permissions to get waitingList. Please contact your administrator"