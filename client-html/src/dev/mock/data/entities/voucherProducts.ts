import { generateArraysOfRecords } from "../../mockUtils";

export function mockVoucherProducts() {
  this.getVoucherProducts = () => {
    return this.voucherProducts;
  };

  this.getVoucherProduct = id => {
    const row = this.voucherProducts.rows.find(row => row.id == id);
    return {
      id: row.id,
      code: row.values[0],
      name: row.values[1],
      feeExTax: row.values[2],
      soldVouchersCount: row.values[4],
      value: row.values[2] + 10,
      courses: [
        {
          code: "AVID",
          id: 1,
          name: "Editing in AVID"
        },
        {
          code: "SPAM",
          id: 2,
          name: "Email Filtering and Security"
        },
        {
          code: "RPL",
          id: 3,
          name: "RPL Evidence and onCourse"
        },
        {
          code: "sss",
          id: 4,
          name: "certificate in software testing"
        },
        {
          code: "FFFFFFF",
          id: 5,
          name: "Far Out Discounts"
        },
        {
          code: "GOGL",
          id: 6,
          name: "Improving your Google Ranking"
        }
      ],
      corporatePasses: [],
      expiryDays: 365,
      liabilityAccountId: 1,
      maxCoursesRedemption: 3,
      status: "Can be purchased in office",
      createdOn: "2012-09-27T17:00:04.000Z",
      modifiedOn: "2015-03-31T10:23:37.000Z",
      description:
        "Gift vouchers can be purchased for any amount. You will be able to nominate the voucher value during the checkout process.",
      validToOffset: null
    };
  };

  this.getVoucherProductPlainList = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "code", type: "string" },
      { name: "name", type: "string" },
      { name: "price", type: "number" }
    ]).map(l => ({
      id: l.id,
      values: [l.code, l.name, l.price]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "VoucherProduct";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  this.createVoucherProduct = item => {
    const data = JSON.parse(item);
    const voucherProducts = this.voucherProducts;
    const totalRows = voucherProducts.rows;

    data.id = totalRows.length + 1;

    voucherProducts.rows.push({
      id: data.id,
      values: [
        data.code,
        data.name,
        data.feeExTax,
        data.soldVouchersCount,
        data.status,
        data.value,
        data.expiryDays,
        data.description
      ]
    });

    this.voucherProducts = voucherProducts;
  };

  this.removeVoucherProduct = id => {
    this.voucherProducts.rows = this.voucherProducts.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "code", type: "string" },
    { name: "name", type: "string" },
    { name: "feeExTax", type: "number" },
    { name: "isWebVisible", type: "boolean" },
    { name: "soldVouchersCount", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.code, l.name, l.feeExTax, "true", l.soldVouchersCount]
  }));

  const columns = [
    {
      title: "SKU",
      attribute: "sku",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
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
      title: "Price",
      attribute: "priceExTax",
      sortable: true,
      visible: true,
      width: 200,
      type: "Money",
      sortFields: []
    },
    {
      title: "Online purchase",
      attribute: "isWebVisible",
      sortable: true,
      visible: true,
      width: 200,
      type: "Boolean",
      sortFields: []
    },
    {
      title: "Number sold",
      attribute: "soldVouchersCount",
      sortable: false,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "VoucherProduct";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = "(isOnSale == true)";
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "sku", ascending: true, complexAttribute: [] }];

  return response;
}
