@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/sales'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/sales'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update Article sale by admin

#       <---> Change status to Delivered:
        * def productItemToUpdate =
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
        "purchasePrice":100,
        "status":"Delivered",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1002'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
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
        "status":"Delivered",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """


    Scenario: (+) Update Membership sale by admin

#       <---> Change expiresOn:
        * def productItemToUpdate =
        """
        {
        "id":1016,
        "productId":1003,
        "productType":"Membership",
        "productName":"MembershipWithVoucher",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2129-01-02",
        "purchasePrice":500,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{"mcf1":"updatedValue"}
        }
        """

        Given path ishPath + '/1016'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1016'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1016,
        "productId":1003,
        "productType":"Membership",
        "productName":"Membership#1",
        "purchasedById":32,
        "purchasedByName":"voucher customField",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2129-01-02",
        "purchasePrice":500.00,
        "status":"Active",
        "payments":[],
        "validFrom":"2128-01-02",
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{"mcf1":"updatedValue"}
        }
        """

#       <--->  Scenario have been finished. Now change back entity:
        * def productItemToDefault =
        """
        {
        "id":1016,
        "productId":1003,
        "productType":"Membership",
        "productName":"MembershipWithCustomField#1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2129-01-01",
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{"mcf1":"12345"}
        }
        """

        Given path ishPath + '/1016'
        And request productItemToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Membership sale by notadmin with access rights

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

#       <---> Change expiresOn:
        * def productItemToUpdate =
        """
        {
        "id":1000,
        "productId":1003,
        "productType":"Membership",
        "productName":"Membership#1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2030-01-02",
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1000'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
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
        "expiresOn":"2030-01-02",
        "purchasePrice":50.00,
        "status":"Active",
        "payments":[],
        "validFrom":"2029-01-02",
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

#       <--->  Scenario have been finished. Now change back entity:
        * def productItemToDefault =
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
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1000'
        And request productItemToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Voucher sale by admin

#       <---> Change expiresOn and redeemableById:
        * def productItemToUpdate =
        """
        {
        "id":1001,
        "productId":1002,
        "productType":"Voucher",
        "productName":"voucherType1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2030-07-22",
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes",
        "voucherCode":"2zMEaTEz",
        "redeemableById":15,
        "redeemableByName":"stud8",
        "customFields":{}
        }
        """

        Given path ishPath + '/1001'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "productId":1002,
        "productType":"Voucher",
        "productName":"voucherType1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2030-07-22",
        "purchasePrice":50.00,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes",
        "voucherCode":"2zMEaTEz",
        "redeemableById":15,
        "redeemableByName":"stud9",
        "customFields":{}
        }
        """

#       <--->  Scenario have been finished. Now change back entity:
        * def productItemToDefault =
        """
        {
        "id":1001,
        "productId":1002,
        "productType":"Voucher",
        "productName":"voucherType1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2029-07-21",
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes",
        "voucherCode":"2zMEaTEz",
        "redeemableById":14,
        "redeemableByName":"stud9",
        "customFields":{}
        }
        """

        Given path ishPath + '/1001'
        And request productItemToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Voucher sale by notadmin with access rights

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

#       <---> Change expiresOn and redeemableById:
        * def productItemToUpdate =
        """
        {"id":1001,"productId":1002,"productType":"Voucher","productName":"voucherType1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2019-07-22","expiresOn":"2030-07-22","purchasePrice":50,"status":"Active","payments":[],"validFrom":null,"valueRemaining":"1 classes","voucherCode":"2zMEaTEz","redeemableById":15,"redeemableByName":"stud8","customFields":{}}
        """

        Given path ishPath + '/1001'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {"id":1001,"productId":1002,"productType":"Voucher","productName":"voucherType1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2019-07-22","expiresOn":"2030-07-22","purchasePrice":50.00,"status":"Active","payments":[],"validFrom":null,"valueRemaining":"1 classes","voucherCode":"2zMEaTEz","redeemableById":15,"redeemableByName":"stud9","customFields":{}}
        """

#       <--->  Scenario have been finished. Now change back entity:
        * def productItemToDefault =
        """
        {"id":1001,"productId":1002,"productType":"Voucher","productName":"voucherType1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2019-07-22","expiresOn":"2029-07-21","purchasePrice":50,"status":"Active","payments":[],"validFrom":null,"valueRemaining":"1 classes","voucherCode":"2zMEaTEz","redeemableById":14,"redeemableByName":"stud9","customFields":{}}
        """

        Given path ishPath + '/1001'
        And request productItemToDefault
        When method PUT
        Then status 204



    Scenario: (-) Update Membership sale by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <---> Change expiresOn:
        * def productItemToUpdate =
        """
        {
        "id":1000,
        "productId":1003,
        "productType":"Membership",
        "productName":"Membership#1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2030-01-02",
        "purchasePrice":50,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1000'
        And request productItemToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit sale. Please contact your administrator"



    Scenario: (-) Update Voucher sale by notadmin with access rights

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

#       <---> Change expiresOn and redeemableById:
        * def productItemToUpdate =
        """
        {"id":1001,"productId":1002,"productType":"Voucher","productName":"voucherType1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2019-07-22","expiresOn":"2030-07-22","purchasePrice":50,"status":"Active","payments":[],"validFrom":null,"valueRemaining":"1 classes","voucherCode":"2zMEaTEz","redeemableById":15,"redeemableByName":"stud8","customFields":{}}
        """

        Given path ishPath + '/1001'
        And request productItemToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit sale. Please contact your administrator"



    Scenario: (-) Update Membership sale not editable fields

#       <---> Change not editable fields:
        * def productItemToUpdate =
        """
        {
        "id":1000,
        "productId":1003,
        "productType":"Membership",
        "productName":"Membership#1upd",
        "purchasedById":15,
        "purchasedByName":"stud9",
        "purchasedOn":"2018-07-20",
        "expiresOn":"2029-01-01",
        "purchasePrice":100,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1000'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (values should not be changed):
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



    Scenario: (-) Update Voucher sale not editable fields

#       <---> Change not editable fields:
        * def productItemToUpdate =
        """
        {
        "id":1001,
        "productId":1002,
        "productType":"Voucher",
        "productName":"voucherType1upd",
        "purchasedById":15,
        "purchasedByName":"stud9",
        "purchasedOn":"2020-07-20",
        "expiresOn":"2029-07-21",
        "purchasePrice":100,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes upd",
        "voucherCode":"2zMEaTEzUPD",
        "redeemableById":14,
        "redeemableByName":"stud8",
        "customFields":{}
        }
        """

        Given path ishPath + '/1001'
        And request productItemToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (values should not be changed):
        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "productId":1002,
        "productType":"Voucher",
        "productName":"voucherType1",
        "purchasedById":14,
        "purchasedByName":"stud8",
        "purchasedOn":"2019-07-22",
        "expiresOn":"2029-07-21",
        "purchasePrice":50.00,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":"1 classes",
        "voucherCode":"2zMEaTEz",
        "redeemableById":14,
        "redeemableByName":"stud8",
        "customFields":{}
        }
        """



    Scenario: (-) Update delivered Article sale

#       <---> Change not editable fields:
        * def productItemToUpdate =
        """
        {
        "id":1002,
        "productId":1001,
        "productType":"Product",
        "productName":"product1_upd",
        "purchasedById":15,
        "purchasedByName":"stud9",
        "purchasedOn":"2020-07-20",
        "expiresOn":null,
        "purchasePrice":80.00,
        "status":"Active",
        "payments":[],
        "validFrom":null,
        "valueRemaining":null,
        "voucherCode":null,
        "redeemableById":null,
        "redeemableByName":null,
        "customFields":{}
        }
        """

        Given path ishPath + '/1002'
        And request productItemToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Only ProductItem with active status can be modified."



    Scenario: (-) Update expired Membership sale

#       <---> Change not editable fields:
        * def productItemToUpdate = {"id":1003,"productId":1003,"productType":"Membership","productName":"Membership#1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2018-07-22","expiresOn":"2020-05-05","purchasePrice":50.00,"status":"Active","payments":[],"validFrom":null,"valueRemaining":null,"voucherCode":null,"redeemableById":null,"redeemableByName":null}

        Given path ishPath + '/1003'
        And request productItemToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Only ProductItem with active status can be modified."



    Scenario: (-) Update expired Voucher sale

#       <---> Change not editable fields:
        * def productItemToUpdate = {"id":1004,"productId":1002,"productType":"Voucher","productName":"voucherType1","purchasedById":14,"purchasedByName":"stud8","purchasedOn":"2018-05-22","expiresOn":"2019-01-21","purchasePrice":50.00,"status":"Expired","payments":[],"validFrom":null,"valueRemaining":"1 classes","voucherCode":"2zMEaTEr","redeemableById":15,"redeemableByName":"stud8"}

        Given path ishPath + '/1004'
        And request productItemToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Only ProductItem with active status can be modified."



    Scenario: (-) Update not existing sale

        Given path ishPath + '/99999'
        And request {}
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."