@ignore
@parallel=false
Feature: re-usable feature to remove entity from DB using entityId
    
    Scenario:
        * url 'https://127.0.0.1:8182/a/v1'
        * def data = {path: '#(path)', id: '#(entityId)'}
    
        Given path data.path + '/' + data.id
        When method DELETE
        Then status 204