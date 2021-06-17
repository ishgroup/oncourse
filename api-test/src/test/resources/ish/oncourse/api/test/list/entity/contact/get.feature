@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/contact'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



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
        "financialData":[
            {"date":"2018-11-29","amount":1800.0,"balance":1800.0,"referenceNumber":"1","relatedEntityId":1,"description":"Invoice (office)","owing":0.0,"type":"Invoice","createdOn":"#ignore","status":"Success"},
            {"date":"2018-11-29","amount":1800.0,"balance":0.0,"referenceNumber":null,"relatedEntityId":1,"description":"Cash payment payment in (office)","owing":null,"type":"PaymentIn","createdOn":"#ignore","status":"Success"},
            {"date":"2019-04-12","amount":350.0,"balance":350.0,"referenceNumber":"4","relatedEntityId":3,"description":"Invoice (office)","owing":0.0,"type":"Invoice","createdOn":"#ignore","status":"Success"},
            {"date":"2019-04-12","amount":350.0,"balance":0.0,"referenceNumber":null,"relatedEntityId":3,"description":"Cash payment payment in (office)","owing":null,"type":"PaymentIn","createdOn":"#ignore","status":"Success"},
            {"date":"2019-04-12","amount":450.0,"balance":450.0,"referenceNumber":"5","relatedEntityId":4,"description":"Invoice (office)","owing":0.0,"type":"Invoice","createdOn":"#ignore","status":"Success"},
            {"date":"2019-04-12","amount":450.0,"balance":0.0,"referenceNumber":null,"relatedEntityId":4,"description":"Cash payment payment in (office)","owing":null,"type":"PaymentIn","createdOn":"#ignore","status":"Success"}
            ],
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
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Contact'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8"]



    Scenario: (+) Get Contact by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
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
        "financialData":[
            {"date":"2018-11-29","amount":1800.0,"balance":1800.0,"referenceNumber":"2","relatedEntityId":2,"description":"Invoice (office)","owing":0.0,"type":"Invoice","createdOn":"#ignore","status":"Success"},
            {"date":"2018-11-29","amount":1800.0,"balance":0.0,"referenceNumber":null,"relatedEntityId":2,"description":"Cash payment payment in (office)","owing":null,"type":"PaymentIn","createdOn":"#ignore","status":"Success"},
            {"date":"2022-08-01","amount":300.0,"balance":300.0,"referenceNumber":"12","relatedEntityId":11,"description":"Invoice (office)","owing":300.0,"type":"Invoice","createdOn":"#ignore","status":"Success"}
        ],
        "rules":[]
        }
        """

