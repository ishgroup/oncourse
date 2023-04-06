import { VoucherProduct } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockVoucherProducts() {
  this.getVoucherProducts = () => this.voucherProducts;

  this.getVoucherProduct = (id: any): VoucherProduct => {
    const row = this.voucherProducts.rows.find(voucher => Number(voucher.id) === Number(id));
    return {
      id: row.id,
      code: row.values[0],
      name: row.values[1],
      feeExTax: row.values[2],
      liabilityAccountId: 1,
      underpaymentAccountId: 1,
      expiryDays: 365,
      value: row.values[2] + 10,
      maxCoursesRedemption: 3,
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
      description:
        "Gift vouchers can be purchased for any amount. You will be able to nominate the voucher value during the checkout process.",
      dataCollectionRuleId: 55667,
      status: "Can be purchased in office",
      corporatePasses: [],
      soldVouchersCount: row.values[4],
      relatedSellables: [],
      createdOn: "2012-09-27T17:00:04.000Z",
      modifiedOn: "2015-03-31T10:23:37.000Z",

    };
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

    return getEntityResponse({
      entity: "VoucherProduct",
      rows,
      plain: true
    });
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

  return getEntityResponse({
    entity: "VoucherProduct",
    rows,
    columns: [
      {
        title: "SKU",
        attribute: "sku",
        sortable: true
      },
      {
        title: "Name",
        attribute: "name",
        sortable: true
      },
      {
        title: "Price",
        attribute: "priceExTax",
        sortable: true,
        type: "Money"
      },
      {
        title: "Online purchase",
        attribute: "isWebVisible",
        sortable: true,
        type: "Boolean"
      },
      {
        title: "Number sold",
        attribute: "soldVouchersCount",
        width: 100
      }
    ],
    res: {
      search: "(isOnSale == true)",
      sort: [{ attribute: "sku", ascending: true, complexAttribute: [] }]
    }
  });
}
