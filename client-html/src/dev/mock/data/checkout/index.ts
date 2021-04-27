
export function mockCheckout() {
  this.getProcessedCheckout = () => this.checkout;

  return {
    "sessionId": null,
    "ccFormUrl": null,
    "merchantReference": null,
    "paymentId": null,
    "invoice": {
      "id": null,
      "contactId": null,
      "contactName": null,
      "customerReference": null,
      "invoiceNumber": 100,
      "relatedFundingSourceId": null,
      "billToAddress": null,
      "shippingAddress": null,
      "invoiceDate": null,
      "dateDue": null,
      "overdue": null,
      "invoiceLines": [
        {
          "id": null,
          "title": null,
          "quantity": 1,
          "unit": null,
          "incomeAccountId": null,
          "incomeAccountName": null,
          "priceEachExTax": 120.00,
          "discountEachExTax": 0.00,
          "discountId": null,
          "discountName": null,
          "taxEach": 12.00,
          "finalPriceToPayIncTax": 132.00,
          "taxId": null,
          "taxName": null,
          "description": null,
          "courseClassId": null,
          "courseName": null,
          "courseCode": null,
          "classCode": null,
          "enrolmentId": null,
          "enrolledStudent": null,
          "courseId": null,
          "enrolment": {
            "id": null,
            "classId": 262,
            "appliedDiscountId": null,
            "totalOverride": null,
            "appliedVoucherId": null,
            "relatedFundingSourceId": null,
            "studyReason": null
          },
          "voucher": null,
          "article": null,
          "membership": null,
          "contactId": 2663
        }
      ],
      "total": null,
      "amountOwing": 132.00,
      "publicNotes": null,
      "paymentPlans": [

      ],
      "source": null,
      "createdByUser": null,
      "sendEmail": null,
      "createdOn": null,
      "modifiedOn": null
    }
  };
}