@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/course'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Courses by admin

        Given path ishPathList
        And param entity = 'Course'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4"]



    Scenario: (+) Get Course by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ contains
        """
       {"relatedSellables":[],"code":"course1","qualificationId":1,"documents":[],"isTraineeship":false,"customFields":{},"hasEnrolments":true,"rules":[],"fieldOfEducation":"0313","createdOn":"#ignore","modifiedOn":"#ignore","studentWaitingListCount":0,"reportableHours":0,"dataCollectionRuleId":102,"id":1,"selfPacedclassesCount":0,"webDescription":null,"passedClasseCount":1,"isVET":true,"cancelledClassesCount":0,"currentClassesCount":1,"qualNationalCode":"UEE30807","allowWaitingLists":true,"modules":[],"tags":[],"qualLevel":"Certificate III in","enrolmentType":"Open enrolment","isSufficientForQualification":false,"brochureDescription":null,"currentlyOffered":true,"name":"Course1","dataCollectionRuleName":"Accredited course","futureClasseCount":1,"unscheduledClasseCount":0,"qualTitle":"Electrotechnology Electrician","status":"Enabled and visible online"}
        """



    Scenario: (+) Get Course without Qualification by admin

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ contains
        """
        {"relatedSellables":[],"code":"course2","qualificationId":null,"documents":[],"isTraineeship":false,"customFields":{},"hasEnrolments":true,"rules":[],"fieldOfEducation":null,"createdOn":"#ignore","modifiedOn":"#ignore","studentWaitingListCount":2,"reportableHours":0,"dataCollectionRuleId":102,"id":2,"selfPacedclassesCount":0,"webDescription":null,"passedClasseCount":0,"isVET":false,"cancelledClassesCount":0,"currentClassesCount":3,"qualNationalCode":null,"allowWaitingLists":true,"modules":[],"tags":[],"qualLevel":null,"enrolmentType":"Open enrolment","isSufficientForQualification":false,"brochureDescription":null,"currentlyOffered":true,"name":"Course2","dataCollectionRuleName":"Accredited course","futureClasseCount":0,"unscheduledClasseCount":0,"qualTitle":null,"status":"Enabled and visible online"}
        """



    Scenario: (-) Get not existing Course

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get list of all Courses by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Course'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4"]



    Scenario: (+) Get Course by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/4'
        When method GET
        Then status 200
