import { Banking } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockBankings() {
  this.getBankings = () => this.bankings;

  this.getBanking = id => {
    const row = this.bankings.rows.find(row => row.id == id);
    return {
      id: row.id,
      reconciledStatus: "N/A",
      settlementDate: row.values[1],
      adminSite: "online",
      administrationCenterId: 200,
      createdBy: row.values[4],
      total: row.values[5],
      createdOn: "2016-06-28T10:56:46.000Z",
      modifiedOn: "2016-06-28T11:26:13.000Z",
      payments: [
        {
          amount: 20,
          contactId: 3326,
          contactName: "James, Parent",
          created: "2018-08-21",
          id: "p4108",
          paymentDate: "2018-08-21",
          paymentId: 4108,
          paymentMethodName: "Credit card",
          paymentTypeName: "payment in",
          reconcilable: true,
          reconciled: true,
          source: "web",
          status: "Success"
        },
        {
          amount: 500,
          contactId: 3318,
          contactName: "Miller, James",
          created: "2018-08-17",
          id: "p4068",
          paymentDate: "2018-08-17",
          paymentId: 4068,
          paymentMethodName: "Credit card",
          paymentTypeName: "payment in",
          reconcilable: true,
          reconciled: false,
          source: "web",
          status: "Success"
        }
      ]
    };
  };

  this.createBanking = item => {
    const data = JSON.parse(item);
    const bankings = this.bankings;
    const totalRows = bankings.rows;

    data.id = totalRows.length + 1;

    bankings.rows.push({
      id: data.id,
      values: [data.reconciledStatus, data.settlementDate, data.adminSite, data.createdBy, data.total]
    });

    this.bankings = bankings;
  };

  this.createBankingMock = (): Banking => ({
    id: 21,
    reconciledStatus: "N/A",
    settlementDate: "2016-06-28",
    adminSite: "online",
    administrationCenterId: 200,
    createdBy: "onCourse Administrator",
    total: 40,
    payments: [
      {
        amount: 20,
        contactId: 3326,
        contactName: "James, Parent",
        created: "2018-08-21",
        id: "p4108",
        paymentDate: "2018-08-21",
        paymentId: 4108,
        paymentMethodName: "Credit card",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: true,
        source: "web",
        status: "Success"
      },
      {
        amount: 500,
        contactId: 3318,
        contactName: "Miller, James",
        created: "2018-08-17",
        id: "p4068",
        paymentDate: "2018-08-17",
        paymentId: 4068,
        paymentMethodName: "Credit card",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "web",
        status: "Success"
      }
    ]
  });

  this.removeBanking = id => {
    this.bankings.rows = this.bankings.rows.filter(a => a.id !== id);
  };

  this.getDepositPayment = () => [
      {
        amount: 32835,
        contactId: 3085,
        contactName: "Jackson",
        created: "2017-05-26",
        id: "p3655",
        paymentDate: "2017-05-26",
        paymentId: 3655,
        paymentMethodName: "Cheque",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "office",
        status: "Success"
      },
      {
        amount: -32835,
        contactId: 3085,
        contactName: "Jackson",
        created: "2017-05-26",
        id: "p3656",
        paymentDate: "2017-05-26",
        paymentId: 3656,
        paymentMethodName: "Cheque",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "office",
        status: "Success"
      },
      {
        amount: 700,
        contactId: 3195,
        contactName: "Banks, Jim C",
        created: "2018-04-12",
        id: "p3857",
        paymentDate: "2018-04-12",
        paymentId: 3857,
        paymentMethodName: "Credit card",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "office",
        status: "Success"
      },
      {
        amount: 550,
        contactId: 3191,
        contactName: "Swinbanks, James",
        created: "2018-06-21",
        id: "p3976",
        paymentDate: "2018-06-21",
        paymentId: 3976,
        paymentMethodName: "Credit card",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "office",
        status: "Success"
      },
      {
        amount: 118.8,
        contactId: 1304,
        contactName: "Grumpy, Mr",
        created: "2012-10-19",
        id: "p1442",
        paymentDate: "2012-10-19",
        paymentId: 1442,
        paymentMethodName: "Cash",
        paymentTypeName: "payment in",
        reconcilable: true,
        reconciled: false,
        source: "office",
        status: "Success"
      }
    ];

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "reconciledStatus", type: "string" },
    { name: "settlementDate", type: "Datetime" },
    { name: "type", type: "string" },
    { name: "adminSite", type: "string" },
    { name: "createdBy", type: "string" },
    { name: "total", type: "Money" }
  ]).map(l => ({
    id: l.id,
    values: [l.reconciledStatus, l.settlementDate, l.type, l.adminSite, l.createdBy, l.total]
  }));

  const columns = [
    {
      title: "Reconciled",
      attribute: "reconciledStatus",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Date",
      attribute: "settlementDate",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Type",
      attribute: "type",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Site",
      attribute: "adminSite.name",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "User",
      attribute: "createdBy.login",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Total",
      attribute: "total",
      sortable: false,
      visible: true,
      width: 200,
      type: "Money",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Banking";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = "";
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "settlementDate", ascending: false, complexAttribute: [] }];

  return response;
}
