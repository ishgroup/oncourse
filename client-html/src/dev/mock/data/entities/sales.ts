import { generateArraysOfRecords } from "../../mockUtils";

export function mockSales() {
  this.getProductItems = () => {
    return this.sales;
  };

  this.getProductItem = id => {
    const row = this.sales.rows.find(row => row.id == id);
    return {
      id: row.id,
      productName: row.values[0],
      productType: row.values[1],
      productId: row.values[2],
      expiresOn: row.values[3],
      status: "Active",
      purchasedByName: row.values[5],
      purchasedOn: row.values[6],
      purchasePrice: 200,
      payments: [],
      validFrom: null,
      valueRemaining: "$200.00",
      voucherCode: "x8eWKRsS",
      redeemableById: null,
      redeemableByName: null
    };
  };

  this.createProductItem = item => {
    const data = JSON.parse(item);
    const sales = this.sales;
    const totalRows = sales.rows;

    data.id = totalRows.length + 1;

    sales.rows.push({
      id: data.id,
      values: [
        data.productName,
        data.productType,
        data.productId,
        data.expiresOn,
        "Active",
        data.purchasedByName,
        data.purchasedOn
      ]
    });

    this.sales = sales;
  };

  this.removeProductItem = id => {
    this.sales.rows = this.sales.rows.filter(a => a.id !== id);
  };

  this.getPlainProductItemList = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "status", type: "string" },
      { name: "type", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.active, l.type]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "ProductItem";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "productName", type: "string" },
    { name: "productType", type: "string" },
    { name: "productId", type: "string" },
    { name: "expiresOn", type: "Datetime" },
    { name: "status", type: "string" },
    { name: "purchasedByName", type: "string" },
    { name: "purchasedOn", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [l.productName, l.productType, l.productId, l.expiresOn, "Active", l.purchasedByName, l.purchasedOn]
  }));

  const columns = [
    {
      title: "Name",
      attribute: "product.name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      system: null,
      sortFields: []
    },
    {
      title: "Type",
      attribute: "typeString",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      system: null,
      sortFields: []
    },
    {
      title: "SKU",
      attribute: "product.sku",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      system: null,
      sortFields: []
    },
    {
      title: "Expires",
      attribute: "expiryDate",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      system: null,
      sortFields: []
    },
    {
      title: "Status",
      attribute: "displayStatus",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      system: null,
      sortFields: ["status"]
    },
    {
      title: "Purchased by",
      attribute: "invoiceLine.invoice.contact.fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      system: null,
      sortFields: [
        "invoiceLine.invoice.contact.lastName",
        "nvoiceLine.invoice.contact.firstName",
        "invoiceLine.invoice.contact.middleName"
      ]
    },
    {
      title: "Purchased on",
      attribute: "createdOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      system: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "ProductItem";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search =
    "( ((type is ARTICLE)) or ((type is MEMBERSHIP)) or ((type is VOUCHER)) ) and ((status == ACTIVE and ((type is ARTICLE or type is VOUCHER) or expiryDate after today)))";
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "product.name", ascending: true, complexAttribute: [] }];

  return response;
}
