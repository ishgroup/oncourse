@parallel=false
Feature: Main feature for all GET requests with path 'list/plain'


    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/plain'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get plain list for each entity by admin

        * table getListOfEntity

        | entity                |
        | 'Account'             |
        | 'AccountTransaction'  |
        | 'Audit'               |
        | 'CorporatePass'       |
        | 'Discount'            |
        | 'Invoice'             |
        | 'Module'              |
        | 'Qualification'       |
        | 'Payslip'             |
        | 'Room'                |
        | 'Script'              |
        | 'Site'                |
        | 'Document'            |
        | 'Banking'             |
        | 'WaitingList'         |
        | 'Application'         |
        | 'ArticleProduct'      |
        | 'MembershipProduct'   |
        | 'VoucherProduct'      |
        | 'Certificate'         |
        | 'Survey'              |
        | 'Outcome'             |
        | 'ProductItem'         |
        | 'Message'             |
        | 'EmailTemplate'       |
        | 'Report'              |
        | 'ReportOverlay'       |
        | 'Enrolment'           |
        | 'Assessment'          |
        | 'ExportTemplate'      |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'DefinedTutorRole'    |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |
        | 'PriorLearning'       |

        * call read('getPlainListOfEntity.feature') getListOfEntity



    Scenario: (+) Get plain list for each entity by notadmin with access rights Delete

#       <---> Login as notadmin
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

        * table getListOfEntity

        | entity                |
        | 'Account'             |
        | 'AccountTransaction'  |
        | 'Audit'               |
        | 'CorporatePass'       |
        | 'Discount'            |
        | 'Invoice'             |
        | 'Module'              |
        | 'Qualification'       |
        | 'Payslip'             |
        | 'Room'                |
        | 'Script'              |
        | 'Site'                |
        | 'Document'            |
        | 'Banking'             |
        | 'WaitingList'         |
        | 'Application'         |
        | 'ArticleProduct'      |
        | 'MembershipProduct'   |
        | 'VoucherProduct'      |
        | 'Certificate'         |
        | 'Survey'              |
        | 'Outcome'             |
        | 'ProductItem'         |
        | 'Message'             |
        | 'EmailTemplate'       |
        | 'Report'              |
        | 'ReportOverlay'       |
        | 'Enrolment'           |
        | 'Assessment'          |
        | 'ExportTemplate'      |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'DefinedTutorRole'    |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |
        | 'PriorLearning'       |

        * call read('getPlainListOfEntity.feature') getListOfEntity



    Scenario: (+) Get plain list for each entity by notadmin with access rights Hide

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table getListOfEntity

        | entity                |
        | 'Module'              |
        | 'Qualification'       |
        | 'Room'                |
        | 'Script'              |
        | 'Site'                |
        | 'Document'            |
        | 'ArticleProduct'      |
        | 'MembershipProduct'   |
        | 'VoucherProduct'      |
        | 'Survey'              |
        | 'ProductItem'         |
        | 'Message'             |
        | 'EmailTemplate'       |
        | 'Report'              |
        | 'ReportOverlay'       |
        | 'Assessment'          |
        | 'ExportTemplate'      |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |

        * call read('getPlainListOfEntity.feature') getListOfEntity



    Scenario: (-) Get plain list for each entity by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table getListOfEntity

        | entity                |
        | 'Account'             |
        | 'AccountTransaction'  |
        | 'Audit'               |
        | 'CorporatePass'       |
        | 'Discount'            |
        | 'Invoice'             |
        | 'Payslip'             |
        | 'Banking'             |
        | 'WaitingList'         |
        | 'Application'         |
        | 'Certificate'         |
        | 'Outcome'             |
        | 'Enrolment'           |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'PriorLearning'       |

        * call read('getPlainListOfEntityWithoutRights.feature') getListOfEntity
