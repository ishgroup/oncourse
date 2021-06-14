@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/articleProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/articleProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create ArticleProduct by admin

        * def newArticleProduct =
        """
        {
        "name":"product100",
        "code":"prd100",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'ArticleProduct'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["product100"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"product100",
        "code":"prd100",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Create ArticleProduct by notadmin with access rights

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

        * def newArticleProduct =
        """
        {
        "name":"product101",
        "code":"prd101",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'ArticleProduct'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["product101"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#ignore",
        "name":"product101",
        "code":"prd101",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedSellables":[],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Create new ArticleProduct by notadmin without access rights

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

        * def newArticleProduct =
        """
        {
        "name":"product102",
        "code":"prd102",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create article. Please contact your administrator"



    Scenario: (-) Create new ArticleProduct with empty Name

        * def newArticleProduct =
        """
        {
        "name":"",
        "code":"prd103",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new ArticleProduct with empty Code

        * def newArticleProduct =
        """
        {
        "name":"product104",
        "code":"",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create new ArticleProduct with existing Code

        * def newArticleProduct =
        """
        {
        "name":"product105",
        "code":"prd100",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create new ArticleProduct with Code of more than 10 symbols

        * def newArticleProduct =
        """
        {
        "name":"product106",
        "code":"10987654321",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Create new ArticleProduct with not valid symbols in Code

        * def newArticleProduct =
        """
        {
        "name":"product106",
        "code":"code-+$#1",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath
        And request newArticleProduct
        When method POST
        Then status 400
        And match $.errorMessage == "Code must start and end with letters or numbers and can contain full stops."


