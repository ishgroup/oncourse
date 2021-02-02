import { format } from "date-fns";
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockInvoices() {
  this.getInvoices = () => this.invoices;

  this.getInvoice = id => {
    const row = this.invoices.rows.find(row => row.id == id);

    return {
      id: row.id,
      amountOwing: row.values[4],
      billToAddress: "",
      contactId: 323,
      contactName: row.values[3],
      createdByUser: "admin",
      createdOn: "2021-01-20T05:31:37.412Z",
      customerReference: null,
      dateDue: format(new Date(row.values[2]), "yyyy-MM-dd"),
      invoiceDate: format(new Date(row.values[2]), "yyyy-MM-dd"),
      invoiceLines: [
        {
          id: 461,
          title: "Student, New Email Filtering and Security",
          quantity: 1.0,
          unit: null,
          incomeAccountId: 15,
          incomeAccountName: "Student enrolments 41000",
          cosAccountId: null,
          cosAccountName: null,
          priceEachExTax: 120.0,
          discountEachExTax: 0.0,
          taxEach: 12.0,
          taxId: 1,
          taxName: "Australian GST",
          description: "SPAM-07 Email Filtering and Security",
          courseClassId: null,
          courseName: null,
          courseCode: null,
          classCode: null,
          enrolmentId: null,
          enrolledStudent: null,
          courseId: null
        }
      ],
      invoiceNumber: row.values[0],
      modifiedOn: "2021-01-20T05:31:37.412Z",
      notes: [],
      overdue: row.values[6],
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
      publicNotes: null,
      sendEmail: false,
      shippingAddress: null,
      source: row.values[1],
      total: row.values[5]
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
    const columnList = params.columns.split(",");
    let rows = [];

    if (columnList.includes("amountOwing")) {
      const id = params.search.replace(/\D/g, "");
      const invoice = this.getInvoice(id);
      rows.push({
        id,
        values: [invoice.contactId, `lastName ${id}`, `firstName ${id}`, invoice.overdue]
      });
    } else {
      rows = this.invoices.rows;
    }

    return getEntityResponse({
      entity: "Invoice",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "invoiceNumber", type: "number" },
    { name: "source", type: "string" },
    { name: "dateDue", type: "Datetime" },
    { name: "contactName", type: "string" },
    { name: "amountOwing", type: "number" },
    { name: "total", type: "number" },
    { name: "overdue", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.invoiceNumber, l.source, l.dateDue, l.contactName, 132, 132, 132]
  }));

  return getEntityResponse({
    entity: "Invoice",
    rows,
    columns: [
      {
        title: "Invoice number",
        attribute: "invoiceNumber",
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
