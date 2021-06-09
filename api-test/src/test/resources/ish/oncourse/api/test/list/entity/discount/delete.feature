@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/discount'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/discount'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing discount without relations by admin

#       <----->  Add a new entity for deleting and get id:
        * def newDiscount = {"addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0.1,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount100","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null}

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount100"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Discount with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing discount by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newDiscount = {"addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0.1,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount101","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null}

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount101"])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Discount with id:" + id + " doesn't exist"



    Scenario: (-) Delete existing discount by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newDiscount = {"addByDefault":false,"code":null,"availableOnWeb":false,"corporatePassDiscounts":[],"cosAccount":null,"description":null,"discountConcessionTypes":[],"discountCourseClasses":[],"discountMax":null,"discountMemberships":[],"discountMin":null,"discountPercent":0.1,"discountType":"Percent","discountValue":0,"hideOnWeb":false,"minEnrolments":0,"minValue":0,"name":"testDiscount102","predictedStudentsPercentage":0.1,"rounding":"No Rounding","studentAge":null,"studentAgeUnder":null,"studentEnrolledWithinDays":null,"studentPostcode":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null}

        Given path ishPath
        And request newDiscount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Discount'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testDiscount102"])].id
        * print "id = " + id

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete discount. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing discount with relation by admin

        Given path ishPath + '/1001'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Discount cannot be deleted because it has already been used. Instead use expiry date in the past."



    Scenario: (-) Delete existing discount with relation by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/1001'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Discount cannot be deleted because it has already been used. Instead use expiry date in the past."



    Scenario: (-) Delete existing discount by notadmin without access rights

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

        Given path ishPath + '/1001'
        When method DELETE
        Then status 403
        And match response.errorMessage == "Sorry, you have no permissions to delete discount. Please contact your administrator"



    Scenario: (-) Delete NOT existing discount

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Discount with id:99999 doesn't exist"

        