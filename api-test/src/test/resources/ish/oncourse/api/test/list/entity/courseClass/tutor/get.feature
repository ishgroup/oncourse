@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/tutor'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass tutor by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ == [{"id":1,"classId":1,"contactId":1,"roleId":4,"tutorName":"tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}]



    Scenario: (+) Get CourseClass tutor by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200

    Scenario: (-) Get not existing CourseClass tutor

        Given path ishPath + '/99999'
        When method GET
        Then status 400