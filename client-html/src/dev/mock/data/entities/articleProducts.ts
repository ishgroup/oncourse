import { generateArraysOfRecords } from "../../mockUtils";

export function mockArticleProducts() {
  this.getArticleProducts = () => this.articleProducts;

  this.getArticleProduct = id => {
    const row = this.articleProducts.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      code: row.values[1],
      description: "This is a no refundable fee for students process",
      incomeAccountId: 1,
      feeExTax: 15,
      totalFee: row.values[2],
      taxId: 1,
      status: "Can be purchased in office & online",
      active: true,
      documents: [],
      notes: [],
      tags: [this.getTag(1)]
    };
  };

  this.getPlainArticleProductList = () => {
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

    response.entity = "ArticleProduct";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  this.createArticleProduct = item => {
    const data = JSON.parse(item);
    const articleProducts = this.articleProducts;
    const totalRows = articleProducts.rows;

    data.id = totalRows.length + 1;

    articleProducts.rows.push({
      id: data.id,
      values: [data.name, data.code, data.totalFee, 10]
    });

    this.articleProducts = articleProducts;
  };

  this.mockedCreateArticleProduct = () => ({
    "code": "qq50",
    "corporatePasses": [],
    "description": "This is a no refundable fee for students wishing to apply for courses which have an application and assessment process\n  {render:\"textile\"}",
    "feeExTax": 45.45,
    "id": 0,
    "incomeAccountId": 15,
    "relatedSellables": [],
    "name": "$50 Application",
    "taxId": 1,
    "totalFee": 50,
    "status": "Can be purchased in office & online"
  });

  this.removeArticleProduct = id => {
    this.articleProducts.rows = this.articleProducts.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "code", type: "string" },
    { name: "totalFee", type: "number" },
    { name: "number_sold", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.code, l.totalFee, l.number_sold]
  }));

  const columns = [
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
      title: "Code",
      attribute: "sku",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Price",
      attribute: "totalFee",
      sortable: false,
      visible: true,
      width: 200,
      type: "Money",
      sortFields: []
    },
    {
      title: "Number sold",
      attribute: "number_sold",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "ArticleProduct";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "name", ascending: true, complexAttribute: [] }];

  return response;
}
