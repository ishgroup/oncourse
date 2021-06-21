@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/contact'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'



  Scenario: (+) Create Contact (student) by admin

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
            "concessions":[{"number":"50","type":{"id":"2"},"expiresOn":"2030-01-01"}]
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
        "firstName":"John1",
        "lastName":"Smith1",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":201}],
        "tags":[{"id":234}],
        "memberships":[],
        "profilePicture":null,
        "relations":[{"relationId":-1,"contactToId":null,"contactToName":null,"contactFromName":"stud1, stud1","contactFromId":2}],
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

    * def id = get[0] response.rows[?(@.values == ["Smith1"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":
            {
                "id":"#number",
                "countryOfBirth":{"id":2,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"United States of America","saccCode":null},
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
                "studentNumber":"#number",
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
                "waitingLists":[],
                "concessions":[{"id":"#ignore","number":"50","type":{"id":2,"name":"Student","requireExpary":false,"requireNumber":false,"allowOnWeb":true,"created":"#ignore","modified":"#ignore"},"expiresOn":"2030-01-01","created":"#ignore","modified":"#ignore"}]
            },
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
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
        "uniqueCode":"#present",
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John1",
        "lastName":"Smith1",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":"#ignore",
        "tags":[{"id":234,"name":"contacts2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "memberships":[],
        "profilePicture":null,
        "relations":[{"id":"#number","contactFromId":2,"contactFromName":"stud1","contactToId":null,"contactToName":null,"relationId":-1}],
        "financialData":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "messages":[],
        "rules":[{"id":"#ignore","description":"test","startDate":null,"endDate":null,"startDateTime":"2020-02-01T00:00:00.000Z","endDateTime":"2020-02-29T00:00:00.000Z","repeat":"week","repeatEnd":"never","repeatEndAfter":null,"repeatOn":null,"created":"#ignore","modified":"#ignore"}]
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (+) Create Contact (tutor) by admin

    * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
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
        "firstName":"John2",
        "lastName":"Smith2",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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

    * def id = get[0] response.rows[?(@.values == ["Smith2"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":null,
        "tutor":{"defaultPayType":null, "id":"#number","dateFinished":"2025-01-01","dateStarted":"2018-02-02","familyNameLegal":"familyNameLegal","givenNameLegal":"givenNameLegal","payrollRef":"some payrollRef","resume":"some resume","wwChildrenCheckedOn":"2018-03-03","wwChildrenExpiry":"2024-04-04","wwChildrenRef":"some wwChildrenRef","wwChildrenStatus":"Interim barred","currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0},
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
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
        "uniqueCode":"#present",
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John2",
        "lastName":"Smith2",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
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

#       <--->  Scenario have been finished. Now remove created object from DB:
    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (+) Create Contact (student and tutor) by admin

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
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
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
        "firstName":"John12",
        "lastName":"Smith12",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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

    * def id = get[0] response.rows[?(@.values == ["Smith12"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":{"id":"#number","countryOfBirth":{"id":2,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"United States of America","saccCode":null},"disabilityType":"Mental illness","labourForceStatus":"Not stated","englishProficiency":"Not Well","highestSchoolLevel":"Year 10","indigenousStatus":"Neither","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Certificate I","specialNeeds":"BBB","yearSchoolCompleted":null,"studentNumber":"#number","countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":"bbb","usi":"2222222222","usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":"b","specialNeedsAssistance":false,"clientIndustryEmployment":"Retail Trade (G)","clientOccupationIdentifier":"Not Stated","waitingLists":[],"concessions":[]},
        "tutor":{"defaultPayType":null, "id":"#number","dateFinished":"2025-01-01","dateStarted":"2018-02-02","familyNameLegal":"familyNameLegal","givenNameLegal":"givenNameLegal","payrollRef":"some payrollRef","resume":"some resume","wwChildrenCheckedOn":"2018-03-03","wwChildrenExpiry":"2024-04-04","wwChildrenRef":"some wwChildrenRef","wwChildrenStatus":"Interim barred","currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0},
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
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
        "uniqueCode":"#present",
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John12",
        "lastName":"Smith12",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf2":"asdfgh","cf1":"qwerty"},
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

#       <--->  Scenario have been finished. Now remove created object from DB:
    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (+) Create Contact (tutor and company) by admin

    * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
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
        "isCompany":true,
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
        "firstName":"John13",
        "lastName":"Smith13",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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

    * def id = get[0] response.rows[?(@.values == ["Smith13"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":null,
        "tutor":{"defaultPayType":null, "id":"#number","dateFinished":"2025-01-01","dateStarted":"2018-02-02","familyNameLegal":"familyNameLegal","givenNameLegal":"givenNameLegal","payrollRef":"some payrollRef","resume":"some resume","wwChildrenCheckedOn":"2018-03-03","wwChildrenExpiry":"2024-04-04","wwChildrenRef":"some wwChildrenRef","wwChildrenStatus":"Interim barred","currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0},
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":"0431048488",
        "isCompany":true,
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
        "uniqueCode":"#present",
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John13",
        "lastName":"Smith13",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf2":"asdfgh","cf1":"qwerty"},
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

#       <--->  Scenario have been finished. Now remove created object from DB:
    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (+) Create Contact (company) by admin

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":true,
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
        "firstName":"John3",
        "lastName":"Smith3",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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

    * def id = get[0] response.rows[?(@.values == ["Smith3"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
        "id":"#(~~id)",
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "fax":"0431048488",
        "isCompany":true,
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
        "uniqueCode":"#present",
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"John3",
        "lastName":"Smith3",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf2":"asdfgh","cf1":"qwerty"},
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

#       <--->  Scenario have been finished. Now remove created object from DB:
    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (-) Create new Contact with empty firstName

    * def newContact =
        """
        {
        "student":{"countryOfBirth":{"id":2},"disabilityType":"Mental illness","labourForceStatus":"Default popup option","englishProficiency":"Not Well","highestSchoolLevel":"Year 10","indigenousStatus":"Neither","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Certificate I","specialNeeds":"BBB","yearSchoolCompleted":null,"studentNumber":16,"countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":"bbb","usi":"2222222222","usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":"b","specialNeedsAssistance":false,"clientIndustryEmployment":"Retail Trade (G)","clientOccupationIdentifier":"Not Stated","concessions":[]},
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
        "firstName":"",
        "lastName":"Smith3",
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
        }
        """

    Given path ishPath
    And request newContact
    When method POST
    Then status 400
    And match $.errorMessage == "You need to enter a contact first name."



  Scenario: (-) Create new Contact with empty lastName

    * def newContact =
        """
        {
        "student":{"countryOfBirth":{"id":2},"disabilityType":"Mental illness","labourForceStatus":"Default popup option","englishProficiency":"Not Well","highestSchoolLevel":"Year 10","indigenousStatus":"Neither","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Certificate I","specialNeeds":"BBB","yearSchoolCompleted":null,"studentNumber":16,"countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":"bbb","usi":"2222222222","usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":"b","specialNeedsAssistance":false,"clientIndustryEmployment":"Retail Trade (G)","clientOccupationIdentifier":"Not Stated","concessions":[]},
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
        "firstName":"John4",
        "lastName":"",
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
        }
        """

    Given path ishPath
    And request newContact
    When method POST
    Then status 400
    And match $.errorMessage == "You need to enter a contact last name."



  Scenario: (-) Create new Contact (student and company) by admin

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
        "isCompany":true,
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
        "firstName":"John6",
        "lastName":"Smith6",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "A student cannot be a company."



  Scenario: (-) Create new Contact with empty mandatory custom field

    * def newContact =
        """
        {
        "student":{"countryOfBirth":{"id":2},"disabilityType":"Mental illness","labourForceStatus":"Default popup option","englishProficiency":"Not Well","highestSchoolLevel":"Year 10","indigenousStatus":"Neither","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Certificate I","specialNeeds":"BBB","yearSchoolCompleted":null,"studentNumber":16,"countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":"bbb","usi":"2222222222","usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":"b","specialNeedsAssistance":false,"clientIndustryEmployment":"Retail Trade (G)","clientOccupationIdentifier":"Not Stated","concessions":[]},
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
        "firstName":"John5",
        "lastName":"Smith5",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"","cf2":"qwerty"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Custom field type 'cf1' is mandatory for contact."



  Scenario: (-) Create new Contact with 'firstName' >128 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "firstName":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
        "lastName":"Smith21",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "First name length should be less than 128 characters."



  Scenario: (-) Create new Contact with 'lastName' >128 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "firstName":"John21",
        "lastName":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Last name length should be less than 128 characters."



  Scenario: (-) Create Contact with 'middleName' >128 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Middle name length should be less than 128 characters."



  Scenario: (-) Create Contact with 'email' >128 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "email":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120@gmail.com",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Email length should be less than 128 characters."



  Scenario: (-) Create Contact with incorrect 'email' address

    * def newContact =
        """
        {
        "student":null,
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
        "email":"abc123",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Please enter an email address in the correct format."



  Scenario: (-) Create Contact with 'abn' >50 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"123456789012345678901234567890123456789012345678901",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Business Number (ABN) should be less than 50 characters."



  Scenario: (-) Create Contact with not numeric 'abn' symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"123Abc",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Business Number (ABN) should be numeric."



  Scenario: (-) Create Contact with 'homePhone' >20 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"043104848904310484890",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Home phone should be less than 20 characters."



  Scenario: (-) Create Contact with 'mobilePhone' >20 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"Student has hearing disability",
        "homePhone":"0431048487",
        "mobilePhone":"043104848904310484890",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Mobile phone should be less than 20 characters."



  Scenario: (-) Create Contact with 'workPhone' >20 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "workPhone":"043104848904310484890",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Work phone should be less than 20 characters."



  Scenario: (-) Create Contact with 'fax' >20 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"043104848904310484890",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Fax should be less than 20 characters."



  Scenario: (-) Create Contact with 'honorific' >256 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "honorific":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A2",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Honorific should be less than 256 characters."



  Scenario: (-) Create Contact with 'message' >500 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":null,
        "abn":"12345678901",
        "birthDate":"1991-07-20",
        "country":{"id":1},
        "fax":"0431048488",
        "isCompany":false,
        "gender":"Male",
        "message":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A1",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Message should be less than 500 characters."



  Scenario: (-) Create Contact with postcode >20 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "postcode":"012345678901234567890",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Postcode should be less than 20 characters."



  Scenario: (-) Create Contact with 'state' >20 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "state":"A3A5A7A9A12A15A18A21A",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "State should be less than 20 characters."



  Scenario: (-) Create Contact with 'street' >200 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "street":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Street should be less than 200 characters."



  Scenario: (-) Create Contact with 'suburb' >128 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Suburb should be less than 128 characters."



  Scenario: (-) Create Contact with 'tfn' >64 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "tfn":"12345678901234567890123456789012345678901234567890123456789012345",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Tax File Number should be less than 64 characters."



  Scenario: (-) Create Contact with 'tfn' containing not numeric symbols

    * def newContact =
        """
        {
        "student":null,
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
        "tfn":"A3A5A7A9A12A15A18A21A24A27A30",
        "deliveryStatusEmail":0,
        "deliveryStatusSms":0,
        "deliveryStatusPost":0,
        "allowPost":true,
        "allowSms":true,
        "allowEmail":true,
        "honorific":"Dr",
        "title":"Vice President of Marketing",
        "email":"testContact@gmail.com",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Tax File Number (TFN) should be numeric."



  Scenario: (-) Create Contact with 'title' >32 symbols

    * def newContact =
        """
        {
        "student":null,
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
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A",
        "email":"testContact@gmail.com",
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Title should be less than 32 characters."



  Scenario: (-) Create Contact (student) with 'chessn' >10 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "chessn":"12345678901",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Commonwealth Higher Education Student Support Number should be less than 10 characters."



  Scenario: (-) Create Contact (student) with 'medicalInsurance' >500 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "medicalInsurance":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A1",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Medical Insurance length should be less than 500 characters."



  Scenario: (-) Create Contact (student) with 'passportNumber' >100 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "passportNumber":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Passport Number length should be less than 100 characters."



  Scenario: (-) Create Contact (student) with 'townOfBirth' >256 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "townOfBirth":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A2",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Town of Birth length should be less than 256 characters."



  Scenario: (-) Create Contact (student) with 'uniqueLearnerIdentifier' >10 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "uniqueLearnerIdentifier":"A3A5A7A9A12",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Unique Learner Identifier length should be less than 10 characters."



  Scenario: (-) Create Contact (student) with 'usi' >64 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "usi":"12345678901234567890123456789012345678901234567890123456789012345",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "USI length should be less than 64 characters."



  Scenario: (-) Create Contact (student) with 'visaNumber' >100 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "visaNumber":"12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Visa Number length should be less than 100 characters."



  Scenario: (-) Create Contact (student) with 'visaType' >500 symbols

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "visaType":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A1",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Visa Type length should be less than 500 characters."



  Scenario: (-) Create Contact (tutor) with 'payrollRef' >32 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
              "payrollRef":"A3A5A7A9A12A15A18A21A24A27A30A33A",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Payroll Reference length should be less than 32 characters."



  Scenario: (-) Create Contact (tutor) with 'resume' >32000 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
              "payrollRef":"some payrollRef",
              "resume":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A515A519A523A527A531A535A539A543A547A551A555A559A563A567A571A575A579A583A587A591A595A599A603A607A611A615A619A623A627A631A635A639A643A647A651A655A659A663A667A671A675A679A683A687A691A695A699A703A707A711A715A719A723A727A731A735A739A743A747A751A755A759A763A767A771A775A779A783A787A791A795A799A803A807A811A815A819A823A827A831A835A839A843A847A851A855A859A863A867A871A875A879A883A887A891A895A899A903A907A911A915A919A923A927A931A935A939A943A947A951A955A959A963A967A971A975A979A983A987A991A995A999A1004A1009A1014A1019A1024A1029A1034A1039A1044A1049A1054A1059A1064A1069A1074A1079A1084A1089A1094A1099A1104A1109A1114A1119A1124A1129A1134A1139A1144A1149A1154A1159A1164A1169A1174A1179A1184A1189A1194A1199A1204A1209A1214A1219A1224A1229A1234A1239A1244A1249A1254A1259A1264A1269A1274A1279A1284A1289A1294A1299A1304A1309A1314A1319A1324A1329A1334A1339A1344A1349A1354A1359A1364A1369A1374A1379A1384A1389A1394A1399A1404A1409A1414A1419A1424A1429A1434A1439A1444A1449A1454A1459A1464A1469A1474A1479A1484A1489A1494A1499A1504A1509A1514A1519A1524A1529A1534A1539A1544A1549A1554A1559A1564A1569A1574A1579A1584A1589A1594A1599A1604A1609A1614A1619A1624A1629A1634A1639A1644A1649A1654A1659A1664A1669A1674A1679A1684A1689A1694A1699A1704A1709A1714A1719A1724A1729A1734A1739A1744A1749A1754A1759A1764A1769A1774A1779A1784A1789A1794A1799A1804A1809A1814A1819A1824A1829A1834A1839A1844A1849A1854A1859A1864A1869A1874A1879A1884A1889A1894A1899A1904A1909A1914A1919A1924A1929A1934A1939A1944A1949A1954A1959A1964A1969A1974A1979A1984A1989A1994A1999A2004A2009A2014A2019A2024A2029A2034A2039A2044A2049A2054A2059A2064A2069A2074A2079A2084A2089A2094A2099A2104A2109A2114A2119A2124A2129A2134A2139A2144A2149A2154A2159A2164A2169A2174A2179A2184A2189A2194A2199A2204A2209A2214A2219A2224A2229A2234A2239A2244A2249A2254A2259A2264A2269A2274A2279A2284A2289A2294A2299A2304A2309A2314A2319A2324A2329A2334A2339A2344A2349A2354A2359A2364A2369A2374A2379A2384A2389A2394A2399A2404A2409A2414A2419A2424A2429A2434A2439A2444A2449A2454A2459A2464A2469A2474A2479A2484A2489A2494A2499A2504A2509A2514A2519A2524A2529A2534A2539A2544A2549A2554A2559A2564A2569A2574A2579A2584A2589A2594A2599A2604A2609A2614A2619A2624A2629A2634A2639A2644A2649A2654A2659A2664A2669A2674A2679A2684A2689A2694A2699A2704A2709A2714A2719A2724A2729A2734A2739A2744A2749A2754A2759A2764A2769A2774A2779A2784A2789A2794A2799A2804A2809A2814A2819A2824A2829A2834A2839A2844A2849A2854A2859A2864A2869A2874A2879A2884A2889A2894A2899A2904A2909A2914A2919A2924A2929A2934A2939A2944A2949A2954A2959A2964A2969A2974A2979A2984A2989A2994A2999A3004A3009A3014A3019A3024A3029A3034A3039A3044A3049A3054A3059A3064A3069A3074A3079A3084A3089A3094A3099A3104A3109A3114A3119A3124A3129A3134A3139A3144A3149A3154A3159A3164A3169A3174A3179A3184A3189A3194A3199A3204A3209A3214A3219A3224A3229A3234A3239A3244A3249A3254A3259A3264A3269A3274A3279A3284A3289A3294A3299A3304A3309A3314A3319A3324A3329A3334A3339A3344A3349A3354A3359A3364A3369A3374A3379A3384A3389A3394A3399A3404A3409A3414A3419A3424A3429A3434A3439A3444A3449A3454A3459A3464A3469A3474A3479A3484A3489A3494A3499A3504A3509A3514A3519A3524A3529A3534A3539A3544A3549A3554A3559A3564A3569A3574A3579A3584A3589A3594A3599A3604A3609A3614A3619A3624A3629A3634A3639A3644A3649A3654A3659A3664A3669A3674A3679A3684A3689A3694A3699A3704A3709A3714A3719A3724A3729A3734A3739A3744A3749A3754A3759A3764A3769A3774A3779A3784A3789A3794A3799A3804A3809A3814A3819A3824A3829A3834A3839A3844A3849A3854A3859A3864A3869A3874A3879A3884A3889A3894A3899A3904A3909A3914A3919A3924A3929A3934A3939A3944A3949A3954A3959A3964A3969A3974A3979A3984A3989A3994A3999A4004A4009A4014A4019A4024A4029A4034A4039A4044A4049A4054A4059A4064A4069A4074A4079A4084A4089A4094A4099A4104A4109A4114A4119A4124A4129A4134A4139A4144A4149A4154A4159A4164A4169A4174A4179A4184A4189A4194A4199A4204A4209A4214A4219A4224A4229A4234A4239A4244A4249A4254A4259A4264A4269A4274A4279A4284A4289A4294A4299A4304A4309A4314A4319A4324A4329A4334A4339A4344A4349A4354A4359A4364A4369A4374A4379A4384A4389A4394A4399A4404A4409A4414A4419A4424A4429A4434A4439A4444A4449A4454A4459A4464A4469A4474A4479A4484A4489A4494A4499A4504A4509A4514A4519A4524A4529A4534A4539A4544A4549A4554A4559A4564A4569A4574A4579A4584A4589A4594A4599A4604A4609A4614A4619A4624A4629A4634A4639A4644A4649A4654A4659A4664A4669A4674A4679A4684A4689A4694A4699A4704A4709A4714A4719A4724A4729A4734A4739A4744A4749A4754A4759A4764A4769A4774A4779A4784A4789A4794A4799A4804A4809A4814A4819A4824A4829A4834A4839A4844A4849A4854A4859A4864A4869A4874A4879A4884A4889A4894A4899A4904A4909A4914A4919A4924A4929A4934A4939A4944A4949A4954A4959A4964A4969A4974A4979A4984A4989A4994A4999A5004A5009A5014A5019A5024A5029A5034A5039A5044A5049A5054A5059A5064A5069A5074A5079A5084A5089A5094A5099A5104A5109A5114A5119A5124A5129A5134A5139A5144A5149A5154A5159A5164A5169A5174A5179A5184A5189A5194A5199A5204A5209A5214A5219A5224A5229A5234A5239A5244A5249A5254A5259A5264A5269A5274A5279A5284A5289A5294A5299A5304A5309A5314A5319A5324A5329A5334A5339A5344A5349A5354A5359A5364A5369A5374A5379A5384A5389A5394A5399A5404A5409A5414A5419A5424A5429A5434A5439A5444A5449A5454A5459A5464A5469A5474A5479A5484A5489A5494A5499A5504A5509A5514A5519A5524A5529A5534A5539A5544A5549A5554A5559A5564A5569A5574A5579A5584A5589A5594A5599A5604A5609A5614A5619A5624A5629A5634A5639A5644A5649A5654A5659A5664A5669A5674A5679A5684A5689A5694A5699A5704A5709A5714A5719A5724A5729A5734A5739A5744A5749A5754A5759A5764A5769A5774A5779A5784A5789A5794A5799A5804A5809A5814A5819A5824A5829A5834A5839A5844A5849A5854A5859A5864A5869A5874A5879A5884A5889A5894A5899A5904A5909A5914A5919A5924A5929A5934A5939A5944A5949A5954A5959A5964A5969A5974A5979A5984A5989A5994A5999A6004A6009A6014A6019A6024A6029A6034A6039A6044A6049A6054A6059A6064A6069A6074A6079A6084A6089A6094A6099A6104A6109A6114A6119A6124A6129A6134A6139A6144A6149A6154A6159A6164A6169A6174A6179A6184A6189A6194A6199A6204A6209A6214A6219A6224A6229A6234A6239A6244A6249A6254A6259A6264A6269A6274A6279A6284A6289A6294A6299A6304A6309A6314A6319A6324A6329A6334A6339A6344A6349A6354A6359A6364A6369A6374A6379A6384A6389A6394A6399A6404A6409A6414A6419A6424A6429A6434A6439A6444A6449A6454A6459A6464A6469A6474A6479A6484A6489A6494A6499A6504A6509A6514A6519A6524A6529A6534A6539A6544A6549A6554A6559A6564A6569A6574A6579A6584A6589A6594A6599A6604A6609A6614A6619A6624A6629A6634A6639A6644A6649A6654A6659A6664A6669A6674A6679A6684A6689A6694A6699A6704A6709A6714A6719A6724A6729A6734A6739A6744A6749A6754A6759A6764A6769A6774A6779A6784A6789A6794A6799A6804A6809A6814A6819A6824A6829A6834A6839A6844A6849A6854A6859A6864A6869A6874A6879A6884A6889A6894A6899A6904A6909A6914A6919A6924A6929A6934A6939A6944A6949A6954A6959A6964A6969A6974A6979A6984A6989A6994A6999A7004A7009A7014A7019A7024A7029A7034A7039A7044A7049A7054A7059A7064A7069A7074A7079A7084A7089A7094A7099A7104A7109A7114A7119A7124A7129A7134A7139A7144A7149A7154A7159A7164A7169A7174A7179A7184A7189A7194A7199A7204A7209A7214A7219A7224A7229A7234A7239A7244A7249A7254A7259A7264A7269A7274A7279A7284A7289A7294A7299A7304A7309A7314A7319A7324A7329A7334A7339A7344A7349A7354A7359A7364A7369A7374A7379A7384A7389A7394A7399A7404A7409A7414A7419A7424A7429A7434A7439A7444A7449A7454A7459A7464A7469A7474A7479A7484A7489A7494A7499A7504A7509A7514A7519A7524A7529A7534A7539A7544A7549A7554A7559A7564A7569A7574A7579A7584A7589A7594A7599A7604A7609A7614A7619A7624A7629A7634A7639A7644A7649A7654A7659A7664A7669A7674A7679A7684A7689A7694A7699A7704A7709A7714A7719A7724A7729A7734A7739A7744A7749A7754A7759A7764A7769A7774A7779A7784A7789A7794A7799A7804A7809A7814A7819A7824A7829A7834A7839A7844A7849A7854A7859A7864A7869A7874A7879A7884A7889A7894A7899A7904A7909A7914A7919A7924A7929A7934A7939A7944A7949A7954A7959A7964A7969A7974A7979A7984A7989A7994A7999A8004A8009A8014A8019A8024A8029A8034A8039A8044A8049A8054A8059A8064A8069A8074A8079A8084A8089A8094A8099A8104A8109A8114A8119A8124A8129A8134A8139A8144A8149A8154A8159A8164A8169A8174A8179A8184A8189A8194A8199A8204A8209A8214A8219A8224A8229A8234A8239A8244A8249A8254A8259A8264A8269A8274A8279A8284A8289A8294A8299A8304A8309A8314A8319A8324A8329A8334A8339A8344A8349A8354A8359A8364A8369A8374A8379A8384A8389A8394A8399A8404A8409A8414A8419A8424A8429A8434A8439A8444A8449A8454A8459A8464A8469A8474A8479A8484A8489A8494A8499A8504A8509A8514A8519A8524A8529A8534A8539A8544A8549A8554A8559A8564A8569A8574A8579A8584A8589A8594A8599A8604A8609A8614A8619A8624A8629A8634A8639A8644A8649A8654A8659A8664A8669A8674A8679A8684A8689A8694A8699A8704A8709A8714A8719A8724A8729A8734A8739A8744A8749A8754A8759A8764A8769A8774A8779A8784A8789A8794A8799A8804A8809A8814A8819A8824A8829A8834A8839A8844A8849A8854A8859A8864A8869A8874A8879A8884A8889A8894A8899A8904A8909A8914A8919A8924A8929A8934A8939A8944A8949A8954A8959A8964A8969A8974A8979A8984A8989A8994A8999A9004A9009A9014A9019A9024A9029A9034A9039A9044A9049A9054A9059A9064A9069A9074A9079A9084A9089A9094A9099A9104A9109A9114A9119A9124A9129A9134A9139A9144A9149A9154A9159A9164A9169A9174A9179A9184A9189A9194A9199A9204A9209A9214A9219A9224A9229A9234A9239A9244A9249A9254A9259A9264A9269A9274A9279A9284A9289A9294A9299A9304A9309A9314A9319A9324A9329A9334A9339A9344A9349A9354A9359A9364A9369A9374A9379A9384A9389A9394A9399A9404A9409A9414A9419A9424A9429A9434A9439A9444A9449A9454A9459A9464A9469A9474A9479A9484A9489A9494A9499A9504A9509A9514A9519A9524A9529A9534A9539A9544A9549A9554A9559A9564A9569A9574A9579A9584A9589A9594A9599A9604A9609A9614A9619A9624A9629A9634A9639A9644A9649A9654A9659A9664A9669A9674A9679A9684A9689A9694A9699A9704A9709A9714A9719A9724A9729A9734A9739A9744A9749A9754A9759A9764A9769A9774A9779A9784A9789A9794A9799A9804A9809A9814A9819A9824A9829A9834A9839A9844A9849A9854A9859A9864A9869A9874A9879A9884A9889A9894A9899A9904A9909A9914A9919A9924A9929A9934A9939A9944A9949A9954A9959A9964A9969A9974A9979A9984A9989A9994A10000A10006A10012A10018A10024A10030A10036A10042A10048A10054A10060A10066A10072A10078A10084A10090A10096A10102A10108A10114A10120A10126A10132A10138A10144A10150A10156A10162A10168A10174A10180A10186A10192A10198A10204A10210A10216A10222A10228A10234A10240A10246A10252A10258A10264A10270A10276A10282A10288A10294A10300A10306A10312A10318A10324A10330A10336A10342A10348A10354A10360A10366A10372A10378A10384A10390A10396A10402A10408A10414A10420A10426A10432A10438A10444A10450A10456A10462A10468A10474A10480A10486A10492A10498A10504A10510A10516A10522A10528A10534A10540A10546A10552A10558A10564A10570A10576A10582A10588A10594A10600A10606A10612A10618A10624A10630A10636A10642A10648A10654A10660A10666A10672A10678A10684A10690A10696A10702A10708A10714A10720A10726A10732A10738A10744A10750A10756A10762A10768A10774A10780A10786A10792A10798A10804A10810A10816A10822A10828A10834A10840A10846A10852A10858A10864A10870A10876A10882A10888A10894A10900A10906A10912A10918A10924A10930A10936A10942A10948A10954A10960A10966A10972A10978A10984A10990A10996A11002A11008A11014A11020A11026A11032A11038A11044A11050A11056A11062A11068A11074A11080A11086A11092A11098A11104A11110A11116A11122A11128A11134A11140A11146A11152A11158A11164A11170A11176A11182A11188A11194A11200A11206A11212A11218A11224A11230A11236A11242A11248A11254A11260A11266A11272A11278A11284A11290A11296A11302A11308A11314A11320A11326A11332A11338A11344A11350A11356A11362A11368A11374A11380A11386A11392A11398A11404A11410A11416A11422A11428A11434A11440A11446A11452A11458A11464A11470A11476A11482A11488A11494A11500A11506A11512A11518A11524A11530A11536A11542A11548A11554A11560A11566A11572A11578A11584A11590A11596A11602A11608A11614A11620A11626A11632A11638A11644A11650A11656A11662A11668A11674A11680A11686A11692A11698A11704A11710A11716A11722A11728A11734A11740A11746A11752A11758A11764A11770A11776A11782A11788A11794A11800A11806A11812A11818A11824A11830A11836A11842A11848A11854A11860A11866A11872A11878A11884A11890A11896A11902A11908A11914A11920A11926A11932A11938A11944A11950A11956A11962A11968A11974A11980A11986A11992A11998A12004A12010A12016A12022A12028A12034A12040A12046A12052A12058A12064A12070A12076A12082A12088A12094A12100A12106A12112A12118A12124A12130A12136A12142A12148A12154A12160A12166A12172A12178A12184A12190A12196A12202A12208A12214A12220A12226A12232A12238A12244A12250A12256A12262A12268A12274A12280A12286A12292A12298A12304A12310A12316A12322A12328A12334A12340A12346A12352A12358A12364A12370A12376A12382A12388A12394A12400A12406A12412A12418A12424A12430A12436A12442A12448A12454A12460A12466A12472A12478A12484A12490A12496A12502A12508A12514A12520A12526A12532A12538A12544A12550A12556A12562A12568A12574A12580A12586A12592A12598A12604A12610A12616A12622A12628A12634A12640A12646A12652A12658A12664A12670A12676A12682A12688A12694A12700A12706A12712A12718A12724A12730A12736A12742A12748A12754A12760A12766A12772A12778A12784A12790A12796A12802A12808A12814A12820A12826A12832A12838A12844A12850A12856A12862A12868A12874A12880A12886A12892A12898A12904A12910A12916A12922A12928A12934A12940A12946A12952A12958A12964A12970A12976A12982A12988A12994A13000A13006A13012A13018A13024A13030A13036A13042A13048A13054A13060A13066A13072A13078A13084A13090A13096A13102A13108A13114A13120A13126A13132A13138A13144A13150A13156A13162A13168A13174A13180A13186A13192A13198A13204A13210A13216A13222A13228A13234A13240A13246A13252A13258A13264A13270A13276A13282A13288A13294A13300A13306A13312A13318A13324A13330A13336A13342A13348A13354A13360A13366A13372A13378A13384A13390A13396A13402A13408A13414A13420A13426A13432A13438A13444A13450A13456A13462A13468A13474A13480A13486A13492A13498A13504A13510A13516A13522A13528A13534A13540A13546A13552A13558A13564A13570A13576A13582A13588A13594A13600A13606A13612A13618A13624A13630A13636A13642A13648A13654A13660A13666A13672A13678A13684A13690A13696A13702A13708A13714A13720A13726A13732A13738A13744A13750A13756A13762A13768A13774A13780A13786A13792A13798A13804A13810A13816A13822A13828A13834A13840A13846A13852A13858A13864A13870A13876A13882A13888A13894A13900A13906A13912A13918A13924A13930A13936A13942A13948A13954A13960A13966A13972A13978A13984A13990A13996A14002A14008A14014A14020A14026A14032A14038A14044A14050A14056A14062A14068A14074A14080A14086A14092A14098A14104A14110A14116A14122A14128A14134A14140A14146A14152A14158A14164A14170A14176A14182A14188A14194A14200A14206A14212A14218A14224A14230A14236A14242A14248A14254A14260A14266A14272A14278A14284A14290A14296A14302A14308A14314A14320A14326A14332A14338A14344A14350A14356A14362A14368A14374A14380A14386A14392A14398A14404A14410A14416A14422A14428A14434A14440A14446A14452A14458A14464A14470A14476A14482A14488A14494A14500A14506A14512A14518A14524A14530A14536A14542A14548A14554A14560A14566A14572A14578A14584A14590A14596A14602A14608A14614A14620A14626A14632A14638A14644A14650A14656A14662A14668A14674A14680A14686A14692A14698A14704A14710A14716A14722A14728A14734A14740A14746A14752A14758A14764A14770A14776A14782A14788A14794A14800A14806A14812A14818A14824A14830A14836A14842A14848A14854A14860A14866A14872A14878A14884A14890A14896A14902A14908A14914A14920A14926A14932A14938A14944A14950A14956A14962A14968A14974A14980A14986A14992A14998A15004A15010A15016A15022A15028A15034A15040A15046A15052A15058A15064A15070A15076A15082A15088A15094A15100A15106A15112A15118A15124A15130A15136A15142A15148A15154A15160A15166A15172A15178A15184A15190A15196A15202A15208A15214A15220A15226A15232A15238A15244A15250A15256A15262A15268A15274A15280A15286A15292A15298A15304A15310A15316A15322A15328A15334A15340A15346A15352A15358A15364A15370A15376A15382A15388A15394A15400A15406A15412A15418A15424A15430A15436A15442A15448A15454A15460A15466A15472A15478A15484A15490A15496A15502A15508A15514A15520A15526A15532A15538A15544A15550A15556A15562A15568A15574A15580A15586A15592A15598A15604A15610A15616A15622A15628A15634A15640A15646A15652A15658A15664A15670A15676A15682A15688A15694A15700A15706A15712A15718A15724A15730A15736A15742A15748A15754A15760A15766A15772A15778A15784A15790A15796A15802A15808A15814A15820A15826A15832A15838A15844A15850A15856A15862A15868A15874A15880A15886A15892A15898A15904A15910A15916A15922A15928A15934A15940A15946A15952A15958A15964A15970A15976A15982A15988A15994A16000A16006A16012A16018A16024A16030A16036A16042A16048A16054A16060A16066A16072A16078A16084A16090A16096A16102A16108A16114A16120A16126A16132A16138A16144A16150A16156A16162A16168A16174A16180A16186A16192A16198A16204A16210A16216A16222A16228A16234A16240A16246A16252A16258A16264A16270A16276A16282A16288A16294A16300A16306A16312A16318A16324A16330A16336A16342A16348A16354A16360A16366A16372A16378A16384A16390A16396A16402A16408A16414A16420A16426A16432A16438A16444A16450A16456A16462A16468A16474A16480A16486A16492A16498A16504A16510A16516A16522A16528A16534A16540A16546A16552A16558A16564A16570A16576A16582A16588A16594A16600A16606A16612A16618A16624A16630A16636A16642A16648A16654A16660A16666A16672A16678A16684A16690A16696A16702A16708A16714A16720A16726A16732A16738A16744A16750A16756A16762A16768A16774A16780A16786A16792A16798A16804A16810A16816A16822A16828A16834A16840A16846A16852A16858A16864A16870A16876A16882A16888A16894A16900A16906A16912A16918A16924A16930A16936A16942A16948A16954A16960A16966A16972A16978A16984A16990A16996A17002A17008A17014A17020A17026A17032A17038A17044A17050A17056A17062A17068A17074A17080A17086A17092A17098A17104A17110A17116A17122A17128A17134A17140A17146A17152A17158A17164A17170A17176A17182A17188A17194A17200A17206A17212A17218A17224A17230A17236A17242A17248A17254A17260A17266A17272A17278A17284A17290A17296A17302A17308A17314A17320A17326A17332A17338A17344A17350A17356A17362A17368A17374A17380A17386A17392A17398A17404A17410A17416A17422A17428A17434A17440A17446A17452A17458A17464A17470A17476A17482A17488A17494A17500A17506A17512A17518A17524A17530A17536A17542A17548A17554A17560A17566A17572A17578A17584A17590A17596A17602A17608A17614A17620A17626A17632A17638A17644A17650A17656A17662A17668A17674A17680A17686A17692A17698A17704A17710A17716A17722A17728A17734A17740A17746A17752A17758A17764A17770A17776A17782A17788A17794A17800A17806A17812A17818A17824A17830A17836A17842A17848A17854A17860A17866A17872A17878A17884A17890A17896A17902A17908A17914A17920A17926A17932A17938A17944A17950A17956A17962A17968A17974A17980A17986A17992A17998A18004A18010A18016A18022A18028A18034A18040A18046A18052A18058A18064A18070A18076A18082A18088A18094A18100A18106A18112A18118A18124A18130A18136A18142A18148A18154A18160A18166A18172A18178A18184A18190A18196A18202A18208A18214A18220A18226A18232A18238A18244A18250A18256A18262A18268A18274A18280A18286A18292A18298A18304A18310A18316A18322A18328A18334A18340A18346A18352A18358A18364A18370A18376A18382A18388A18394A18400A18406A18412A18418A18424A18430A18436A18442A18448A18454A18460A18466A18472A18478A18484A18490A18496A18502A18508A18514A18520A18526A18532A18538A18544A18550A18556A18562A18568A18574A18580A18586A18592A18598A18604A18610A18616A18622A18628A18634A18640A18646A18652A18658A18664A18670A18676A18682A18688A18694A18700A18706A18712A18718A18724A18730A18736A18742A18748A18754A18760A18766A18772A18778A18784A18790A18796A18802A18808A18814A18820A18826A18832A18838A18844A18850A18856A18862A18868A18874A18880A18886A18892A18898A18904A18910A18916A18922A18928A18934A18940A18946A18952A18958A18964A18970A18976A18982A18988A18994A19000A19006A19012A19018A19024A19030A19036A19042A19048A19054A19060A19066A19072A19078A19084A19090A19096A19102A19108A19114A19120A19126A19132A19138A19144A19150A19156A19162A19168A19174A19180A19186A19192A19198A19204A19210A19216A19222A19228A19234A19240A19246A19252A19258A19264A19270A19276A19282A19288A19294A19300A19306A19312A19318A19324A19330A19336A19342A19348A19354A19360A19366A19372A19378A19384A19390A19396A19402A19408A19414A19420A19426A19432A19438A19444A19450A19456A19462A19468A19474A19480A19486A19492A19498A19504A19510A19516A19522A19528A19534A19540A19546A19552A19558A19564A19570A19576A19582A19588A19594A19600A19606A19612A19618A19624A19630A19636A19642A19648A19654A19660A19666A19672A19678A19684A19690A19696A19702A19708A19714A19720A19726A19732A19738A19744A19750A19756A19762A19768A19774A19780A19786A19792A19798A19804A19810A19816A19822A19828A19834A19840A19846A19852A19858A19864A19870A19876A19882A19888A19894A19900A19906A19912A19918A19924A19930A19936A19942A19948A19954A19960A19966A19972A19978A19984A19990A19996A20002A20008A20014A20020A20026A20032A20038A20044A20050A20056A20062A20068A20074A20080A20086A20092A20098A20104A20110A20116A20122A20128A20134A20140A20146A20152A20158A20164A20170A20176A20182A20188A20194A20200A20206A20212A20218A20224A20230A20236A20242A20248A20254A20260A20266A20272A20278A20284A20290A20296A20302A20308A20314A20320A20326A20332A20338A20344A20350A20356A20362A20368A20374A20380A20386A20392A20398A20404A20410A20416A20422A20428A20434A20440A20446A20452A20458A20464A20470A20476A20482A20488A20494A20500A20506A20512A20518A20524A20530A20536A20542A20548A20554A20560A20566A20572A20578A20584A20590A20596A20602A20608A20614A20620A20626A20632A20638A20644A20650A20656A20662A20668A20674A20680A20686A20692A20698A20704A20710A20716A20722A20728A20734A20740A20746A20752A20758A20764A20770A20776A20782A20788A20794A20800A20806A20812A20818A20824A20830A20836A20842A20848A20854A20860A20866A20872A20878A20884A20890A20896A20902A20908A20914A20920A20926A20932A20938A20944A20950A20956A20962A20968A20974A20980A20986A20992A20998A21004A21010A21016A21022A21028A21034A21040A21046A21052A21058A21064A21070A21076A21082A21088A21094A21100A21106A21112A21118A21124A21130A21136A21142A21148A21154A21160A21166A21172A21178A21184A21190A21196A21202A21208A21214A21220A21226A21232A21238A21244A21250A21256A21262A21268A21274A21280A21286A21292A21298A21304A21310A21316A21322A21328A21334A21340A21346A21352A21358A21364A21370A21376A21382A21388A21394A21400A21406A21412A21418A21424A21430A21436A21442A21448A21454A21460A21466A21472A21478A21484A21490A21496A21502A21508A21514A21520A21526A21532A21538A21544A21550A21556A21562A21568A21574A21580A21586A21592A21598A21604A21610A21616A21622A21628A21634A21640A21646A21652A21658A21664A21670A21676A21682A21688A21694A21700A21706A21712A21718A21724A21730A21736A21742A21748A21754A21760A21766A21772A21778A21784A21790A21796A21802A21808A21814A21820A21826A21832A21838A21844A21850A21856A21862A21868A21874A21880A21886A21892A21898A21904A21910A21916A21922A21928A21934A21940A21946A21952A21958A21964A21970A21976A21982A21988A21994A22000A22006A22012A22018A22024A22030A22036A22042A22048A22054A22060A22066A22072A22078A22084A22090A22096A22102A22108A22114A22120A22126A22132A22138A22144A22150A22156A22162A22168A22174A22180A22186A22192A22198A22204A22210A22216A22222A22228A22234A22240A22246A22252A22258A22264A22270A22276A22282A22288A22294A22300A22306A22312A22318A22324A22330A22336A22342A22348A22354A22360A22366A22372A22378A22384A22390A22396A22402A22408A22414A22420A22426A22432A22438A22444A22450A22456A22462A22468A22474A22480A22486A22492A22498A22504A22510A22516A22522A22528A22534A22540A22546A22552A22558A22564A22570A22576A22582A22588A22594A22600A22606A22612A22618A22624A22630A22636A22642A22648A22654A22660A22666A22672A22678A22684A22690A22696A22702A22708A22714A22720A22726A22732A22738A22744A22750A22756A22762A22768A22774A22780A22786A22792A22798A22804A22810A22816A22822A22828A22834A22840A22846A22852A22858A22864A22870A22876A22882A22888A22894A22900A22906A22912A22918A22924A22930A22936A22942A22948A22954A22960A22966A22972A22978A22984A22990A22996A23002A23008A23014A23020A23026A23032A23038A23044A23050A23056A23062A23068A23074A23080A23086A23092A23098A23104A23110A23116A23122A23128A23134A23140A23146A23152A23158A23164A23170A23176A23182A23188A23194A23200A23206A23212A23218A23224A23230A23236A23242A23248A23254A23260A23266A23272A23278A23284A23290A23296A23302A23308A23314A23320A23326A23332A23338A23344A23350A23356A23362A23368A23374A23380A23386A23392A23398A23404A23410A23416A23422A23428A23434A23440A23446A23452A23458A23464A23470A23476A23482A23488A23494A23500A23506A23512A23518A23524A23530A23536A23542A23548A23554A23560A23566A23572A23578A23584A23590A23596A23602A23608A23614A23620A23626A23632A23638A23644A23650A23656A23662A23668A23674A23680A23686A23692A23698A23704A23710A23716A23722A23728A23734A23740A23746A23752A23758A23764A23770A23776A23782A23788A23794A23800A23806A23812A23818A23824A23830A23836A23842A23848A23854A23860A23866A23872A23878A23884A23890A23896A23902A23908A23914A23920A23926A23932A23938A23944A23950A23956A23962A23968A23974A23980A23986A23992A23998A24004A24010A24016A24022A24028A24034A24040A24046A24052A24058A24064A24070A24076A24082A24088A24094A24100A24106A24112A24118A24124A24130A24136A24142A24148A24154A24160A24166A24172A24178A24184A24190A24196A24202A24208A24214A24220A24226A24232A24238A24244A24250A24256A24262A24268A24274A24280A24286A24292A24298A24304A24310A24316A24322A24328A24334A24340A24346A24352A24358A24364A24370A24376A24382A24388A24394A24400A24406A24412A24418A24424A24430A24436A24442A24448A24454A24460A24466A24472A24478A24484A24490A24496A24502A24508A24514A24520A24526A24532A24538A24544A24550A24556A24562A24568A24574A24580A24586A24592A24598A24604A24610A24616A24622A24628A24634A24640A24646A24652A24658A24664A24670A24676A24682A24688A24694A24700A24706A24712A24718A24724A24730A24736A24742A24748A24754A24760A24766A24772A24778A24784A24790A24796A24802A24808A24814A24820A24826A24832A24838A24844A24850A24856A24862A24868A24874A24880A24886A24892A24898A24904A24910A24916A24922A24928A24934A24940A24946A24952A24958A24964A24970A24976A24982A24988A24994A25000A25006A25012A25018A25024A25030A25036A25042A25048A25054A25060A25066A25072A25078A25084A25090A25096A25102A25108A25114A25120A25126A25132A25138A25144A25150A25156A25162A25168A25174A25180A25186A25192A25198A25204A25210A25216A25222A25228A25234A25240A25246A25252A25258A25264A25270A25276A25282A25288A25294A25300A25306A25312A25318A25324A25330A25336A25342A25348A25354A25360A25366A25372A25378A25384A25390A25396A25402A25408A25414A25420A25426A25432A25438A25444A25450A25456A25462A25468A25474A25480A25486A25492A25498A25504A25510A25516A25522A25528A25534A25540A25546A25552A25558A25564A25570A25576A25582A25588A25594A25600A25606A25612A25618A25624A25630A25636A25642A25648A25654A25660A25666A25672A25678A25684A25690A25696A25702A25708A25714A25720A25726A25732A25738A25744A25750A25756A25762A25768A25774A25780A25786A25792A25798A25804A25810A25816A25822A25828A25834A25840A25846A25852A25858A25864A25870A25876A25882A25888A25894A25900A25906A25912A25918A25924A25930A25936A25942A25948A25954A25960A25966A25972A25978A25984A25990A25996A26002A26008A26014A26020A26026A26032A26038A26044A26050A26056A26062A26068A26074A26080A26086A26092A26098A26104A26110A26116A26122A26128A26134A26140A26146A26152A26158A26164A26170A26176A26182A26188A26194A26200A26206A26212A26218A26224A26230A26236A26242A26248A26254A26260A26266A26272A26278A26284A26290A26296A26302A26308A26314A26320A26326A26332A26338A26344A26350A26356A26362A26368A26374A26380A26386A26392A26398A26404A26410A26416A26422A26428A26434A26440A26446A26452A26458A26464A26470A26476A26482A26488A26494A26500A26506A26512A26518A26524A26530A26536A26542A26548A26554A26560A26566A26572A26578A26584A26590A26596A26602A26608A26614A26620A26626A26632A26638A26644A26650A26656A26662A26668A26674A26680A26686A26692A26698A26704A26710A26716A26722A26728A26734A26740A26746A26752A26758A26764A26770A26776A26782A26788A26794A26800A26806A26812A26818A26824A26830A26836A26842A26848A26854A26860A26866A26872A26878A26884A26890A26896A26902A26908A26914A26920A26926A26932A26938A26944A26950A26956A26962A26968A26974A26980A26986A26992A26998A27004A27010A27016A27022A27028A27034A27040A27046A27052A27058A27064A27070A27076A27082A27088A27094A27100A27106A27112A27118A27124A27130A27136A27142A27148A27154A27160A27166A27172A27178A27184A27190A27196A27202A27208A27214A27220A27226A27232A27238A27244A27250A27256A27262A27268A27274A27280A27286A27292A27298A27304A27310A27316A27322A27328A27334A27340A27346A27352A27358A27364A27370A27376A27382A27388A27394A27400A27406A27412A27418A27424A27430A27436A27442A27448A27454A27460A27466A27472A27478A27484A27490A27496A27502A27508A27514A27520A27526A27532A27538A27544A27550A27556A27562A27568A27574A27580A27586A27592A27598A27604A27610A27616A27622A27628A27634A27640A27646A27652A27658A27664A27670A27676A27682A27688A27694A27700A27706A27712A27718A27724A27730A27736A27742A27748A27754A27760A27766A27772A27778A27784A27790A27796A27802A27808A27814A27820A27826A27832A27838A27844A27850A27856A27862A27868A27874A27880A27886A27892A27898A27904A27910A27916A27922A27928A27934A27940A27946A27952A27958A27964A27970A27976A27982A27988A27994A28000A28006A28012A28018A28024A28030A28036A28042A28048A28054A28060A28066A28072A28078A28084A28090A28096A28102A28108A28114A28120A28126A28132A28138A28144A28150A28156A28162A28168A28174A28180A28186A28192A28198A28204A28210A28216A28222A28228A28234A28240A28246A28252A28258A28264A28270A28276A28282A28288A28294A28300A28306A28312A28318A28324A28330A28336A28342A28348A28354A28360A28366A28372A28378A28384A28390A28396A28402A28408A28414A28420A28426A28432A28438A28444A28450A28456A28462A28468A28474A28480A28486A28492A28498A28504A28510A28516A28522A28528A28534A28540A28546A28552A28558A28564A28570A28576A28582A28588A28594A28600A28606A28612A28618A28624A28630A28636A28642A28648A28654A28660A28666A28672A28678A28684A28690A28696A28702A28708A28714A28720A28726A28732A28738A28744A28750A28756A28762A28768A28774A28780A28786A28792A28798A28804A28810A28816A28822A28828A28834A28840A28846A28852A28858A28864A28870A28876A28882A28888A28894A28900A28906A28912A28918A28924A28930A28936A28942A28948A28954A28960A28966A28972A28978A28984A28990A28996A29002A29008A29014A29020A29026A29032A29038A29044A29050A29056A29062A29068A29074A29080A29086A29092A29098A29104A29110A29116A29122A29128A29134A29140A29146A29152A29158A29164A29170A29176A29182A29188A29194A29200A29206A29212A29218A29224A29230A29236A29242A29248A29254A29260A29266A29272A29278A29284A29290A29296A29302A29308A29314A29320A29326A29332A29338A29344A29350A29356A29362A29368A29374A29380A29386A29392A29398A29404A29410A29416A29422A29428A29434A29440A29446A29452A29458A29464A29470A29476A29482A29488A29494A29500A29506A29512A29518A29524A29530A29536A29542A29548A29554A29560A29566A29572A29578A29584A29590A29596A29602A29608A29614A29620A29626A29632A29638A29644A29650A29656A29662A29668A29674A29680A29686A29692A29698A29704A29710A29716A29722A29728A29734A29740A29746A29752A29758A29764A29770A29776A29782A29788A29794A29800A29806A29812A29818A29824A29830A29836A29842A29848A29854A29860A29866A29872A29878A29884A29890A29896A29902A29908A29914A29920A29926A29932A29938A29944A29950A29956A29962A29968A29974A29980A29986A29992A29998A30004A30010A30016A30022A30028A30034A30040A30046A30052A30058A30064A30070A30076A30082A30088A30094A30100A30106A30112A30118A30124A30130A30136A30142A30148A30154A30160A30166A30172A30178A30184A30190A30196A30202A30208A30214A30220A30226A30232A30238A30244A30250A30256A30262A30268A30274A30280A30286A30292A30298A30304A30310A30316A30322A30328A30334A30340A30346A30352A30358A30364A30370A30376A30382A30388A30394A30400A30406A30412A30418A30424A30430A30436A30442A30448A30454A30460A30466A30472A30478A30484A30490A30496A30502A30508A30514A30520A30526A30532A30538A30544A30550A30556A30562A30568A30574A30580A30586A30592A30598A30604A30610A30616A30622A30628A30634A30640A30646A30652A30658A30664A30670A30676A30682A30688A30694A30700A30706A30712A30718A30724A30730A30736A30742A30748A30754A30760A30766A30772A30778A30784A30790A30796A30802A30808A30814A30820A30826A30832A30838A30844A30850A30856A30862A30868A30874A30880A30886A30892A30898A30904A30910A30916A30922A30928A30934A30940A30946A30952A30958A30964A30970A30976A30982A30988A30994A31000A31006A31012A31018A31024A31030A31036A31042A31048A31054A31060A31066A31072A31078A31084A31090A31096A31102A31108A31114A31120A31126A31132A31138A31144A31150A31156A31162A31168A31174A31180A31186A31192A31198A31204A31210A31216A31222A31228A31234A31240A31246A31252A31258A31264A31270A31276A31282A31288A31294A31300A31306A31312A31318A31324A31330A31336A31342A31348A31354A31360A31366A31372A31378A31384A31390A31396A31402A31408A31414A31420A31426A31432A31438A31444A31450A31456A31462A31468A31474A31480A31486A31492A31498A31504A31510A31516A31522A31528A31534A31540A31546A31552A31558A31564A31570A31576A31582A31588A31594A31600A31606A31612A31618A31624A31630A31636A31642A31648A31654A31660A31666A31672A31678A31684A31690A31696A31702A31708A31714A31720A31726A31732A31738A31744A31750A31756A31762A31768A31774A31780A31786A31792A31798A31804A31810A31816A31822A31828A31834A31840A31846A31852A31858A31864A31870A31876A31882A31888A31894A31900A31906A31912A31918A31924A31930A31936A31942A31948A31954A31960A31966A31972A31978A31984A31990A31996A32002",
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
        "firstName":"Field",
        "lastName":"Length",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "Tutor Resume length should be less than 32000 characters."



  Scenario: (-) Create Contact (tutor) with 'wwChildrenRef' >32 symbols

    * def newContact =
        """
        {
        "student":null,
        "tutor":
            {
              "dateFinished":"2025-01-01",
              "dateStarted":"2018-02-02",
              "familyNameLegal":"familyNameLegal",
              "givenNameLegal":"givenNameLegal",
              "payrollRef":"some payrollRef",
              "resume":"some resume",
              "wwChildrenCheckedOn":"2018-03-03",
              "wwChildrenExpiry":"2024-04-04",
              "wwChildrenRef":"A3A5A7A9A12A15A18A21A24A27A30A33A",
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
        "firstName":"John2",
        "lastName":"Smith2",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":null,
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
    Then status 400
    And match $.errorMessage == "WW Children Reference Number length should be less than 32 characters."



  Scenario: (-) Create Contact (student) when Contact relation does not belong to saved contact

    * def newContact =
        """
        {
        "student":
            {
            "countryOfBirth":{"id":2},
            "disabilityType":"Mental illness",
            "labourForceStatus":"Default popup option",
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
            "concessions":[{"number":"50","type":{"id":"2"},"expiresOn":"2030-01-01"}]
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
        "firstName":"John99",
        "lastName":"Smith99",
        "middleName":"Fitzgerald",
        "invoiceTerms":10,
        "taxId":1,
        "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
        "documents":[{"id":201}],
        "tags":[{"id":234}],
        "memberships":[],
        "profilePicture":null,
        "relations":[{"contactFromId":2,"contactFromName":"stud1, stud1","contactToId":3,"contactToName":"stud2, stud2","relationId":1}],
        "financialData":[]
        }
        """

    Given path ishPath
    And request newContact
    When method POST
    Then status 400
    And match $.errorMessage == "One of contact should be null (current), second contact should point to other contact."



  Scenario: (+) Create Contact by notadmin with access rights

#       <--->  Login as notadmin
    * configure headers = { Authorization:  'UserWithRightsCreate'}


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
            "usi":null,
            "usiStatus":null,
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
        "firstName":"John4",
        "lastName":"Smith4",
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
        "rules":[{"id":null,"description":"test","startDate":"2020-02-11","endDate":"2020-02-11","repeatEnd":"onDate","repeat":"hour","repeatEndAfter":0,"repeatOn":"2020-02-11","startDateTime":null,"endDateTime":null}]
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

    * def id = get[0] response.rows[?(@.values == ["Smith4"])].id
    * print "id = " + id

    Given path ishPath + '/' + id
    When method GET
    Then status 200
    And match $ contains
        """
        {
            "id":"#(~~id)",
            "student":{"id":"#number","countryOfBirth":{"id":2,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"United States of America","saccCode":null},"disabilityType":"Mental illness","labourForceStatus":"Not stated","englishProficiency":"Not Well","highestSchoolLevel":"Year 10","indigenousStatus":"Neither","isOverseasClient":false,"isStillAtSchool":false,"language":null,"priorEducationCode":"Certificate I","specialNeeds":"BBB","yearSchoolCompleted":null,"studentNumber":"#number","countryOfResidency":null,"visaNumber":null,"visaType":null,"visaExpiryDate":null,"passportNumber":null,"medicalInsurance":null,"uniqueLearnerIdentifier":"bbb","usi":null,"usiStatus":"Not supplied","chessn":null,"feeHelpEligible":false,"citizenship":"No information","townOfBirth":"b","specialNeedsAssistance":false,"clientIndustryEmployment":"Retail Trade (G)","clientOccupationIdentifier":"Not Stated","waitingLists":[],"concessions":[]},
            "tutor":null,
            "abn":"12345678901",
            "birthDate":"1991-07-20",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
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
            "uniqueCode":"#present",
            "honorific":"Dr",
            "title":"Vice President of Marketing",
            "email":"testContact@gmail.com",
            "firstName":"John4",
            "lastName":"Smith4",
            "middleName":"Fitzgerald",
            "invoiceTerms":10,
            "taxId":1,
            "customFields":{"cf1":"qwerty","cf2":"asdfgh"},
            "documents":[],
            "tags":[],
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
    * configure headers = { Authorization: 'admin'}

    Given path ishPath + '/' + id
    When method DELETE
    Then status 204



  Scenario: (-) Create new Contact by notadmin without access rights

#       <--->  Login as notadmin
    * configure headers = { Authorization:  'UserWithRightsEdit'}

    * def newContact = {}

    Given path ishPath
    And request newContact
    When method POST
    Then status 403
    And match $.errorMessage == "Sorry, you have no permissions to create contact. Please contact your administrator"
