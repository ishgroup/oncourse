@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/voucherProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/voucherProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create VoucherProduct by admin: Enrolment In type

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct100",
        "code":"vprod100",
        "feeExTax":50.00,
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

        * def id = get[0] response.rows[?(@.values == ["vprod100"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"voucherProduct100",
        "code":"vprod100",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":1,"code":"course1","name":"Course1"}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null,
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
        }
        """



    Scenario: (+) Create VoucherProduct by admin: Purchase Price type

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct130",
        "code":"vprod130",
        "feeExTax":null,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}],
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

        * def id = get[0] response.rows[?(@.values == ["vprod130"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"voucherProduct130",
        "code":"vprod130",
        "feeExTax":null,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":null,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null,
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
        }
        """



    Scenario: (+) Create VoucherProduct by admin: Money Value type

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct131",
        "code":"vprod131",
        "feeExTax":90.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":222,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}],
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

        * def id = get[0] response.rows[?(@.values == ["vprod131"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"voucherProduct131",
        "code":"vprod131",
        "feeExTax":90.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":222,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null,
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
        }
        """



    Scenario: (+) Create VoucherProduct by notadmin with access rights

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

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct101",
        "code":"vprod101",
        "feeExTax":80.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":10,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}],
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

        * def id = get[0] response.rows[?(@.values == ["vprod101"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"voucherProduct101",
        "code":"vprod101",
        "feeExTax":80.00,
        "liabilityAccountId":4,
        "expiryDays":180,
        "value":10,
        "maxCoursesRedemption":null,
        "courses":[],
        "description":"some description",
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001,"contactFullName":"company #1"}],
        "soldVouchersCount":0,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null,
        "underpaymentAccountId":11,
        "dataCollectionRuleId":103
        }
        """



    Scenario: (-) Create new VoucherProduct by notadmin without access rights

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

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct102",
        "code":"vprod102",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":2}],
        "description":"some description",
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newVoucherProduct
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create voucher product. Please contact your administrator"



    Scenario: (-) Create new VoucherProduct with empty Name

        * def newVoucherProduct =
        """
        {
        "name":"",
        "code":"vprod103",
        "feeExTax":50.00,
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
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new VoucherProduct with empty Code

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct104",
        "code":"",
        "feeExTax":50.00,
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
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create new VoucherProduct with existing Code

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct105",
        "code":"VOU1",
        "feeExTax":50.00,
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
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create new VoucherProduct with Code of 11 symbols

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct106",
        "code":"vprod106000",
        "feeExTax":50.00,
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
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Create new VoucherProduct for not existing course

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct106",
        "code":"vprod106",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[{"id":99999}],
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
        Then status 400
        And match $.errorMessage == "Course with id=99999 doesn't exist."



    Scenario: (-) Create new VoucherProduct with not valid symbols in Code

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct106",
        "code":"code-+$#1",
        "feeExTax":50.00,
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
        Then status 400
        And match $.errorMessage == "Code must start and end with letters or numbers and can contain full stops."



    Scenario: (-) Create not linked to course VoucherProduct with maxCoursesRedemption=1

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct107",
        "code":"vprod107",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":1,
        "courses":[],
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
        Then status 400
        And match $.errorMessage == "If voucher product is not linked to any course then maxCoursesRedemption should be null."



    Scenario: (-) Create linked to course VoucherProduct with maxCoursesRedemption=null

        * def newVoucherProduct =
        """
        {
        "name":"voucherProduct108",
        "code":"vprod108",
        "feeExTax":50.00,
        "liabilityAccountId":5,
        "expiryDays":365,
        "value":null,
        "maxCoursesRedemption":null,
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
        Then status 400
        And match $.errorMessage == "If voucher product is linked to courses then maxCoursesRedemption should not be null and be greater than zero."

