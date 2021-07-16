@parallel=false
Feature: Main feature for all POST requests with path 'filter'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'filter'
        * def ishPathLogin = 'login'
        * def ishPathTag = 'tag'
        


    Scenario: (+) Create new custom filter by admin

        * table createFilter

        | entity                | dataToCreate                                                                                                                                              | dataToAssert                                                                                                                                                          |
        | 'Qualification'       | {"name":"filter1","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}    | [{"name":"filter1","id":'#(id)',"entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}] |
        | 'Module'              | {"name":"filter1","entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}               | [{"name":"filter1","id":'#(id)',"entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}]            |
        | 'Site'                | {"name":"filter1","entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}]                                                       |
        | 'Room'                | {"name":"filter1","entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}]                                                       |
        | 'Invoice'             | {"name":"filter1","entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}                                              | [{"name":"filter1","id":'#(id)',"entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}]                                           |
        | 'PaymentIn'           | {"name":"filter1","entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}                                               | [{"name":"filter1","id":'#(id)',"entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}]                                            |
        | 'CorporatePass'       | {"name":"filter1","entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}                                   | [{"name":"filter1","id":'#(id)',"entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}]                                |
        | 'AccountTransaction'  | {"name":"filter1","entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}                                                        | [{"name":"filter1","id":'#(id)',"entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}]                                                     |
        | 'Payslip'             | {"name":"filter1","entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}                                             | [{"name":"filter1","id":'#(id)',"entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}]                                          |
        | 'Account'             | {"name":"filter1","entity":"Account","expression":"id == 1","showForCurrentOnly":true}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Account","expression":"id == 1","showForCurrentOnly":true}]                                                                 |
        | 'Audit'               | {"name":"filter1","entity":"Audit","expression":"created today ","showForCurrentOnly":true}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Audit","expression":"created today ","showForCurrentOnly":true}]                                                            |
        | 'Script'              | {"name":"filter1","entity":"Script","expression":"description !== null ","showForCurrentOnly":true}                                                       | [{"name":"filter1","id":'#(id)',"entity":"Script","expression":"description !== null ","showForCurrentOnly":true}]                                                    |
        | 'Banking'             | {"name":"filter1","entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}]                                                         |
        | 'WaitingList'         | {"name":"filter1","entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Application'         | {"name":"filter1","entity":"Application","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Application","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Certificate'         | {"name":"filter1","entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Survey'              | {"name":"filter1","entity":"Survey","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Survey","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'Outcome'             | {"name":"filter1","entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}]                                                                |
        | 'Audit'               | {"name":"filter1","entity":"Audit","expression":"id == 1","showForCurrentOnly":false}                                                                     | [{"name":"filter1","id":'#(id)',"entity":"Audit","expression":"id == 1","showForCurrentOnly":false}]                                                                  |
        | 'Discount'            | {"name":"filter1","entity":"Discount","expression":"id == 1","showForCurrentOnly":false}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Discount","expression":"id == 1","showForCurrentOnly":false}]                                                               |
        | 'Course'              | {"name":"filter1","entity":"Course","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Course","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'CourseClass'         | {"name":"filter1","entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Enrolment'           | {"name":"filter1","entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}]                                                               |
        | 'ProductItem'         | {"name":"filter1","entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}                                                                | [{"name":"filter1","id":'#(id)',"entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}]                                                             |
        | 'PaymentOut'          | {"name":"filter1","entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}]                                                         |
        | 'Contact'             | {"name":"filter1","entity":"Contact","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Contact","expression":"id == 1","showForCurrentOnly":false}]                                                                    |

        * call read('createFilter.feature') createFilter



    Scenario: (+) Create new custom filter by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <---> Login as notadmin

        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table createFilter

        | entity                | dataToCreate                                                                                                                                              | dataToAssert                                                                                                                                                          |
        | 'Qualification'       | {"name":"filter1","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}    | [{"name":"filter1","id":'#(id)',"entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}] |
        | 'Module'              | {"name":"filter1","entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}               | [{"name":"filter1","id":'#(id)',"entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}]            |
        | 'Site'                | {"name":"filter1","entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}]                                                       |
        | 'Room'                | {"name":"filter1","entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}]                                                       |
        | 'Invoice'             | {"name":"filter1","entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}                                              | [{"name":"filter1","id":'#(id)',"entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}]                                           |
        | 'PaymentIn'           | {"name":"filter1","entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}                                               | [{"name":"filter1","id":'#(id)',"entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}]                                            |
        | 'CorporatePass'       | {"name":"filter1","entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}                                   | [{"name":"filter1","id":'#(id)',"entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}]                                |
        | 'AccountTransaction'  | {"name":"filter1","entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}                                                        | [{"name":"filter1","id":'#(id)',"entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}]                                                     |
        | 'Payslip'             | {"name":"filter1","entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}                                             | [{"name":"filter1","id":'#(id)',"entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}]                                          |
        | 'Account'             | {"name":"filter1","entity":"Account","expression":"id == 1","showForCurrentOnly":true}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Account","expression":"id == 1","showForCurrentOnly":true}]                                                                 |
        | 'Audit'               | {"name":"filter1","entity":"Audit","expression":"created today ","showForCurrentOnly":true}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Audit","expression":"created today ","showForCurrentOnly":true}]                                                            |
        | 'Script'              | {"name":"filter1","entity":"Script","expression":"description !== null ","showForCurrentOnly":true}                                                       | [{"name":"filter1","id":'#(id)',"entity":"Script","expression":"description !== null ","showForCurrentOnly":true}]                                                    |
        | 'Banking'             | {"name":"filter1","entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}]                                                         |
        | 'WaitingList'         | {"name":"filter1","entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Application'         | {"name":"filter1","entity":"Application","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Application","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Certificate'         | {"name":"filter1","entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Survey'              | {"name":"filter1","entity":"Survey","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Survey","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'Outcome'             | {"name":"filter1","entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}]                                                                |
        | 'Audit'               | {"name":"filter1","entity":"Audit","expression":"id == 1","showForCurrentOnly":false}                                                                     | [{"name":"filter1","id":'#(id)',"entity":"Audit","expression":"id == 1","showForCurrentOnly":false}]                                                                  |
        | 'Discount'            | {"name":"filter1","entity":"Discount","expression":"id == 1","showForCurrentOnly":false}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Discount","expression":"id == 1","showForCurrentOnly":false}]                                                               |
        | 'Course'              | {"name":"filter1","entity":"Course","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Course","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'CourseClass'         | {"name":"filter1","entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Enrolment'           | {"name":"filter1","entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}]                                                               |
        | 'ProductItem'         | {"name":"filter1","entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}                                                                | [{"name":"filter1","id":'#(id)',"entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}]                                                             |
        | 'PaymentOut'          | {"name":"filter1","entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}]                                                         |
        | 'Contact'             | {"name":"filter1","entity":"Contact","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Contact","expression":"id == 1","showForCurrentOnly":false}]                                                                    |

        * call read('createFilter.feature') createFilter



    Scenario: (+) Create new custom filter by notadmin with access rights Hide

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table createFilter

        | entity                | dataToCreate                                                                                                                                              | dataToAssert                                                                                                                                                          |
        | 'Qualification'       | {"name":"filter1","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}    | [{"name":"filter1","id":'#(id)',"entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}] |
        | 'Module'              | {"name":"filter1","entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}               | [{"name":"filter1","id":'#(id)',"entity":"Module","expression":"( isOffered == true or isOffered == false ) and ~ \"Create\" ","showForCurrentOnly":true}]            |
        | 'Site'                | {"name":"filter1","entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Site","expression":"name starts with 's'","showForCurrentOnly":true}]                                                       |
        | 'Room'                | {"name":"filter1","entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}                                                          | [{"name":"filter1","id":'#(id)',"entity":"Room","expression":"name starts with 'r'","showForCurrentOnly":true}]                                                       |
        | 'Script'              | {"name":"filter1","entity":"Script","expression":"description !== null ","showForCurrentOnly":true}                                                       | [{"name":"filter1","id":'#(id)',"entity":"Script","expression":"description !== null ","showForCurrentOnly":true}]                                                    |
        | 'Survey'              | {"name":"filter1","entity":"Survey","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Survey","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'Course'              | {"name":"filter1","entity":"Course","expression":"id == 1","showForCurrentOnly":false}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Course","expression":"id == 1","showForCurrentOnly":false}]                                                                 |
        | 'CourseClass'         | {"name":"filter1","entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"CourseClass","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'ProductItem'         | {"name":"filter1","entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}                                                                | [{"name":"filter1","id":'#(id)',"entity":"ProductItem","expression":"id == 1","showForCurrentOnly":true}]                                                             |
        | 'Contact'             | {"name":"filter1","entity":"Contact","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Contact","expression":"id == 1","showForCurrentOnly":false}]                                                                    |

        * call read('createFilter.feature') createFilter



    Scenario: (-) Create new custom filter by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table createFilter

        | entity                | dataToCreate                                                                                                                                              | dataToAssert                                                                                                                                                          |
        | 'Invoice'             | {"name":"filter1","entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}                                              | [{"name":"filter1","id":'#(id)',"entity":"Invoice","expression":"contact.firstName == 'stud1'","showForCurrentOnly":false}]                                           |
        | 'PaymentIn'           | {"name":"filter1","entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}                                               | [{"name":"filter1","id":'#(id)',"entity":"PaymentIn","expression":"payer.firstName == 'stud1'","showForCurrentOnly":true}]                                            |
        | 'CorporatePass'       | {"name":"filter1","entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}                                   | [{"name":"filter1","id":'#(id)',"entity":"CorporatePass","expression":"contact.firstName starts with 'co'","showForCurrentOnly":true}]                                |
        | 'AccountTransaction'  | {"name":"filter1","entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}                                                        | [{"name":"filter1","id":'#(id)',"entity":"AccountTransaction","expression":"id == 1","showForCurrentOnly":false}]                                                     |
        | 'Payslip'             | {"name":"filter1","entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}                                             | [{"name":"filter1","id":'#(id)',"entity":"Payslip","expression":"contact.firstName == 'tutor1'","showForCurrentOnly":false}]                                          |
        | 'Account'             | {"name":"filter1","entity":"Account","expression":"id == 1","showForCurrentOnly":true}                                                                    | [{"name":"filter1","id":'#(id)',"entity":"Account","expression":"id == 1","showForCurrentOnly":true}]                                                                 |
        | 'Audit'               | {"name":"filter1","entity":"Audit","expression":"created today ","showForCurrentOnly":true}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Audit","expression":"created today ","showForCurrentOnly":true}]                                                            |
        | 'Banking'             | {"name":"filter1","entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"Banking","expression":"type == MANUAL ","showForCurrentOnly":true}]                                                         |
        | 'WaitingList'         | {"name":"filter1","entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"WaitingList","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Application'         | {"name":"filter1","entity":"Application","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Application","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Certificate'         | {"name":"filter1","entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}                                                               | [{"name":"filter1","id":'#(id)',"entity":"Certificate","expression":"id == 1","showForCurrentOnly":false}]                                                            |
        | 'Outcome'             | {"name":"filter1","entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}                                                                   | [{"name":"filter1","id":'#(id)',"entity":"Outcome","expression":"id == 1","showForCurrentOnly":false}]                                                                |
        | 'Discount'            | {"name":"filter1","entity":"Discount","expression":"id == 1","showForCurrentOnly":false}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Discount","expression":"id == 1","showForCurrentOnly":false}]                                                               |
        | 'Enrolment'           | {"name":"filter1","entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}                                                                  | [{"name":"filter1","id":'#(id)',"entity":"Enrolment","expression":"id == 1","showForCurrentOnly":true}]                                                               |
        | 'PaymentOut'          | {"name":"filter1","entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}                                                            | [{"name":"filter1","id":'#(id)',"entity":"PaymentOut","expression":"amount <= 30","showForCurrentOnly":true}]                                                         |

        * call read('createFilterWithoutRights.feature') createFilter



    Scenario: (-) Add new filter with existing name

#       <--->  Add new filter and get its id:
        Given path ishPath
        And param entity = 'Qualification'
        And request {"name":"filter111","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}
        When method POST
        Then status 204

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'filter111')].id
#       <--->

        Given path ishPath
        And param entity = 'Qualification'
        And request {"name":"filter111","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}
        When method POST
        Then status 400
        And match $.errorMessage == "Filter with name 'filter111' exists."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        And param entity = 'Qualification'
        When method DELETE
        Then status 204


