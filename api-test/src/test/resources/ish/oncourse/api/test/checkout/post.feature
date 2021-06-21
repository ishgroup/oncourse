@parallel=false
Feature: Main feature for all POST requests with path 'checkout'

  Background: Authorize first
    * configure headers = { Authorization: 'admin' }
    * url 'https://127.0.0.1:8182/a/v1'
    * def ishPathLogin = 'login'
    * def ishPath = 'checkout'
    * def ishPathList = 'list'
    * def ishPathPlain = 'list/plain'
    * def ishPathEntity = 'list/entity/'
    * def fun =
      """
        function(list, sum, i) {
          if (list[i]) {
            return fun(list, sum + parseFloat(list[i]), ++i)
          } else {
            return sum
          }
        }
      """
    
  Scenario: Performance of the full payment cycle without previous invoices. Try to enrolling again after
    * def contactData = { "id": 6, "classId": 14 }
    * call read('fullPaymentCycle.feature') contactData
