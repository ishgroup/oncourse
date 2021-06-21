@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/survey'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/survey'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all surveys by admin

        Given path ishPathList
        And param entity = 'Survey'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004"]



    Scenario: (+) Get list of all surveys by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Survey'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004"]



    Scenario: (+) Get survey by admin

        Given path ishPath + "/1000"
        When method GET
        Then status 200

        And match $ ==
        """
        {
        "id":1000,
        "studentContactId":4,
        "studentName":"stud3",
        "netPromoterScore":null,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Public testimonial",
        "testimonial":"5 stars",
        "comment":"The tutor spent most of an enTire evening promoting his Italian mate's business",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":"#ignore"
        }
        """



    Scenario: (+) Get survey by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1001"
        When method GET
        Then status 200

        And match $ ==
        """
        {
        "id":1001,
        "studentContactId":4,
        "studentName":"stud3",
        "netPromoterScore":null,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Public testimonial",
        "testimonial":"5 stars",
        "comment":"I really enjoyed the class and I learned much more than I expected.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":"#ignore"
        }
        """



    Scenario: (+) Get not existing survey

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."