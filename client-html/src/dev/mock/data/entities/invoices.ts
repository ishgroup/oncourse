import { Invoice } from '@api/model';
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockInvoices() {
  this.getInvoices = () => this.invoices;

  this.getInvoice = (id): Invoice => {
    const row = this.invoices.rows.find(invoice => Number(invoice.id) === Number(id));

    return {
      id: row.id,
      type: row.values[1],
      contactId: 323,
      leadId: -1,
      leadCustomerName: "",
      contactName: row.values[6],
      customerReference: "Test",
      invoiceNumber: row.values[2],
      quoteNumber: row.values[3],
      relatedFundingSourceId: null,
      billToAddress: "Surtherland",
      title: "Test",
      description: "Lorem ipsum",
      shippingAddress: "Lorem ipsum",
      invoiceDate: "2011-08-03",
      dateDue: "2011-08-03",
      overdue: row.values[9],
      invoiceLines: [
        {
          id: 461,
          title: "Student, New Email Filtering and Security",
          quantity: 1.0,
          unit: null,
          incomeAccountId: 15,
          incomeAccountName: "Student enrolments 41000",
          priceEachExTax: 120.0,
          discountEachExTax: 0.0,
          discountId: null,
          discountName: null,
          taxEach: 12.0,
          finalPriceToPayIncTax: null,
          taxId: 1,
          taxName: "Australian GST",
          description: "SPAM-07 Email Filtering and Security",
          courseClassId: null,
          courseName: null,
          courseCode: null,
          classCode: null,
          enrolmentId: null,
          enrolledStudent: null,
          courseId: null,
          enrolment: null,
          voucher: null,
          article: null,
          membership: null,
          contactId: null,
        }
      ],
      total: row.values[8],
      amountOwing: row.values[7],
      publicNotes: "",
      paymentPlans: [
        {
          id: 461,
          date: "2011-08-01",
          type: "Invoice office",
          successful: true,
          amount: 132.0,
          entityName: "Invoice"
        },
        {
          id: 517,
          date: "2018-04-17",
          type: "Payment due",
          successful: true,
          amount: 132.0,
          entityName: "InvoiceDueDate"
        },
        {
          id: 381,
          date: "2011-08-01",
          type: "payment in (Credit card)",
          successful: false,
          amount: 132.0,
          entityName: "PaymentIn"
        },
        {
          id: 583,
          date: "2011-08-18",
          type: "payment in (Credit card)",
          successful: false,
          amount: 132.0,
          entityName: "PaymentIn"
        },
        {
          id: 1875,
          date: "2012-12-20",
          type: "payment in (Credit card)",
          successful: false,
          amount: 132.0,
          entityName: "PaymentIn"
        },
        {
          id: 1878,
          date: "2012-12-20",
          type: "payment in (Credit card)",
          successful: false,
          amount: 132.0,
          entityName: "PaymentIn"
        },
        {
          id: 1906,
          date: "2012-12-20",
          type: "payment in (Credit card)",
          successful: false,
          amount: 132.0,
          entityName: "PaymentIn"
        }
      ],
      source: row.values[4],
      createdByUser: "admin",
      sendEmail: false,
      tags: [this.getTag(1), this.getTag(2)],
      createdOn: "2011-08-03T08:04:51.000Z",
      modifiedOn: "2021-01-20T05:31:37.412Z",
    };
  };

  this.createInvoice = item => {
    const data = JSON.parse(item);
    const invoices = this.invoices;
    const totalRows = invoices.rows;

    data.id = totalRows.length + 1;

    invoices.rows.push({
      id: data.id,
      values: [
        data.invoiceNumber,
        data.source,
        data.dateDue,
        data.contactName,
        data.amountOwing,
        data.total,
        data.overdue
      ]
    });

    this.invoices = invoices;
  };

  this.removeInvoice = id => {
    this.invoices = removeItemByEntity(this.invoices, id);
  };

  this.getPlainInvoiceLines = () => {
    const rows = generateArraysOfRecords(5, [
      { name: "id", type: "number" },
      { name: "invoiceNumber", type: "number" },
      { name: "finalPriceToPayIncTax", type: "number" },
      { name: "lastName", type: "string" },
      { name: "firstName", type: "string" },
      { name: "isCompany", type: "boolean" }
    ]).map(l => ({
      id: l.id,
      values: [l.invoiceNumber, "132.00", l.lastName, l.firstName, false]
    }));

    return getEntityResponse({
      entity: "InvoiceLine",
      rows,
      plain: true
    });
  };

  this.getPlainInvoices = params => {
    let rows = [];
    if (params.columns === "contact.id,contact.lastName,contact.firstName,amountOwing") {
      rows = generateArraysOfRecords(5, [
        { name: "id", type: "number" },
        { name: "contact.id", type: "number" },
        { name: "contact.lastName", type: "string" },
        { name: "contact.firstName", type: "string" },
        { name: "amountOwing", type: "number" },
      ]).map(({ id, ...values }) => ({
        id,
        values
      }));
    } else {
      rows = this.invoices.rows;
    }

    return getEntityResponse({
      entity: params.entity || "Invoice",
      rows,
      plain: true
    });
  };

  this.getAmountOwing = amountOwing => {
    if (amountOwing) {
      return [
        {
          "id": 5332,
          "contactId": null,
          "contactName": null,
          "customerReference": null,
          "invoiceNumber": 3251,
          "relatedFundingSourceId": null,
          "billToAddress": null,
          "shippingAddress": null,
          "invoiceDate": "2020-05-08",
          "dateDue": "2020-05-15",
          "overdue": null,
          "payDueAmounts": true,
          "invoiceLines": [

          ],
          "total": null,
          "amountOwing": 234.00,
          "publicNotes": null,
          "paymentPlans": [
            {
              "id": 5332,
              "date": "2020-05-08",
              "type": "Invoice office",
              "successful": true,
              "amount": 264.00,
              "entityName": "Invoice"
            },
            {
              "id": 4162,
              "date": "2020-05-08",
              "type": "payment in (Cash)",
              "successful": true,
              "amount": 0.00,
              "entityName": "PaymentIn"
            },
            {
              "id": 4166,
              "date": "2020-05-08",
              "type": "payment in (Cash)",
              "successful": true,
              "amount": 30.00,
              "entityName": "PaymentIn"
            }
          ],
          "source": null,
          "createdByUser": null,
          "sendEmail": null,
          "createdOn": null,
          "modifiedOn": null
        },
        {
          "id": 5334,
          "contactId": null,
          "contactName": null,
          "customerReference": null,
          "invoiceNumber": 3253,
          "relatedFundingSourceId": null,
          "billToAddress": null,
          "shippingAddress": null,
          "invoiceDate": "2020-05-08",
          "dateDue": "2020-05-15",
          "overdue": null,
          "payDueAmounts": true,
          "invoiceLines": [

          ],
          "total": null,
          "amountOwing": 200.00,
          "publicNotes": null,
          "paymentPlans": [
            {
              "id": 5334,
              "date": "2020-05-08",
              "type": "Invoice office",
              "successful": true,
              "amount": 200.00,
              "entityName": "Invoice"
            },
            {
              "id": 4166,
              "date": "2020-05-08",
              "type": "payment in (Cash)",
              "successful": true,
              "amount": 0.00,
              "entityName": "PaymentIn"
            }
          ],
          "source": null,
          "createdByUser": null,
          "sendEmail": null,
          "createdOn": null,
          "modifiedOn": null
        },
        {
          "id": 5335,
          "contactId": null,
          "contactName": null,
          "customerReference": null,
          "invoiceNumber": 3254,
          "relatedFundingSourceId": null,
          "billToAddress": null,
          "shippingAddress": null,
          "invoiceDate": "2020-05-08",
          "dateDue": "2020-05-15",
          "overdue": null,
          "payDueAmounts": true,
          "invoiceLines": [

          ],
          "total": null,
          "amountOwing": 102.00,
          "publicNotes": null,
          "paymentPlans": [
            {
              "id": 5335,
              "date": "2020-05-08",
              "type": "Invoice office",
              "successful": true,
              "amount": 132.00,
              "entityName": "Invoice"
            },
            {
              "id": 4167,
              "date": "2020-05-08",
              "type": "payment in (Credit card)",
              "successful": true,
              "amount": 30.00,
              "entityName": "PaymentIn"
            }
          ],
          "source": null,
          "createdByUser": null,
          "sendEmail": null,
          "createdOn": null,
          "modifiedOn": null
        },
        {
          "id": 5339,
          "contactId": null,
          "contactName": null,
          "customerReference": null,
          "invoiceNumber": 3258,
          "relatedFundingSourceId": null,
          "billToAddress": null,
          "shippingAddress": null,
          "invoiceDate": "2020-06-05",
          "dateDue": "2020-07-31",
          "overdue": null,
          "payDueAmounts": true,
          "invoiceLines": [

          ],
          "total": null,
          "amountOwing": 200.00,
          "publicNotes": null,
          "paymentPlans": [
            {
              "id": 5339,
              "date": "2020-06-05",
              "type": "Invoice office",
              "successful": true,
              "amount": 200.00,
              "entityName": "Invoice"
            }
          ],
          "source": null,
          "createdByUser": null,
          "sendEmail": null,
          "createdOn": null,
          "modifiedOn": null
        },
        {
          "id": 5340,
          "contactId": null,
          "contactName": null,
          "customerReference": null,
          "invoiceNumber": 3259,
          "relatedFundingSourceId": null,
          "billToAddress": null,
          "shippingAddress": null,
          "invoiceDate": "2020-06-05",
          "dateDue": "2020-06-12",
          "overdue": null,
          "payDueAmounts": true,
          "invoiceLines": [

          ],
          "total": null,
          "amountOwing": 200.00,
          "publicNotes": null,
          "paymentPlans": [
            {
              "id": 5340,
              "date": "2020-06-05",
              "type": "Invoice office",
              "successful": true,
              "amount": 200.00,
              "entityName": "Invoice"
            }
          ],
          "source": null,
          "createdByUser": null,
          "sendEmail": null,
          "createdOn": null,
          "modifiedOn": null
        }
      ];
    }
    return [];
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "tagIds", type: "array" },
    { name: "type", type: "string" },
    { name: "invoiceNumber", type: "number" },
    { name: "quoteNumber", type: "number" },
    { name: "source", type: "string" },
    { name: "dateDue", type: "Datetime" },
    { name: "contactName", type: "string" },
    { name: "amountOwing", type: "number" },
    { name: "total", type: "number" },
    { name: "overdue", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [[], "Invoice", l.invoiceNumber, null, l.source, l.dateDue, l.contactName, 132, 132, 132]
  }));

  return getEntityResponse({
    entity: "Invoice",
    rows,
    columns: [
      {
        title: "Tags",
        attribute: "tagIds",
        type: "Tags",
        width: 100,
      },
      {
        title: "Type",
        attribute: "type",
        sortable: true,
        width: 100,
      },
      {
        title: "Invoice number",
        attribute: "invoiceNumber",
        sortable: true
      },
      {
        title: "Quote number",
        attribute: "quoteNumber",
        sortable: true
      },
      {
        title: "Source",
        attribute: "source",
        sortable: true
      },
      {
        title: "Date due",
        attribute: "dateDue",
        type: "Datetime",
        sortable: true
      },
      {
        title: "Name",
        attribute: "contact.fullName",
        sortable: true,
        sortFields: ["contact.lastName", "contact.firstName", "contact.middleName"]
      },
      {
        title: "Owing",
        attribute: "amountOwing",
        type: "Money",
        sortable: true
      },
      {
        title: "Total",
        attribute: "totalIncTax",
        type: "Money"
      },
      {
        title: "Overdue",
        attribute: "overdue",
        type: "Money",
        sortable: true
      },
      {
        title: "Invoice date",
        attribute: "invoiceDate",
        type: "Date",
        sortable: true,
        visible: false,
      },
      {
        title: "Reference",
        attribute: "customerReference",
        sortable: true,
        visible: false,
      }
    ],
    res: {
      sort: [
        {
          attribute: "invoiceNumber",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}
