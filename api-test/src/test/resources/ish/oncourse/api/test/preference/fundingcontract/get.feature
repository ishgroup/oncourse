@parallel=false
Feature: Main feature for all GET requests with path 'preference/fundingcontract'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/fundingcontract'
        



    Scenario: (+) Get list of Funding Contracts by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":13,"created":"#ignore","modified":"#ignore","active":false,"name":"AVETARS (ACT)","flavour":"AVETARS (ACT)"},
        {"id":3,"created":"#ignore","modified":"#ignore","active":false,"name":"CSO (Community Colleges)","flavour":"CSO (Community Colleges)"},
        {"id":5,"created":"#ignore","modified":"#ignore","active":false,"name":"DETConnect (Queensland)","flavour":"DETConnect (Queensland)"},
        {"id":12,"created":"#ignore","modified":"#ignore","active":false,"name":"Northern Territories VET Provider Portal","flavour":"Northern Territories VET Provider Portal"},
        {"id":7,"created":"#ignore","modified":"#ignore","active":false,"name":"Skills Tasmania","flavour":"Skills Tasmania"},
        {"id":8,"created":"#ignore","modified":"#ignore","active":false,"name":"Skills Victoria","flavour":"Skills Victoria"},
        {"id":9,"created":"#ignore","modified":"#ignore","active":false,"name":"STARS (WA)","flavour":"STARS (WA)"},
        {"id":6,"created":"#ignore","modified":"#ignore","active":false,"name":"STELA (South Australia)","flavour":"STELA (South Australia)"},
        {"id":4,"created":"#ignore","modified":"#ignore","active":false,"name":"STSOnline (NSW)","flavour":"STSOnline (NSW)"},
        {"id":11,"created":"#ignore","modified":"#ignore","active":false,"name":"WA RAPT","flavour":"WA RAPT"}
        ]
        """



    Scenario: (+) Get list of Funding Contracts by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}


#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":13,"created":"#ignore","modified":"#ignore","active":false,"name":"AVETARS (ACT)","flavour":"AVETARS (ACT)"},
        {"id":3,"created":"#ignore","modified":"#ignore","active":false,"name":"CSO (Community Colleges)","flavour":"CSO (Community Colleges)"},
        {"id":5,"created":"#ignore","modified":"#ignore","active":false,"name":"DETConnect (Queensland)","flavour":"DETConnect (Queensland)"},
        {"id":12,"created":"#ignore","modified":"#ignore","active":false,"name":"Northern Territories VET Provider Portal","flavour":"Northern Territories VET Provider Portal"},
        {"id":7,"created":"#ignore","modified":"#ignore","active":false,"name":"Skills Tasmania","flavour":"Skills Tasmania"},
        {"id":8,"created":"#ignore","modified":"#ignore","active":false,"name":"Skills Victoria","flavour":"Skills Victoria"},
        {"id":9,"created":"#ignore","modified":"#ignore","active":false,"name":"STARS (WA)","flavour":"STARS (WA)"},
        {"id":6,"created":"#ignore","modified":"#ignore","active":false,"name":"STELA (South Australia)","flavour":"STELA (South Australia)"},
        {"id":4,"created":"#ignore","modified":"#ignore","active":false,"name":"STSOnline (NSW)","flavour":"STSOnline (NSW)"},
        {"id":11,"created":"#ignore","modified":"#ignore","active":false,"name":"WA RAPT","flavour":"WA RAPT"}
        ]
        """

