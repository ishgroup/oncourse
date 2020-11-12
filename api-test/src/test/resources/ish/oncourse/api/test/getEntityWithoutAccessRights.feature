@ignore
@parallel=false
Feature: re-usable feature to get entities from DB without access rights
    
    Scenario:
        * url 'https://127.0.0.1:8182/a/v1'
        * def data = {path: '#(path)'}
        
        Given path data.path
        When method GET
        Then status 403