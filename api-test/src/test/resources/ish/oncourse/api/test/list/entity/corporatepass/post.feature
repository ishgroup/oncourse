@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/corporatepass'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/corporatepass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Corporate Pass by admin

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass1",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","discountValue":null,"discountPercent":0.1}],
        "linkedSalables":
            [
            {"id":4,"name":"Course2","code":"course2-1","type":"Class"},
            {"id":1003,"name":"Membership#1","code":"SKU01","type":"Membership"},
            {"id":1001,"name":"product1","code":"prd1","type":"Product"},
            {"id":1002,"name":"voucherType1","code":"VOU1","type":"Voucher"}
            ]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {"id":"#number",
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass1",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","rounding":null,"discountValue":null,"discountPercent":0.100,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":null,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null,"relationDiscount"=null}],
        "linkedSalables":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished.
#       Now remove linked Classes, Discounts and Products:
        * def corporatePassToUpdate =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass1",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

#       Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Corporate Pass by notadmin with access rights

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

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass2",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","discountValue":null,"discountPercent":0.1}],
        "linkedSalables":
            [
            {"id":4,"name":"Course2","code":"course2-1","active":true,"type":"Class"},
            {"id":1003,"name":"Membership#1","code":"SKU01","active":true,"type":"Membership"},
            {"id":1001,"name":"product1","code":"prd1","active":true,"type":"Product"},
            {"id":1002,"name":"voucherType1","code":"VOU1","active":true,"type":"Voucher"}
            ]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass2"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {"id":"#number",
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass2",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","rounding":null,"discountValue":null,"discountPercent":0.100,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":null,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null,"relationDiscount":null}],
        "linkedSalables":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished.
#       Now remove linked Classes, Discounts and Products:
        * def corporatePassToUpdate =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass2",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

#       <-----> Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Corporate Pass by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass3",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create corporate pass. Please contact your administrator"



    Scenario: (-) Create new Corporate Pass with empty Contact

        * def newCorporatePass =
        """
        {
        "contactId":null,
        "contactFullName":null,
        "password":"pass3",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 400
        And match $.errorMessage == "Contact id is required."



    Scenario: (-) Create new Corporate Pass with empty Password

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 400
        And match $.errorMessage == "Password is required."



    Scenario: (-) Create new Corporate Pass with existing password

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"password1",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 400
        And match $.errorMessage == "Password must be unique."



    Scenario: (-) Create new Corporate Pass with too long password: >100 symbols

        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 400
        And match $.errorMessage == "Password cannot be more than 100 chars."
