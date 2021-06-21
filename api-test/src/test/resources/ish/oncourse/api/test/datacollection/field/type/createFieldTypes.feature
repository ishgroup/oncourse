@ignore
@parallel=false
Feature: re-usable feature create one Field type for every entity .e.g Enrolment, Application, etc
    
    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'

    Scenario:
        * def fields = 
        """
        [
            { dataType:'Text', name:'enrolmentFieldType', defaultValue: 'someValue', fieldKey: 'fieldKey1', mandatory: false, sortOrder: 0, entityType: 'Enrolment'},
            { dataType:'Text', name:'applicationFieldType', defaultValue: 'someValue', fieldKey: 'fieldKey2', mandatory: false, sortOrder: 0, entityType: 'Application'},
            { dataType:'Text', name:'waitingListFieldType', defaultValue: 'someValue', fieldKey: 'fieldKey3', mandatory: false, sortOrder: 0, entityType: 'WaitingList'},
            { dataType:'Text', name:'contactFieldType', defaultValue: 'someValue', fieldKey: 'fieldKey4', mandatory: false, sortOrder: 0, entityType: 'Contact'},
            { dataType:'Text', name:'courseFieldType', defaultValue: 'someValue', fieldKey: 'fieldKey5', mandatory: false, sortOrder: 0, entityType: 'Course'}
        ]
        """
        
        Given path 'preference/field/type'
        And request fields
        When method POST
        Then status 204