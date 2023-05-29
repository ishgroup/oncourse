def checkouts = query {
    entity "Checkout"
    query "createdOn < now - "+ expireInterval
}

if(!checkouts.empty) {
    def context = checkouts.first().context
    context.deleteObjects(checkouts)
    context.commitChanges()
}