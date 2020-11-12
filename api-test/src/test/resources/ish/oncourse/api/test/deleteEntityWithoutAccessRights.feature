@ignore
@parallel=false
Feature: re-usable feature to DELETE entity from DB without access rights
    
    Scenario:
        * url 'https://127.0.0.1:8182/a/v1'
        * def data = {path: '#(path)', id: '#(entityId)'}
    
        Given path data.path + "/" + data.id
        When method DELETE
        Then status 403