@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/discount'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/discount'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update discount with type:"Percent" by admin

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"CUBE1",
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
        "name":"testDiscount200",
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount200"])].id
        * print "id = " + id
#       <--->

        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount200upd",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0,
        "discountPercent":0.2,
        "discountMin":100,
        "discountMax":300,
        "cosAccount":null,
        "predictedStudentsPercentage":0.2,
        "availableOnWeb":false,
        "code":"CUBE1UPD",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":"2028-07-11",
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":10,
        "studentAgeUnder":false,
        "studentAge":3,
        "studentPostcode":"123456upd",
        "discountConcessionTypes":[{"id":"3","name":"Pensioner","allowOnWeb":"true"}],
        "discountMemberships":[],
        "discountCourseClasses":[{"id":3,"name":"Course1","code":"course1-3","active":true,"type":"Class"}],
        "addByDefault":false,
        "minEnrolments":2,
        "minValue":"5",
        "corporatePassDiscounts":[],
        "limitPreviousEnrolment":false
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testDiscount200upd",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0.00,
        "discountPercent":0.200,
        "discountMin":100.00,
        "discountMax":300.00,
        "cosAccount":null,
        "predictedStudentsPercentage":0.20,
        "availableOnWeb":false,
        "code":"CUBE1UPD",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":"2028-07-11",
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":10,
        "studentAgeUnder":false,
        "studentAge":3,
        "studentPostcode":"123456upd",
        "discountConcessionTypes":[{"id":3,"name":"Pensioner","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-3","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":3,"type":"Class"}
            ],
        "addByDefault":false,
        "minEnrolments":2,
        "minValue":5.00,
        "corporatePassDiscounts":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":false
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount200upd","discountType":"Percent","rounding":"Nearest 50 cents","discountValue":0,"discountPercent":0.2,"discountMin":100,"discountMax":300,"cosAccount":null,"predictedStudentsPercentage":0.2,"availableOnWeb":false,"code":"CUBE1UPD","validFrom":null,"validFromOffset":null,"validTo":"2028-07-11","validToOffset":null,"hideOnWeb":false,"description":"some description upd","studentEnrolledWithinDays":10,"studentAgeUnder":false,"studentAge":3,"studentPostcode":"123456upd","discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":false,"minEnrolments":2,"minValue":5,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update discount with type:"Dollar" by admin

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"WABCD2",
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
        "limitPreviousEnrolment":null,
        "minEnrolments":0,
        "minValue":0,
        "name":"testDiscount201",
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount201"])].id
        * print "id = " + id
#       <--->

        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount201upd",
        "discountType":"Dollar",
        "rounding":"Nearest dollar",
        "discountValue":55.5,
        "discountPercent":null,
        "discountMin":0,
        "discountMax":0,
        "cosAccount":null,
        "predictedStudentsPercentage":0.11,
        "availableOnWeb":true,
        "code":"WABCD2upd",
        "validFrom":null,
        "validFromOffset":-3,
        "validTo":null,
        "validToOffset":"23",
        "hideOnWeb":true,
        "description":"some description upd",
        "studentEnrolledWithinDays":"",
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":"abc",
        "discountConcessionTypes":[{"id":"2","name":"Student","allowOnWeb":"true"}],
        "discountMemberships":[],
        "discountCourseClasses":[{"id":2,"name":"Course1","code":"course1-2","type":"Class","active":true}],
        "addByDefault":true,
        "minEnrolments":"10",
        "minValue":"222",
        "corporatePassDiscounts":[],
        "limitPreviousEnrolment":true
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testDiscount201upd",
        "discountType":"Dollar",
        "rounding":"Nearest dollar",
        "discountValue":55.50,
        "discountPercent":null,
        "discountMin":0.00,
        "discountMax":0.00,
        "cosAccount":null,
        "predictedStudentsPercentage":0.11,
        "availableOnWeb":true,
        "code":"WABCD2upd",
        "validFrom":null,
        "validFromOffset":-3,
        "validTo":null,
        "validToOffset":23,
        "hideOnWeb":true,
        "description":"some description upd",
        "studentEnrolledWithinDays":null,
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":"abc",
        "discountConcessionTypes":[{"id":2,"name":"Student","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-2","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":2,"type":"Class"}
            ],
        "addByDefault":true,
        "minEnrolments":10,
        "minValue":222.00,
        "corporatePassDiscounts":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "limitPreviousEnrolment":true
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount201upd","discountType":"Dollar","rounding":"Nearest dollar","discountValue":55.5,"discountPercent":null,"discountMin":0,"discountMax":0,"cosAccount":null,"predictedStudentsPercentage":0.11,"availableOnWeb":true,"code":"ABCD2upd","validFrom":null,"validFromOffset":-3,"validTo":null,"validToOffset":23,"hideOnWeb":true,"description":"some description upd","studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":"abc","discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":true,"minEnrolments":10,"minValue":222,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update discount with type:"Override fee" by admin

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"EABCD3",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
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
        "name":"testDiscount202",
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount202"])].id
        * print "id = " + id
#       <--->

        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount202upd",
        "discountType":"Fee override",
        "rounding":"No Rounding",
        "discountValue":100,
        "discountPercent":null,
        "discountMin":0,
        "discountMax":0,
        "cosAccount":null,
        "predictedStudentsPercentage":0.2,
        "availableOnWeb":false,
        "code":"EABCD3upd",
        "validFrom":null,
        "validFromOffset":-6,
        "validTo":null,
        "validToOffset":"5",
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":null,
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":null,
        "discountConcessionTypes":[],
        "discountMemberships":[],
        "discountCourseClasses":[{"id":6,"name":"Course4","code":"course4-1","type":"Class","active":true}],
        "addByDefault":true,
        "minEnrolments":"5",
        "minValue":"205",
        "corporatePassDiscounts":[{"id":1001,"contactFullName":"company #1"}]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testDiscount202upd",
        "discountType":"Fee override",
        "rounding":"No Rounding",
        "discountValue":100.00,
        "discountPercent":null,
        "discountMin":0.00,
        "discountMax":0.00,
        "cosAccount":null,
        "predictedStudentsPercentage":0.20,
        "availableOnWeb":false,
        "code":"EABCD3upd",
        "validFrom":null,
        "validFromOffset":-6,
        "validTo":null,
        "validToOffset":5,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":null,
        "studentAgeUnder":null,
        "studentAge":null,
        "studentPostcode":null,
        "discountConcessionTypes":[],
        "discountMemberships":[],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course4-1","entityFromId":null,"name":"Course4","active":true,"relationId":null,"id":6,"type":"Class"}
            ],
        "addByDefault":true,
        "minEnrolments":5,
        "minValue":205.00,
        "corporatePassDiscounts":[{"id":1001,"contactFullName":"company #1"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * def discountToUpdate = {"id":"#(id)","name":"testDiscount202upd","discountType":"Fee override","rounding":"No Rounding","discountValue":100,"discountPercent":null,"discountMin":0,"discountMax":0,"cosAccount":null,"predictedStudentsPercentage":0.2,"availableOnWeb":false,"code":"ABCD3upd","validFrom":null,"validFromOffset":-6,"validTo":null,"validToOffset":5,"hideOnWeb":false,"description":"some description upd","studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":true,"minEnrolments":5,"minValue":205,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update discount by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"sCUBE123",
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
        "minEnrolments":"2",
        "minValue":"50",
        "name":"testDiscount203",
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount203"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount203upd",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0,
        "discountPercent":0.2,
        "discountMin":100,
        "discountMax":300,
        "cosAccount":null,
        "predictedStudentsPercentage":0.2,
        "availableOnWeb":false,
        "code":"sCUBE123UPD",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":"2028-07-11",
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":10,
        "studentAgeUnder":false,
        "studentAge":3,
        "studentPostcode":"123456upd",
        "discountConcessionTypes":[{"id":"3","name":"Pensioner","allowOnWeb":"true"}],
        "discountMemberships":[],
        "discountCourseClasses":[{"id":3,"name":"Course1","code":"course1-3","active":true,"type":"Class"}],
        "addByDefault":false,
        "minEnrolments":2,
        "minValue":"5",
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testDiscount203upd",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0.00,
        "discountPercent":0.200,
        "discountMin":100.00,
        "discountMax":300.00,
        "cosAccount":null,
        "predictedStudentsPercentage":0.20,
        "availableOnWeb":false,
        "code":"sCUBE123UPD",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":"2028-07-11",
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":10,
        "studentAgeUnder":false,
        "studentAge":3,
        "studentPostcode":"123456upd",
        "discountConcessionTypes":[{"id":3,"name":"Pensioner","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"}],
        "discountMemberships":[],
        "discountCourseClasses":
            [
            {"expiryDate":null,"entityToId":null,"code":"course1-3","entityFromId":null,"name":"Course1","active":true,"relationId":null,"id":3,"type":"Class"}
            ],
        "addByDefault":false,
        "minEnrolments":2,
        "minValue":5.00,
        "corporatePassDiscounts":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove relations and then remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        * def discountToUpdate = {"id":"#(id)","name":"testDiscount203upd","discountType":"Percent","rounding":"Nearest 50 cents","discountValue":0,"discountPercent":0.2,"discountMin":100,"discountMax":300,"cosAccount":null,"predictedStudentsPercentage":0.2,"availableOnWeb":false,"code":"CUBE123UPD","validFrom":null,"validFromOffset":null,"validTo":"2028-07-11","validToOffset":null,"hideOnWeb":false,"description":"some description upd","studentEnrolledWithinDays":10,"studentAgeUnder":false,"studentAge":3,"studentPostcode":"123456upd","discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":false,"minEnrolments":2,"minValue":5,"corporatePassDiscounts":[]}

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update discount by notadmin without rights

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":true,
        "code":"jCUBE21",
        "availableOnWeb":true,
        "corporatePassDiscounts":[],
        "cosAccount":10,
        "description":"some description",
        "discountConcessionTypes":[],
        "discountCourseClasses":[],
        "discountMax":null,
        "discountMemberships":[],
        "discountMin":null,
        "discountPercent":0.12,
        "discountType":"Percent",
        "discountValue":0,
        "hideOnWeb":true,
        "minEnrolments":"2",
        "minValue":"50",
        "name":"testDiscount204",
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount204"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount204upd",
        "discountType":"Percent",
        "rounding":"Nearest 50 cents",
        "discountValue":0,
        "discountPercent":0.2,
        "discountMin":100,
        "discountMax":300,
        "cosAccount":null,
        "predictedStudentsPercentage":0.2,
        "availableOnWeb":false,
        "code":"jCUBE21UPD",
        "validFrom":null,
        "validFromOffset":null,
        "validTo":"2028-07-11",
        "validToOffset":null,
        "hideOnWeb":false,
        "description":"some description upd",
        "studentEnrolledWithinDays":10,
        "studentAgeUnder":false,
        "studentAge":3,
        "studentPostcode":"123456upd",
        "discountConcessionTypes":[{"id":"3","name":"Pensioner","allowOnWeb":"true"}],
        "discountMemberships":[],
        "discountCourseClasses":[],
        "addByDefault":false,
        "minEnrolments":2,
        "minValue":"5",
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit discount. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update discount required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {"addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0.1,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":5,"minValue":50,"name":"testDiscount205","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null}
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount205"])].id
        * print "id = " + id

#       <--->  Update discount to empty Name:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":5,
        "minValue":50,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update discount to empty discountType:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount205",
        "discountType":null,
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":5,
        "minValue":50,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Type is required."

#       <--->  Update discount to empty discountPercent:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount205",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":null,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":5,
        "minValue":50,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Discount percent (value) is required."

#       <--->  Update discount to empty predictedStudentsPercentage:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount205",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":null,
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
        "minEnrolments":5,
        "minValue":50,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Default forecast take-up is required."

#       <--->  Update discount to empty minEnrolments:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount205",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":null,
        "minValue":50,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Min enrolments is required."

#       <--->  Update discount to empty minValue:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount205",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":5,
        "minValue":null,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Min value is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update discount to discountMax < discountMin

#       <----->  Add a new entity to update and define its id:
        * def newDiscount = {"addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":20,"discountMemberships":[],"discountMin":10,"discountPercent":0.1,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount208","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null}        """

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount208"])].id
        * print "id = " + id

#       <--->  Update discount to discountMax < discountMin:
        * def discountToUpdate =
        """
        {
        "id":"#(id)","name":"testDiscount208","discountType":"Percent","rounding":"No Rounding","discountValue":0,"discountPercent":0.1,
        "discountMin":30,
        "discountMax":20,
        "cosAccount":null,"predictedStudentsPercentage":0.1,"availableOnWeb":false,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":false,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":false,"minEnrolments":0,"minValue":0,"corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Discount max value should be greater than discount min value."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update discount to validTo < validFrom

#       <----->  Add a new entity to update and define its id:
        * def newDiscount =
        """
        {
        "addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount209","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,
        "validFrom":"2019-07-11","validFromOffset":null,
        "validTo":"2019-07-31","validToOffset":null
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

        * def id = get[0] response.rows[?(@.values == ["testDiscount209"])].id
        * print "id = " + id

#       <--->  Update discount to validTo < validFrom:
        * def discountToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testDiscount209","discountType":"Percent","rounding":"No Rounding","discountValue":0,"discountPercent":0,"discountMin":0,"discountMax":0,"cosAccount":null,"predictedStudentsPercentage":0.1,"availableOnWeb":false,"code":null,
        "validFrom":"2019-07-11","validFromOffset":null,
        "validTo":"2019-07-01","validToOffset":null,
        "hideOnWeb":false,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":false,"minEnrolments":0,"minValue":0,"corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/' + id
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Valid to date cannot be before valid from date."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing discount

        * def discountToUpdate =
        """
        {
        "id":99999,
        "name":"testDiscount205",
        "discountType":"Percent",
        "rounding":"No Rounding",
        "discountValue":0,
        "discountPercent":0.1,
        "discountMin":null,
        "discountMax":null,
        "cosAccount":null,
        "predictedStudentsPercentage":0.1,
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
        "minEnrolments":5,
        "minValue":null,
        "corporatePassDiscounts":[]
        }
        """

        Given path ishPath + '/99999'
        And request discountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Discount with id:99999 doesn't exist"



