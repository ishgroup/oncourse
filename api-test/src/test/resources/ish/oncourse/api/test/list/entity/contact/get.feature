@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/contact'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all Contacts by admin

        Given path ishPathList
        And param entity = 'Contact'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8"]



    Scenario: (+) Get Contact student by admin

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":2,
        "student":{"id":1,"countryOfBirth":null,"disabilityType":"Not stated","labourForceStatus":"Not stated","englishProficiency":"Not stated","highestSchoolLevel":"Not stated","indigenousStatus":"Not stated","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Not stated","specialNeeds":null,"yearSchoolCompleted":null,"studentNumber":1,"countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":null,"usi":null,"usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":null,"specialNeedsAssistance":false,"clientIndustryEmployment":"Not Stated","clientOccupationIdentifier":"Not Stated","waitingLists":[],"concessions":[]},
        "tutor":null,
        "abn":null,
        "birthDate":"2005-05-01",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":null,
        "isCompany":false,
        "gender":"Male",
        "message":null,
        "homePhone":null,
        "mobilePhone":"444777888",
        "workPhone":null,
        "postcode":"5000",
        "state":"SA",
        "street":"address str.",
        "suburb":"Adelaide",
        "tfn":null,
        "deliveryStatusEmail":"#number",
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"mGhSKaSbd9Ma7ESo",
        "honorific":null,
        "title":null,
        "email":"stud1@gmail.com",
        "firstName":"stud1",
        "lastName":"stud1",
        "middleName":null,
        "invoiceTerms":null,
        "taxId":null,
        "customFields":{},
        "documents":[],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[{"relatedEntityId":1,"type":"PaymentIn","description":"Cash payment payment in (office)","date":"2018-11-29","createdOn":"2018-11-28T14:01:15.122Z","referenceNumber":null,"status":"Success","owing":null,"amount":1800.00,"balance":-1800.00},{"relatedEntityId":1,"type":"Invoice","description":"Invoice (office)","date":"2018-11-29","createdOn":"2018-11-28T14:01:15.132Z","referenceNumber":"1","status":"Success","owing":0.00,"amount":1800.00,"balance":0.00},{"relatedEntityId":3,"type":"PaymentIn","description":"Cash payment payment in (office)","date":"2019-04-12","createdOn":"2019-04-12T12:32:52.325Z","referenceNumber":null,"status":"Success","owing":null,"amount":350.00,"balance":-350.00},{"relatedEntityId":3,"type":"Invoice","description":"Invoice (office)","date":"2019-04-12","createdOn":"2019-04-12T12:32:52.331Z","referenceNumber":"4","status":"Success","owing":0.00,"amount":350.00,"balance":0.00},{"relatedEntityId":4,"type":"PaymentIn","description":"Cash payment payment in (office)","date":"2019-04-12","createdOn":"2019-04-12T12:33:40.519Z","referenceNumber":null,"status":"Success","owing":null,"amount":450.00,"balance":-450.00},{"relatedEntityId":4,"type":"Invoice","description":"Invoice (office)","date":"2019-04-12","createdOn":"2019-04-12T12:33:40.521Z","referenceNumber":"5","status":"Success","owing":0.00,"amount":450.00,"balance":0.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "messages":[],
        "rules":[]
        }
        """



    Scenario: (+) Get Contact tutor by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1,
        "student":null,
        "tutor":{"id":1,"dateFinished":null,"dateStarted":null,"familyNameLegal":null,"givenNameLegal":null,"payrollRef":null,"resume":null,"wwChildrenCheckedOn":null,"wwChildrenExpiry":null,"wwChildrenRef":null,"wwChildrenStatus":"Not checked","currentClassesCount":4,"futureClasseCount":1,"selfPacedclassesCount":#number,"unscheduledClasseCount":0,"passedClasseCount":1,"cancelledClassesCount":#number},
        "abn":null,
        "birthDate":"1972-05-01",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":null,
        "isCompany":false,
        "gender":"Male",
        "message":null,
        "homePhone":null,
        "mobilePhone":"444555666",
        "workPhone":null,
        "postcode":"5000",
        "state":"SA",
        "street":"address str.",
        "suburb":"Adelaide",
        "tfn":null,
        "deliveryStatusEmail":"#number",
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"XykjWMd3zJ7KUX2n",
        "honorific":null,
        "title":null,
        "email":"tutor1@gmail.com",
        "firstName":"tutor1",
        "lastName":"tutor1",
        "middleName":null,
        "invoiceTerms":null,
        "taxId":null,
        "customFields":{},
        "documents":[],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "messages":[],
        "rules":[]
        }
        """



    Scenario: (+) Get Contact company by admin

        Given path ishPath + '/7'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":7,
        "student":null,
        "tutor":null,
        "abn":null,
        "birthDate":null,
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":"444777123",
        "isCompany":true,
        "gender":null,
        "message":"some alert message",
        "homePhone":"444777123",
        "mobilePhone":"444777123",
        "workPhone":null,
        "postcode":"5001",
        "state":"SA",
        "street":"some Address 1",
        "suburb":"some Suburb 1",
        "tfn":null,
        "deliveryStatusEmail":"#number",
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"vZYSUCSimRXchU2S",
        "honorific":null,
        "title":null,
        "email":"co1@gmail.com",
        "firstName":null,
        "lastName":"company #1",
        "middleName":null,
        "invoiceTerms":null,
        "taxId":null,
        "customFields":{},
        "documents":[],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "messages":[],
        "rules":[]
        }
        """



    Scenario: (-) Get not existing Contact

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get list of all Contacts by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'Contact'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8"]



    Scenario: (+) Get Contact by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":3,
        "student":{"id":2,"countryOfBirth":null,"disabilityType":"Not stated","labourForceStatus":"Not stated","englishProficiency":"Not stated","highestSchoolLevel":"Not stated","indigenousStatus":"Not stated","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Not stated","specialNeeds":null,"yearSchoolCompleted":null,"studentNumber":2,"countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":null,"usi":null,"usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":null,"specialNeedsAssistance":false,"clientIndustryEmployment":"Not Stated","clientOccupationIdentifier":"Not Stated","waitingLists":["Course2"],"concessions":[]},
        "tutor":null,
        "abn":null,
        "birthDate":"2000-05-01",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":null,
        "isCompany":false,
        "gender":"Female",
        "message":null,
        "homePhone":null,
        "mobilePhone":"444999666",
        "workPhone":null,
        "postcode":"5000",
        "state":"SA",
        "street":"address str.",
        "suburb":"Adelaide",
        "tfn":null,
        "deliveryStatusEmail":"#number",
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"WUeh3hZSa3FJGG4e",
        "honorific":null,
        "title":null,
        "email":"stud2@gmail.com",
        "firstName":"stud2",
        "lastName":"stud2",
        "middleName":null,
        "invoiceTerms":null,
        "taxId":null,
        "customFields":{},
        "documents":[],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[{"relatedEntityId":2,"type":"PaymentIn","description":"Cash payment payment in (office)","date":"2018-11-29","createdOn":"2018-11-28T14:03:35.668Z","referenceNumber":null,"status":"Success","owing":null,"amount":1800.00,"balance":-1800.00},{"relatedEntityId":2,"type":"Invoice","description":"Invoice (office)","date":"2018-11-29","createdOn":"2018-11-28T14:03:35.683Z","referenceNumber":"2","status":"Success","owing":0.00,"amount":1800.00,"balance":0.00},{"relatedEntityId":11,"type":"Invoice","description":"Invoice (office)","date":"2022-08-01","createdOn":"2019-07-02T15:35:09.443Z","referenceNumber":"12","status":"Success","owing":300.00,"amount":300.00,"balance":300.00}],"createdOn":null,"modifiedOn":null,"messages":[{"messageId":1002,"createdOn":"2019-08-07T10:49:22.857Z","sentOn":null,"subject":"Invoice 12 Payment Reminder","creatorKey":null,"status":"Queued","type":"Email"},{"messageId":1002,"createdOn":"2019-08-07T10:49:22.857Z","sentOn":null,"subject":"Invoice 12 Payment Reminder","creatorKey":null,"status":"Queued","type":"Sms"},{"messageId":1002,"createdOn":"2019-08-07T10:49:22.857Z","sentOn":null,"subject":"Invoice 12 Payment Reminder","creatorKey":null,"status":"Queued","type":"Post"}],
        "rules":[]
        }
        """

