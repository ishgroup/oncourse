@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/corporatepass'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/corporatepass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update Corporate Pass by admin

#       <----->  Add a new entity to update and define its id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass001",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass001"])].id
        * print "id = " + id
#       <--->

        * def corporatePassToUpdate =
        """
        {
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass001_upd",
        "expiryDate":"2026-02-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass001_upd",
        "expiryDate":"2026-02-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Remove linked Discounts and Products:
        * def corporatePassToUpdate =
        """
        {
        "contactId":8,
        "contactFullName":"company #2",
        "password":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A",
        "expiryDate":"2016-02-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Corporate Pass by notadmin with rights

#       <----->  Add a new entity to update and define its id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass002",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass002"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def corporatePassToUpdate =
        """
        {
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass002_upd",
        "expiryDate":null,
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","discountValue":null,"discountPercent":0.1}],
        "linkedSalables":
            [
            {"id":4,"name":"Course2","code":"course2-1","active":true,"type":"Class","expiryDate":null},
            {"id":1003,"name":"Membership#1","code":"SKU01","active":true,"type":"Membership","expiryDate":null},
            {"id":1001,"name":"product1","code":"prd1","active":true,"type":"Product","expiryDate":null},
            {"id":1002,"name":"voucherType1","code":"VOU1","active":true,"type":"Voucher","expiryDate":null}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass002_upd",
        "expiryDate":null,
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","rounding":null,"discountValue":null,"discountPercent":0.100,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":null,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null,"relationDiscount":null}],
        "linkedSalables":[{"expiryDate":null,"entityToId":null,"code":"course2-1","entityFromId":null,"name":"Course2","active":true,"relationId":null,"id":4,"type":"Class"},{"expiryDate":null,"entityToId":null,"code":"SKU01","entityFromId":null,"name":"Membership#1","active":true,"relationId":null,"id":1003,"type":"Membership"},{"expiryDate":null,"entityToId":null,"code":"prd1","entityFromId":null,"name":"product1","active":true,"relationId":null,"id":1001,"type":"Product"},{"expiryDate":null,"entityToId":null,"code":"VOU1","entityFromId":null,"name":"voucherType1","active":true,"relationId":null,"id":1002,"type":"Voucher"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Remove linked Discounts and Products:
        * def corporatePassToUpdate =
        """
        {
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass002_upd",
        "expiryDate":"2026-02-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 204

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Corporate Pass by notadmin without rights

#       <----->  Add a new entity to update and define its id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass003",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass003"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def corporatePassToUpdate =
        """
        {
        "contactId":8,
        "contactFullName":"company #2",
        "password":"pass003_upd",
        "expiryDate":"2026-02-01",
        "invoiceEmail":"co2@gmail.com",
        "linkedDiscounts":[{"id":1001,"name":"discount1","discountType":"Percent","discountValue":null,"discountPercent":0.1}],
        "linkedSalables":
            [
            {"id":4,"name":"Course2","code":"course2-1","active":true,"type":"Class"},
            {"id":1003,"name":"Membership#1","code":"SKU01","active":true,"type":"Membership"},
            {"id":1001,"name":"product1","code":"prd1","active":true,"type":"Product"},
            {"id":1002,"name":"voucherType1","code":"VOU1","active":true,"type":"Voucher"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request corporatePassToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit corporate pass. Please contact your administrator"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Corporate Pass required fields to empty

#       <--->  Update Corporate Pass to empty Contact:
        * def corporatePassToUpdate =
        """
        {
        "contactId":null,
        "contactFullName":null,
        "password":"password1",
        "expiryDate":null,
        "invoiceEmail":"co1@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/1001'
        And request corporatePassToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Contact id is required."

#       <--->  Update Corporate Pass to empty Password:
        * def corporatePassToUpdate =
        """
        {
        "contactId":7,
        "contactFullName":"company #1",
        "password":"",
        "expiryDate":null,
        "invoiceEmail":"co1@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/1001'
        And request corporatePassToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Password is required."



    Scenario: (-) Update Corporate Pass to existing password

        * def corporatePassToUpdate =
        """
        {
        "contactId":7,
        "contactFullName":"company #1",
        "password":"password2",
        "expiryDate":null,
        "invoiceEmail":"co1@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/1001'
        And request corporatePassToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Password must be unique."



    Scenario: (-) Update password in Corporate Pass to >100 symbols

        * def corporatePassToUpdate =
        """
        {
        "contactId":7,
        "contactFullName":"company #1",
        "password":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "expiryDate":null,
        "invoiceEmail":"co1@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath + '/1001'
        And request corporatePassToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Password cannot be more than 100 chars."



