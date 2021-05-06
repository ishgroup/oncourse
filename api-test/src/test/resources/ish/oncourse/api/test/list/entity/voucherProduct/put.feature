@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/voucherProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/voucherProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update VoucherProduct by admin

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct200",
        "code":"vprod200",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod200"])].id
        * print "id = " + id
#       <--->

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct2000",
        "code":"vprod2000",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"voucherProduct2000",
        "code":"vprod2000",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2,"code":"course2","name":"Course2"}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Update VoucherProduct by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct201",
        "code":"vprod201",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod201"])].id
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

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct2010",
        "code":"vprod2010",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":20,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"voucherProduct2010",
        "code":"vprod2010",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":20,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Update VoucherProduct by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct202",
        "code":"vprod202",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod202"])].id
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

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product2020",
        "code":"prd2020",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":300.00,
        "tax":{"id":2},
        "incomeAccount":{"id":8},
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit voucher product. Please contact your administrator"



    Scenario: (-) Update VoucherProduct required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct203",
        "code":"vprod203",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod203"])].id
        * print "id = " + id

#       <--->  Update VoucherProduct to empty Name:
        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "code":"vprod203",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update VoucherProduct to empty Code:
        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct203",
        "code":"",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Update VoucherProduct Code to out of range: >10symbols

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct204",
        "code":"vprod204",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod204"])].id
        * print "id = " + id

#       <--->  Update VoucherProduct Code to 11 symbols:
        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct204",
        "code":"74837269834",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Update VoucherProduct to not existing course

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct205",
        "code":"vprod205",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod205"])].id
        * print "id = " + id
#       <--->

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct205",
        "code":"vprod205",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":99999}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Course with id=99999 doesn't exist."



    Scenario: (-) Update VoucherProduct to not valid symbols in Code

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct206",
        "code":"vprod206",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod206"])].id
        * print "id = " + id
#       <--->

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct206",
        "code":"code-+$#1",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must start and end with letters or numbers and can contain full stops."



    Scenario: (-) Update VoucherProduct to not linked to course but with maxCoursesRedemption=1

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct207",
        "code":"vprod207",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod207"])].id
        * print "id = " + id
#       <--->

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct207",
        "code":"vprod207",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "If voucher product is not linked to any course then maxCoursesRedemption should be null."



    Scenario: (-) Update VoucherProduct to linked to course but with maxCoursesRedemption=null

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct208",
        "code":"vprod208",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod208"])].id
        * print "id = " + id
#       <--->

        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct208",
        "code":"vprod208",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":null,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "If voucher product is linked to courses then maxCoursesRedemption should not be null and be greater than zero."



    Scenario: (-) Update VoucherProduct Code to existing

#       <----->  Add a new entity to update and define its id:
        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct209",
        "code":"vprod209",
        "feeExTax":150.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'VoucherProduct'
        And param columns = 'sku'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["vprod209"])].id
        * print "id = " + id

#       <--->  Update VoucherProduct Code to 11 symbols:
        * def voucherProductToUpdate =
        """
        {
        "id":"#(id)",
        "name":"voucherProduct209",
        "code":"VOU1",
        "feeExTax":250.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":2,
        "courses":[{"id":2}],
        "description":"some description upd",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Update VoucherProduct which was sold

#       <---> Update "Can be redeemed for":
        * def voucherProductToUpdate =
        """
        {
        "id":1002,
        "name":"voucherType1",
        "code":"VOU1",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":66,
        "maxCoursesRedemption":null,
        "courses":[{"id":1,"code":"course1","name":"Course1"}],
        "description":null,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}]
        }
        """

        Given path ishPath + '/1002'
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Wrong value"

#       <---> Update "Course":
        * def voucherProductToUpdate =
        """
        {
        "id":1002,
        "name":"voucherType1",
        "code":"VOU1",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":2,"code":"course2","name":"Course2"}],
        "description":null,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}]
        }
        """

        Given path ishPath + '/1002'
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Wrong value"