def transactions = query {
    entity "AccountTransaction"
    query "createdOn after ${fromDate} and createdOn before ${toDate}"
}

export {
    template accountTransactionMYOBExportTemplate
    record transactions.sort { it.createdOn }
}
