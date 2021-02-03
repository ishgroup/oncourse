import { AccountType } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockAccountTransactions() {
  this.getAccountTransactions = () => this.accountTransactions;

  this.getAccountTransaction = id => {
    const row = this.accountTransactions.rows.find(row => row.id == id);
    return {
      id: row.id,
      transactionDate: row.values[0],
      fromAccount: row.values[1],
      amount: row.values[4],
      toAccount: null
    };
  };

  this.createAccountTransaction = item => {
    const data = JSON.parse(item);
    const accountTransactions = this.accountTransactions;
    const totalRows = accountTransactions.rows;

    data.id = totalRows.length + 1;

    accountTransactions.rows.push({
      id: data.id,
      values: [data.transactionDate, data.fromAccount, "Deposited funds", AccountType.asset, data.amount, "Payment in line", new Date().toISOString()]
    });

    this.accountTransactions = accountTransactions;
  };

  this.removeAccountTransaction = id => {
    this.accountTransactions = this.accountTransactions.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "transactionDate", type: "Datetime" },
    { name: "fromAccount", type: "number" },
    { name: "amount", type: "number" },
    { name: "createdOn", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [l.transactionDate, l.fromAccount, "Deposited funds", AccountType.asset, 100 * l.amount, "Payment in line", l.createdOn]
  }));

  return getEntityResponse({
    entity: "AccountTransaction",
    rows,
    columns: [
      {
        title: "Date",
        attribute: "transactionDate",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Account code",
        attribute: "account.accountCode",
        sortable: true,
        width: 100
      },
      {
        title: "Account description",
        attribute: "account.description",
        sortable: true
      },
      {
        title: "Account type",
        attribute: "account.type",
        width: 100
      },
      {
        title: "Amount",
        attribute: "amount",
        sortable: true,
        type: "Money"
      },
      {
        title: "Source",
        attribute: "tableName",
        sortable: true,
        width: 100
      },
      {
        title: "Created",
        attribute: "createdOn",
        type: "Datetime",
        sortable: true
      }
    ],
    res: {
      search: "( (tableName == 'I') or (tableName == 'P' or tableName == 'O') )",
      sort: [{ attribute: "transactionDate", ascending: true, complexAttribute: [] }]
    }
  });
}
