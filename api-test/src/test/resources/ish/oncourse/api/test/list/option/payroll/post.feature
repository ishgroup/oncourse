@parallel=false
Feature: Main feature for all POST requests with path 'list/option/payroll'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/option/payroll'
        * def ishPathList = 'list'
        * def ishPathPayslip = 'list/entity/payslip'
        * def ishPathControl = 'control'
        



    Scenario: (+) Generate tutor pay with confirmation by admin

        * def getWages = {"untilDate":"2019-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
#        And match response == {"totalWagesCount":1,"unprocessedWagesCount":0,"unconfirmedWagesCount":1,"unconfirmedWages":[2],"allowedToConfirm":true}

        * def newTutorPay = {"untilDate":"2019-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And param bulkConfirmTutorWages = true
        And request newTutorPay
        When method POST
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200
        * match $ == {"status":"#ignore","message":null}

        * def sleep =
             """
             function(seconds){
               for(i = 0; i <= seconds; i++)
               {
                 java.lang.Thread.sleep(1*1000);
                 karate.log(i);
               }
             }
             """
           * call sleep 1

#       <---> Define if for created entity:
        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

        Given path ishPathPayslip + '/' + id
        When method GET
        Then status 200
        And match $.tutorId == 1
        And match $.tutorFullName == "tutor1"
        And match $.paylines[*].value == [15.0]

#       <--->  Scenario have been finished. Now remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPathPayslip + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Generate tutor pay with confirmation by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def getWages = {"untilDate":"2020-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
#        And match response == {"totalWagesCount":1,"unprocessedWagesCount":1,"unconfirmedWagesCount":0,"unconfirmedWages":[],"allowedToConfirm":true}

        * def newTutorPay = {"untilDate":"2020-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And param bulkConfirmTutorWages = true
        And request newTutorPay
        When method POST
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200
        * match $ == {"status":"#ignore","message":null}

        * def sleep =
             """
             function(seconds){
               for(i = 0; i <= seconds; i++)
               {
                 java.lang.Thread.sleep(1*1000);
                 karate.log(i);
               }
             }
             """
           * call sleep 1

#       <---> Define if for created entity:
        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

        Given path ishPathPayslip + '/' + id
        When method GET
        Then status 200
        And match $.tutorId == 1
        And match $.tutorFullName == "tutor1"
        And match $.paylines[*].value == [15.0,15.0]

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPathPayslip + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Generate tutor pay without confirmation by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def getWages = {"untilDate":"2021-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
#        And match response == {"totalWagesCount":1,"unprocessedWagesCount":1,"unconfirmedWagesCount":0,"unconfirmedWages":[],"allowedToConfirm":true}

        * def newTutorPay = {"untilDate":"2021-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And param bulkConfirmTutorWages = false
        And request newTutorPay
        When method POST
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200
        * match $ == {"status":"#ignore","message":null}

        * def sleep =
             """
             function(seconds){
               for(i = 0; i <= seconds; i++)
               {
                 java.lang.Thread.sleep(1*1000);
                 karate.log(i);
               }
             }
             """
           * call sleep 1

#       <---> Define if for created entity:
        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

        Given path ishPathPayslip + '/' + id
        When method GET
        Then status 200
        And match $.tutorId == 1
        And match $.tutorFullName == "tutor1"
        And match $.paylines[*].value == [15.00,15.00]

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPathPayslip + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Generate tutor pay by notadmin without access rights to payroll

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def getWages = {"untilDate":"2021-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to generate payroll. Please contact your administrator"



    Scenario: (-) Generate tutor pay with confirmation by notadmin without access rights to confirmation

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
#       <--->

        * def getWages = {"untilDate":"2021-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
        And match response == {"totalWagesCount":"#number","unprocessedWagesCount":"#number","unconfirmedWagesCount":"#number","unconfirmedClassesIds":"#notnull"}

        * def newTutorPay = {"untilDate":"2021-01-01","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And param bulkConfirmTutorWages = true
        And request newTutorPay
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to generate payroll. Please contact your administrator"

