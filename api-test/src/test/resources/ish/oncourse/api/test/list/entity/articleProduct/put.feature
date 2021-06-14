@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/articleProduct'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/articleProduct'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update ArticleProduct by admin

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product200",
        "code":"prd200",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product200"])].id
        * print "id = " + id
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product2000",
        "code":"prd2000123",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"product2000",
        "code":"prd2000123",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (+) Update ArticleProduct by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product201",
        "code":"prd201",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product201"])].id
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

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product2010",
        "code":"prd2010",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Disabled",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"product2010",
        "code":"prd2010",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Disabled",
        "corporatePasses":[{"id":1002,"contactFullName":"company #2"}],
        "dataCollectionRuleId":null
        }
        """



    Scenario: (-) Update ArticleProduct by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product202",
        "code":"prd202",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product202"])].id
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

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product2020",
        "code":"prd2020",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":200.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit article. Please contact your administrator"



    Scenario: (-) Update ArticleProduct required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product203",
        "code":"prd203",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product203"])].id
        * print "id = " + id

#       <--->  Update ArticleProduct to empty Name:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "code":"prd203",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update ArticleProduct to empty Code:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product203",
        "code":"",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Update ArticleProduct Code to out of range: >10symbols

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product204",
        "code":"prd204",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product204"])].id
        * print "id = " + id

#       <--->  Update ArticleProduct Code to 11 symbols:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product203",
        "code":"12345678901",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code cannot be more than 10 chars."



    Scenario: (-) Update ArticleProduct to incorrect product price

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product205",
        "code":"prd205",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product205"])].id
        * print "id = " + id
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product205",
        "code":"prd2005",
        "description":"any description upd",
        "feeExTax":200.00,
        "totalFee":300.00,
        "taxId":2,
        "incomeAccountId":8,
        "status":"Can be purchased in office",
        "corporatePasses":[{"id":1002}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Incorrect money values for product price."



    Scenario: (-) Update ArticleProduct Code to existing value

#       <----->  Add a new entity to update and define its id:
        * def newArticleProduct =
        """
        {
        "name":"product206",
        "code":"prd206",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
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

        * def id = get[0] response.rows[?(@.values == ["product206"])].id
        * print "id = " + id

#       <--->  Update ArticleProduct to empty Name:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "name":"product206",
        "code":"prd1",
        "description":"any description",
        "feeExTax":100.00,
        "totalFee":110.00,
        "taxId":1,
        "incomeAccountId":7,
        "status":"Can be purchased in office & online",
        "corporatePasses":[{"id":1001}]
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."


