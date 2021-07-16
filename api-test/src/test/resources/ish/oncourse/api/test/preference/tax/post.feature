@parallel=false
Feature: Main feature for all POST requests with path 'preference/tax'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/tax'
        


    Scenario: (+) Create new valid taxType
        * def someTaxTypeArray = [{code: 'someName#1', rate: '0.15', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 4

        * def list = get response[?(@.code == 'someName#1')]
        And match list[*].code contains 'someName#1'
        And match list[*].rate == [0.15]
        And match list[*].gst == [true]
        And match list[*].payableAccountId == [2]
        And match list[*].receivableAccountId == [4]
        And match list[*].description contains 'someDescription'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * def id = get[0] response[?(@.code == 'someName#1')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName#1'


    Scenario: (+) Create new valid ('Rate' is '0') taxType

        * def someTaxTypeArray = [{code: 'someName#2', rate: '0', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 4

        * def list = get response[?(@.code == 'someName#2')]
        And match list[*].rate == [0]

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * def id = get[0] response[?(@.code == 'someName#2')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName#2'


    Scenario: (-) Create new invalid (empty) taxType

        * def emptyTaxTypeArray = [{}]

        Given path ishPath
        And request emptyTaxTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax code can not be empty"


    Scenario: (-) Create new invalid ('Tax Code' field is empty) taxType

        * def emptyTaxCodeArray = [{code: '', rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request emptyTaxCodeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax code can not be empty"


    Scenario: (-) Create new invalid ('Tax Code' field is null) taxType

        * def emptyTaxCodeArray = [{code: null, rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request emptyTaxCodeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax code can not be empty"


    Scenario: (-) Create new invalid ('Tax Code' consists more than 10 symbols) taxType

        * def tooLongCodeArray = [{code: 'tooLongName', rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request tooLongCodeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax code too long"
        And match response[*].code !contains 'tooLongName'

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'tooLongName'


    Scenario: (-) Create new invalid ('Rate' is '1,0001') taxType

        * def someTaxTypeArray = [{code: 'someName', rate: '1.01', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax rate is wrong"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Create new invalid ('Payable Account' field is empty) taxType

        * def emptyPayableAccountArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: '', receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request emptyPayableAccountArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax payable account can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Create new invalid ('Payable Account' is null) taxType

        * def emptyPayableAccountArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: null, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request emptyPayableAccountArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax payable account can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Create new invalid ('Receivable Account' field is empty) taxType

        * def emptyReceivableAccountArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: '', description: 'someDescription'}]

        Given path ishPath
        And request emptyReceivableAccountArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax receivable account can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Create new invalid ('Receivable Account' is null) taxType

        * def emptyReceivableAccountArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: null, description: 'someDescription'}]

        Given path ishPath
        And request emptyReceivableAccountArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax receivable account can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Create taxType with not unique name /the same as system ones/

        * def nonUniqueTaxCodeArray = [{code: 'GST', rate: '0.01', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request nonUniqueTaxCodeArray
        When method POST
        Then status 400
        And match response.errorMessage == "You already have GST tax"


    Scenario: (+) Create taxType with not unique name /not system ones/

        * def taxTypeArray =
        """
        [
        {code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'},
        {code: 'someName', rate: '0.09', gst: true, payableAccountId: 1, receivableAccountId: 5, description: 'someDescription'}
        ]
        """

        Given path ishPath
        And request taxTypeArray
        When method POST
        Then status 204

#       <---> Scenario have been finished. Now find and remove created objects from DB:
        Given path ishPath
        When method GET
        Then status 200
        And def taxType1 = get[0] response[?(@.code == 'someName')]
        And def taxType2 = get[1] response[?(@.code == 'someName')]

        Given path ishPath + '/' + taxType1.id
        When method DELETE
        Then status 204
        Given path ishPath + '/' + taxType2.id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (+) Create a bunch of valid taxTypes

        * def taxTypeArray =
        """
        [
        {code: 'someName1', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'},
        {code: 'someName2', rate: '0.09', gst: true, payableAccountId: 1, receivableAccountId: 5, description: 'someDescription'}
        ]
        """

        Given path ishPath
        And request taxTypeArray
        When method POST
        Then status 204

#       <---> Scenario have been finished. Now find and remove created objects from DB:
        Given path ishPath
        When method GET
        Then status 200
        And def taxType1 = get[0] response[?(@.code == 'someName1')]
        And def taxType2 = get[0] response[?(@.code == 'someName2')]

        Given path ishPath + '/' + taxType1.id
        When method DELETE
        Then status 204

        Given path ishPath + '/' + taxType2.id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName1'
        And match response[*].code !contains 'someName2'


    Scenario: (-) Create a bunch of taxTypes some of them are INVALID because without name

        * def taxTypeArray =
        """
        [
        {code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'},
        {code: '', rate: '0.09', gst: true, payableAccountId: 1, receivableAccountId: 5, description: 'someDescription'}
        ]
        """

        Given path ishPath
        And request taxTypeArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax code can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'
        And match response[*].code !contains ''


    Scenario: (+) Update existing taxCode

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName#3', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someNameUPD'

        * def id = get[0] response[?(@.code == 'someName#3')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'nameUPD', rate: '0.05', gst: false, payableAccountId: 2, receivableAccountId: 5, description: 'updatedSomeDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = get response[?(@.code == 'nameUPD')]
        And match list[*].code contains 'nameUPD'
        And match list[*].rate == [0.05]
        And match list[*].gst == [false]
        And match list[*].payableAccountId == [2]
        And match list[*].receivableAccountId == [5]
        And match list[*].description contains 'updatedSomeDescription'
        And match list[*].code !contains 'someName#3'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName#3'
        And match response[*].code !contains 'nameUPD'


    Scenario: (+) Update taxCode to existing one /not system ones/

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'updName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 204

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

       Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'
        And match response[*].code !contains 'updName'


    Scenario: (+) Update 'Rate' to '0'

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '0', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 4

        * def list = get response[?(@.code == 'someName')]
        And match list[*].rate == [0]

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxCode to existing one /the same as system ones/

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'GST', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'You already have GST tax'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxCode name to empty

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: '', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Tax code can not be empty'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxCode name to null

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: null, rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Tax code can not be empty'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxCode name to 11 symbols

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'

        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName011', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == 'Tax code too long'

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName011'
        And match response[*].code !contains 'someName'


    Scenario: (-) Update 'Rate' to '1,01'

#       <---> Prepare new taxType to update it:
            * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

            Given path ishPath
            And request someTaxTypeArray
            When method POST
            Then status 204
#       <--->

            Given path ishPath
            When method GET
            Then status 200
            And match response[*].code contains 'someName'

            * def id = get[0] response[?(@.code == 'someName')].id

            * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '1.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

            Given path ishPath
            And request taxTypeToUpdateArray
            When method POST
            Then status 400
            And match response.errorMessage == 'Tax rate is wrong'

#       <---> Scenario have been finished. Now find and remove created object from DB:
            Given path ishPath + '/' + id
            When method DELETE
            Then status 204

            Given path ishPath
            When method GET
            Then status 200
            And match response[*].code !contains 'someName'


    Scenario: (-) Update taxType's payableAccount to not 'asset' account

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'
        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '0.01', gst: true, payableAccountId: 333, receivableAccountId: 4, description: 'someDescription'}]
        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax payable account is wrong"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxType's payableAccount to null

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'
        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '0.01', gst: true, payableAccountId: null, receivableAccountId: 4, description: 'someDescription'}]
        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax payable account can not be empty"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxType's receivableAccount to not 'asset' account

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'
        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 444, description: 'someDescription'}]
        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax receivable account is wrong"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update taxType's receivableAccount to null

#       <---> Prepare new taxType to update it:
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code contains 'someName'
        * def id = get[0] response[?(@.code == 'someName')].id

        * def taxTypeToUpdateArray = [{id: '#(id)', code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: null, description: 'someDescription'}]
        Given path ishPath
        And request taxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax receivable account can not be empty"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'


    Scenario: (-) Update not existing taxType

        * def nonExistingTaxTypeToUpdateArray = [{id: 11111, code: 'nonExistingName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]

        Given path ishPath
        And request nonExistingTaxTypeToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Tax 11111 is not exist"

