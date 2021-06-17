@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/contact'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update contact Student by admin

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Not Well",
            "highestSchoolLevel":"Year 10",
            "indigenousStatus":"Neither",
            "isOverseasClient":false,
            "isStillAtSchool":false,
            "language":null,
            "priorEducationCode":"Certificate I",
            "specialNeeds":"BBB",
            "yearSchoolCompleted":null,
            "studentNumber":16,
            "countryOfResidency":null,
            "visaNumber":null,
            "visaType":null,
            "visaExpiryDate":null,
            "passportNumber":null,
            "medicalInsurance":null,
            "uniqueLearnerIdentifier":"bbb",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":null,
            "feeHelpEligible":false,
            "citizenship":"No information",
            "townOfBirth":"b",
            "specialNeedsAssistance":false,
            "clientIndustryEmployment":"Retail Trade (G)",
            "clientOccupationIdentifier":"Not Stated",
            "concessions":[]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John101",
        "lastName":"Smith101",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":200}],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith101"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John101UPD",
        "lastName":"Smith101UPD",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "rules":[{"id":null,"description":"test","startDate":"2020-02-11","endDate":"2020-02-11","repeatEnd":"onDate","repeat":"hour","repeatEndAfter":0,"repeatOn":"2020-02-11","startDateTime":null,"endDateTime":null}]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":{"id":"#number","countryOfBirth":{"id":3,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Poland","saccCode":null},"disabilityType":"Learning","labourForceStatus":"Not stated","englishProficiency":"Well","highestSchoolLevel":"Year 11","indigenousStatus":"Aboriginal","isOverseasClient":true,"isStillAtSchool":true,"language":null,"priorEducationCode":"Certificate II","specialNeeds":"BBBUPD","yearSchoolCompleted":null,"studentNumber":"#number","countryOfResidency":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},"visaNumber":"UPD","visaType":"UPD","visaExpiryDate":"2035-11-12","passportNumber":"3645766","medicalInsurance":"UPD","uniqueLearnerIdentifier":"UPD","usi":"2222222222","usiStatus":"Not supplied","chessn":"UPD","feeHelpEligible":true,"citizenship":"New Zealand citizen","townOfBirth":"bUPD","specialNeedsAssistance":true,"clientIndustryEmployment":"Wholesale Trade (F)","clientOccupationIdentifier":"Manager (1)","waitingLists":[],"concessions":[{"id":"#ignore","number":"50","type":{"id":2,"name":"Student","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"},"expiresOn":"2030-01-01","created":null,"modified":null}]},
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"#ignore",
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John101UPD",
        "lastName":"Smith101UPD",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":"#ignore",
        "tags":[{"id":233,"name":"contacts1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "createdOn":null,
        "modifiedOn":null,
        "messages":[],
        "rules":[{"id":"#ignore","description":"test","startDate":"2020-02-11","endDate":"2020-02-11","startDateTime":null,"endDateTime":null,"repeat":"hour","repeatEnd":"onDate","repeatEndAfter":null,"repeatOn":"2020-02-11","created":"#ignore","modified":"#ignore"}]
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        

    Scenario: (+) Update contact Company by admin

#       <----->  Add a new entity to update and define its id:
        * def newContact =
    """
    {
    "id":0,
    "student":null,
    "tutor":null,
    "lastName":"testCompany1",
    "middleName":null,
    "birthDate":null,
    "street":"123",
    "suburb":"Sububurb",
    "allowSms":false,
    "allowPost":false,
    "allowEmail":false,
    "isCompany":true,
    "deliveryStatusPost":0,
    "deliveryStatusSms":0,
    "deliveryStatusEmail":0,
    "customFields":{},
    "state":"NSW",
    "country":{"id":2,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"United States of America","saccCode":null},
    "email":"abs@tut.by",
    "homePhone":"0431048487",
    "fax":"0431048488",
    "postcode":"2010",
    "mobilePhone":"0431048488",
    "message":"message",
    "workPhone":"0431048488",
    "abn":"12345678901",
    "relations":[],
    "invoiceTerms":"30",
    "taxId":2,
    "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
    }
    """
        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testCompany1"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":null,
        "tutor":null,
        "abn":"12345678000",
        "birthDate":null,
        "country":{"id":3,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Poland","saccCode":null},
        "fax":"0431048000",
        "isCompany":true,
        "gender":"Female",
        "message":"messageUPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2015",
        "state":"UPD",
        "street":"123UPD",
        "suburb":"SububurbUPD",
        "tfn":null,
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":false,
        "allowSms":false,
        "allowEmail":false,
        "honorific":null,
        "title":null,
        "email":"absUPD@tut.by",
        "lastName":"testCompany1",
        "middleName":null,
        "invoiceTerms":"50",
        "taxId":1,
        "customFields":{"cf2":"","cf1":"qwertyUPD"},
        "documents":[{"id":200}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "messages":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":null,
        "tutor":null,
        "abn":"12345678000",
        "birthDate":null,
        "country":{"id":3,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Poland","saccCode":null},
        "fax":"0431048000",
        "isCompany":true,
        "gender":"Female",
        "message":"messageUPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2015",
        "state":"UPD",
        "street":"123UPD",
        "suburb":"SububurbUPD",
        "tfn":null,
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":false,
        "allowSms":false,
        "allowEmail":false,
        "uniqueCode":"#present",
        "honorific":null,
        "title":null,
        "email":"absUPD@tut.by",
        "firstName":null,
        "lastName":"testCompany1",
        "middleName":null,
        "invoiceTerms":50,
        "taxId":1,
        "customFields":{"cf2":null,"cf1":"qwertyUPD"},
        "documents":"#ignore",
        "tags":[{"id":233,"name":"contacts1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
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

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Contact firstName fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Not Well",
            "highestSchoolLevel":"Year 10",
            "indigenousStatus":"Neither",
            "isOverseasClient":false,
            "isStillAtSchool":false,
            "language":null,
            "priorEducationCode":"Certificate I",
            "specialNeeds":"BBB",
            "yearSchoolCompleted":null,
            "studentNumber":16,
            "countryOfResidency":null,
            "visaNumber":null,
            "visaType":null,
            "visaExpiryDate":null,
            "passportNumber":null,
            "medicalInsurance":null,
            "uniqueLearnerIdentifier":"bbb",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":null,
            "feeHelpEligible":false,
            "citizenship":"No information",
            "townOfBirth":"b",
            "specialNeedsAssistance":false,
            "clientIndustryEmployment":"Retail Trade (G)",
            "clientOccupationIdentifier":"Not Stated",
            "concessions":[]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John105",
        "lastName":"Smith105",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":200}],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith105"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"",
        "lastName":"Smith105",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "You need to enter a contact first name."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Contact lastName fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Not Well",
            "highestSchoolLevel":"Year 10",
            "indigenousStatus":"Neither",
            "isOverseasClient":false,
            "isStillAtSchool":false,
            "language":null,
            "priorEducationCode":"Certificate I",
            "specialNeeds":"BBB",
            "yearSchoolCompleted":null,
            "studentNumber":16,
            "countryOfResidency":null,
            "visaNumber":null,
            "visaType":null,
            "visaExpiryDate":null,
            "passportNumber":null,
            "medicalInsurance":null,
            "uniqueLearnerIdentifier":"bbb",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":null,
            "feeHelpEligible":false,
            "citizenship":"No information",
            "townOfBirth":"b",
            "specialNeedsAssistance":false,
            "clientIndustryEmployment":"Retail Trade (G)",
            "clientOccupationIdentifier":"Not Stated",
            "concessions":[]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John106",
        "lastName":"Smith106",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":200}],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith106"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John106",
        "lastName":"",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "You need to enter a contact last name."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Contact mandatory custom field to empty

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Not Well",
            "highestSchoolLevel":"Year 10",
            "indigenousStatus":"Neither",
            "isOverseasClient":false,
            "isStillAtSchool":false,
            "language":null,
            "priorEducationCode":"Certificate I",
            "specialNeeds":"BBB",
            "yearSchoolCompleted":null,
            "studentNumber":16,
            "countryOfResidency":null,
            "visaNumber":null,
            "visaType":null,
            "visaExpiryDate":null,
            "passportNumber":null,
            "medicalInsurance":null,
            "uniqueLearnerIdentifier":"bbb",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":null,
            "feeHelpEligible":false,
            "citizenship":"No information",
            "townOfBirth":"b",
            "specialNeedsAssistance":false,
            "clientIndustryEmployment":"Retail Trade (G)",
            "clientOccupationIdentifier":"Not Stated",
            "concessions":[]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John107",
        "lastName":"Smith107",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":200}],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith107"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John107",
        "lastName":"Smith107",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Custom field type 'cf1' is mandatory for contact."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update contact Student to Company

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Not Well",
            "highestSchoolLevel":"Year 10",
            "indigenousStatus":"Neither",
            "isOverseasClient":false,
            "isStillAtSchool":false,
            "language":null,
            "priorEducationCode":"Certificate I",
            "specialNeeds":"BBB",
            "yearSchoolCompleted":null,
            "studentNumber":16,
            "countryOfResidency":null,
            "visaNumber":null,
            "visaType":null,
            "visaExpiryDate":null,
            "passportNumber":null,
            "medicalInsurance":null,
            "uniqueLearnerIdentifier":"bbb",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":null,
            "feeHelpEligible":false,
            "citizenship":"No information",
            "townOfBirth":"b",
            "specialNeedsAssistance":false,
            "clientIndustryEmployment":"Retail Trade (G)",
            "clientOccupationIdentifier":"Not Stated",
            "concessions":[]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John108",
        "lastName":"Smith108",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":200}],
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith108"])].id
        * print "id = " + id
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":true,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John108",
        "lastName":"Smith108",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "A student cannot be a company."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Contact

        * def contactToUpdate =
        """
        {
        "id":99999,
        "student":
            {
            "countryOfBirth":{"id":3},
            "disabilityType":"Learning",
            "labourForceStatus":"Not stated",
            "englishProficiency":"Well",
            "highestSchoolLevel":"Year 11",
            "indigenousStatus":"Aboriginal",
            "isOverseasClient":true,
            "isStillAtSchool":true,
            "language":null,
            "priorEducationCode":"Certificate II",
            "specialNeeds":"BBBUPD",
            "yearSchoolCompleted":null,
            "countryOfResidency":{"id":1},
            "visaNumber":"UPD",
            "visaType":"UPD",
            "visaExpiryDate":"2035-11-12",
            "passportNumber":"3645766",
            "medicalInsurance":"UPD",
            "uniqueLearnerIdentifier":"UPD",
            "usi":"2222222222",
            "usiStatus":"Not supplied",
            "chessn":"UPD",
            "feeHelpEligible":true,
            "citizenship":"New Zealand citizen",
            "townOfBirth":"bUPD",
            "specialNeedsAssistance":true,
            "clientIndustryEmployment":"Wholesale Trade (F)",
            "clientOccupationIdentifier":"Manager (1)",
            "concessions":[{"number":"50","type":{"id":2},"expiresOn":"2030-01-01"}]
            },
        "tutor":null,
        "abn":"12345678000",
        "birthDate":"1999-06-02",
        "country":{"id":1},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2018",
        "state":"VIC",
        "street":"32-5 Ulianovskaya Str",
        "suburb":"Sububurb",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"UPD Title",
        "email":"testContactUPD@gmail.com",
        "firstName":"John101UPD",
        "lastName":"Smith101UPD",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":22,
        "taxId":2,
        "customFields":{"cf1":"qwertyUPD","cf2":"asdfghUPD"},
        "documents":[{"id":201}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[]
        }
        """

        Given path ishPath + '/99999'
        And request contactToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Update Contact by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal4",
              "givenNameLegal":"givenNameLegal4",
              "payrollRef":"some payrollRef",
              "resume":"some resume",
              "wwChildrenCheckedOn":"2018-03-03",
              "wwChildrenExpiry":"2024-04-04",
              "wwChildrenRef":"some wwChildrenRef",
              "wwChildrenStatus":"Interim barred"
            },
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"0431048488",
        "workPhone":"0431048489",
        "postcode":"2011",
        "state":"NSW",
        "street":"72-86 William St",
        "suburb":"Woolloomooloo",
        "tfn":"172665394",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John104",
        "lastName":"Smith104",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
        "tags":[],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "rules":[{"id":null,"description":"test","startDate":null,"endDate":null,"repeatEnd":"never","repeat":"week","repeatEndAfter":0,"startDateTime":"2020-02-01T00:00:00.000Z","endDateTime":"2020-02-29T00:00:00.000Z"}]
        }
        """

        Given path ishPath
        And request newContact
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Contact'
        And param columns = 'lastName'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Smith104"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def tutorId = get[0] response.tutor.id
        * print "tutorId = " + tutorId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def contactToUpdate =
        """
        {
        "id":"#(id)",
        "student":null,
        "tutor":{"id":"#(tutorId)","dateFinished":"2025-01-05","dateStarted":"2015-02-05","familyNameLegal":"familyNameLegal4UPD","givenNameLegal":"givenNameLegal4UPD","payrollRef":"some payrollRefUPD","resume":"some resume UPD","wwChildrenCheckedOn":"2015-03-05","wwChildrenExpiry":"2025-04-05","wwChildrenRef":"some wwChildrenRefUPD","wwChildrenStatus":"Cleared"},
        "abn":"12345678000",
        "birthDate":"1995-07-25",
        "country":{"id":3,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Poland","saccCode":null},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2015",
        "state":"UPD",
        "street":"72-86 William StUPD",
        "suburb":"WoolloomoolooUPD",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr UPD",
        "title":"Vice President of Marketing UPD",
        "email":"testContactUPD@gmail.com",
        "firstName":"John104UPD",
        "lastName":"Smith104UPD",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":"50",
        "taxId":2,
        "customFields":{"cf2":"","cf1":"qwertyUPD"},
        "documents":[{"id":200}],
        "tags":[{"id":233}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "createdOn":null,
        "modifiedOn":null,
        "messages":[],
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request contactToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":null,
        "tutor":{"defaultPayType":null,"id":"#(~~tutorId)","dateFinished":"2025-01-05","dateStarted":"2015-02-05","familyNameLegal":"familyNameLegal4UPD","givenNameLegal":"givenNameLegal4UPD","payrollRef":"some payrollRefUPD","resume":"some resume UPD","wwChildrenCheckedOn":"2015-03-05","wwChildrenExpiry":"2025-04-05","wwChildrenRef":"some wwChildrenRefUPD","wwChildrenStatus":"Cleared","currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0},
        "abn":"12345678000",
        "birthDate":"1995-07-25",
        "country":{"id":3,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Poland","saccCode":null},
        "fax":"0431048000",
        "isCompany":false,
        "gender":"Female",
        "message":"Student has hearing disability UPD",
        "homePhone":"0431048000",
        "mobilePhone":"0431048000",
        "workPhone":"0431048000",
        "postcode":"2015",
        "state":"UPD",
        "street":"72-86 William StUPD",
        "suburb":"WoolloomoolooUPD",
        "tfn":"172665000",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "uniqueCode":"#ignore",
        "honorific":"Dr UPD",
        "title":"Vice President of Marketing UPD",
        "email":"testContactUPD@gmail.com",
        "firstName":"John104UPD",
        "lastName":"Smith104UPD",
        "middleName":"FitzgeraldUPD",
        "invoiceTerms":50,
        "taxId":2,
        "customFields":{"cf2":null,"cf1":"qwertyUPD"},
        "documents":"#ignore",
        "tags":[{"id":233,"name":"contacts1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "memberships":[],
        "profilePicture":null,
        "relations":[],
        "financialData":[],
        "createdOn":null,
        "modifiedOn":null,
        "messages":[],
        "rules":[]
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Contact by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/1'
        And request {"id":0,"student":{"labourForceStatus":"Not stated","englishProficiency":"Not stated","highestSchoolLevel":"Not stated","citizenship":"No information","feeHelpEligible":false,"indigenousStatus":"Not stated","priorEducationCode":"Not stated","isOverseasClient":false,"disabilityType":"Not stated","clientIndustryEmployment":"Not Stated","clientOccupationIdentifier":"Not Stated"},"tutor":null,"firstName":"fn1","lastName":"ln1","middleName":null,"birthDate":null,"street":null,"suburb":null,"allowSms":false,"allowPost":false,"allowEmail":false,"isCompany":false,"deliveryStatusPost":0,"deliveryStatusSms":0,"deliveryStatusEmail":0,"customFields":{"cf1":"123"}}
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit contact. Please contact your administrator"

