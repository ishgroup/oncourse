def memberships = query {
    entity "Membership"
    query "expiryDate after today - 1 days and expiryDate before today + 7 days"
}

message {
    template renewalNotificationTemplate
    records memberships
}
