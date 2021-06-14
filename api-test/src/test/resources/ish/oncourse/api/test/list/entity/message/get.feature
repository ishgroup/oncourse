@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/message'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/message'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Messages by admin

        Given path ishPathList
        And param entity = 'Message'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003"]



    Scenario: (+) Get list of all Messages by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'Message'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003"]



    Scenario: (+) Get Message by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "subject":"Invoice 8 Payment Reminder",
        "message":"some body 1",
        "htmlMessage":"test@test1",
        "sms":"sms",
        "postDescription":"post",
        "creatorKey":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "sentToContactFullname":"stud5 ContraInvoice5 and 2 others"
        }
        """



    Scenario: (+) Get Message by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "subject":"Invoice 10 Payment Reminder",
        "message":"some body 2",
        "htmlMessage":"test@test2",
        "sms":"sms",
        "postDescription":"post",
        "creatorKey":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "sentToContactFullname":"stud6 ContraInvoice6 and 2 others"
        }
        """



    Scenario: (-) Get not existing Message

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

