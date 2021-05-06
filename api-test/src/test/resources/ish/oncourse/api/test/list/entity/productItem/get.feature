@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/sales'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/sales'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all productItems by admin

        Given path ishPathList
        And param entity = 'ProductItem'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000", "1001", "1002", "1003", "1004"]



    Scenario: (+) Get list of all productItems by notadmin

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
        And param entity = 'ProductItem'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000", "1001", "1002", "1003", "1004"]



    Scenario: (+) Get productItem (Membership) by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "productId":1003,
        "productType":"Membership",
        "productName":"Membership#1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2029-01-01",
        "purchasePrice":50.00,
        "status":"Active",
        "payments":[],
        "validFrom":"2028-01-01",
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """



    Scenario: (+) Get productItem (Voucher) by admin

        Given path ishPath + '/1015'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1015,
        "productId":1005,
        "productType":"Voucher",
        "productName":"voucherType2",
        "purchasedById":32,
        "purchasedByName":"voucher customField",
        "purchasedOn":"2019-08-30",
        "expiresOn":"2120-08-29",
        "purchasePrice":170.00,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes",
        "voucherCode":"g3zEmUdf",
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{"vcf1":"ABCDEF"}
        }
        """



    Scenario: (+) Get productItem (Article) by admin

        Given path ishPath + '/1002'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1002,
        "productId":1001,
        "productType":"Product",
        "productName":"product1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":null,
        "purchasePrice":100.00,
        "status":"#string",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """



    Scenario: (+) Get productItem by notadmin

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
        And match $ ==
        """
        {
        "id":1002,
        "productId":1001,
        "productType":"Product",
        "productName":"product1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":null,
        "purchasePrice":100.00,
        "status":"#string",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """



    Scenario: (-) Get not existing productItem

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
