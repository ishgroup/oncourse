@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/contact/merge'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact/merge'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get Merge data for tutor and student/tutor

        Given path ishPath
        And param contactA = 24
        And param contactB = 21
        When method GET
        Then status 200
        And match $.mergeLines contains only 
        """
    [
    {"key":"Contact.abn","label":"Abn","a":"555","b":"111"},
    {"key":"Contact.birthDate","label":"Birth date","a":"1972-05-05","b":"1995-05-04"},
    {"key":"Contact.email","label":"Email","a":"merge4@gmail.com","b":"merge1@gmail.com"},
    {"key":"Contact.fax","label":"Fax","a":"tA","b":"a"},
    {"key":"Contact.firstName","label":"First name","a":"tutorA","b":"studentA"},
    {"key":"Contact.homePhone","label":"Home phone","a":"tA","b":"a"},
    {"key":"Contact.honorific","label":"Honorific","a":"tA","b":"a"},
    {"key":"Contact.invoiceTerms","label":"Invoice terms","a":null,"b":"10"},
    {"key":"Contact.lastName","label":"Last name","a":"mergeA","b":"mergeA"},
    {"key":"Contact.message","label":"Message","a":"tA","b":"a"},
    {"key":"Contact.middleName","label":"Middle name","a":null,"b":"A"},
    {"key":"Contact.mobilePhone","label":"Mobile phone","a":"444555333","b":"444662210"},
    {"key":"Contact.postcode","label":"Postcode","a":"5003","b":"5000"},
    {"key":"Contact.state","label":"State","a":"SC","b":"SA"},
    {"key":"Contact.street","label":"Street","a":"address str3","b":"address str1"},
    {"key":"Contact.suburb","label":"Suburb","a":"Adelaide3","b":"Adelaide1"},
    {"key":"Contact.tfn","label":"Tfn","a":"555","b":"111"},
    {"key":"Contact.title","label":"Title","a":"tA","b":"sA"},
    {"key":"Contact.workPhone","label":"Work phone","a":"tA","b":"a"},
    {"key":"Contact.gender","label":"Gender","a":"Male","b":"Male"},
    {"key":"Contact.taxOverride","label":"Tax Override","a":null,"b":"N"},
    {"key":"Contact.country","label":"Country","a":"Australia","b":"Australia"},
    {"key":"tags","label":"Tags","a":"#contacts1","b":"#contacts1"},
    {"key":"customField.cf2","label":"contact field2","a":"tA","b":"aa"},
    {"key":"customField.cf1","label":"contact field1","a":"tA","b":"aa"},
    {"key":"Tutor.dateFinished","label":"Date finished","a":"#present","b":"#present"},
    {"key":"Tutor.dateStarted","label":"Date started","a":"#present","b":"#present"},
    {"key":"Tutor.payrollRef","label":"Payroll ref","a":"555","b":"111"},
    {"key":"Tutor.resume","label":"Resume","a":"tA","b":"Resume A"},
    {"key":"Tutor.wwChildrenCheckedOn","label":"Ww children checked on","a":"#present","b":"#present"},
    {"key":"Tutor.wwChildrenExpiry","label":"Ww children expiry","a":"#present","b":"#present"},
    {"key":"Tutor.wwChildrenRef","label":"Ww children ref","a":"555","b":"111"},
    {"key":"Tutor.wwChildrenStatus","label":"Ww children status","a":"Barred","b":"Application in progress"},
    {"key":"Tutor.payType", "label":"Pay type", "a":"employee", "b":"employee" },
    ]
    """
    And match $.infoLines contains only
    """
        [
        {"label":null,"a":"Tutor","b":"Tutor, student"},
        {"label":"Created","a":"#present","b":"#present"},
        {"label":"Modified","a":"#present","b":"#present"},
        {"label":"Last enrolled","a":"","b":"#present"},
        {"label":"Invoices","a":"#present","b":"#present"},
        {"label":"Enrolments","a":"","b":"#present"},
        {"label":"USI","a":"","b":"2222222222"}
        ]
    }
    """

    Scenario: (+) Get Merge data for company and company

        Given path ishPath
        And param contactA = 27
        And param contactB = 28
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "mergeLines":
            [
            {"key":"Contact.abn","label":"Abn","a":"777","b":null},
            {"key":"Contact.email","label":"Email","a":"merge8@gmail.com","b":"merge9@gmail.com"},
            {"key":"Contact.fax","label":"Fax","a":"444777124","b":"444777124"},
            {"key":"Contact.homePhone","label":"Home phone","a":"444777001","b":"444777002"},
            {"key":"Contact.lastName","label":"Last name","a":"mergeCompanyA","b":"mergeCompanyB"},
            {"key":"Contact.message","label":"Message","a":"some alert message8","b":"some alert message9"},
            {"key":"Contact.mobilePhone","label":"Mobile phone","a":"444777788","b":"444777789"},
            {"key":"Contact.postcode","label":"Postcode","a":"5008","b":"5009"},
            {"key":"Contact.state","label":"State","a":"SU","b":"SP"},
            {"key":"Contact.street","label":"Street","a":"some Address 8","b":"some Address 9"},
            {"key":"Contact.suburb","label":"Suburb","a":"some Suburb 8","b":"some Suburb 9"},
            {"key":"Contact.workPhone","label":"Work phone","a":"Co1","b":null},
            {"key":"Contact.taxOverride","label":"Tax Override","a":null,"b":"N"},
            {"key":"Contact.country","label":"Country","a":"Australia","b":"Australia"},
            {"key":"tags","label":"Tags","a":"#contacts1","b":""},
            {"key":"customField.cf2","label":"contact field2","a":"Co1","b":null},
            {"key":"customField.cf1","label":"contact field1","a":"Co1","b":"Co2"}
            ],
        "infoLines":
            [
            {"label":null,"a":"Company","b":"Company"},
            {"label":"Created","a":"#present","b":"#present"},
            {"label":"Modified","a":"#present","b":"#present"},
            {"label":"Last enrolled","a":"","b":""},
            {"label":"Invoices","a":"#present","b":"#present"},
            {"label":"Enrolments","a":"","b":""},
            {"label":"USI","a":"","b":""}
            ]
        }
        """


    Scenario: (-) Get Merge data by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath
        And param contactA = 24
        And param contactB = 21
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to merge contact. Please contact your administrator"



    Scenario: (-) Get Merge data for company and student

        Given path ishPath
        And param contactA = 27
        And param contactB = 21
        When method GET
        Then status 400
        And match $.errorMessage == "You can not merge company and person."



    Scenario: (-) Get Merge data for company and tutor

        Given path ishPath
        And param contactA = 27
        And param contactB = 24
        When method GET
        Then status 400
        And match $.errorMessage == "You can not merge company and person."



    Scenario: (-) Get Merge data for students with different USI

        Given path ishPath
        And param contactA = 21
        And param contactB = 23
        When method GET
        Then status 400
        And match $.errorMessage == "The selected students have different unique student identifiers (USI)"



    Scenario: (-) Get Merge data for students which enrolled in the same class

        Given path ishPath
        And param contactA = 19
        And param contactB = 20
        When method GET
        Then status 400
        And match $.errorMessage == "The selected students are currently enrolled in the same class (course4-1), please cancel or refund the appropriate enrolment prior to merging of those contacts."



    Scenario: (-) Get Merge data if one of both persons is not existing

        Given path ishPath
        And param contactA = 1
        And param contactB = 99999
        When method GET
        Then status 400
        And match $.errorMessage == "Contact B not found."
