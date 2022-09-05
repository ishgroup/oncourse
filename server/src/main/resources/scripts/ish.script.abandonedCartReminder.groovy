records = query {
    entity "Checkout"
    query "createdOn yesterday"
}
message {
    template emailTemplate
    record records
}