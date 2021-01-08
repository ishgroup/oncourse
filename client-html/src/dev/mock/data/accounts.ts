import { AccountType, Account } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockAccounts(): Account[] {
  this.getAccounts = () => this.account;

  this.getPlainAccounts = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "description", type: "string" },
      { name: "accountCode", type: "string" },
      { name: "type", type: "string" },
      { name: "taxId", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.id, l.description, l.accountCode, AccountType.asset, l.taxId]
    }));

    const columns = [];

    const accounts = { rows, columns } as any;

    accounts.columns = [];
    accounts.count = null;
    accounts.filterColumnWidth = null;
    accounts.filteredCount = null;
    accounts.layout = null;
    accounts.sort = [];
    return accounts;
  };

  this.getAccount = (id: number): Account => {
    const row = this.getAccountList().rows.find(row => row.id == id);
    return {
      accountCode: row.values[0],
      createdOn: new Date().toISOString(),
      description: row.values[3],
      id: row.id,
      isDefaultAccount: true,
      isEnabled: true,
      modifiedOn: new Date().toISOString(),
      tax: null,
      type: row.values[2]
    };
  };

  this.createAccount = item => {
    const data = JSON.parse(item);
    const accounts = this.accounts;
    const totalRows = accounts.rows;

    data.id = totalRows.length + 1;

    accounts.rows.push({
      id: data.id,
      values: [data.accountCode, data.isEnabled, data.type, data.description]
    });

    this.accounts = accounts;
  };

  this.createNewAccount = () => ({
    accountCode: "accountCode21",
    description: "description 21",
    isEnabled: true,
    tax: null,
    type: "asset",
  });

  this.removeAccount = id => {
    this.accounts = this.accounts.rows.filter(m => m.id !== id);
  };

  this.getAccountList = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "accountCode", type: "number" },
      { name: "isEnabled", type: "boolean" },
      { name: "type", type: "string" },
      { name: "description", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [`accountCode ${l.accountCode}`, false, AccountType.asset, l.description]
    }));

    const columns = [
      {
        title: "Code",
        attribute: "accountCode",
        sortable: true,
        visible: true,
        width: 200,
        type: null,
        sortFields: []
      },
      {
        title: "Enabled",
        attribute: "isEnabled",
        sortable: true,
        visible: true,
        width: 200,
        type: "Boolean",
        sortFields: []
      },
      {
        title: "Type",
        attribute: "type",
        sortable: true,
        visible: true,
        width: 100,
        type: null,
        sortFields: []
      },
      {
        title: "Description",
        attribute: "description",
        sortable: true,
        visible: true,
        width: 200,
        type: null,
        sortFields: []
      }
    ];

    const response = { rows, columns } as any;

    response.entity = "Account";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 10;
    response.search = "";
    response.count = rows.length;
    response.sort = [
      {
        attribute: "accountCode",
        ascending: true,
        complexAttribute: []
      }
    ];

    return response;
  };

  return [
    {
      accountCode: "11200",
      description: "Trade debtors",
      id: 1,
      isEnabled: true,
      type: AccountType.asset
    },
    {
      accountCode: "11500",
      description: "Petty cash",
      id: 2,
      isEnabled: true,
      type: AccountType.asset
    },
    {
      accountCode: "21400",
      description: "Voucher liability",
      id: 3,
      isEnabled: true,
      type: AccountType.liability
    },
    {
      accountCode: "21500",
      description: "Trade creditors",
      id: 4,
      isEnabled: true,
      type: AccountType.liability
    },
    {
      accountCode: "21800",
      description: "General courses",
      id: 5,
      isEnabled: true,
      type: AccountType.income
    },
    {
      accountCode: "21900",
      description: "Income account",
      id: 6,
      isEnabled: true,
      type: AccountType.income
    },
    {
      accountCode: "30500",
      description: "Voucher underpayment",
      id: 9,
      isEnabled: true,
      type: AccountType.expense
    },
    {
      accountCode: "30600",
      description: "Employment",
      id: 10,
      isEnabled: true,
      type: AccountType.expense
    }
  ];
}
