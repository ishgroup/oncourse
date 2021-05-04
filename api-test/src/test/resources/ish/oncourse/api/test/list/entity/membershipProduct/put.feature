@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/membershipProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/membershipProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update MembershipProduct by admin

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct200",
        "code":"mprod200",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod200"])].id
        * print "id = " + id
#       <--->

        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct2000",
        "code":"mprod2000",
        "description":"Membership description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":999,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1001}],
        "membershipDiscounts":[{"discountId":1001,"discountName":"discount1","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"MembershipProduct2000",
        "code":"mprod2000",
        "description":"Membership description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":999,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "membershipDiscounts":[{"discountId":1001,"discountName":"discount1","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "customFields":{"mf1":"MembershipValue1"}
        }
        """



    Scenario: (+) Update MembershipProduct by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct201",
        "code":"mprod201",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod201"])].id
        * print "id = " + id

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

        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct2010",
        "code":"mprod2010",
        "description":"Membership description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":888,
        "incomeAccountId":8,
        "status":"Disabled",
        "corporatePasses":[],
        "membershipDiscounts":[],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"MembershipProduct2010",
        "code":"mprod2010",
        "description":"Membership description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":888,
        "incomeAccountId":8,
        "status":"Disabled",
        "corporatePasses":[],
        "membershipDiscounts":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "customFields":{"mf1":"MembershipValue1"}
        }
        """



    Scenario: (-) Update MembershipProduct by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct202",
        "code":"mprod202",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod202"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct2020",
        "code":"mprod2020",
        "description":"Membership description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":888,
        "incomeAccountId":8,
        "status":"Disabled",
        "corporatePasses":[],
        "membershipDiscounts":[],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit membership product. Please contact your administrator"



    Scenario: (-) Update MembershipProduct required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct203",
        "code":"mprod203",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod203"])].id
        * print "id = " + id

#       <--->  Update MembershipProduct to empty Name:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "code":"mprod203",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update MembershipProduct to empty Code:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct203",
        "code":"",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."

#       <--->  Update MembershipProduct to empty feeExTax:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct203",
        "code":"mprod203",
        "description":"Membership description",
        "feeExTax":null,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Fee ex tax is required."

#       <--->  Update MembershipProduct to empty expiryType:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct203",
        "code":"mprod203",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":null,
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Expiry type is required."

#       <--->  Update MembershipProduct to empty expiryDays:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct203",
        "code":"mprod203",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"Days",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Expiry days is required for this expiry type."



    Scenario: (-) Update MembershipProduct Code to out of range: >10symbols

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct204",
        "code":"mprod204",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod203"])].id
        * print "id = " + id

#       <--->  Update MembershipProduct Code to 11 symbols:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct204",
        "code":"12345678900",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Update MembershipProduct Code to existing value

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct205",
        "code":"mprod205",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod205"])].id
        * print "id = " + id

#       <--->  Update MembershipProduct Code to 11 symbols:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct205",
        "code":"SKU01",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."


    Scenario: (-) Update MembershipProduct without mandatory custom field

#       <----->  Add a new entity to update and define its id:
        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct206",
        "code":"mprod206",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{"mf1":"MembershipValue1"}
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'MembershipProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["mprod206"])].id
        * print "id = " + id

#       <--->  Update MembershipProduct Code to 11 symbols:
        * def membershipProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"MembershipProduct206",
        "code":"mprod206",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request membershipProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "MembershipField1 is required."



