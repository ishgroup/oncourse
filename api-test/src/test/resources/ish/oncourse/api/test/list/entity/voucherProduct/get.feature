@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/voucherProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/voucherProduct'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all VoucherProducts by admin

        Given path ishPathList
        And param entity = 'VoucherProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1002"]



    Scenario: (+) Get list of all VoucherProducts by notadmin with access rights Hide

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
        And param entity = 'VoucherProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1002"]



    Scenario: (+) Get VoucherProduct by admin

        Given path ishPath + '/1002'
        When method GET
        Then status 200
        And match $ contains
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
        "courses":[{"id":1,"code":"course1","name":"Course1"}],
        "description":null,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "soldVouchersCount": "#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Get VoucherProduct by notadmin with access rights Hide

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

        Given path ishPath + '/1002'
        When method GET
        Then status 200
        And match $ contains
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
        "courses":[{"id":1,"code":"course1","name":"Course1"}],
        "description":null,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "soldVouchersCount": "#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Get not existing VoucherProduct

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '9999' doesn't exist."
