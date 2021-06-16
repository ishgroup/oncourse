@ignore
@parallel=false
Feature: re-usable feature to getting checkout model

  Scenario:

    * def getDate =
      """
      function() {
        var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
        var sdf = new SimpleDateFormat('yyyy-MM-dd');
        var date = new java.util.Date();
        return sdf.format(date);
      }
      """

    * def contactNodes = []
    * def membership = {"productId": #(checkoutModelTable[i].membershipIds.first), "validTo": "2030-10-23"}
    * def voucher = {"productId": #(checkoutModelTable[i].voucherData.id), "validTo": "2031-04-23", "value": #(checkoutModelTable[i].voucherData.value), "restrictToPayer": false}
    * def firstContactNode =
      """
        {
          "contactId": #(firstId),
          "enrolments": #(enrolment),
          "memberships": [],
          "vouchers": [],
          "products": #(product),
          "sendConfirmation": true
        }
      """
    * if (membership.productId != null) firstContactNode.memberships.push(membership)
    * if (voucher.productId != null) firstContactNode.vouchers.push(voucher)
    * if (checkoutModelTable[i].firstId != null) contactNodes.push(firstContactNode)

    * def membership = {"productId": #(checkoutModelTable[i].membershipIds.second), "validTo": "2030-10-23"}
    * def secondContactNode =
      """
        {
          "contactId": #(secondId),
          "enrolments": #(enrolment),
          "memberships": [],
          "vouchers": [],
          "products": [],
          "sendConfirmation": true
        }
      """
    * if (membership.productId != null) secondContactNode.memberships.push(membership)
    * if (checkoutModelTable[i].secondId != null) contactNodes.push(secondContactNode)

    * def checkoutModel =
      """
        {
          "payerId": #(payerId),
          "paymentMethodId": 1,
          "payNow": 0,
          "merchantReference": "",
          "contactNodes": #(contactNodes),
          "sendInvoice": true,
          "previousInvoices": {},
          "allowAutoPay": true,
          "payForThisInvoice": 0,
          "invoiceDueDate": '2020-04-24',
          "payWithSavedCard": false,
          "redeemedVouchers": {}
        }
      """
    * set checkoutModel.invoiceDueDate = getDate()