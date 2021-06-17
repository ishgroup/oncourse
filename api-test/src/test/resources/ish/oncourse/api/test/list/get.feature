@parallel=false
Feature: Main feature for all GET requests with path 'list'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get list for each entity by admin

        * table getListOfEntity

        | entity                |
        | 'Account'             |
        | 'AccountTransaction'  |
        | 'Audit'               |
        | 'Banking'             |
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
        | 'Enrolment'           |
        | 'Assessment'          |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |
        | 'PriorLearning'       |

        * call read('getListOfEntity.feature') getListOfEntity



    Scenario: (+) Get list for each entity by notadmin with access rights View

        Given path '/logout'
        And request {}
        When method PUT
        
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
        | 'Banking'             |
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
        | 'Enrolment'           |
        | 'Assessment'          |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |
        | 'PriorLearning'       |

        * call read('getListOfEntity.feature') getListOfEntity



    Scenario: (+) Get list for each entity by notadmin with access rights Hide

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table getListOfEntity

        | entity                |
        | 'Audit'               |
        | 'Module'              |
        | 'Qualification'       |
        | 'Room'                |
        | 'Script'              |
        | 'Site'                |
        | 'Document'            |
        | 'Application'         |
        | 'ArticleProduct'      |
        | 'MembershipProduct'   |
        | 'VoucherProduct'      |
        | 'Survey'              |
        | 'ProductItem'         |
        | 'Message'             |
        | 'Assessment'          |
        | 'Course'              |
        | 'CourseClass'         |
        | 'Contact'             |

        * call read('getListOfEntity.feature') getListOfEntity



    Scenario: (-) Get list for each entity by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
        | 'Banking'             |
        | 'CorporatePass'       |
        | 'Discount'            |
        | 'Invoice'             |
        | 'Payslip'             |
        | 'WaitingList'         |
        | 'Application'         |
        | 'Certificate'         |
        | 'Outcome'             |
        | 'Enrolment'           |
        | 'PaymentIn'           |
        | 'PaymentOut'          |
        | 'PriorLearning'       |

        * call read('getListOfEntityWithoutRights.feature') getListOfEntity

