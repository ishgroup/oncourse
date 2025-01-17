records = query {
    entity "Checkout"
    query "createdOn yesterday"
}

records = records.findAll{!it.getShoppingCartProducts().empty || !it.getShoppingCartClasses().empty}
message {
    template emailTemplate
    record records
}