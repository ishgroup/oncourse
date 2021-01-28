import { format } from "date-fns";
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockPaymentsOut() {
  this.getPaymentsOut = () => this.paymentsOut;

  this.getPaymentOut = id => {
    const row = this.paymentsOut.rows.find(row => row.id == id);
    const site = this.getSite(row.id);

    return {
      id: row.id,
      createdOn: "2021-01-28T07:10:28.418Z",
      modifiedOn: "2021-01-28T07:10:28.418Z",
      accountOut: 3,
      administrationCenterId: site.id,
      administrationCenterName: site.name,
      amount: row.values[2],
      chequeSummary: {},
      createdBy: "admin",
      dateBanked: row.values[4],
      datePayed: row.values[3],
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
      payeeId: 532,
      payeeName: row.values[1],
      paymentMethodId: 886543,
      privateNotes: "",
      refundableId: null,
      status: row.values[5],
      type: row.values[0]
    };
  };

  this.createPaymentOut = item => {
    const data = JSON.parse(item);
    const paymentsOut = this.paymentsOut;
    const totalRows = paymentsOut.rows;

    data.id = totalRows.length + 1;

    paymentsOut.rows.push({
      id: data.id,
      values: [data.type, data.payeeName, data.amount, data.datePayed, data.dateBanked, data.status]
    });

    this.paymentsOut = paymentsOut;
  };

  this.removePaymentOut = id => {
    this.paymentsOut = removeItemByEntity(this.paymentsOut, id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "type", type: "string" },
    { name: "payeeName", type: "string" },
    { name: "amount", type: "number" },
    { name: "datePayed", type: "Datetime" },
    { name: "dateBanked", type: "Datetime" },
    { name: "status", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [
      "Cheque",
      l.payeeName,
      l.amount * 100,
      format(new Date(l.datePayed), "yyyy-MM-dd"),
      format(new Date(l.dateBanked), "yyyy-MM-dd"),
      "Success"
    ]
  }));

  return getEntityResponse({
    entity: "PaymentOut",
    rows,
    columns: [
      {
        title: "Type",
        attribute: "paymentMethod.name",
        sortable: true
      },
      {
        title: "Payee name",
        attribute: "payee.fullName",
        sortable: true,
        sortFields: ["payee.lastName", "payee.firstName", "payee.middleName"]
      },
      {
        title: "Amount",
        attribute: "amount",
        type: "Money",
        sortable: true
      },
      {
        title: "Date paid",
        attribute: "paymentDate",
        type: "Datetime",
        sortable: true
      },
      {
        title: "Banked",
        attribute: "banking.settlementDate",
        type: "Datetime",
        sortable: true
      },
      {
        title: "Status",
        attribute: "status",
        sortable: true
      },
    ],
    res: {
      sort: [
        {
          attribute: "paymentDate",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}
