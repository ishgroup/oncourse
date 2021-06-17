@parallel=false
Feature: Main feature for all POST requests with path 'access'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPathLogout = 'logout'

        

#       <---> Users and Roles that are used in tests:
#       UserWithRightsDelete with RoleWithRightsDelete (all checkboxes are checked)
#       UserWithRightsCreate with RoleWithRightsCreate
#       UserWithRightsEdit with RoleWithRightsEdit
#       UserWithRightsPrint with RoleWithRightsPrint
#       UserWithRightsView with RoleWithRightsView
#       UserWithRightsHide with RoleWithRightsHide (all checkboxes are unchecked)

    
    Scenario: Check access for role with access rights Delete
        
        Given path ishPathLogout
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

        * table accessRights

            | entity                | path                                       | method   | result             |
            | 'Audit'               | '/a/v1/list/entity/audit/1'                | 'GET'    | {"hasAccess":true} |
            | 'Tag'                 | '/a/v1/tag'                                | 'GET'    | {"hasAccess":true} |
            | 'Tag'                 | '/a/v1/tag'                                | 'POST'   | {"hasAccess":true} |
            | 'Tag'                 | '/a/v1/tag/1'                              | 'PUT'    | {"hasAccess":true} |
            | 'Tag'                 | '/a/v1/tag/1'                              | 'DELETE' | {"hasAccess":true} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'         | 'GET'    | {"hasAccess":true} |
            | 'Qualification'       | '/a/v1/list/entity/qualification'          | 'POST'   | {"hasAccess":true} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'        | 'PUT'    | {"hasAccess":true} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'        | 'DELETE' | {"hasAccess":true} |
            | 'Module'              | '/a/v1/list/entity/module/'                | 'GET'    | {"hasAccess":true} |
            | 'Module'              | '/a/v1/list/entity/module'                 | 'POST'   | {"hasAccess":true} |
            | 'Module'              | '/a/v1/list/entity/module/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Module'              | '/a/v1/list/entity/module/1'               | 'DELETE' | {"hasAccess":true} |
            | 'AVETMISS8'           | '/a/v1/export/avetmiss8/outcomes'          | 'GET'    | {"hasAccess":true} |
            | 'AVETMISS8'           | '/a/v1/export/avetmiss8/outcomes'          | 'PUT'    | {"hasAccess":true} |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'GET'    | {"hasAccess":true} |
            | 'Script'              | '/a/v1/list/entity/script'                 | 'POST'   | {"hasAccess":true} |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'DELETE' | {"hasAccess":true}|
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'PATCH'  | {"hasAccess":true} |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'GET'    | {"hasAccess":true} |
            | 'Site'                | '/a/v1/list/entity/site'                   | 'POST'   | {"hasAccess":true} |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'PUT'    | {"hasAccess":true} |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'DELETE' | {"hasAccess":true} |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'GET'    | {"hasAccess":true} |
            | 'Room'                | '/a/v1/list/entity/room'                   | 'POST'   | {"hasAccess":true} |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'PUT'    | {"hasAccess":true} |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'DELETE' | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'      | 'GET'    | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'        | 'POST'   | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site'  | 'GET'    | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'    | 'POST'   | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'      | 'GET'    | {"hasAccess":true} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'        | 'POST'   | {"hasAccess":true} |
            | 'Document'            | '/a/v1/list/entity/document/1'             | 'GET'    | {"hasAccess":true} |
            | 'Document'            | '/a/v1/list/entity/document'               | 'POST'   | {"hasAccess":true} |
            | 'Document'            | '/a/v1/list/entity/document/export/1'      | 'GET'    | {"hasAccess":true} |
            | 'Document'            | '/a/v1/list/entity/document/search'        | 'POST'   | {"hasAccess":true} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'GET'    | {"hasAccess":true} |
            | 'Payslip'             | '/a/v1/list/entity/payslip'                | 'POST'   | {"hasAccess":true} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'DELETE' | {"hasAccess":true} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'        | 'POST'   | {"hasAccess":true} |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'GET'    | {"hasAccess":true} |
            | 'Account'             | '/a/v1/list/entity/account'                | 'POST'   | {"hasAccess":true} |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'DELETE' | {"hasAccess":true} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'     | 'POST'   | {"hasAccess":true} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'              | 'GET'    | {"hasAccess":true} |
            | 'Invoice'             | '/a/v1/list/entity/invoice'                | 'POST'   | {"hasAccess":true} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/contra/1'       | 'POST'   | {"hasAccess":true} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'GET'    | {"hasAccess":true} |
            | 'Discount'            | '/a/v1/list/entity/discount'               | 'POST'   | {"hasAccess":true} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'PUT'    | {"hasAccess":true} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'DELETE' | {"hasAccess":true} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'GET'    | {"hasAccess":true} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'          | 'POST'   | {"hasAccess":true} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'PUT'    | {"hasAccess":true} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'DELETE' | {"hasAccess":true} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'              | 'GET'    | {"hasAccess":true} |
            | 'Banking'             | '/a/v1/list/entity/banking'                | 'POST'   | {"hasAccess":true} |
            | 'Banking'             | '/a/v1/list/entity/banking/reconcile'      | 'POST'   | {"hasAccess":true} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'              | 'DELETE' | {"hasAccess":true} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'GET'    | {"hasAccess":true} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'            | 'POST'   | {"hasAccess":true} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'PUT'    | {"hasAccess":true} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'DELETE' | {"hasAccess":true} |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'GET'    | {"hasAccess":true} |
            | 'Application'         | '/a/v1/list/entity/application'            | 'POST'   | {"hasAccess":true} |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'PUT'    | {"hasAccess":true} |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'DELETE' | {"hasAccess":true} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'GET'    | {"hasAccess":true} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'         | 'POST'   | {"hasAccess":true} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'PUT'    | {"hasAccess":true} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'DELETE' | {"hasAccess":false}|
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'GET'    | {"hasAccess":true} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'      | 'POST'   | {"hasAccess":true} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'PUT'    | {"hasAccess":true} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'DELETE' | {"hasAccess":false}|
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'GET'    | {"hasAccess":true} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'         | 'POST'   | {"hasAccess":true} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'PUT'    | {"hasAccess":true} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'DELETE' | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":true} |
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":true} |
            | 'Survey'              | '/a/v1/list/entity/survey/1'               | 'GET'    | {"hasAccess":true} |
            | 'Survey'              | '/a/v1/list/entity/survey/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'GET'    | {"hasAccess":true} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'DELETE' | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":true} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true} |
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":true} |
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":true} |
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":true} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":true} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false}|
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":true} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":true} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":true} |
#            | 'Note'                | '/a/v1/list/entity/note'                   | 'POST'   | {"hasAccess":true} |
            | 'Note'                | '/a/v1/list/entity/note/1'                 | 'PUT'    | {"hasAccess":true} |
            | 'Note'                | '/a/v1/list/entity/note/1'                 | 'DELETE' | {"hasAccess":true} |

         * call read('getAccess.feature') accessRights



    Scenario: Check access for role with access rights Create

        Given path ishPathLogout
        And request {}
        When method PUT
        
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

        * table accessRights

            | entity                | path                                       | method   | result              |
            | 'Audit'               | '/a/v1/list/entity/audit/'                 | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                                | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                                | 'POST'   | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag/1'                              | 'PUT'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag/1'                              | 'DELETE' | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'         | 'GET'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification'          | 'POST'   | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'        | 'PUT'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'        | 'DELETE' | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/'                | 'GET'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module'                 | 'POST'   | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module/1'               | 'PUT'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module/1'               | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script'                 | 'POST'   | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'PUT'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'               | 'PATCH'  | {"hasAccess":true} |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'GET'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site'                   | 'POST'   | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'PUT'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site/1'                 | 'DELETE' | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'GET'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room'                   | 'POST'   | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'PUT'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room/1'                 | 'DELETE' | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'      | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'        | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site'  | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'    | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'      | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'        | 'POST'   | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document'               | 'POST'   | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/export/1'      | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/search'        | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip'                | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'PUT'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'        | 'POST'   | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account'                | 'POST'   | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'PUT'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account/1'              | 'DELETE' | {"hasAccess":false} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'     | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice'                | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'              | 'PUT'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount'               | 'POST'   | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'PUT'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount/1'             | 'DELETE' | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'GET'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'          | 'POST'   | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'PUT'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'        | 'DELETE' | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'GET'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'            | 'POST'   | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'PUT'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'          | 'DELETE' | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'GET'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application'            | 'POST'   | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'PUT'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application/1'          | 'DELETE' | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'GET'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'         | 'POST'   | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'PUT'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'       | 'DELETE' | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'GET'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'      | 'POST'   | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'PUT'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'    | 'DELETE' | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'GET'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'         | 'POST'   | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'PUT'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":true}  |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":true}  |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":true}  |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":true}  |
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":true}  |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true}  |
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":true}  |
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":true}  |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true}  |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":false}|
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":false}|

         * call read('getAccess.feature') accessRights



    Scenario: Check access for role with access rights Edit

        Given path ishPathLogout
        And request {}
        When method PUT
        
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

        * table accessRights

            | entity                | path                                      | method   | result              |
            | 'Audit'               | '/a/v1/list/entity/audit/'                | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'POST'   | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'PUT'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'DELETE' | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'        | 'GET'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification'         | 'POST'   | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'PUT'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/'               | 'GET'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module'                | 'POST'   | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'PUT'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script'                | 'POST'   | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PUT'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PATCH'  | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site'                  | 'POST'   | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'PUT'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room'                  | 'POST'   | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'PUT'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site' | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'   | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document'              | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/export/1'     | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/search'       | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip'               | 'POST'   | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'PUT'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'DELETE' | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'       | 'POST'   | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account'               | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'PUT'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'DELETE' | {"hasAccess":false} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'    | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice'               | 'POST'   | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'PUT'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount'              | 'POST'   | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'PUT'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'DELETE' | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'GET'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'         | 'POST'   | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'PUT'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'DELETE' | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'GET'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'           | 'POST'   | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'PUT'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'DELETE' | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'GET'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application'           | 'POST'   | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'PUT'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'DELETE' | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'        | 'POST'   | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'PUT'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'GET'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'     | 'POST'   | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'PUT'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'DELETE' | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'        | 'POST'   | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'PUT'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":true} |
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":false}|
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":false}|
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":false}|
#            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":true} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false}|
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":false}|
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":false}|
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":false}|
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":false}|
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":false}|

         * call read('getAccess.feature') accessRights



    Scenario: Check access for role with access rights Print

        Given path ishPathLogout
        And request {}
        When method PUT
        
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

        * table accessRights

            | entity                | path                                      | method   | result              |
            | 'Audit'               | '/a/v1/list/entity/audit/'                | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'POST'   | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'PUT'    | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'DELETE' | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'        | 'GET'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification'         | 'POST'   | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'PUT'    | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/'               | 'GET'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module'                | 'POST'   | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script'                | 'POST'   | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PATCH'  | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site'                  | 'POST'   | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room'                  | 'POST'   | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site' | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'   | 'POST'   | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":true}  |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document'              | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/export/1'     | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/search'       | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip'               | 'POST'   | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'DELETE' | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'       | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account'               | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'DELETE' | {"hasAccess":false} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'    | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice'               | 'POST'   | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount'              | 'POST'   | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'PUT'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'DELETE' | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'GET'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'         | 'POST'   | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'PUT'    | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'DELETE' | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'GET'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'           | 'POST'   | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'PUT'    | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'DELETE' | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'GET'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application'           | 'POST'   | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'PUT'    | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'DELETE' | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'        | 'POST'   | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'GET'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'     | 'POST'   | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'PUT'    | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'DELETE' | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'        | 'POST'   | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":true} |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":false}|
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":false}|
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true} |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":false}|
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":false}|
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":false}|
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":false}|
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false}|
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false}|
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":false}|
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":true} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":false}|
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":false}|
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true} |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":false}|
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":false}|
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":false}|
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":true} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":false}|
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true} |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":false}|
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":false}|
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":false}|
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":false}|
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":false}|

         * call read('getAccess.feature') accessRights



    Scenario: Check access for role with access rights View

        Given path ishPathLogout
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

        * table accessRights

            | entity                | path                                      | method   | result              |
            | 'Audit'               | '/a/v1/list/entity/audit/'                | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'POST'   | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'PUT'    | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'DELETE' | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'        | 'GET'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification'         | 'POST'   | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'PUT'    | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/'               | 'GET'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module'                | 'POST'   | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script'                | 'POST'   | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PATCH'  | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site'                  | 'POST'   | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room'                  | 'POST'   | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site' | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'   | 'POST'   | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document'              | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/export/1'     | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/search'       | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip'               | 'POST'   | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'DELETE' | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'       | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Account'             | '/a/v1/list/entity/account'               | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'DELETE' | {"hasAccess":false} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'    | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'GET'    | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice'               | 'POST'   | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Discount'            | '/a/v1/list/entity/discount'              | 'POST'   | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'PUT'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'DELETE' | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'GET'    | {"hasAccess":true}  |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'         | 'POST'   | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'PUT'    | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'DELETE' | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'GET'    | {"hasAccess":true}  |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'           | 'POST'   | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'PUT'    | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'DELETE' | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'GET'    | {"hasAccess":true}  |
            | 'Application'         | '/a/v1/list/entity/application'           | 'POST'   | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'PUT'    | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'DELETE' | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'        | 'POST'   | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'GET'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'     | 'POST'   | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'PUT'    | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'DELETE' | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'        | 'POST'   | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":true}  |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":false} |
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true}  |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true}  |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true}  |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":false} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true}  |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true}  |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true}  |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":false} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":false} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":false} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":true}  |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true}  |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true}  |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":true}  |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":false} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true}  |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":false} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":false} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":false} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true}  |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":false} |

         * call read('getAccess.feature') accessRights



    Scenario: Check access for role with access rights Hide

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogout
        And request {}
        When method PUT
        
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table accessRights

            | entity                | path                                      | method   | result              |
            | 'Audit'               | '/a/v1/list/entity/audit/'                | 'GET'    | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag'                               | 'GET'    | {"hasAccess":true}  |
            | 'Tag'                 | '/a/v1/tag'                               | 'POST'   | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'PUT'    | {"hasAccess":false} |
            | 'Tag'                 | '/a/v1/tag/1'                             | 'DELETE' | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/'        | 'GET'    | {"hasAccess":true}  |
            | 'Qualification'       | '/a/v1/list/entity/qualification'         | 'POST'   | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'PUT'    | {"hasAccess":false} |
            | 'Qualification'       | '/a/v1/list/entity/qualification/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/'               | 'GET'    | {"hasAccess":true}  |
            | 'Module'              | '/a/v1/list/entity/module'                | 'POST'   | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Module'              | '/a/v1/list/entity/module/1'              | 'DELETE' | {"hasAccess":false} |
            | 'AVETMISS8'           | '/a/v1/export/avetmiss8/outcomes'         | 'GET'    | {"hasAccess":false} |
            | 'AVETMISS8'           | '/a/v1/export/avetmiss8/outcomes'         | 'PUT'    | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Script'              | '/a/v1/list/entity/script'                | 'POST'   | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Script'              | '/a/v1/list/entity/script/1'              | 'PATCH'  | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Site'                | '/a/v1/list/entity/site'                  | 'POST'   | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Site'                | '/a/v1/list/entity/site/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'GET'    | {"hasAccess":true}  |
            | 'Room'                | '/a/v1/list/entity/room'                  | 'POST'   | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Room'                | '/a/v1/list/entity/room/1'                | 'DELETE' | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/pdf/1?entityName=Site' | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/pdf?entityName=Site'   | 'POST'   | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export/1?entityName=Site'     | 'GET'    | {"hasAccess":false} |
            | 'Print'               | '/a/v1/list/export?entityName=Site'       | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/1'            | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document'              | 'POST'   | {"hasAccess":false} |
            | 'Document'            | '/a/v1/list/entity/document/export/1'     | 'GET'    | {"hasAccess":true}  |
            | 'Document'            | '/a/v1/list/entity/document/search'       | 'POST'   | {"hasAccess":true}  |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'GET'    | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip'               | 'POST'   | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/1'             | 'DELETE' | {"hasAccess":false} |
            | 'Payslip'             | '/a/v1/list/entity/payslip/marking'       | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'GET'    | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account'               | 'POST'   | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Account'             | '/a/v1/list/entity/account/1'             | 'DELETE' | {"hasAccess":false} |
            | 'AccountTransaction'  | '/a/v1/list/entity/accountTransaction'    | 'POST'   | {"hasAccess":true}  |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'GET'    | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice'               | 'POST'   | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Invoice'             | '/a/v1/list/entity/invoice/contra/1'      | 'POST'   | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'GET'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount'              | 'POST'   | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'PUT'    | {"hasAccess":false} |
            | 'Discount'            | '/a/v1/list/entity/discount/1'            | 'DELETE' | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'GET'    | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass'         | 'POST'   | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'PUT'    | {"hasAccess":false} |
            | 'CorporatePass'       | '/a/v1/list/entity/corporatepass/1'       | 'DELETE' | {"hasAccess":false} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'             | 'GET'    | {"hasAccess":false} |
            | 'Banking'             | '/a/v1/list/entity/banking'               | 'POST'   | {"hasAccess":false} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'             | 'PUT'    | {"hasAccess":false} |
            | 'Banking'             | '/a/v1/list/entity/banking/1'             | 'DELETE' | {"hasAccess":false} |
            | 'Banking'             | '/a/v1/list/entity/banking/reconcile'     | 'POST'   | {"hasAccess":false} |
            | 'Email'               | '/a/v1/list/option/email/template/1'      | 'GET'    | {"hasAccess":false} |
            | 'Email'               | '/a/v1/list/option/email/'                | 'POST'   | {"hasAccess":false} |
            | 'SendEmail'           | '/a/v1/list/option/email/template'        | 'GET'    | {"hasAccess":false} |
            | 'SendEmail'           | '/a/v1/list/option/email'                 | 'POST'   | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'GET'    | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList'           | 'POST'   | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'PUT'    | {"hasAccess":false} |
            | 'WaitingList'         | '/a/v1/list/entity/waitingList/1'         | 'DELETE' | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'GET'    | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application'           | 'POST'   | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'PUT'    | {"hasAccess":false} |
            | 'Application'         | '/a/v1/list/entity/application/1'         | 'DELETE' | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct'        | 'POST'   | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'ArticleProduct'      | '/a/v1/list/entity/articleProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'GET'    | {"hasAccess":true}  |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct'     | 'POST'   | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'PUT'    | {"hasAccess":false} |
            | 'MembershipProduct'   | '/a/v1/list/entity/membershipProduct/1'   | 'DELETE' | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'GET'    | {"hasAccess":true}  |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct'        | 'POST'   | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'PUT'    | {"hasAccess":false} |
            | 'VoucherProduct'      | '/a/v1/list/entity/voucherProduct/1'      | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'GET'    | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate'            | 'POST'   | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'PUT'    | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/1'          | 'DELETE' | {"hasAccess":false} |
            | 'Certificate'         | '/a/v1/list/entity/certificate/revoke'     | 'POST'   | {"hasAccess":false} |
            | 'Certificate'         |'/a/v1/list/entity/certificate/validation'  | 'POST'   | {"hasAccess":false} |
            | 'Survey'              | '/a/v1/list/entity/survey/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Survey'              | '/a/v1/list/entity/survey/1'               | 'PUT'    | {"hasAccess":false} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'GET'    | {"hasAccess":false} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Outcome'             | '/a/v1/list/entity/outcome/1'              | 'DELETE' | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Course'              | '/a/v1/list/entity/course'                 | 'POST'   | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'PUT'    | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/1'               | 'DELETE' | {"hasAccess":false} |
            | 'Course'              | '/a/v1/list/entity/course/duplicate'       | 'POST'   | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'GET'    | {"hasAccess":true}  |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass'            | 'POST'   | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'PUT'    | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/1'          | 'DELETE' | {"hasAccess":false} |
            | 'CourseClass'         | '/a/v1/list/entity/courseClass/duplicate'  | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'GET'    | {"hasAccess":true}  |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole'       | 'POST'   | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'PUT'    | {"hasAccess":false} |
            | 'DefinedTutorRole'    | '/a/v1/list/entity/definedTutorRole/1'     | 'DELETE' | {"hasAccess":false} |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'GET'    | {"hasAccess":true}  |
            | 'ProductItem'         | '/a/v1/list/entity/sales/1'                | 'PUT'    | {"hasAccess":false} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'GET'    | {"hasAccess":false} |
            | 'Enrolment'           | '/a/v1/list/entity/enrolment/1'            | 'PUT'    | {"hasAccess":false} |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'GET'    | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment'             | 'POST'   | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'PUT'    | {"hasAccess":true}  |
            | 'Assessment'          | '/a/v1/list/entity/assessment/1'           | 'DELETE' | {"hasAccess":true}  |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'GET'    | {"hasAccess":true}  |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate'         | 'POST'   | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'PUT'    | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/1'       | 'DELETE' | {"hasAccess":false} |
            | 'ExportTemplate'      | '/a/v1/list/entity/exportTemplate/export/1'| 'POST'   | {"hasAccess":true}  |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'GET'    | {"hasAccess":true}  |
            | 'Report'              | '/a/v1/list/entity/report'                 | 'POST'   | {"hasAccess":false} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'PUT'    | {"hasAccess":false} |
            | 'Report'              | '/a/v1/list/entity/report/1'               | 'DELETE' | {"hasAccess":false} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'GET'    | {"hasAccess":false} |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/1'            | 'PUT'    | {"hasAccess":true}  |
            | 'PaymentIn'           | '/a/v1/list/entity/paymentIn/reverse/1'    | 'POST'   | {"hasAccess":true}  |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut/1'           | 'GET'    | {"hasAccess":false} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'POST'   | {"hasAccess":false} |
            | 'PaymentOut'          | '/a/v1/list/entity/paymentOut'             | 'PUT'    | {"hasAccess":true}  |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'GET'    | {"hasAccess":true}  |
            | 'Contact'             | '/a/v1/list/entity/contact'                | 'POST'   | {"hasAccess":false} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'PUT'    | {"hasAccess":false} |
            | 'Contact'             | '/a/v1/list/entity/contact/1'              | 'DELETE' | {"hasAccess":false} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'GET'    | {"hasAccess":false} |
            | 'ContactMerge'        | '/a/v1/list/entity/contact/merge'          | 'POST'   | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'GET'    | {"hasAccess":true}  |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay'          | 'POST'   | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'PUT'    | {"hasAccess":false} |
            | 'ReportOverlay'       | '/a/v1/list/entity/reportOverlay/1'        | 'DELETE' | {"hasAccess":false} |
            | 'Note'                | '/a/v1/list/entity/note'                   | 'POST'   | {"hasAccess":false} |
            | 'Note'                | '/a/v1/list/entity/note/1'                 | 'PUT'    | {"hasAccess":false} |
            | 'Note'                | '/a/v1/list/entity/note/1'                 | 'DELETE' | {"hasAccess":false} |

         * call read('getAccess.feature') accessRights
