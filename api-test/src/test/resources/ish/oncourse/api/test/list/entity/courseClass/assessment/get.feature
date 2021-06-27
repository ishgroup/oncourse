@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/assessment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/assessment'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass assessment by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ == [{"id":1000,"assessmentId":1000,"courseClassId":1,"assessmentCode":"code1","gradingTypeId":1,"assessmentName":"assessment 1","contactIds":[1],"moduleIds":[],"releaseDate":"2019-10-01T13:30:56.000Z","dueDate":"2019-11-10T12:31:01.000Z","submissions":[]}]



    Scenario: (+) Get CourseClass assessment by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ == [{"id":1000,"assessmentId":1000,"courseClassId":1,"assessmentCode":"code1","assessmentName":"assessment 1","gradingTypeId":1,"contactIds":[1],"moduleIds":[],"releaseDate":"2019-10-01T13:30:56.000Z","dueDate":"2019-11-10T12:31:01.000Z","submissions":[]}]



    Scenario: (-) Get not existing CourseClass assessment

        Given path ishPath + '/99999'
        When method GET
        Then status 200
        And match $ == []
