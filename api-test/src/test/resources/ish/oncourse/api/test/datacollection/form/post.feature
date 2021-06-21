@parallel=false
Feature: Main feature for all POST requests with path 'datacollection/form'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'datacollection/form'


    Scenario: (+) Create a bunch (different types) of valid DataCollectionForms

        * def someDataCollectionFormArray =
        """
        [
        {"name":"Enrolment#1","type":"Enrolment","fields":[],"headings":[{"name":"Contact Details","description":"We require a few more details to create the contact record. It is important that we have correct contact information in case we need to let you know about course changes. Please enter the details as you would like them to appear on a certificate or invoice.","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"Enter your street address","mandatory":false},{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"Enter your residential suburb","mandatory":false},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"Enter your residential country","mandatory":false},{"type":{"uniqueKey":"postcode","label":"Postcode"},"label":"Postcode","helpText":"Enter your residential postcode","mandatory":false},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"Enter your residential state","mandatory":false},{"type":{"uniqueKey":"homePhoneNumber","label":"Home phone number"},"label":"Home phone number","helpText":"Enter your home phone number number","mandatory":false},{"type":{"uniqueKey":"businessPhoneNumber","label":"Business phone number"},"label":"Business phone number","helpText":"Enter your work phone number number","mandatory":false},{"type":{"uniqueKey":"faxNumber","label":"Fax number"},"label":"Fax number","helpText":"Enter your fax number number","mandatory":false},{"type":{"uniqueKey":"mobilePhoneNumber","label":"Mobile phone number"},"label":"Mobile phone number","helpText":"Enter a 10 digit mobile phone number number","mandatory":false},{"type":{"uniqueKey":"dateOfBirth","label":"Date of Birth"},"label":"Date of Birth","helpText":"Enter your date of birth in the format dd/mm/yyyy","mandatory":false},{"type":{"uniqueKey":"isMale","label":"Gender"},"label":"Gender","helpText":"Enter your Gender","mandatory":false},{"type":{"uniqueKey":"abn","label":"ABN"},"label":"ABN","helpText":"Enter your ABN","mandatory":false},{"type":{"uniqueKey":"specialNeeds","label":"Special needs"},"label":"Special dietary requirements, allergies, accessibility or medical considerations","helpText":"Special dietary requirements, allergies, accessibility or medical considerations","mandatory":false}]},{"name":"Marketing","description":"I would like to receive information and offers via:","fields":[{"type":{"uniqueKey":"isMarketingViaEmailAllowed","label":"E-mail"},"label":"E-mail","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaPostAllowed","label":"Post"},"label":"Post","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaSMSAllowed","label":"SMS"},"label":"SMS","helpText":null,"mandatory":true}]}],"deliverySchedule":null},
        {"name":"Application#1","type":"Application","fields":[],"headings":[{"name":"Contact Details","description":"We require a few more details to create the contact record. It is important that we have correct contact information in case we need to let you know about course changes. Please enter the details as you would like them to appear on a certificate or invoice.","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"Enter your street address","mandatory":false},{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"Enter your residential suburb","mandatory":false},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"Enter your residential country","mandatory":false},{"type":{"uniqueKey":"postcode","label":"Postcode"},"label":"Postcode","helpText":"Enter your residential postcode","mandatory":false},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"Enter your residential state","mandatory":false},{"type":{"uniqueKey":"homePhoneNumber","label":"Home phone number"},"label":"Home phone number","helpText":"Enter your home phone number number","mandatory":false},{"type":{"uniqueKey":"businessPhoneNumber","label":"Business phone number"},"label":"Business phone number","helpText":"Enter your work phone number number","mandatory":false},{"type":{"uniqueKey":"faxNumber","label":"Fax number"},"label":"Fax number","helpText":"Enter your fax number number","mandatory":false},{"type":{"uniqueKey":"mobilePhoneNumber","label":"Mobile phone number"},"label":"Mobile phone number","helpText":"Enter a 10 digit mobile phone number number","mandatory":false},{"type":{"uniqueKey":"dateOfBirth","label":"Date of Birth"},"label":"Date of Birth","helpText":"Enter your date of birth in the format dd/mm/yyyy","mandatory":false},{"type":{"uniqueKey":"isMale","label":"Gender"},"label":"Gender","helpText":"Enter your Gender","mandatory":false},{"type":{"uniqueKey":"abn","label":"ABN"},"label":"ABN","helpText":"Enter your ABN","mandatory":false},{"type":{"uniqueKey":"specialNeeds","label":"Special needs"},"label":"Special dietary requirements, allergies, accessibility or medical considerations","helpText":"Special dietary requirements, allergies, accessibility or medical considerations","mandatory":false}]},{"name":"Marketing","description":"I would like to receive information and offers via:","fields":[{"type":{"uniqueKey":"isMarketingViaEmailAllowed","label":"E-mail"},"label":"E-mail","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaPostAllowed","label":"Post"},"label":"Post","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaSMSAllowed","label":"SMS"},"label":"SMS","helpText":null,"mandatory":true}]}],"deliverySchedule":null},
        {"name":"Waiting List#1","type":"WaitingList","fields":[],"headings":[{"name":"Contact Details","description":"We require a few more details to create the contact record. It is important that we have correct contact information in case we need to let you know about course changes. Please enter the details as you would like them to appear on a certificate or invoice.","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"Enter your street address","mandatory":false},{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"Enter your residential suburb","mandatory":false},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"Enter your residential country","mandatory":false},{"type":{"uniqueKey":"postcode","label":"Postcode"},"label":"Postcode","helpText":"Enter your residential postcode","mandatory":false},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"Enter your residential state","mandatory":false},{"type":{"uniqueKey":"homePhoneNumber","label":"Home phone number"},"label":"Home phone number","helpText":"Enter your home phone number number","mandatory":false},{"type":{"uniqueKey":"businessPhoneNumber","label":"Business phone number"},"label":"Business phone number","helpText":"Enter your work phone number number","mandatory":false},{"type":{"uniqueKey":"faxNumber","label":"Fax number"},"label":"Fax number","helpText":"Enter your fax number number","mandatory":false},{"type":{"uniqueKey":"mobilePhoneNumber","label":"Mobile phone number"},"label":"Mobile phone number","helpText":"Enter a 10 digit mobile phone number number","mandatory":false},{"type":{"uniqueKey":"dateOfBirth","label":"Date of Birth"},"label":"Date of Birth","helpText":"Enter your date of birth in the format dd/mm/yyyy","mandatory":false},{"type":{"uniqueKey":"isMale","label":"Gender"},"label":"Gender","helpText":"Enter your Gender","mandatory":false},{"type":{"uniqueKey":"abn","label":"ABN"},"label":"ABN","helpText":"Enter your ABN","mandatory":false},{"type":{"uniqueKey":"specialNeeds","label":"Special needs"},"label":"Special dietary requirements, allergies, accessibility or medical considerations","helpText":"Special dietary requirements, allergies, accessibility or medical considerations","mandatory":false}]},{"name":"Marketing","description":"I would like to receive information and offers via:","fields":[{"type":{"uniqueKey":"isMarketingViaEmailAllowed","label":"E-mail"},"label":"E-mail","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaPostAllowed","label":"Post"},"label":"Post","helpText":null,"mandatory":true},{"type":{"uniqueKey":"isMarketingViaSMSAllowed","label":"SMS"},"label":"SMS","helpText":null,"mandatory":true}]}],"deliverySchedule":null},
        {"name":"Survey#1","type":"Survey","fields":[],"headings":[],"deliverySchedule":"On enrol"},
        {"name":"Survey#2","type":"Survey","fields":[],"headings":[],"deliverySchedule":"On start"},
        {"name":"Survey#3","type":"Survey","fields":[],"headings":[],"deliverySchedule":"Midway"},
        {"name":"Survey#4","type":"Survey","fields":[],"headings":[],"deliverySchedule":"At completion"},
        {"name":"Survey#5","type":"Survey","fields":[],"headings":[{"name":"Heading#1","description":"Description#1"}],"deliverySchedule":"On demand"},
        {"name":"Payer#1","type":"Payer","fields":[{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"123","mandatory":true},{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null},
        {"name":"Parent#1","type":"Parent","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null}
        ]
        """

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains ['Enrolment#1', 'Application#1', 'Waiting List#1', 'Survey#1', 'Survey#2', 'Survey#3', 'Survey#4', 'Survey#5', 'Payer#1', 'Parent#1']


        Given path ishPath
        And request someDataCollectionFormArray[0]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[1]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[2]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[3]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[4]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[5]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[6]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[7]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[8]
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionFormArray[9]
        When method POST
        Then status 204


        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains ['Enrolment#1', 'Application#1', 'Waiting List#1', 'Survey#1', 'Survey#2', 'Survey#3', 'Survey#4', 'Survey#5', 'Payer#1', 'Parent#1']

        * def list = get response[?(@.name == 'Survey#5')]
        * match each list[*].deliverySchedule contains 'On demand'

        * match each list[*].headings[*].name contains 'Heading#1'
        * match each list[*].headings[*].description contains 'Description#1'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Enrolment#1')].id
        * def id2 = get[0] response[?(@.name == 'Application#1')].id
        * def id3 = get[0] response[?(@.name == 'Waiting List#1')].id
        * def id4 = get[0] response[?(@.name == 'Survey#1')].id
        * def id5 = get[0] response[?(@.name == 'Survey#2')].id
        * def id6 = get[0] response[?(@.name == 'Survey#3')].id
        * def id7 = get[0] response[?(@.name == 'Survey#4')].id
        * def id8 = get[0] response[?(@.name == 'Survey#5')].id
        * def id9 = get[0] response[?(@.name == 'Payer#1')].id
        * def id10 = get[0] response[?(@.name == 'Parent#1')].id

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id3)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id4)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id5)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id6)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id7)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id8)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id9)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id10)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains ['Enrolment#1', 'Application#1', 'Waiting List#1', 'Survey#1', 'Survey#2', 'Survey#3', 'Survey#4', 'Survey#5', 'Payer#1', 'Parent#1']