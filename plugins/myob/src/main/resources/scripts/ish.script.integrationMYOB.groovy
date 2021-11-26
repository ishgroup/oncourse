
def records = query {
    entity "AccountTransaction"
    query "transactionDate >= ${fromDate} and transactionDate <= ${toDate}"
}

myob {
    transactions = records
}