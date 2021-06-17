@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/discount'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/discount'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Create discount with discountType:"Percent" by admin

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD1",
        "availableOnWeb":true,
        "corporatePassDiscounts":[{"id":"1001"}],
        "cosAccount":10,
        "description":"some description",
        "discountConcessionTypes":[{"id":"2"}],
        "discountCourseClasses":[{"id":3}],
        "discountMax":null,
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01"}],
        "discountMin":null,
        "discountPercent":0.12,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":true,
        "limitPreviousEnrolment":true,
        "minEnrolments":"2",
        "minValue":"50",
        "name":"testDiscount1",
        "predictedStudentsPercentage":0.1,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":true,
        "studentEnrolledWithinDays":20,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":-30,
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testDiscount1",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0.00,
        "discountPercent":0.120,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":10,
        "predictedStudentsPercentage":0.10,
        "availableOnWeb":true,
        "code":"ABCD1",
        "validFrom":null,
        "validFromOffset":-30,
        "validTo":null,
        "validToOffset":5,
        "hideOnWeb":true,
        "description":"some description",
        "studentEnrolledWithinDays":20,
        "studentAgeUnder":true,
        "studentAge":15,
        "studentPostcode":"123456",
        "discountConcessionTypes":[{"id":2,"name":"Student","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01","contactRelations":[]}],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-3","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":3,"type":"Class"}
            ],
        "addByDefault":true,
        "minEnrolments":2,
        "minValue":50.00,
        "corporatePassDiscounts":[{"id":1001,"contactFullName":"company #1"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":true,
        "relationDiscount":false
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount1","discountType":"Percent","rounding":"No Rounding","discountValue":0,"discountPercent":0.12,"discountMin":null,"discountMax":null,"cosAccount":10,"predictedStudentsPercentage":0.1,"availableOnWeb":true,"code":"ABCD1","validFrom":null,"validFromOffset":-30,"validTo":null,"validToOffset":5,"hideOnWeb":true,"description":"some description","studentEnrolledWithinDays":20,"studentAgeUnder":true,"studentAge":15,"studentPostcode":"123456","discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":true,"minEnrolments":2,"minValue":50,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create discount with discountType:"Dollar" by admin

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD2",
        "availableOnWeb":false,
        "corporatePassDiscounts":[{"id":"1001"}],
        "cosAccount":10,
        "description":"some description",
        "discountConcessionTypes":[{"id":"3"}],
        "discountCourseClasses":[{"id":3}],
        "discountMax":null,
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01"}],
        "discountMin":null,
        "discountPercent":null,
        "discountType":"Dollar",
        "discountValue":33,
        "hideOnWeb":false,
        "limitPreviousEnrolment":false,
        "minEnrolments":0,
        "minValue":0,
        "name":"testDiscount2",
        "predictedStudentsPercentage":0.1,
        "rounding":"Nearest 10 cents",
        "studentAge":22,
        "studentAgeUnder":true,
        "studentEnrolledWithinDays":20,
        "studentPostcode":"1234567890",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":null,
        "validToOffset":null
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount2"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testDiscount2",
        "discountType":"Dollar",
        "rounding":"Nearest 10 cents",
        "discountValue":33.00,
        "discountPercent":null,
        "discountMin":0.00,
        "discountMax":0.00,
        "cosAccount":10,
        "predictedStudentsPercentage":0.10,
        "availableOnWeb":false,
        "code":"ABCD2",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":null,
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description",
        "studentEnrolledWithinDays":20,
        "studentAgeUnder":true,
        "studentAge":22,
        "studentPostcode":"1234567890",
        "discountConcessionTypes":[{"id":3,"name":"Pensioner","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01","contactRelations":[]}],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-3","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":3,"type":"Class"}
            ],
        "addByDefault":true,
        "minEnrolments":0,
        "minValue":0.00,
        "corporatePassDiscounts":[{"id":1001,"contactFullName":"company #1"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":false,
        "relationDiscount":false
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount2","discountType":"Dollar","rounding":"Nearest 10 cents","discountValue":33,"discountPercent":null,"discountMin":0,"discountMax":0,"cosAccount":10,"predictedStudentsPercentage":0.1,"availableOnWeb":false,"code":"ABCD2","validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":false,"description":"some description","studentEnrolledWithinDays":20,"studentAgeUnder":true,"studentAge":22,"studentPostcode":"1234567890","discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":true,"minEnrolments":0,"minValue":0,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create discount with discountType:"Override fee" by admin

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD3",
        "availableOnWeb":true,
        "corporatePassDiscounts":[{"id":"1001"}],
        "cosAccount":10,
        "description":"some description",
        "discountConcessionTypes":[{"id":"3"}],
        "discountCourseClasses":[{"id":3}],
        "discountMax":null,
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01"}],
        "discountMin":null,
        "discountPercent":null,
        "discountType":"Fee override",
        "discountValue":42,
        "hideOnWeb":true,
        "minEnrolments":"10",
        "minValue":"100",
        "name":"testDiscount3",
        "predictedStudentsPercentage":0.1,
        "rounding":"Nearest dollar",
        "studentAge":null,
        "studentAgeUnder":null,
        "studentEnrolledWithinDays":200,
        "studentPostcode":null,
        "validFrom":"2019-07-01",
        "validFromOffset":null,
        "validTo":"2037-07-31",
        "validToOffset":null
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount3"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testDiscount3",
        "discountType":"Fee override",
        "rounding":"Nearest dollar",
        "discountValue":42.00,
        "discountPercent":null,
        "discountMin":0.00,
        "discountMax":0.00,
        "cosAccount":10,
        "predictedStudentsPercentage":0.10,
        "availableOnWeb":true,
        "code":"ABCD3",
        "validFrom":"2019-07-01",
        "validFromOffset":null,
        "validTo":"2037-07-31",
        "validToOffset":null,
        "hideOnWeb":true,
        "description":"some description",
        "studentEnrolledWithinDays":200,
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":null,
        "discountConcessionTypes":[{"id":3,"name":"Pensioner","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[{"productId":1003,"productName":"Membership#1","productSku":"SKU01","contactRelations":[]}],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-3","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":3,"type":"Class"}
            ],
        "addByDefault":true,
        "minEnrolments":10,
        "minValue":100.00,
        "corporatePassDiscounts":[{"id":1001,"contactFullName":"company #1"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":false,
        "relationDiscount":false
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount3","discountType":"Fee override","rounding":"Nearest dollar","discountValue":42,"discountPercent":null,"discountMin":0,"discountMax":0,"cosAccount":10,"predictedStudentsPercentage":0.1,"availableOnWeb":true,"code":"ABCD3","validFrom":"2019-06-30","validFromOffset":null,"validTo":"2037-07-31","validToOffset":null,"hideOnWeb":true,"description":"some description","studentEnrolledWithinDays":200,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":true,"minEnrolments":10,"minValue":100,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create discount by notadmin with access rights

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

        * def newDiscount =
        """
        {
        "addByDefault":false,
        "code":null,
        "availableOnWeb":false,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":null,
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":55,
        "discountMemberships":[],
        "discountMin":22,
        "discountPercent":0.5,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":0,
        "minValue":0,
        "name":"testDiscount4",
        "predictedStudentsPercentage":0.1,
        "rounding":"Nearest 50 cents",
        "studentAge":null,
        "studentAgeUnder":null,
        "studentEnrolledWithinDays":null,
        "studentPostcode":null,
        "validFrom":null,
        "validFromOffset":null,
        "validTo":null,
        "validToOffset":null
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount4"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testDiscount4",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0.00,
        "discountPercent":0.500,
        "discountMin":22.00,
        "discountMax":55.00,
        "cosAccount":10,
        "predictedStudentsPercentage":0.10,
        "availableOnWeb":false,
        "code":null,
        "validFrom":null,
        "validFromOffset":null,
        "validTo":null,
        "validToOffset":null,
        "hideOnWeb":false,
        "description":null,
        "studentEnrolledWithinDays":null,
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":null,
        "discountConcessionTypes":[],
        "discountMemberships":[],
        "discountCourseClasses":[],
        "addByDefault":false,
        "minEnrolments":0,
        "minValue":0.00,
        "corporatePassDiscounts":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":false,
        "relationDiscount":false
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new discount by notadmin without access rights

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

        * def newDiscount =
        """
        {
        "addByDefault":false,
        "code":null,
        "availableOnWeb":false,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":null,
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":55,
        "discountMemberships":[],
        "discountMin":22,
        "discountPercent":0.5,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":0,
        "minValue":0,
        "name":"testDiscount5",
        "predictedStudentsPercentage":0.1,
        "rounding":"Nearest 50 cents",
        "studentAge":null,
        "studentAgeUnder":null,
        "studentEnrolledWithinDays":null,
        "studentPostcode":null,
        "validFrom":null,
        "validFromOffset":null,
        "validTo":null,
        "validToOffset":null
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create discount. Please contact your administrator"



    Scenario: (-) Create discount with empty Discount Name

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD6",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"",
        "predictedStudentsPercentage":10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create discount with Name length out of range: >100 symbols

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD7",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "predictedStudentsPercentage":10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."



    Scenario: (-) Create discount with empty value

#       <---> Create discount with empty Percent value:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD8",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":null,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount8",
        "predictedStudentsPercentage":10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Discount percent (value) is required."

#       <---> Create discount with empty Dollar value:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD8",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":null,
        "discountType":"Dollar",
        "discountValue":null,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount8",
        "predictedStudentsPercentage":10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Value is required."

#       <---> Create discount with empty Override fee value:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD8",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":null,
        "discountType":"Fee override",
        "discountValue":null,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount8",
        "predictedStudentsPercentage":10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Value is required."



    Scenario: (-) Create discount when discountMax < discountMin

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD10",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":0.1,
        "discountMemberships":[],
        "discountMin":0.2,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount10",
        "predictedStudentsPercentage":0.10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Discount max value should be greater than discount min value."



    Scenario: (-) Create discount with existing code

#       <---> Create discount witn promotional code:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD11",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount11",
        "predictedStudentsPercentage":0.10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount11"])].id
        * print "id = " + id

#       <---> Create discount with the same promotional code:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD11",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount11a",
        "predictedStudentsPercentage":0.10,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "The code must be unique."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create discount with empty predictedStudentsPercentage

        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"ABCD10",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":0.2,
        "discountMemberships":[],
        "discountMin":0.1,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount13",
        "predictedStudentsPercentage":null,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Default forecast take-up is required."



    Scenario: (-) Create discount with wrong percentage values

#       <---> discountPercent > 100:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":null,
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":1.01,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount13",
        "predictedStudentsPercentage":0.22,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Wrong value"

#       <---> predictedStudentsPercentage > 100:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":null,
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":20,
        "discountMemberships":[],
        "discountMin":10,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount14",
        "predictedStudentsPercentage":1.01,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Wrong value"

#       <---> predictedStudentsPercentage < 0:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":null,
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some public description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":22,
        "discountMemberships":[],
        "discountMin":11,
        "discountPercent":0.15,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":false,
        "minEnrolments":"3",
        "minValue":"100",
        "name":"testDiscount15",
        "predictedStudentsPercentage":-0.01,
        "rounding":"No Rounding",
        "studentAge":15,
        "studentAgeUnder":false,
        "studentEnrolledWithinDays":1,
        "studentPostcode":"123456",
        "validFrom":null,
        "validFromOffset":"4",
        "validTo":null,
        "validToOffset":"5"
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Wrong value"



    Scenario: (-) Create discount when validTo < validFrom

        * def newDiscount =
        """
        {
        "addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount16","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,
        "validFrom":"2019-07-11","validFromOffset":null,
        "validTo":"2019-07-01","validToOffset":null
        }
        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 400
        And match $.errorMessage == "Valid to date cannot be before valid from date."