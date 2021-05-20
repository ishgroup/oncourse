import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

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
    this.discounts = removeItemByEntity(this.discounts, id);
  };

  this.getPlainDiscounts = () => {
    const rows = generateArraysOfRecords(1, [
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "code", type: "string" },
      { name: "discountType", type: "string" },
      { name: "rounding", type: "string" },
      { name: "discountDollar", type: "string" },
      { name: "discountPercent", type: "number" },
      { name: "validFrom", type: "Datetime" },
      { name: "validTo", type: "Datetime" }
    ]).map(l => ({
      id: l.id,
      values: [l.id, l.name, l.code, "Percent", "No Rounding", null, 0.2, null, null]
    }));

    return getEntityResponse({
      entity: "Discount",
      rows,
      plain: true
    });
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

  return getEntityResponse({
    entity: "Discount",
    rows,
    columns: [
      {
        title: "Valid from",
        attribute: "validFrom",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Valid to",
        attribute: "validTo",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Name",
        attribute: "name",
        sortable: true
      },
      {
        title: "Promotional code",
        attribute: "code",
        sortable: true
      },
      {
        title: "Discount value",
        attribute: "discountDollar",
        sortable: true,
        width: 100,
        type: "Money"
      },
      {
        title: "Discount percent",
        attribute: "discountPercent",
        sortable: true,
        width: 100,
        type: "Percent"
      },
      {
        title: "Discount type",
        attribute: "discountType",
        sortable: true,
        width: 100
      }
    ],
    res: {
      sort: [{ attribute: "validFrom", ascending: true, complexAttribute: [] }]
    }
  });
}
