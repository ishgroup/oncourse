@parallel=false
Feature: Try to GET entities without access rights

    Background: Authorize first
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'login'
        

    Scenario: (-) Try to POST entities without access rights

        * configure headers = { Authorization: 'UserWithRightsEdit'}

        * def concessionTypeArray = [{name: 'SomeName', requireExpary: false, requireNumber: false, allowOnWeb: true}]
        * def someContactRelationType = [{"relationName":"relationName","reverseRelationName":"reverseRelationName#1","portalAccess":true}]
        * def someFieldType = [{"name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]
        * def someTaxTypeArray = [{code: 'someName#1', rate: '0.15', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        * table data
            | path                                 | entity                  |
            | 'preference/concession/type'         | concessionTypeArray     |
            | 'preference/contact/relation/type'   | someContactRelationType |
            | 'preference/field/type'              | someFieldType           |
            | 'preference/payment/type'            | somePaymentTypeArray    |
            | 'preference/tax'                     | someTaxTypeArray        |

        * call read('postEntityWithoutAccessRights.feature') data


    Scenario: (-) Try to DELETE entities without access rights
#       Prepare new entities to update them
#       <--->
        * call read('signIn.feature')

        * def concessionTypeArray = [{name: 'SomeName', requireExpary: false, requireNumber: false, allowOnWeb: true}]
        * def someContactRelationType = [{"relationName":"relationName","reverseRelationName":"reverseRelationName#1","portalAccess":true}]
        * def someFieldType = [{dataType:"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]
        * def somePaymentTypeArray = [{name: 'someName', active: true, reconcilable: true, bankedAuto: true, accountId: "1", undepositAccountId: "1", type: "Other"}]
        * def someTaxTypeArray = [{code: 'someName#1', rate: '0.15', gst: true, payableAccountId: 2, receivableAccountId: 4, description: 'someDescription'}]

        * table dataToCreate
            | path                                 | entity                  |
            | 'preference/concession/type'         | concessionTypeArray     |
            | 'preference/contact/relation/type'   | someContactRelationType |
            | 'preference/field/type'              | someFieldType           |
            | 'preference/payment/type'            | somePaymentTypeArray    |
            | 'preference/tax'                     | someTaxTypeArray        |

        * call read('createEntity.feature') dataToCreate

        * table dataToDelete
            | path                                 | entityId |
            | 'preference/concession/type'         |          |
            | 'preference/contact/relation/type'   |          |
            | 'preference/field/type'              |          |
            | 'preference/payment/type'            |          |
            | 'preference/tax'                     |          |

        Given path dataToDelete[0].path
        When method GET
        Then status 200
        And set dataToDelete[0].entityId = karate.jsonPath(response, "[?(@.name=='" + dataToCreate[0].entity[0].name + "')]")[0].id

        Given path dataToDelete[1].path
        When method GET
        Then status 200
        And set dataToDelete[1].entityId = karate.jsonPath(response, "[?(@.relationName=='" + dataToCreate[1].entity[0].relationName + "')]")[0].id

        Given path dataToDelete[2].path
        When method GET
        Then status 200
        And set dataToDelete[2].entityId = karate.jsonPath(response, "[?(@.name=='" + dataToCreate[2].entity[0].name + "')]")[0].id

        Given path dataToDelete[3].path
        When method GET
        Then status 200
        And set dataToDelete[3].entityId = karate.jsonPath(response, "[?(@.name=='" + dataToCreate[3].entity[0].name + "')]")[0].id

        Given path dataToDelete[4].path
        When method GET
        Then status 200
        And set dataToDelete[4].entityId = karate.jsonPath(response, "[?(@.code=='" + dataToCreate[4].entity[0].code + "')]")[0].id
#       <--->

        * configure headers = { Authorization: 'UserWithRightsCreate'}
        * call read('deleteEntityWithoutAccessRights.feature') dataToDelete


#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * call read('signIn.feature')
        * call read('removeEntityById.feature') dataToDelete
#       <--->