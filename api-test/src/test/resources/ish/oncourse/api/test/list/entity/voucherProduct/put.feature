@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/voucherProduct'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/voucherProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



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
        "corporatePasses":[{"id":1002}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        "corporatePasses":[{"id":1001}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        "dataCollectionRuleId":null,
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        "corporatePasses":[{"id":1002}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
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
        "corporatePasses":[{"id":1002}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        "corporatePasses":[{"id":1001}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
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
        "corporatePasses":[{"id":1001}],
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
        }
        """

        Given path ishPath + '/' + id
        And request voucherProductToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."
        