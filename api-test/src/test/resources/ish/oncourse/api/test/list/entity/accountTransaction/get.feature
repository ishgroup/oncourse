@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/accountTransaction'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/accountTransaction'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all account transactions by admin

        Given path ishPathList
        And param pageSize = 1000
        And param entity = 'AccountTransaction'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["104","103","102","101","28","27","26","25","24","23","22","21","20","19","18","17","16","15","14","13","12","11","10","9","8","7","6","5","4","3","2","1","118","117","116","115","114","113","112","111","110","109","108","107","106","105","208","207","206","205"]



    Scenario: (+) Get list of all account transactions by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
        And param pageSize = 1000
        And param entity = 'AccountTransaction'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["104","103","102","101","28","27","26","25","24","23","22","21","20","19","18","17","16","15","14","13","12","11","10","9","8","7","6","5","4","3","2","1","118","117","116","115","114","113","112","111","110","109","108","107","106","105","208","207","206","205"]



    Scenario: (+) Get account transaction by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ == {"id":1,"fromAccount":12,"toAccount":null,"amount":1800.00,"transactionDate":"2018-11-29"}



    Scenario: (+) Get account transaction by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ == {"id":3,"fromAccount":2,"toAccount":null,"amount":45.45,"transactionDate":"2018-11-29"}



    Scenario: (-) Get list of all account transactions by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        Given path ishPathList
        And param entity = 'AccountTransaction'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get account transaction by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        Given path ishPath + "/5"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get Transaction. Please contact your administrator"



    Scenario: (-) Get not existing account transaction

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "AccountTransaction with id:9999 doesn't exist"
        
