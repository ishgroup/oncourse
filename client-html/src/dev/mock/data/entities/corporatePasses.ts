import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockCorporatePasses() {
  this.getCorporatePasses = () => this.corporatePasses;

  this.getCorporatePass = id => {
    const row = this.corporatePasses.rows.find(row => row.id == id);
    return {
      id: row.id,
      contactFullName: row.values[0],
      invoiceEmail: row.values[1],
      expiryDate: row.values[2],
      contactId: 1,
      password: "rsa-me",
      createdOn: "2015-09-03T02:15:31.000Z",
      modifiedOn: "2019-10-15T12:06:41.000Z",
      linkedDiscounts: [
        {
          id: 1,
          discountType: "Dollar",
          discountValue: -20,
          name: "Corporate Pass Surcharge"
        },
        {
          id: 2,
          discountType: "Percent",
          discountPercent: -0.5,
          name: "CorpPass surchage with disCode"
        }
      ],
      linkedSalables: [
        {
          id: 1,
          active: false,
          code: "act-act21",
          name: "Accounting",
          type: "Class"
        },
        {
          id: 2,
          active: false,
          code: "act-act19",
          name: "Accounting",
          type: "Class"
        },
        {
          id: 3,
          active: false,
          code: "BUDG-14",
          name: "Budgeting in onCourse",
          type: "Class"
        },
        {
          id: 4,
          active: false,
          code: "CHC2-11",
          name: "Certificate III in Aged Care Work",
          type: "Class"
        },
        {
          id: 5,
          active: false,
          code: "SPAM-669",
          name: "Email Filtering and Security",
          type: "Class"
        },
        {
          id: 6,
          active: false,
          code: "Project-1",
          name: "Project Management",
          type: "Class"
        }
      ]
    };
  };

  this.createCorporatePass = item => {
    const data = JSON.parse(item);
    const corporatePasses = this.corporatePasses;
    const totalRows = corporatePasses.rows;

    data.id = totalRows.length + 1;

    corporatePasses.rows.push({
      id: data.id,
      values: [data.contactFullName, data.invoiceEmail, data.expiryDate, data.timesUsed]
    });

    this.corporatePasses = corporatePasses;
  };

  this.createNewCorporatePasses = (id = 21) => ({
    id,
    contactFullName: `contactFullName ${id}`,
    invoiceEmail: `test${id}@test.com`,
    expiryDate: null,
    contactId: 1,
    password: "rsa-me",
    createdOn: "2015-09-03T02:15:31.000Z",
    modifiedOn: "2019-10-15T12:06:41.000Z",
    linkedDiscounts: [
      {
        id: 1,
        discountType: "Dollar",
        discountValue: -20,
        name: "Corporate Pass Surcharge"
      },
      {
        id: 2,
        discountType: "Percent",
        discountPercent: -0.5,
        name: "CorpPass surchage with disCode"
      }
    ],
    linkedSalables: [
      {
        id: 1,
        active: false,
        code: "act-act21",
        name: "Accounting",
        type: "Class"
      },
      {
        id: 2,
        active: false,
        code: "act-act19",
        name: "Accounting",
        type: "Class"
      },
      {
        id: 3,
        active: false,
        code: "BUDG-14",
        name: "Budgeting in onCourse",
        type: "Class"
      },
      {
        id: 4,
        active: false,
        code: "CHC2-11",
        name: "Certificate III in Aged Care Work",
        type: "Class"
      },
      {
        id: 5,
        active: false,
        code: "SPAM-669",
        name: "Email Filtering and Security",
        type: "Class"
      },
      {
        id: 6,
        active: false,
        code: "Project-1",
        name: "Project Management",
        type: "Class"
      }
    ]
  });

  this.removeCorporatePass = id => {
    this.corporatePasses = removeItemByEntity(this.corporatePasses, id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "contactFullName", type: "string" },
    { name: "invoiceEmail", type: "string" },
    { name: "expiryDate", type: "Datetime" },
    { name: "timesUsed", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.contactFullName, l.invoiceEmail, l.expiryDate, l.timesUsed]
  }));

  return getEntityResponse({
    entity: "CorporatePass",
    rows,
    columns: [
      {
        title: "Contact",
        attribute: "contact.fullName",
        sortFields: ["contact.lastName", "contact.firstName", "contact.middleName"]
      },
      {
        title: "Email to",
        attribute: "email"
      },
      {
        title: "Expiry date",
        attribute: "expiryDate",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Used",
        attribute: "timesUsed",
        width: 100
      }
    ],
    res: {
      search: "(expiryDate is null or expiryDate >= today)",
      sort: [{ attribute: "expiryDate", ascending: true, complexAttribute: [] }]
    }
  });
}
