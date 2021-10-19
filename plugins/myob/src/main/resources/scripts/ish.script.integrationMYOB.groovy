
def records = query {
    entity "AccountTransaction"
    query "transactionDate after ${fromDate} and transactionDate before ${toDate}"
}

myob {
    transactions = records
}