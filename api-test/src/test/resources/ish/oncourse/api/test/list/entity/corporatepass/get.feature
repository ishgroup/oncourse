@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/corporatepass'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/corporatepass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all Corporate Passes by admin

        Given path ishPathList
        And param entity = 'CorporatePass'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001", "1002"]



    Scenario: (+) Get list of all Corporate Passes by notadmin with access rights

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
        And param entity = 'CorporatePass'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001", "1002"]



    Scenario: (+) Get Corporate Pass by admin

        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "contactId":7,
        "contactFullName":"company #1",
        "password":"password1",
        "expiryDate":null,
        "invoiceEmail":"co1@gmail.com",
        "linkedDiscounts":[{"id":1002,"name":"discount2","discountType":"Percent","rounding":null,"discountValue":null,"discountPercent":0.200,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":null,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null,"relationDiscount":null}],
        "linkedSalables":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

        And match $.linkedSalables contains [{"expiryDate":null,"entityToId":null,"code":"course1-1","entityFromId":null,"name":"Course1","active":false,"relationId":null,"id":1,"type":"Class"}]




    Scenario: (+) Get Corporate Pass by notadmin with access rights

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

        Given path ishPath + "/1002"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1002,
        "contactId":8,
        "contactFullName":"company #2",
        "password":"password2",
        "expiryDate":"2030-04-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

        And match $.linkedSalables contains [{"expiryDate":null,"entityToId":null,"code":"SKU01","entityFromId":null,"name":"Membership#1","active":true,"relationId":null,"id":1003,"type":"Membership"},{"expiryDate":null,"entityToId":null,"code":"prd1","entityFromId":null,"name":"product1","active":true,"relationId":null,"id":1001,"type":"Product"},{"expiryDate":null,"entityToId":null,"code":"VOU1","entityFromId":null,"name":"voucherType1","active":true,"relationId":null,"id":1002,"type":"Voucher"}]




    Scenario: (-) Get list of all Corporate Passes by notadmin without access rights

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
        And param entity = 'CorporatePass'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get Corporate Pass by notadmin without access rights

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

        Given path ishPath + "/1001"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get corporate pass. Please contact your administrator"



    Scenario: (-) Get not existing Corporate Pass

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "CorporatePass with id:9999 doesn't exist"
        