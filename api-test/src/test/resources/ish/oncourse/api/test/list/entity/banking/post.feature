@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/banking'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Create Banking deposit by admin

        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["3600.00"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
       """
        {
        "id":"#number",
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <--->  Scenario have been finished. Now remove banking deposit:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Banking deposit by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["3600.00"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
       """
        {
        "id":"#number",
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"UserWithRightsDelete UserWithRightsDelete",
        "reconciledStatus":"No",
        "payments":
        [
        {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
        {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
        ],
        "total":3600.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <--->  Scenario have been finished. Now remove banking deposit:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Banking deposit by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"}
            ],
        "total":3600.00
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create banking. Please contact your administrator"



    Scenario: (-) Create new Banking deposit with empty settlementDate

        * def newBanking =
        """
        {
        "settlementDate":"",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"}
            ],
        "total":3600.00
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 400
        And match $.errorMessage == "Settlement Date is required."



    Scenario: (-) Create new Banking deposit with empty payments

        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":[],
        "total":3600.00
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 400
        And match $.errorMessage == "No payments selected for deposit banking."

