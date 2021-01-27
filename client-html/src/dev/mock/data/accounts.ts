import { AccountType, Account } from "@api/model";
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../mockUtils";

export function mockAccounts() {
  this.getAccounts = (): Account[] => [
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

    return getEntityResponse({
      entity: "Account",
      rows,
      plain: true
    });
  };

  this.getAccount = (id: number): Account => {
    const row = this.accounts.rows.find(row => row.id == id);
    return {
      accountCode: row.values[0],
      createdOn: "2021-01-08T12:59:54.833Z",
      description: row.values[3],
      id: row.id,
      isDefaultAccount: true,
      isEnabled: true,
      modifiedOn: "2021-01-08T12:59:54.833Z",
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
    this.accounts = removeItemByEntity(this.accounts, id);
  };

  this.getAccountList = () => this.accounts;

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

  return getEntityResponse({
    entity: "Account",
    rows,
    columns: [
      {
        title: "Code",
        attribute: "accountCode",
        sortable: true
      },
      {
        title: "Enabled",
        attribute: "isEnabled",
        sortable: true,
        type: "Boolean"
      },
      {
        title: "Type",
        attribute: "type",
        sortable: true,
        width: 100
      },
      {
        title: "Description",
        attribute: "description",
        sortable: true
      }
    ],
    res: {
      sort: [
        {
          attribute: "accountCode",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}