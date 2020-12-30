@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/membershipProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/membershipProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create MembershipProduct by admin

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct100",
        "code":"mprod100",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
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

        * def id = get[0] response.rows[?(@.values == ["mprod100"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"MembershipProduct100",
        "code":"mprod100",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[]
        }
        """



    Scenario: (+) Create MembershipProduct by notadmin with access rights

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

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct101",
        "code":"mprod101",
        "description":"Membership description",
        "feeExTax":77.00,
        "totalFee":77.00,
        "taxId":2,
        "expiryType":"1st July",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
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

        * def id = get[0] response.rows[?(@.values == ["mprod101"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"MembershipProduct101",
        "code":"mprod101",
        "description":"Membership description",
        "feeExTax":77.00,
        "totalFee":77.00,
        "taxId":2,
        "expiryType":"1st July",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[]
        }
        """



    Scenario: (-) Create new MembershipProduct by notadmin without access rights

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

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct102",
        "code":"mprod102",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create membership product. Please contact your administrator"



    Scenario: (-) Create new MembershipProduct with empty Name

        * def newMembershipProduct =
        """
        {
        "name":"",
        "code":"mprod103",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new MembershipProduct with empty Code

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct104",
        "code":"",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create new MembershipProduct with existing Code

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct105",
        "code":"SKU01",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create new MembershipProduct with Code of 11 symbols

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct106",
        "code":"mprod106000",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Create new MembershipProduct with not valid symbols in Code

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct107",
        "code":"mprod!@#",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code must start and end with letters or numbers and can contain full stops."



    Scenario: (-) Create new MembershipProduct with empty feeExTax

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct108",
        "code":"mprod108",
        "description":"Membership description",
        "feeExTax":null,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Fee ex tax is required."



    Scenario: (-) Create new MembershipProduct with empty expiryType

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct109",
        "code":"mprod109",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":null,
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Expiry type is required."



    Scenario: (-) Create new MembershipProduct with empty expiryDays

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct110",
        "code":"mprod110",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Expiry days is required for this expiry type."



    Scenario: (-) Create new MembershipProduct with negative expiryDays

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct111",
        "code":"mprod110",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":-1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Expiry days must be non negative."



    Scenario: (-) Create new MembershipProduct with negative feeExTax

        * def newMembershipProduct =
        """
        {
        "name":"MembershipProduct111",
        "code":"mprod110",
        "description":"Membership description",
        "feeExTax":-50.00,
        "totalFee":50.00,
        "taxId":2,
        "expiryType":"Days",
        "expiryDays":10,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}],
        "membershipDiscounts":[{"discountId":1002,"discountName":"discount2","applyToMemberOnly":true,"contactRelationTypes":[]}]
        }
        """

        Given path ishPath
        And request newMembershipProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Fee ex tax must be non negative."