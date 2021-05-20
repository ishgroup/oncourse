import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockSales() {
  this.getProductItems = () => this.sales;

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

  this.removeProductItem = id => {
    this.sales = removeItemByEntity(this.sales, id);
  };

  this.getPlainProductItemList = () => {
    const rows = generateArraysOfRecords(1, [
      { name: "id", type: "number" },
      { name: "status", type: "string" },
      { name: "type", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: ["Active", "3"]
    }));

    return getEntityResponse({
      entity: "ProductItem",
      rows,
      plain: true,
    });
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

  return getEntityResponse({
    entity: "ProductItem",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "product.name",
        sortable: true
      },
      {
        title: "Type",
        attribute: "typeString"
      },
      {
        title: "SKU",
        attribute: "product.sku",
        sortable: true
      },
      {
        title: "Expires",
        attribute: "expiryDate",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Status",
        attribute: "displayStatus",
        sortable: true,
        sortFields: ["status"]
      },
      {
        title: "Purchased by",
        attribute: "invoiceLine.invoice.contact.fullName",
        sortable: true,
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
        type: "Datetime"
      }
    ],
    res: {
      search: "( ((type is ARTICLE)) or ((type is MEMBERSHIP)) or ((type is VOUCHER)) ) and ((status == ACTIVE and ((type is ARTICLE or type is VOUCHER) or expiryDate after today)))",
      sort: [{ attribute: "product.name", ascending: true, complexAttribute: [] }]
    }
  });
}
