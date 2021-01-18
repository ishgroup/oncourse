import { generateArraysOfRecords } from "../../mockUtils";

export function mockDiscounts() {
  this.getDiscounts = () => this.discounts;

  this.getDiscount = id => {
    const row = this.discounts.rows.find(row => row.id == id);
    return {
      id: row.id,
      validFrom: row.values[0],
      validTo: row.values[1],
      name: row.values[2],
      code: row.values[3],
      discountValue: row.values[4],
      discountPercent: row.values[5],
      discountType: "Percent",
      addByDefault: false,
      availableOnWeb: true,
      corporatePassDiscounts: [
        { id: 1, contactFullName: "Jackson1" },
        { id: 2, contactFullName: "Jackson2" }
      ],
      cosAccount: 41,
      description: null,
      discountConcessionTypes: [
        {
          allowOnWeb: true,
          id: "1",
          name: "Seniors card1",
          requireExpary: true,
          requireNumber: true
        },
        {
          allowOnWeb: true,
          id: "2",
          name: "Seniors card2",
          requireExpary: true,
          requireNumber: true
        }
      ],
      discountCourseClasses: [
        {
          active: false,
          code: "SMSM-04",
          id: 1,
          name: "SMS Marketing - getting it right first time",
          type: "Class"
        },
        {
          active: false,
          code: "FIRE-06",
          id: 2,
          name: "Firewalls, Hackers and You",
          type: "Class"
        },
        {
          active: false,
          code: "TEST-09",
          id: 3,
          name: "Test Course for On-line Enrolment Simulation",
          type: "Class"
        },
        {
          active: false,
          code: "CERT-04",
          id: 4,
          name: "Certificates and Statements of Attainment",
          type: "Class"
        }
      ],
      discountMax: null,
      discountMin: null,
      discountMemberships: [
        {
          contactRelations: [2],
          productId: 1,
          productName: "Staff1",
          productSku: "2601"
        },
        {
          contactRelations: [2],
          productId: 2,
          productName: "Staff2",
          productSku: "2602"
        }
      ],
      hideOnWeb: false,
      limitPreviousEnrolment: false,
      minEnrolments: 1,
      minValue: 0,
      predictedStudentsPercentage: 0,
      rounding: "Nearest dollar",
      studentAge: null,
      studentAgeUnder: null,
      studentEnrolledWithinDays: null,
      studentPostcode: null,
      validFromOffset: null,
      validToOffset: null
    };
  };

  this.createDiscount = item => {
    const data = JSON.parse(item);
    const discounts = this.discounts;
    const totalRows = discounts.rows;

    data.id = totalRows.length + 1;

    discounts.rows.push({
      id: data.id,
      values: [
        data.validFrom,
        data.validTo,
        data.name,
        data.code,
        data.discountValue,
        data.discountPercent,
        data.discountType
      ]
    });

    this.discounts = discounts;
  };

  this.createAndUpdateDiscount = (id = 21) => ({
    id,
    validFrom: null,
    validTo: "2012-11-01",
    name: `name ${id}`,
    code: `code ${id}`,
    discountValue: id,
    discountPercent: 0.2,
    discountType: "Percent",
    addByDefault: false,
    availableOnWeb: true,
    corporatePassDiscounts: [
      { id: 1, contactFullName: "Jackson1" },
      { id: 2, contactFullName: "Jackson2" }
    ],
    cosAccount: 41,
    description: null,
    discountConcessionTypes: [
      {
        allowOnWeb: true,
        id: "1",
        name: "Seniors card1",
        requireExpary: true,
        requireNumber: true
      },
      {
        allowOnWeb: true,
        id: "2",
        name: "Seniors card2",
        requireExpary: true,
        requireNumber: true
      }
    ],
    discountCourseClasses: [
      {
        active: false,
        code: "SMSM-04",
        id: 1,
        name: "SMS Marketing - getting it right first time",
        type: "Class"
      },
      {
        active: false,
        code: "FIRE-06",
        id: 2,
        name: "Firewalls, Hackers and You",
        type: "Class"
      },
      {
        active: false,
        code: "TEST-09",
        id: 3,
        name: "Test Course for On-line Enrolment Simulation",
        type: "Class"
      },
      {
        active: false,
        code: "CERT-04",
        id: 4,
        name: "Certificates and Statements of Attainment",
        type: "Class"
      }
    ],
    discountMax: null,
    discountMin: null,
    discountMemberships: [
      {
        contactRelations: [2],
        productId: 1,
        productName: "Staff1",
        productSku: "2601"
      },
      {
        contactRelations: [2],
        productId: 2,
        productName: "Staff2",
        productSku: "2602"
      }
    ],
    hideOnWeb: false,
    limitPreviousEnrolment: false,
    minEnrolments: 1,
    minValue: 0,
    predictedStudentsPercentage: 0,
    rounding: "Nearest dollar",
    studentAge: null,
    studentAgeUnder: null,
    studentEnrolledWithinDays: null,
    studentPostcode: null,
    validFromOffset: null,
    validToOffset: null
  });

  this.removeDiscount = id => {
    this.discounts.rows = this.discounts.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "validFrom", type: "Datetime" },
    { name: "validTo", type: "Datetime" },
    { name: "name", type: "string" },
    { name: "code", type: "string" },
    { name: "discountValue", type: "number" },
    { name: "discountPercent", type: "number" },
    { name: "discountType", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.validFrom, l.validTo, l.name, l.code, l.discountValue, 0.2, "Percent"]
  }));

  const columns = [
    {
      title: "Valid from",
      attribute: "validFrom",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Valid to",
      attribute: "validTo",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Name",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Promotional code",
      attribute: "code",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Discount value",
      attribute: "discountDollar",
      sortable: true,
      visible: true,
      width: 100,
      type: "Money",
      sortFields: []
    },
    {
      title: "Discount percent",
      attribute: "discountPercent",
      sortable: true,
      visible: true,
      width: 100,
      type: "Percent",
      sortFields: []
    },
    {
      title: "Discount type",
      attribute: "discountType",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Discount";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "validFrom", ascending: true, complexAttribute: [] }];

  return response;
}
