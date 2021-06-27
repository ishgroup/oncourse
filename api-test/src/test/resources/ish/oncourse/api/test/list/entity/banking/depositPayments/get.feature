@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/banking/depositPayments'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking/depositPayments'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get depositPayments by admin

        Given path ishPath + '/1/200'
        When method GET
        Then status 200
        And match $ contains
        """
        [
        {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
        {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
        {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true},
        {"id":"p5","paymentId":5,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":220.00,"created":"2019-06-18","status":"Success","paymentDate":"2019-06-18","reconcilable":true},
        {"id":"p6","paymentId":6,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Credit card","contactId":10,"contactName":"stud4","amount":220.00,"created":"2019-06-18","status":"Success","paymentDate":"2019-06-18","reconcilable":true},
        {"id":"p7","paymentId":7,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":55.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p8","paymentId":8,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":50.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p9","paymentId":9,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":110.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p10","paymentId":10,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":77.00,"created":"2019-08-22","status":"Success","paymentDate":"2019-08-22","reconcilable":true},
        {"id":"p11","paymentId":11,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":88.00,"created":"2019-08-23","status":"Success","paymentDate":"2019-08-23","reconcilable":true},
        {"id":"p12","paymentId":12,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":99.00,"created":"2019-08-24","status":"Success","paymentDate":"2019-08-24","reconcilable":true}
        ]
        """



    Scenario: (+) Get depositPayments for existing account by notadmin with access rights

#       <--->  Login as notadmin

        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + "/1/200"
        When method GET
        Then status 200
        And match $ contains
        """
        [
        {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
        {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
        {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true},
        {"id":"p5","paymentId":5,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":220.00,"created":"2019-06-18","status":"Success","paymentDate":"2019-06-18","reconcilable":true},
        {"id":"p6","paymentId":6,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Credit card","contactId":10,"contactName":"stud4","amount":220.00,"created":"2019-06-18","status":"Success","paymentDate":"2019-06-18","reconcilable":true},
        {"id":"p7","paymentId":7,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":55.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p8","paymentId":8,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":50.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p9","paymentId":9,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":110.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
        {"id":"p10","paymentId":10,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":77.00,"created":"2019-08-22","status":"Success","paymentDate":"2019-08-22","reconcilable":true},
        {"id":"p11","paymentId":11,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":88.00,"created":"2019-08-23","status":"Success","paymentDate":"2019-08-23","reconcilable":true},
        {"id":"p12","paymentId":12,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":16,"contactName":"stud10","amount":99.00,"created":"2019-08-24","status":"Success","paymentDate":"2019-08-24","reconcilable":true}
        ]
        """



    Scenario: (-) Get depositPayments for existing account by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1/200"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permission to create banking. Please contact your administrator"


