@parallel=false
Feature: Main feature for all POST requests with path 'list/option/message'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/option/message'
        * def ishPathPlain = 'list/plain'
        

        #       <---> Get template id:
        Given path ishPathPlain
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def emailTemplateId = get[0] response.rows[?(@.values == ["Simple Email"])].id
        * print "emailTemplateId = " + emailTemplateId
        * def smsTemplateId = get[0] response.rows[?(@.values == ["Simple SMS"])].id
        * print "smsTemplateId = " + smsTemplateId



#    Scenario: (+) Send email to Contact by admin
#
#        * def sendMessageRequest =
#        """
#        {
#            "sendToStudents": true,
#            "sendToTutors": true,
#            "sendToOtherContacts": true,
#            "sendToSuppressStudents": false,
#            "sendToSuppressTutors": false,
#            "sendToSuppressOtherContacts": false,
#            "entity": "Contact",
#            "templateId": #(~~emailTemplateId),
#            "fromAddress": "sales@gmail.com",
#            "searchQuery": {
#                "search": "~\"stud\"",
#                "pageSize": 50,
#                "offset": 0,
#                "filter": "",
#                "tagGroups": []
#            },
#            "variables": {
#                "subjectTxt": "subject",
#                "body": "hello world"
#            }
#        }
#        """
#
#        Given path ishPath
#        And param messageType = 'Email'
#        And param recipientsCount = 8
#        And request sendMessageRequest
#        When method POST
#        Then status 200
#        And match $ == "Messages created successfully"



    Scenario: (+) Send email in Classes by admin



#    Scenario: (+) Send email in Classes by admin
#
##       <--->
#
#        * def sendMessageRequest =
#        """
#        {
#            "sendToStudents": true,
#            "sendToTutors": true,
#            "sendToOtherContacts": true,
#            "sendToSuppressStudents": true,
#            "sendToSuppressTutors": false,
#            "sendToSuppressOtherContacts": false,
#            "entity": "CourseClass",
#            "templateId": #(~~emailTemplateId),
#            "fromAddress": "sales@gmail.com",
#            "searchQuery": {
#                "search": "id in (6)",
#                "pageSize": 50,
#                "offset": 0,
#                "filter": "",
#                "tagGroups": []
#            },
#            "variables": {
#                "subjectTxt": "subject",
#                "body": "hello world"
#            }
#        }
#        """
#
#        Given path ishPath
#        And param messageType = 'Email'
#        And param recipientsCount = 4
#        And request sendMessageRequest
#        When method POST
#        Then status 200
#        And match $ == "Messages created successfully"



#    Scenario: (+) Send sms in Enrolments by admin
#
#        * def sendMessageRequest =
#        """
#        {
#            "sendToStudents": true,
#            "sendToTutors": true,
#            "sendToOtherContacts": true,
#            "sendToSuppressStudents": false,
#            "sendToSuppressTutors": false,
#            "sendToSuppressOtherContacts": false,
#            "entity": "Enrolment",
#            "templateId": #(~~smsTemplateId),
#            "fromAddress": null,
#            "searchQuery": {
#                "search": "id in (105)",
#                "pageSize": 50,
#                "offset": 0,
#                "filter": "",
#                "tagGroups": []
#            },
#            "variables": {
#                "message": "hello sms world"
#            }
#        }
#        """
#
#        Given path ishPath
#        And param messageType = 'Sms'
#        And param recipientsCount = 1
#        And request sendMessageRequest
#        When method POST
#        Then status 200
#        And match $ == "Messages created successfully"



#    Scenario: (+) Send sms in Waiting Lists by admin
#
#        * def sendMessageRequest =
#        """
#        {
#            "sendToStudents": true,
#            "sendToTutors": true,
#            "sendToOtherContacts": true,
#            "sendToSuppressStudents": false,
#            "sendToSuppressTutors": false,
#            "sendToSuppressOtherContacts": false,
#            "entity": "WaitingList",
#            "templateId": #(~~smsTemplateId),
#            "fromAddress": null,
#            "searchQuery": {
#                "search": "id in (1000,1001)",
#                "pageSize": 2,
#                "offset": 2,
#                "filter": "",
#                "tagGroups": []
#            },
#            "variables": {
#                "message": "hello waiting lists"
#            }
#        }
#        """
#
#        Given path ishPath
#        And param messageType = 'Sms'
#        And param recipientsCount = 2
#        And request sendMessageRequest
#        When method POST
#        Then status 200
#        And match $ == "Messages created successfully"



    Scenario: (-) Send email when recipientsCount is wrong

#       <--->
        * def sendMessageRequest =
        """
        {"sendToStudents":true,"sendToTutors":true,"sendToOtherContacts":true,"sendToSuppressStudents":false,"sendToSuppressTutors":false,"sendToSuppressOtherContacts":false,"entity":"WaitingList","templateId":#(~~emailTemplateId),"fromAddress":"sales@gmail.com","searchQuery":{"search":"id in (1000,1001)","pageSize":2,"offset":2,"filter":"","tagGroups":[]},"variables":{"subjectTxt":"hello","body":"world"}}
        """

        Given path ishPath
        And param messageType = 'Email'
        And param recipientsCount = 5
        And request sendMessageRequest
        When method POST
        Then status 400
        And match $.errorMessage == "A real recipients number doesn't equal specified. Specified: 5, Real: 2"




    Scenario: (-) Send email with not existing template

#       <--->
        * def templateId = 10000
        * def sendMessageRequest =
        """
        {"sendToStudents":true,"sendToTutors":true,"sendToOtherContacts":true,"sendToSuppressStudents":false,"sendToSuppressTutors":false,"sendToSuppressOtherContacts":false,"entity":"WaitingList","templateId":#(~~templateId),"fromAddress":"sales@gmail.com","searchQuery":{"search":"id in (1000,1001)","pageSize":2,"offset":2,"filter":"","tagGroups":[]},"variables":{"subjectTxt":"world"}}
        """

        Given path ishPath
        And param messageType = 'Email'
        And param recipientsCount = 2
        And request sendMessageRequest
        When method POST
        Then status 400
        And match $.errorMessage == "The message template didn't find out."
        

    Scenario: (-) Send email by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def sendMessageRequest =
        """
        {
            "sendToStudents": true,
            "sendToTutors": true,
            "sendToOtherContacts": true,
            "sendToSuppressStudents": false,
            "sendToSuppressTutors": false,
            "sendToSuppressOtherContacts": false,
            "entity": "Enrolment",
            "templateId": #(~~emailTemplateId),
            "fromAddress": "sales@gmail.com",
            "searchQuery": {
                "search": "id in (105)",
                "pageSize": 50,
                "offset": 0,
                "filter": "",
                "tagGroups": []
            },
            "variables": {
                "subjectTxt": "subject",
                "body": "hello world"
            }
        }
        """


        Given path ishPath
        And param messageType = 'Email'
        And param recipientsCount = 1
        And request sendMessageRequest
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to send emails. Please contact your administrator"
