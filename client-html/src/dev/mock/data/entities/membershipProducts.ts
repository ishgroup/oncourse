import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockMembershipProducts() {
  this.getMembershipProducts = () => this.membershipProducts;

  this.getMembershipProduct = id => {
    const row = this.membershipProducts.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      totalFee: row.values[1],
      feeExTax: row.values[1],
      code: row.values[2],
      description: "This is an example of a membership. You can have mutiple membership, have discounted memberhsip",
      expiryType: "1st January",
      expiryDays: null,
      taxId: 1,
      incomeAccountId: 1,
      status: "Can be purchased in office & online",
      corporatePasses: [],
      membershipDiscounts: [],
      createdOn: "2016-11-30T11:58:39.000Z",
      modifiedOn: "2018-03-23T13:53:18.000Z"
    };
  };

  this.createMembershipProduct = item => {
    const data = JSON.parse(item);
    const membershipProducts = this.membershipProducts;
    const totalRows = membershipProducts.rows;

    data.id = totalRows.length + 1;

    membershipProducts.rows.push({
      id: data.id,
      values: [data.name, data.code, data.totalFee]
    });

    this.membershipProducts = membershipProducts;
  };

  this.removeMembershipProduct = id => {
    this.membershipProducts = removeItemByEntity(this.membershipProducts, id);
  };

  this.getMembershipProductPlainList = () => {
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
      entity: "MembershipProduct",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "totalFee", type: "number" },
    { name: "code", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.totalFee, l.code]
  }));

  return getEntityResponse({
    entity: "MembershipProduct",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "name",
        sortable: true
      },
      {
        title: "Price",
        attribute: "price_with_tax",
        sortable: true,
        type: "Money"
      },
      {
        title: "SKU",
        attribute: "sku",
        sortable: true
      }
    ],
    res: {
      search: "(isOnSale == true)",
      sort: [{ attribute: "name", ascending: true, complexAttribute: [] }]
    }
  });
}
