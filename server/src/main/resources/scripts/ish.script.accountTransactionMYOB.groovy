def transactions = query {
    entity "AccountTransaction"
    query "createdOn after ${fromDate} and createdOn before ${toDate}"
}

transactions = transactions.sort { it.createdOn }

export {
    template accountTransactionMYOBExportTemplate
    records transactions
}
