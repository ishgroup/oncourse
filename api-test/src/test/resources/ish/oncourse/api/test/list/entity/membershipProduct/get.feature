@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/membershipProduct'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/membershipProduct'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all MembershipProducts by admin

        Given path ishPathList
        And param entity = 'MembershipProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1003"]



    Scenario: (+) Get list of all MembershipProducts by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'MembershipProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1003"]



    Scenario: (+) Get MembershipProduct by admin

        Given path ishPath + '/1003'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1003,
        "name":"Membership#1",
        "code":"SKU01",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"contactFullName":"company #2","id":1002}],
        "membershipDiscounts":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Get MembershipProduct by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/1003'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1003,
        "name":"Membership#1",
        "code":"SKU01",
        "description":"Membership description",
        "feeExTax":50.00,
        "totalFee":55.00,
        "taxId":1,
        "expiryType":"1st January",
        "expiryDays":null,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"contactFullName":"company #2","id":1002}],
        "membershipDiscounts":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Get not existing MembershipProduct

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '9999' doesn't exist."

