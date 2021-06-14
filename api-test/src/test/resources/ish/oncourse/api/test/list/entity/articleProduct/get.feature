@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/articleProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/articleProduct'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all ArticleProducts by admin

        Given path ishPathList
        And param entity = 'ArticleProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001"]



    Scenario: (+) Get list of all ArticleProducts by notadmin with access rights Hide

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
        And param entity = 'ArticleProduct'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001"]



    Scenario: (+) Get ArticleProduct by admin

        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "name":"product1",
        "code":"prd1",
        "description":null,
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Get ArticleProduct by notadmin with access rights Hide

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

        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "name":"product1",
        "code":"prd1",
        "description":null,
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Get not existing ArticleProduct

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '9999' doesn't exist."

