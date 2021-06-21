@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/duplicate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/duplicate'
        * def ishPathCourseClass = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * def ishPathList = 'list'
        



    Scenario: (+) Duplicate CourseClass by admin

        * def duplicateCourseClass = {"classIds":[6], "daysTo":1, "copyTutors":true, "copyTrainingPlans":true, "applyDiscounts":true, "copyCosts":true, "copySitesAndRooms":true, "copyPayableTimeForSessions":true, "copyVetData":true, "copyNotes":false, "copyAssessments":true, "copyOnlyMandatoryTags":true}

        Given path ishPath
        And request duplicateCourseClass
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'CourseClass'
        And param search = 'createdOn is today'
        And param columns = 'startDateTime,endDateTime,tutorsAbridged,feeIncGst,notes,room.name,room.site.name'
        And param sortings = 'createdOn'
        And param ascending = false
        When method GET
        Then status 200
        And def newCourseClass = response.rows[0]
        * print newCourseClass

        * def id = newCourseClass.id
        * def startDateTime = newCourseClass.values[0]
        * def endDateTime = newCourseClass.values[1]
        * def tutor = newCourseClass.values[2]
        * def fee = newCourseClass.values[3]
        * def notes = newCourseClass.values[4]
        * def room = newCourseClass.values[5]
        * def site = newCourseClass.values[6]

#       <---> Verification:
        * match startDateTime == "2027-02-02T11:14:40.000Z"
        * match endDateTime == "2027-02-06T12:14:40.000Z"
        * match fee == "220.00"
        * match room == "room2"
        * match site == "site1"

#       <---->  Scenario have been finished. Now delete created entity:
        Given path ishPathCourseClass + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Duplicate CourseClass by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * def duplicateCourseClass = {"classIds":[6], "daysTo":1, "copyTutors":true, "copyTrainingPlans":true, "applyDiscounts":true, "copyCosts":true, "copySitesAndRooms":true, "copyPayableTimeForSessions":true, "copyVetData":true, "copyNotes":false, "copyAssessments":true, "copyOnlyMandatoryTags":true}

        Given path ishPath
        And request duplicateCourseClass
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'CourseClass'
        And param search = 'createdOn is today'
        And param columns = 'startDateTime,endDateTime,tutorsAbridged,feeIncGst,notes,room.name,room.site.name'
        And param sortings = 'createdOn'
        And param ascending = false
        When method GET
        Then status 200
        And def newCourseClass = response.rows[0]

        * def id = newCourseClass.id
        * def startDateTime = newCourseClass.values[0]
        * def endDateTime = newCourseClass.values[1]
        * def tutor = newCourseClass.values[2]
        * def fee = newCourseClass.values[3]
        * def notes = newCourseClass.values[4]
        * def room = newCourseClass.values[5]
        * def site = newCourseClass.values[6]

#       <---> Verification:
        * match startDateTime == "2027-02-02T11:14:40.000Z"
        * match endDateTime == "2027-02-06T12:14:40.000Z"
        * match fee == "220.00"
        * match room == "room2"
        * match site == "site1"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathCourseClass + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Duplicate CourseClass by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * def duplicateCourseClass = {"classIds":[6], "daysTo":1, "copyTutors":true, "copyTrainingPlans":true, "applyDiscounts":true, "copyCosts":true, "copySitesAndRooms":true, "copyPayableTimeForSessions":true, "copyVetData":true, "copyNotes":false, "copyAssessments":true, "copyOnlyMandatoryTags":true}

        Given path ishPath
        And request duplicateCourseClass
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to duplicate class. Please contact your administrator"



    Scenario: (-) Duplicate not existing CourseClass

        * def duplicateCourseClass = {"classIds":[99999], "daysTo":1, "copyTutors":true, "copyTrainingPlans":true, "applyDiscounts":true, "copyCosts":true, "copySitesAndRooms":true, "copyPayableTimeForSessions":true, "copyVetData":true, "copyNotes":false, "copyAssessments":true, "copyOnlyMandatoryTags":true}

        Given path ishPath
        And request duplicateCourseClass
        When method POST
        Then status 400
        And match $.errorMessage == "Class with id=99999 not found."