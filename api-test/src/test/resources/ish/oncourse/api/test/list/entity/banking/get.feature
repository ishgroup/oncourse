@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/banking'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Banking by admin

        Given path ishPathList
        And param entity = 'Banking'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001"]



    Scenario: (+) Get list of all Banking by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Banking'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001"]



    Scenario: (+) Get Banking by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "settlementDate":"2019-04-12",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p3","paymentId":3,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":350.00,"created":"2019-04-12","status":"Success","paymentDate":"2019-04-12","reconcilable":true},
            {"id":"p4","paymentId":4,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":450.00,"created":"2019-04-12","status":"Success","paymentDate":"2019-04-12","reconcilable":true}
            ],
        "total":800.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """



    Scenario: (+) Get Banking by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + "/1000"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "settlementDate":"2019-04-12",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p3","paymentId":3,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":350.00,"created":"2019-04-12","status":"Success","paymentDate":"2019-04-12","reconcilable":true},
            {"id":"p4","paymentId":4,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":450.00,"created":"2019-04-12","status":"Success","paymentDate":"2019-04-12","reconcilable":true}
            ],
        "total":800.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """



    Scenario: (-) Get list of all Banking by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Banking'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get Banking by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1001"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get banking. Please contact your administrator"



    Scenario: (-) Get not existing Banking

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Banking with id:9999 doesn't exist"

