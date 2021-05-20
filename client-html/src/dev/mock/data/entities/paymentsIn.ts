import { format } from "date-fns";
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockPaymentsIn() {
  this.getPaymentsIn = () => this.paymentsIn;

  this.getPaymentIn = id => {
    const row = this.paymentsIn.rows.find(row => row.id == id);
    const site = this.getSite(row.id);

    return {
      id: row.id,
      createdOn: "2020-01-25T05:46:52.854Z",
      modifiedOn: "2020-01-25T05:46:52.854Z",
      accountInName: row.values[5],
      administrationCenterId: site.id,
      administrationCenterName: site.name,
      amount: row.values[4],
      ccSummary: [],
      ccTransaction: "1098103",
      chequeSummary: {},
      createdBy: "admin",
      dateBanked: null,
      datePayed: row.values[6],
      emailConfirmation: false,
      invoices: [
        {
          id: 1, dateDue: "2011-08-03", invoiceNumber: 31, amountOwing: 110.0, amount: 110.0
        },
        {
          id: 2, dateDue: "2011-08-03", invoiceNumber: 32, amountOwing: 0.0, amount: -110.0
        },
        {
          id: 3, dateDue: "2011-08-04", invoiceNumber: 53, amountOwing: 132.0, amount: 132.0
        }
      ],
      payerId: 532,
      payerName: row.values[3],
      paymentInType: row.values[1],
      source: row.values[0],
      status: row.values[2]
    };
  };

  this.createPaymentIn = item => {
    const data = JSON.parse(item);
    const paymentsIn = this.paymentsIn;
    const totalRows = paymentsIn.rows;

    data.id = totalRows.length + 1;

    paymentsIn.rows.push({
      id: data.id,
      values: [
        data.source,
        data.paymentInType,
        data.status,
        data.payerName,
        data.amount,
        data.accountInName,
        data.datePayed
      ]
    });

    this.paymentsIn = paymentsIn;
  };

  this.removePaymentIn = id => {
    this.paymentsIn = removeItemByEntity(this.paymentsIn, id);
  };

  this.getPlainPaymentsIn = params => {
    let rows: any[];
    const columns = params.columns.split(",");

    if (columns.includes("gatewayReference")) {
      rows = generateArraysOfRecords(2, [
        { name: "id", type: "number" },
        { name: "createdOn", type: "Datetime" },
        { name: "gatewayReference", type: "string" },
        { name: "creditCardClientReference", type: "string" },
        { name: "amount", type: "number" },
        { name: "privateNotes", type: "string" }
      ]).map(l => ({
        id: l.id,
        values: [
          "2021-01-28T08:09:37.835Z",
          "test ref",
          "cc ref",
          l.amount * 100,
          l.privateNotes
        ]
      }));
    } if (columns.includes("creditCardNumber")) {
      rows = generateArraysOfRecords(1, [
        { name: "id", type: "number" },
        { name: "creditCardNumber", type: "string" },
        { name: "creditCardType", type: "string" },
        { name: "createdOn", type: "Datetime" }
      ]).map(l => ({
        id: l.id,
        values: [
          "424242......4242",
          "VISA",
          "2020-05-08T07:39:42.000Z"
        ]
      }));
    } else {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "status", type: "string" },
        { name: "paymentInType", type: "string" },
        { name: "source", type: "string" },
        { name: "totalOwing", type: "number" },
        { name: "reconciled", type: "boolean" },
        { name: "reversalOf", type: "string" },
        { name: "reversedBy", type: "string" },
        { name: "bookingId", type: "number" },
        { name: "amount", type: "number" },
        { name: "payerName", type: "string" }
      ]).map(l => ({
        id: l.id,
        values: [
          "Success",
          "Credit card",
          "office",
          "132.00",
          "true",
          l.reversalOf,
          l.reversedBy,
          l.bookingId,
          l.amount * 100,
          l.payerName
        ]
      }));
    }

    return getEntityResponse({
      entity: "PaymentIn",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "source", type: "string" },
    { name: "paymentInType", type: "string" },
    { name: "status", type: "string" },
    { name: "payerName", type: "string" },
    { name: "amount", type: "number" },
    { name: "accountInName", type: "number" },
    { name: "datePayed", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [
      "office",
      "Credit card",
      "Success",
      l.payerName,
      l.amount * 100,
      "11100",
      format(new Date(l.datePayed), "yyyy-MM-dd")
    ]
  }));

  return getEntityResponse({
    entity: "PaymentIn",
    rows,
    columns: [
      {
        title: "Source",
        attribute: "source",
        sortable: true
      },
      {
        title: "Type",
        attribute: "paymentMethod.name",
        sortable: true
      },
      {
        title: "Status",
        attribute: "statusString",
        sortable: true,
        sortFields: ["status"]
      },
      {
        title: "Name",
        attribute: "payer.fullName",
        sortable: true,
        sortFields: ["payer.lastName", "payer.firstName", "payer.middleName"]
      },
      {
        title: "Amount",
        attribute: "amount",
        type: "Money",
        sortable: true
      },
      {
        title: "Account",
        attribute: "accountIn.accountCode",
        sortable: true
      },
      {
        title: "Date paid",
        attribute: "paymentDate",
        type: "Datetime",
        sortable: true
      }
    ],
    res: {
      sort: [
        {
          attribute: "source",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}
