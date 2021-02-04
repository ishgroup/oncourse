records = query {
    entity "Document"
    query "isRemoved is true and modifiedOn <= today - 24 days"
}

if (!records.empty) {
    message {
        template templateForNotification
        to preference.email.admin
        documents records*.currentVersion
    }
}